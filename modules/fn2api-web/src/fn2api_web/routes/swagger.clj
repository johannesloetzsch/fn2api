(ns fn2api-web.routes.swagger
  (:require [reitit.swagger :refer [create-swagger-handler]]
            [fn2api-web.routes.todo :refer [file-example-routes]]))

(defn swagger-json
  [&[{:keys [path title] :or {path "/swagger.json" title ""}}]]
  [path
   {:get {:no-doc true
          :swagger {:info {:title title}}
          :handler (create-swagger-handler)}}])

(defn swagger-example-routes
  "can be used with `fn2api-web.app.swagger/router->app`"
  []
  [(swagger-json)
   (file-example-routes)
   ["/" {:name :index
         :swagger {:produces ["text/html"]}
         :get {:handler (fn [& args]
                            {:status 200
                             :headers {"Content-Type" "text/html"}
                             :body "<a href=\"./swagger\">Swagger API</a>"})}} ]])
