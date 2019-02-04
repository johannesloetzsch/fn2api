(ns fn2api-web.middleware.defaults
  (:require [reitit.ring.coercion :as coercion]
            [reitit.coercion.spec :refer [coercion]]
            [muuntaja.core :refer [instance]]
            [ring.middleware.params :as params]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.multipart :as multipart]
            [com.unbounce.encors :refer [wrap-cors]]
            #_[clojure.stacktrace :refer [print-stack-trace #_print-cause-trace]]))

(def default-exception-middleware
  (exception/create-exception-middleware
    (merge
      exception/default-handlers
      {::exception/wrap
        (fn [handler e request]
          (let [orig (handler e request)]
               {:status (:status orig 500)
                :body {:exception (merge
                        (Throwable->map e)
                        {:type (get-in orig [:body :type])
                         :data (ex-data e)
                         :original orig})}}))})))

(defn cors-middleware [app]
  ;; TODO may want use com.unbounce.encors.aleph/wrap-cors
  (wrap-cors app {:allowed-origins :star-origin
                  :allowed-methods #{:head :options :get :post}
                  :request-headers #{"Content-Type"}
                  :exposed-headers nil
                  :allow-credentials? true
                  :origin-varies? true ;; false
                  :max-age nil
                  :require-origin? false
                  :ignore-failures? true
                  }))

(def default-opts
  {:data {:coercion coercion
          :muuntaja instance
          :middleware [cors-middleware

                       ;; query-params & form-params
                       params/wrap-params
                       ;; content-negotiation
                       muuntaja/format-negotiate-middleware
                       ;; encoding response body
                       muuntaja/format-response-middleware
                       ;; exception handling
                       exception/exception-middleware
                       #_default-exception-middleware
                       ;; decoding request body
                       muuntaja/format-request-middleware
                       ;; coercing response bodys
                       coercion/coerce-response-middleware
                       ;; coercing request parameters
                       coercion/coerce-request-middleware
                       ;; multipart
                       multipart/multipart-middleware]}})
