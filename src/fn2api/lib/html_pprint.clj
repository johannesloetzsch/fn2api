(ns fn2api.lib.html-pprint
  (:require [clojure.pprint :refer [pprint]]
            [clojure.string :as s]))

(defn pprint-html [x]
  (-> x
      pprint
      with-out-str
      (s/replace #"\n" "<br/>")
      (s/replace #" " "&nbsp;")))
