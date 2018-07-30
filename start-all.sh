#!/bin/bash

#kafka service
nohup zookeeper-server-start.sh $KAFKA_HOME/config/zookeeper.properties > ./zookeeper.log 2>&1 &
nohup kafka-server-start.sh $KAFKA_HOME/config/server.properties > ./kafka.log 2>&1 &

kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic hrvs

#storm service
storm nimbus &
storm supervisor &
storm ui &

#flume service
flume-ng agent -c conf -f flume/hrv_simulation/flume-kafka-sink.conf -n producer -Dflume.root.logger=INFO,console &
