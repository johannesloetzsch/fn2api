(ns fn2api-web.mounts.router-test
  (:require [clojure.test :refer :all]
            [mount.core]
            [fn2api-web.mounts.router :refer [router]]
            [reitit.core :refer [match-by-path match-by-name]]
            [reitit.ring :refer [ring-handler]]
            [fn2api-web.app.minimal :refer [router->app]]))

(deftest routing

  (testing "matching by path&name on placeholder from fn2api-web.mounts.routes"
    (mount.core/start)

    (is (= (:name (:data (match-by-path ((:dev router)) "/"))) :placeholder))
    (is (= (:path (match-by-name ((:dev router)) :placeholder)) "/"))

    ;; this only works for :prod since :dev yields always a new instance
    (is (= (match-by-path ((:prod router)) "/")
           (match-by-name ((:prod router)) :placeholder)))
    (is (not= (match-by-path ((:dev router)) "/")
              (match-by-name ((:dev router)) :placeholder))))

  (testing "handler"
    (is (= ((get-in (match-by-name ((:dev router)) :placeholder) [:data :get :handler]))
           ((ring-handler ((:dev router))) {:request-method :get :uri "/"})
           ((router->app (:dev router)) {:request-method :get :uri "/"})
           {:status 404 :headers {"Content-Type" "text/html"} :body "Not found - Please add some routes"})))

  (testing "router after routes have been replaced"
    (mount.core/stop)
    (mount.core/start-with-args {:fn2api-web {:routes (fn [] [["/" {:name :index
                                                                    :get {:handler (fn [& args]
                                                                                       {:status 200
                                                                                        :body "Hello World"})}} ]])}})
    (is (= ((get-in (match-by-path ((:prod router)) "/") [:data :get :handler]))
           ((get-in (match-by-path ((:dev router)) "/") [:data :get :handler]))
           {:status 200 :body "Hello World"}))))
