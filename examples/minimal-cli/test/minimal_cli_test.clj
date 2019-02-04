(ns minimal-cli-test
  (:require [clojure.test :refer :all]
            [minimal-cli :refer [-main]]
            [clojure.string]))

(defn singlespace [s]
  (clojure.string/replace s #" +" " "))


(deftest working-correctly

  (testing "default defined by `:fn2api/default`"
    (is (= (with-out-str (-main))
           "Start imaginary server at port 80\n")))

  (testing "short-opt defined by `:fn2api/long-opt`"
    (is (= (with-out-str (-main "-p22"))
           "Start imaginary server at port 22\n")))

  (testing "long-opt (`:fn2api/long-opt`) infered from spec-name"
    (is (= (with-out-str (-main "--port" "8080"))
           "Start imaginary server at port 8080\n"))))

(deftest error-handling

  (let [usage (singlespace (str "Usage:\n"
                                "  -h, --help           Show help\n"
                                "  -v, --verbose        Increase verbosity (can be applied multiple times)\n"
                                "  -p, --port PORT  80  Port Number\n"))]

       (testing "missing argument"
         (is (= (str "Missing required argument for \"-p PORT\"\n" usage)
                (singlespace (with-out-str (-main "-p"))))))

       (testing "coercion/validation fail"
         (is (= (str "Failed to validate \"-p not-a-number\": Must be a number between 0 and 65536\n" usage)
                (singlespace (with-out-str (-main "-p" "not-a-number"))))))))
