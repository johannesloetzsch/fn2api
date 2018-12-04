(ns fn2api-web.mounts.router
  (:require [mount.core :refer [defstate]]
            [fn2api-web.mounts.routes :refer [routes]]
            [reitit.ring :as rr]
            [fn2api-web.routing.defaults :refer [default-opts]]))

(defstate router
  :start ;; See https://metosin.github.io/reitit/advanced/dev_workflow.html
         ;; This allows a dev-workflow without the need of restarting the state
         ;; and a fast compiled `:prod` router that still can be reloaded via mount
         {:dev #(rr/router (routes) default-opts)
          :prod (constantly (rr/router (routes) default-opts))}
  :stop {})
