(ns tfidf-cascalog.core
  (:require [clojurewerkz.cassaforte.client :as cc]
            [clojurewerkz.cassaforte.schema :as sch]
            [clojurewerkz.cassaforte.conversion :as cconv]
            [clojurewerkz.cassaforte.cql    :as cql]
            [clojurewerkz.cassaforte.bytes  :as bytes]

            [clojurewerkz.cassaforte.thrift.core :as thrift]
            [clojurewerkz.cassaforte.thrift.column-definition :as cd]
            [clojurewerkz.cassaforte.thrift.column-family-definition :as cfd]
            )
  (:use cascalog.api
        clojure.test
        [midje sweet cascalog]
        [cascalog.more-taps :only (hfs-delimited)]
        [clj-time.core :only (today in-minutes interval)]
            [clj-time.coerce :only (from-long to-long)]
            [clj-time.local :only (local-now)])
  (:require [cascalog.io :as io]
            [clojure.string :as s]
            [cascalog.ops :as c]
            [cascalog.tap :as tap]
            )
  (:import [cascading.tuple Fields]
           [cascading.scheme Scheme]
           [cascading.avro AvroScheme]
           [org.apache.avro Schema]
           [com.ifesdjeen.cascading.cassandra CassandraTap CassandraScheme]
           [org.apache.cassandra.utils ByteBufferUtil]
           [org.apache.cassandra.thrift Column]))

(def storm_keyspace "storm")

(deffilterop timing-correct? [doc-time] 
  (let [now (local-now)
        interval (in-minutes (interval (from-long doc-time) now))]
    (if (< interval 60) false true)))

(defmapcatop split [line]
  "reads in a line of string and splits it by regex"
  (s/split line #"[\[\]\\\(\),.)\s]+"))

(defn etl-docs-gen [rain stop]
  (<- [?doc-id ?time ?word]
      (rain ?doc-id ?time ?line)
      (split ?line :> ?word-dirty)
      ((c/comp s/trim s/lower-case) ?word-dirty :> ?word)
      (stop ?word :> false)
      (timing-correct? ?time)))

(defn D [src]
  (let [src  (select-fields src ["?doc-id"])]
    (<- [?key ?d-str]
        (src ?doc-id)
        (c/distinct-count ?doc-id :> ?n-docs)
        (str "d" :> ?key)
        (str ?n-docs :> ?d-str))))

(defn DF [src]
  (<- [?key ?df-count-str]
      (src ?doc-id ?time ?df-word)
      (c/distinct-count ?doc-id ?df-word :> ?df-count)
      (str ?df-word :> ?key)
      (str ?df-count :> ?df-count-str))
  )

(defn TF [src]
  (<- [?key ?tf-count-str]
      (src ?doc-id ?time ?tf-word)
      (c/count ?tf-count)
      (str ?doc-id ?tf-word :> ?key)
      (str ?tf-count :> ?tf-count-str)))

(defn create-tap [rowkey cassandra-ip]
  (let [keyspace storm_keyspace
            column-family "tfidfbatch"
            scheme        (CassandraScheme. cassandra-ip
                                            "9160"
                                            keyspace
                                            column-family
                                            rowkey
                                            {"cassandra.inputPartitioner" "org.apache.cassandra.dht.RandomPartitioner"
                                             "cassandra.outputPartitioner" "org.apache.cassandra.dht.RandomPartitioner"})
            tap           (CassandraTap. scheme)]
        tap))

(defn create-d-tap [cassandra-ip]
  (create-tap "d"cassandra-ip))

(defn create-df-tap [cassandra-ip]
  (create-tap "df" cassandra-ip))

(defn create-tf-tap [cassandra-ip]
  (create-tap "tf" cassandra-ip))

(defn load-schema []
  (Schema/parse (.getResourceAsStream (clojure.lang.RT/baseLoader) "document.avsc")))

(defn execute [in stop cassandra-ip]
  (cc/connect! cassandra-ip)
  (sch/set-keyspace storm_keyspace)
  (let [input (tap/hfs-tap (AvroScheme. (load-schema)) in)
        stop (hfs-delimited stop :skip-header? true)
        src  (etl-docs-gen input stop)]
    (?- (create-d-tap cassandra-ip)
        (D src))
    (?- (create-df-tap cassandra-ip)
        (DF src))
    (?- (create-tf-tap cassandra-ip)
        (TF src))))

(defn -main [in stop cassandra-ip & args]
  (execute in stop cassandra-ip))

