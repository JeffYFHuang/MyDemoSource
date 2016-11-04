(defproject polyglot-count-topology "0.0.1-SNAPSHOT"
  :source-paths ["src/clj"]
  :java-source-paths ["src/jvm" "test/jvm"]
  :test-paths ["test/clj"]
  :javac-options     ["-target" "1.6" "-source" "1.6"]
  :resource-paths ["multilang"]
  :main storm.cookbook.count-topology
  :aot :all
  :min-lein-version "2.0.0"
  :dependencies [[org.slf4j/slf4j-log4j12 "1.6.1"]
                 [org.clojure/clojure "1.4.0"]
                   [commons-collections/commons-collections "3.2.1"]
                   [storm-starter "0.0.1-SNAPSHOT"]]

  :profiles {:dev {:dependencies [[storm "0.8.2"]
                     [junit/junit "4.11"]
                     [org.testng/testng "6.1.1"]]}}
  
  )

