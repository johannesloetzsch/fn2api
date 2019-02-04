(ns fn2api-web.middleware.error-test
  (:require [clojure.test :refer :all]
            [mount.core]
            [fn2api-web.mounts.router :refer [router]]
            [fn2api-web.app.minimal :refer [router->app]]
            [ring.mock.request :refer [request header]]
            [clojure.edn :as edn]
            [clojure.spec.alpha :as s]))

(defn edn-request [query-string]
  (-> (request :get query-string)
      (header "Accept" "application/edn")
      ((router->app (:dev router)))
      (update-in [:body] #(edn/read-string (slurp %)))
      (select-keys [:status :body])))

(deftest example
  (s/def ::a number?)
  (s/def ::b number?)  ;; we want an error when dividing by 0
  (s/def ::div (s/keys :req-un [::a ::b]))
  
  (mount.core/stop)
  (mount.core/start-with-args {:fn2api-web {:routes (fn [] [["/" {:get {:parameters {:query ::div}
                                                                        :handler (fn [{:keys [parameters]}]  ;; TODO wrap
                                                                                     (let [query (:query parameters)
                                                                                           a (:a query)
                                                                                           b (:b query)]
                                                                                          {:status 200
                                                                                           :body {:result (/ a b)}}))}} ]])}})

  (testing "correct"
    (is (= (edn-request "/?a=69&b=3")
           {:status 200 :body {:result 23.0}})))
  
  (testing "spec-error"
    (is (= (-> (edn-request "/?a=69") ;; second argument `b` missing
               (update-in [:body] #(select-keys % [:type :in])))
           {:status 400 :body {:type :reitit.coercion/request-coercion
                               :in [:request :query-params]}}))
  
    (is (= (-> (edn-request "/?a=69&b=foo") ;; second argument `b` can not be cast to number
               (update-in [:body] #(select-keys % [:type :in])))
           {:status 400 :body {:type :reitit.coercion/request-coercion
                               :in [:request :query-params]}})))
  
  (testing "exception"
    (is (= (edn-request "/?a=1&b=0")
           {:status 500 :body {:type "exception" :class "java.lang.ArithmeticException"}}))))
