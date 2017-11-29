(ns fn2api.web.server
  (:require [taoensso.timbre :as t]
            [aleph.http :as http]))

(defonce server (atom {:process nil
                       :config {:host "localhost"     
                                :port 3000
                                :->app nil}}))

(defn server-start! []
  (if (:process @server)
    (t/info "Server already running; please use (server-restart)")
    (swap! server
           assoc :process
           (http/start-server ((get-in @server [:config :->app]))
                              {:host (get-in @server [:config :host])
                               :port (get-in @server [:config :port])})))
    (t/info "Server started at"
            (get-in @server [:config :host]) ":"
            (get-in @server [:config :port])))

(defn server-restart! []
  (.close (:process @server))
  (swap! server assoc :process nil)
  (server-start!))
