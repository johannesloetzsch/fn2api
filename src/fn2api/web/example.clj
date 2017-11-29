(ns fn2api.web.example
  (:require [fn2api.web.server :refer [server server-start!]]
            [fn2api.web.compojure :refer [->app ->context]]
            [clojure.spec.alpha :as s]
            #_[spec-tools.core :as st]
            [spec-tools.spec :as sp]))


(s/def ::x sp/int?)
(s/def ::y (s/and sp/int? #(not (zero? %))))
(s/def ::x+y (s/keys :req-un [::x ::y]))

(s/fdef example-function
  :args (s/cat :params ::x+y))

(defn
  ^{:methods {:get {:parameters {:query-params ::x+y}}
              :post {:parameters {:body-params ::x+y}}}}
  example-function
  "pi*x/y"
  [{:keys [x y]}]
  {:return :ok
   :result (let [pi 3.1416]
                (int (/ (* pi x) y)))})


(defn example-app []
  (->app {:data {:info {:title "Example api"}
                 :tags [{:name "api" :description "Example tag 'api'"}]}
          :contexts [(->context (var example-function) "/test")]}))

(defn example-server-start! []
  (swap! server assoc-in [:config :->app] example-app)
  (server-start!))
