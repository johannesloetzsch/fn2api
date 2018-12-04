(ns minimal-webserver-test
  (:require [clojure.test :refer :all]
            ;[mount.core :refer [start-with-args]]
            ;[fn2api.components.app :refer [app]]
            ;[fn2api.lib.mount.info :refer [info]]
            ;[fn2api.example :refer [example-routes]]
            [ring.mock.request :refer [request json-body]]))

;(start-with-args {:routes #'example-routes})
;(info)
;
;(defn app-result [ring-request]
;  (-> ring-request ((:app app)) :body slurp))
;
;
;(deftest example-server
;
;  (testing "GET"
;    (is (= (app-result (request :get "/plus?x=20&y=3"))
;           (app-result {:request-method :get :uri "/plus" :query-string "x=20&y=3"})
;           (app-result {:request-method :get :uri "/plus" :query-params {:x 20 :y 3}})
;           "{\"total\":23}")))
;
;  (testing "POST"
;    (is (= (app-result (-> (request :post "/plus") (json-body {:x 40 :y 2})))
;           (app-result {:request-method :post :uri "/plus" :body-params {:x 40 :y 2}})
;           "{\"total\":42}")))
;
;  (testing "Download"
;    (is (= (-> {:request-method :get :uri "/files/download"}
;               ((:app app)) :body (#(slurp % :encoding "ascii")) count)  ;; binary
;           (.length (clojure.java.io/file "resources/reitit.png"))
;           506325)))
;
;  (testing "Upload"
;    (let [file (clojure.java.io/file "resources/reitit.png")
;          multipart-temp-file-part {:tempfile file
;                                    :size (.length file)
;                                    :filename (.getName file)
;                                    :content-type "image/png;"}]
;         (is (= (app-result {:request-method :post :uri "/files/upload" :multipart-params {:file multipart-temp-file-part}})
;                "{\"name\":\"reitit.png\",\"size\":506325}")))))
