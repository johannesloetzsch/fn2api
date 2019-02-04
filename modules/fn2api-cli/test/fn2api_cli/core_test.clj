(ns fn2api-cli.core-test
  (:require [clojure.test :refer :all]
            [fn2api-cli.core :refer [spec->cli-options fn->cli]]
            [clojure.spec.alpha :as s]
            [spec-tools.core :as st]
            [clojure.tools.cli :refer [parse-opts]]))

(s/def ::port (st/spec {:spec int? #_(s/and int? #(< 0 % 0x10000))
                        :id 'port
                        :description "Port Number"
                        :reason "Must be a number between 0 and 65536"
                        :fn2api/short-opt "p"
                        :fn2api/default 80}))

(deftest quickstart  ;; like https://github.com/clojure/tools.cli#quick-start
  (let [cli-options (spec->cli-options [::port :fn2api-cli.core/verbosity :fn2api-cli.core/help])]
       (testing "summary"
         (is (= (:summary (parse-opts [] cli-options))
                "  -p, --port PORT  80  Port Number\n  -v, --verbose        Increase verbosity (can be applied multiple times)\n  -h, --help           Show help")))
       (testing "parse-opts"
         (is (= (dissoc (parse-opts [] cli-options) :summary)
                {:options {"port" 80, "verbosity" 0, "help" false}, :arguments [], :errors nil}))
         (is (= (dissoc (parse-opts ["-p22" "--verbose" "-vv" "--help"] cli-options) :summary)
                {:options {"port" 22, "verbosity" 3, "help" true}, :arguments [], :errors nil})))))

;(s/fdef port-example
;  :args (s/cat :p ::port))
;
;(defn port-example [port]
;  (println "Start imaginary server at port" port))
;
;(require '[fn2api-core.signatures :refer [fn->type fn->signature-map]])
;(fn->signature-map (var port-example))
;(fn->type (var port-example))
;
;(require '[fn2api-core.signatures-spec.parse-by-transform :refer [fn->specs]])
;(fn->specs (var port-example))
;
;;(require '[fn2api-core.signatures-spec :refer [fn->specs_fallback]])
;;(fn->specs_fallback (var port-example))
