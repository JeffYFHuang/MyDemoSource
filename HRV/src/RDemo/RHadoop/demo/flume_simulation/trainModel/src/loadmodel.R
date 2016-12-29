Sys.setenv("HADOOP_CMD"="/media/data/hadoop_ecosystem/hadoop/bin/hadoop")
Sys.setenv("HADOOP_STREAMING"="/media/data/hadoop_ecosystem/hadoop/share/hadoop/tools/lib/hadoop-streaming-2.6.0.jar")

library(rmr2)
#library(rhdfs)
library(randomForest)

raw.forests <- values(from.dfs("/data/output", format=make.input.format("csv", sep = "\t")))
print(raw.forests)
forest <- do.call(combine, raw.forests)
summay(forest)
