(ns fn2api-web.core
  (:require [mount.core :refer [start-with-args]]
            [fn2api-web.routes.swagger :refer [swagger-example-routes]]
            [fn2api-web-server.mounts.aleph]
            [fn2api-web-server.mounts.jetty])
  (:gen-class))

(defn -main []
  (println (start-with-args {:fn2api-web {:routes swagger-example-routes}})))
