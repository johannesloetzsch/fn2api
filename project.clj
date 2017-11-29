(defproject fn2api "0.1.0-SNAPSHOT"
  :description "Simple generation of cli- and web-apis from functions + metadata"
  ;:url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.9.0-RC1"]
                 [com.taoensso/timbre "4.10.0"]
                 [aleph "0.4.4"]
                 [compojure "1.6.0"]
                 [metosin/ring-http-response "0.9.0"]
                 [metosin/compojure-api "2.0.0-alpha12"]
                 [metosin/spec-tools "0.5.1"]
                 [org.clojure/test.check "0.9.0"]]
  :main ^:skip-aot fn2api.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
