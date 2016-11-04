# tfidf-cascalog

Implements a portion of the TF-IDF algorithm. Takes Avro based file as input, calculates TF, DF and D portions in a batch mode and places the results into a cassandra table. This will later be read by a storm-trident DRPC query and combined with the realtime data to form a complete view of the world. 

## Usage

Ensure that both hadoop and cassandra are started, then:

	lein deps
	lein compile
	lein uberjar
	
	copydata.sh
	hadoop jar ./target/tfidf-cascalog-0.1.0-SNAPSHOT-standalone.jar data/document.avro data/en.stop 127.0.0.1
	
Obviously replacing the IP address with the appropriate cassandra IP address.

