(ns fn2api-web-server.mounts.aleph
  (:require [mount.core :refer [defstate]]
            [fn2api-lib.mounts.config :refer [config]]
            [fn2api-web.mounts.app :refer [app]]
            [aleph.http :refer [start-server]]))

(defstate aleph
  :start {:instance (start-server #'app {:port (get-in config [:fn2api-web-server :aleph :port] 3001)})}
  :stop (do (.close (:instance aleph))
            (.wait-for-close (:instance aleph))  ;; There is a problem that seems to be related with
                                                 ;; https://github.com/ztellman/aleph/issues/365
                                                 ;; Restarting might not work as expected
            {:instance nil}))
