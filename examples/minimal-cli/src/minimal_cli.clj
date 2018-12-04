(ns minimal-cli
  (:require [fn2api-cli.core :refer [fn->cli]]
            [clojure.spec.alpha :as s]
            [spec-tools.core :as st])
  (:gen-class))

(s/def ::port (st/spec {:spec (s/and int? #(< 0 % 0x10000))
                        :description "Port Number"
                        :reason "Must be a number between 0 and 65536"
                        :fn2api/short-opt "p"
                        :fn2api/default 80}))

(s/fdef port-example
  ;:args (s/cat :kwargs (s/keys :req-un [::port])))
  :args (s/cat :p ::port :variadic #_any? sequential?))  ;; TODO any? is not working

;(defn port-example [{:keys [port]}]
(defn port-example [port & ignored]
  (println "Start imaginary server at port" port))

(defn -main [& args]
  (fn->cli (var port-example) args))
