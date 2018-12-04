(ns fn2api-lib.mounts.logging
  (:require [mount.core :refer [defstate]]
            [fn2api-lib.mounts.config :refer [config]]
            [taoensso.encore :refer [nested-merge]]
            [taoensso.timbre :as t]
            [taoensso.timbre.appenders.core]))

(defn evaluate-config
  "`timbre/merge-config!` is not able to merge the configuration of appenders.
   This is why we `nested-merge` the config first and than use this function to get appender instances.
   The function to create the appender is taken from the self added `(:implementation appender-conf)`"
  [conf]
  (let [appenders (->> (map (fn [[name:kw appender-conf]]
                                {name:kw (case (:implementation appender-conf)
                                               'spit-appender
                                                 (taoensso.timbre.appenders.core/spit-appender appender-conf)
                                               appender-conf)})
                            (:appenders conf))
                       (apply merge))]
       (assoc conf :appenders appenders)))

(defstate logging
  :start (let [defaults {:appenders {:println {:enabled? true}
                                     :spit {:implementation 'spit-appender
                                            :enabled? false
                                            :fname "out/fn2api.log"}}}
               config||defaults (nested-merge defaults (get-in config [:fn2api-lib :logging]))
               config:evaluated (evaluate-config config||defaults)]

              (t/set-level! :trace) ;; we staticly set it to lowest available level
                                    ;; this can still be overwritten like:
                                    ;; * TIMBRE_LEVEL=:debug
                                    ;; * (timbre/with-level :debug)

              (t/merge-config! config:evaluated)
              ;; Since it's not possible to take value of a macro, we wrap them into functions.
              ;; Probably in future we also want support variadic arity
              {:trace #(t/trace %)
               :debug #(t/debug %)
               :info #(t/info %)
               :warn #(t/warn %) 
               :error #(t/error %)
               :fatal #(t/fatal %)
               :report #(t/report %)}))
