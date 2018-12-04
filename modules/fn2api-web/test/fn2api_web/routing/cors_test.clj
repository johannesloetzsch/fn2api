(ns fn2api-web.routing.cors-test
  (:require [clojure.test :refer :all]
            [fn2api-web.mounts.router :refer [router]]
            [fn2api-web.app.minimal :refer [router->app]]
            [ring.mock.request :refer [request header]]))

(deftest CORS
  (is (= (-> (request :get "/")
             (header "Origin" "example.com")
             ((router->app (:dev router)))
             (get-in [:headers "Access-Control-Allow-Origin"]))
         "*")))
 
