#!/bin/bash

ssh data1 "nohup $KAFKA_HOME/bin/kafka-server-start.sh $KAFKA_HOME/config/server.properties > ./kafka.log 2>&1 &"
ssh data2 "nohup $KAFKA_HOME/bin/kafka-server-start.sh $KAFKA_HOME/config/server.properties > ./kafka.log 2>&1 &"
