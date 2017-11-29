(ns fn2api.web.example-test
  (:require [clojure.test :refer :all]
            [fn2api.web.example :refer :all]
            [fn2api.web.compojure :refer [->context ->resource]]))

(deftest fn2api-web-example
  (let [expected-result {:status 200 :headers {} :body {:result 42}}]
       (testing "useage of fn2api-web"
         (is (= ((->resource (var example-function))
                 {:request-method :post :body-params {:x 1337 :y 100}})
                expected-result))
         (is (= ((->context (var example-function) "/test")
                 {:request-method :post :uri "/test" :body-params {:x 1337 :y 100}})
                expected-result))
         (is (= (-> {:request-method :post :uri "/api/test" :body-params {:x 1337 :y 100}}
                    ((example-app))
                    :body slurp)
                "{\"result\":42}")))))
