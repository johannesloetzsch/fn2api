(ns fn2api-lib.mounts.config
  (:require [mount.core :refer [defstate args]]
            [config.core :refer [load-env]]
            [taoensso.encore :refer [nested-merge]]))

(defstate
  ^{:doc "Provides config from yogthos/config merged with args supplied by mount.core/start-with-arg"}
  config
  :start (nested-merge (load-env) (args)))


(defn config-merge
  "you may want use `overwrite-args` from `environ.core/env`"
  [overwrite-args:map &[{:keys [only-start-config?]}]]
  (let [merged (merge (if (map? config) config)
                      overwrite-args:map)]
       (mount.core/stop)
       (mount.core/start-with-args merged (if only-start-config? #'config))))
