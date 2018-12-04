(ns fn2api-web.routes.placeholder)

(defn placeholder-routes []
  [["/" {:name :placeholder
         :swagger {:produces ["text/html"]}
         :get {:handler (fn [& args]
                            {:status 404
                             :headers {"Content-Type" "text/html"}
                             :body "Not found - Please add some routes"})}} ]])
