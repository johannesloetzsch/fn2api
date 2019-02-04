(ns fn2api-web.app.swagger-test
  (:require [clojure.test :refer :all]
            [mount.core]
            [fn2api-web.mounts.router :refer [router]]
            [fn2api-web.app.swagger]))

(deftest swagger
  (mount.core/start)
  ;; with n2api-web.app.swagger/router->app we can add swagger support to any router
  (is (= ((fn2api-web.app.swagger/router->app (:prod router) {:path "/swagger"})
          {:request-method :get :uri "/swagger"})
         {:status 302, :headers {"Location" "/swagger/index.html"}, :body ""})))
