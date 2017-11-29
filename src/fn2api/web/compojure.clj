(ns fn2api.web.compojure
  (:require [compojure.api.sweet :refer [api context resource]]
            [compojure.core :refer [routes]]
            [compojure.route :refer [not-found]]
            [ring.util.http-response :refer [ok]]
            [clojure.spec.alpha :as s]
            #_[spec-tools.core :as st]
            #_[spec-tools.spec :as sp]
            [clojure.repl :refer [doc]]
            [fn2api.lib.html-pprint :refer [pprint-html]]))

(defn wrap-f [f]
  (fn [{:as params}]
      (let [merged-params (merge (:query-params params)
                                 (:body-params params)
                                 {:params params})
            result-map (f merged-params)]
           (case (:return result-map)
                 :ok (ok (select-keys result-map [:result]))))))

(defn ->resource-data [var-f]
  (let [f (var-get var-f)]
       (apply merge
         {:coercion :spec}
         (for [method [:get :post]]
              {method
               (merge
                 {:summary (str "\\" (ns-resolve (:ns (meta var-f)) (:name (meta var-f))))
                  :description (str (pprint-html (as-> (meta var-f) x
                                                       (apply dissoc x [:ns :name])))
                                    ;;; maybe add s/describe + recursively resolved with s/get-spec or s/registry
                                    "Example calls:<br/>"
                                    (pprint-html (s/exercise-fn var-f 3)))
                  ;:responses {200 {:schema ::total-map}}
                  :handler (wrap-f f)}
                 (get-in (meta var-f) [:methods method]))}))))

(defn ->resource [f]
  (resource (->resource-data f)))

(defn ->context [f path]
  (context path [] (->resource f)))

(defn ->app 
  [{:keys [data contexts not-found-body]}]
  (routes

    (api
      {:swagger
        {:ui "/api"
         :spec "/api/swagger.json"
         :data data}}

      (context "/api" []
        :tags ["api"]
        :coercion :spec

        contexts))

    (not-found (or not-found-body
                   (str "<div align=\"center\">"
                        "No such page, but you can use the "
                        "<a href=\"/api\">API Documentation</a>"
                        "</div>")))))
