(ns fn2api-web.mounts.app
  (:require [mount.core :refer [defstate]]
            [fn2api-lib.mounts.config :refer [config]]
            [fn2api-web.mounts.router :refer [router]]
            [fn2api-web.app.minimal]
            [fn2api-web.app.swagger]))

(defstate app
  :start (let [default-app (fn2api-web.app.swagger/router->app (:dev router) {:path "/swagger"})]
              (get-in config [:fn2api-web :app] default-app))
  :end nil)
