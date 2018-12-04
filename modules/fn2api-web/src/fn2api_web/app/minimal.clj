(ns fn2api-web.app.minimal
  (:require [reitit.ring :as rr :refer [ring-handler]]))

(defn router->app [router:fn]
  (ring-handler (router:fn)))
