#!/bin/sh 

cwd=$(pwd)
KAFKA_HOME=/media/Data/hadoop_ecosystem/kafka

#get a local build going
mvn clean package

#ensure we don't have any zookeeper, kafka or topology instances running
pkill -9 -f storm.cookbook.OrderManagementTopology
pkill -9 -f kafka
#pkill -9 -f zookeeper
sleep 5

#remove all previous logs
#rm -rfv /tmp/zookeeper
rm -rfv /media/Data/tmp/kafka

#launch zookeeper and kafka
cd $KAFKA_HOME
#bin/zookeeper-server-start.sh config/zookeeper.properties &
#sleep 15
bin/kafka-server-start.sh config/server.properties &
sleep 5

cd $cwd
mvn exec:java -Dexec.classpathScope=compile -Dexec.mainClass=storm.cookbook.OrderManagementTopology &
#allow some time for the topology to start
sleep 20
