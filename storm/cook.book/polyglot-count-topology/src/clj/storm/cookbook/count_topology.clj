(ns storm.cookbook.count-topology
  (:import (backtype.storm StormSubmitter LocalCluster)
           (storm.cookbook QtSplitSentence RubyCount))
  (:use [backtype.storm clojure config])
   )

(defspout sentence-spout ["sentence"]
  [conf context collector]
  (let [sentences ["a little brown dog"
                   "the man petted the dog"
                   "four score and seven years ago"
                   "an apple a day keeps the doctor away"]]
    (spout
     (nextTuple []
       (Thread/sleep 100)
       (emit-spout! collector [(rand-nth sentences)])         
       )
     (ack [id]
        ))))

(defn mk-topology []

  (topology
   {"1" (spout-spec sentence-spout)}
   {"3" (bolt-spec {"1" :shuffle}
                   (QtSplitSentence.)
                   :p 1)
    "4" (bolt-spec {"3" ["word"]}
                   (RubyCount.)
                   :p 1)}))

(defn run-local! []
  (let [cluster (LocalCluster.)]
    (.submitTopology cluster "word-count" {TOPOLOGY-DEBUG true} (mk-topology))
    (Thread/sleep 10000)
    (.shutdown cluster)
    ))

(defn submit-topology! [name]
  (StormSubmitter/submitTopology
   name
   {TOPOLOGY-DEBUG true
    TOPOLOGY-WORKERS 3}
   (mk-topology)))

(defn -main
  ([]
   (run-local!))
  ([name]
   (submit-topology! name)))

