(ns fn2api.core
  (:require [fn2api.web.example :refer [example-server-start!]])
  (:gen-class))

(defn -main
  [& args]
  (example-server-start!))
