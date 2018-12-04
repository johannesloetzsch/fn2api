(ns fn2api-web.mounts.routes
  (:require [mount.core :refer [defstate start-with-states]]
            [fn2api-lib.mounts.config :refer [config]]
            [fn2api-web.routes.placeholder :refer [placeholder-routes]]))

(defstate routes
  :start (get-in config [:fn2api-web :routes] #'placeholder-routes)
  :stop placeholder-routes)
