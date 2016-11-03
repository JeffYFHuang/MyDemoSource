java -cp KafkaOffsetMonitor-assembly-0.2.1.jar \
     com.quantifind.kafka.offsetapp.OffsetGetterWeb \
     --zk master \
     --port 8080 \
     --refresh 10.seconds \
     --retain 2.days
