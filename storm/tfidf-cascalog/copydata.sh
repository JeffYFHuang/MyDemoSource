#!/bin/sh
hadoop fs -rm data/document.avro
hadoop fs -copyFromLocal ./data/document.avro data/document.avro
