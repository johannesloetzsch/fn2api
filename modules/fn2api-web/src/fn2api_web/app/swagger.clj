(ns fn2api-web.app.swagger
  (:require [reitit.ring :as rr]
            [reitit.swagger-ui :refer [create-swagger-ui-handler]]))

(defn router->app [router:fn &[{:keys [path] :or {path "/"}}]]
  (rr/ring-handler (router:fn)
                   (rr/routes
                     (create-swagger-ui-handler
                       {:path path
                        :config {:validatorUrl nil}})
                     (rr/create-resource-handler {:path "/"})  ;; serve /resource/public
                     (rr/create-default-handler
                       {:not-found (constantly {:status 404, :body "404 - no routes matches"})
                        :method-not-allowed (constantly {:status 405, :body "405 - no method matches"})
                        :not-acceptable (constantly {:status 406, :body "406 - handler returned `nil`"})}))))
