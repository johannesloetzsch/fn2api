(ns fn2api-web.routes.todo
  ;; This examples show features available by reitit but not yet supported by fn2api natively
  (:require [clojure.spec.alpha :as s]
            [spec-tools.spec :as sts]
            [reitit.ring.middleware.multipart :as multipart]
            [clojure.java.io :as io]))

(s/def ::file multipart/temp-file-part)
(s/def ::file-params (s/keys :req-un [::file]))

(s/def ::name sts/string?)
(s/def ::size sts/int?)
(s/def ::file-response (s/keys :req-un [::name ::size]))
 
(defn file-example-routes []
  ["/files"
   {:swagger {:tags ["files"]}}

   ["/upload"
    {:post {:summary "upload a file"
            :parameters {:multipart ::file-params}
            :responses {200 {:body ::file-response}}
            :handler (fn [{{{:keys [file]} :multipart} :parameters}]
                       {:status 200
                        :body {:name (:filename file)
                               :size (:size file)}})}}]

   ["/download"
    {:get {:summary "downloads a file"
           :swagger {:produces ["image/x-icon"]}
           :handler (fn [_]
                      {:status 200
                       :headers {"Content-Type" "image/x-icon"}
                       :body (io/input-stream
                               ;; relative to ./resources/
                               (io/resource "public/favicon.ico"))})}}]])
