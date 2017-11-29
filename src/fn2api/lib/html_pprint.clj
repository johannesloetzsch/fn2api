(ns fn2api.lib.html-pprint
  (:require [clojure.pprint :refer [pprint]]
            [clojure.string :refer [replace]]))

(defn pprint-html [x]
  (-> x
      pprint
      with-out-str
      (replace #"\n" "<br/>")
      (replace #" " "&nbsp;")))
