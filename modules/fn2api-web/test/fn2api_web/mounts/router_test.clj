(ns fn2api-web.mounts.router-test
  (:require [clojure.test :refer :all]
            [mount.core]
            [fn2api-web.mounts.router :refer [router]]
            [reitit.core :refer [match-by-path match-by-name]]
            [reitit.ring :refer [ring-handler]]
            [fn2api-web.app.minimal :refer [router->app]]))

(deftest routing

  (testing "matching by path&name on placeholder from fn2api-web.mounts.routes"
    ;; reset routes in case it was set different by other testcase
    (mount.core/stop)
    (mount.core/start-with-args {})

    (is (= :placeholder (:name (:data (match-by-path ((:dev router)) "/")))))
    (is (= "/" (:path (match-by-name ((:dev router)) :placeholder))))

    ;; this only works for :prod since :dev yields always a new instance
    (is (= (match-by-name ((:prod router)) :placeholder)
           (match-by-path ((:prod router)) "/")))
    (is (not= (match-by-name ((:dev router)) :placeholder)
              (match-by-path ((:dev router)) "/"))))

  (testing "handler"
    (is (= {:status 404 :headers {"Content-Type" "text/html"} :body "Not found - Please add some routes"}
           ((get-in (match-by-name ((:dev router)) :placeholder) [:data :get :handler]))
           ((ring-handler ((:dev router))) {:request-method :get :uri "/"})
           ((router->app (:dev router)) {:request-method :get :uri "/"}))))

  (testing "router after routes have been replaced"
    (mount.core/stop)
    (mount.core/start-with-args {:fn2api-web {:routes (fn [] [["/" {:name :index
                                                                    :get {:handler (fn [& args]
                                                                                       {:status 200
                                                                                        :body "Hello World"})}} ]])}})
    (is (= {:status 200 :body "Hello World"}
           ((get-in (match-by-path ((:prod router)) "/") [:data :get :handler]))
           ((get-in (match-by-path ((:dev router)) "/") [:data :get :handler]))))))
