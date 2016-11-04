(defproject tfidf-cascalog "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :source-paths ["src/clj"]
  :test-paths   ["test/clj"]
  :resource-paths ["src/resources"]
  :main tfidf-cascalog.core
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
  				[cascalog "1.10.1"]
  				[org.apache.cassandra/cassandra-all "1.1.5" 
           :exclusions [org.apache.cassandra.deps/avro]]
  				[clojurewerkz/cassaforte "1.0.0-beta11-SNAPSHOT"]
          [quintona/cascading-cassandra "0.0.7-SNAPSHOT"]
          [clj-time "0.5.0"]
          [cascading.avro/avro-scheme "2.2-SNAPSHOT"]
          [cascalog-more-taps "0.3.0"]
                [org.apache.httpcomponents/httpclient "4.2.3"]]
  :profiles { :dev {:dependencies [[org.apache.hadoop/hadoop-core "0.20.2-dev"]
  					[lein-midje "3.0.1"]
  					[cascalog/midje-cascalog "1.10.1"]]}}	)
