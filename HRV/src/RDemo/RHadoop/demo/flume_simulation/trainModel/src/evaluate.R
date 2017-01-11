#! /usr/bin/env Rscript

# reducer.R - Wordcount program in R
# script for Reducer (R-Hadoop integration)
#library(rhbase)

Sys.setenv("HADOOP_CMD"="/media/data/hadoop_ecosystem/hadoop/bin/hadoop")
Sys.setenv("HADOOP_STREAMING"="/media/data/hadoop_ecosystem/hadoop/share/hadoop/tools/lib/hadoop-streaming-2.6.0.jar")
require(compiler)
require(rmr2)
require(rjson)
require(caret)
require(randomForest)
a<-enableJIT(3)

  #confusionMatrix(Predict, testing$label)
#data <- from.dfs("/data/output", format="text")
mod.fit<-from.dfs("/data/train_output")
#pred <- function(mod, newdata) {predict(mod$model, newdata)}
#lapply(mod.fit$val, pred, newdata=testing)

predict_labels <- NULL
orignal_labels <- NULL

for (x in mod.fit$val) {
    # get data
    predict_labels <- c(predict_labels, x$p)
    orignal_labels <- c(orignal_labels, x$t)
}

confusionMatrix(predict_labels, orignal_labels)
