(defproject fn2api-parent "0.2.0"
  :description "Simple generation of cli+web APIs+UIs from specced functions (+metadata)"
  :url "https://github.com/johannesloetzsch/fn2api/"
  :scm {:name "git" :url "https://github.com/johannesloetzsch/fn2api"}
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-sub "0.3.0"]
            [lein-shell "0.5.0"]]
  :dependencies [[org.clojure/clojure "1.10.0"]]
  :managed-dependencies [[fn2api-core "0.2.0"]
                         [fn2api-lib "0.2.0"]
                         [fn2api-format "0.2.0"]

                         [yogthos/config "1.1.1"]
                         [mount "0.1.15"]
                         #_[org.clojure/tools.namespace "0.2.11"]
                         [com.taoensso/encore "2.105.0"]
                         [com.taoensso/timbre "4.10.0"]

                         [metosin/muuntaja "0.6.3"]
                         [metosin/muuntaja-yaml "0.6.3"]
                         [metosin/spec-tools "0.8.3"]

                         [org.clojure/tools.cli "0.4.1"]

                         [metosin/reitit "0.2.12"]
                         [ring/ring-mock "0.3.2"]
                         [ring/ring-jetty-adapter "1.7.1"]
                         [aleph "0.4.7-alpha4"]
                         [com.unbounce/encors "2.4.0"]

                         [criterium "0.4.4"]

                         ;;[compojure "1.6.1"]
                         ;;[metosin/ring-http-response "0.9.1"]
                         ;;[metosin/compojure-api "2.0.0-alpha12"]
                         #_[org.clojure/test.check "0.9.0"]]
  ;:profiles {:dev {:source-paths ["modules/fn2api-format/src"]}}
  :sub ["modules/fn2api-core"
        "modules/fn2api-lib"
        "modules/fn2api-format"
        "modules/fn2api-cli"
        "modules/fn2api-web"
        ;; The examples don't use `lein-parent`, but can still be tested via `lein-sub`
        "examples/minimal-cli"
        #_"examples/minimal-webserver"]
  :aliases {"install" ["sub" "install"]  ;; Use this to install sub-repos locally as dependency for other sub-repos
            "test" ["sub" "test"]
            "deploy" ["sub" "deploy" "clojars"]  ;; you want have a ~/.lein/credentials.clj.gpg
            "uberjar" ["do" ["sub" ["do" ["clean"]
                                         ["uberjar"]]]
                            ["shell" "find" "modules" "examples" "-name" "*.jar" "-not" "-name" "*standalone.jar"
                                                                 "-exec" "du" "-h" "{}" "\\;"]
                            ["shell" "find" "modules" "examples" "-name" "*standalone.jar"
                                                                 "-exec" "du" "-h" "{}" "\\;"]]})
