(ns fn2api-web-server.mounts.jetty
  (:require [mount.core :refer [defstate]]
            [fn2api-lib.mounts.config :refer [config]]
            [fn2api-web.mounts.app :refer [app]]
            [ring.adapter.jetty :refer [run-jetty]]))

(defstate jetty
  :start {:instance (run-jetty #'app {:port (get-in config [:fn2api-web-server :jetty :port] 3002) :join? false})}
  :stop (do (.stop (:instance jetty))
            {:instance nil}))
