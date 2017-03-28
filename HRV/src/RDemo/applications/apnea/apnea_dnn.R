#! /usr/bin/env Rscript

# mapper.R - Wordcount program in R
# script for Mapper (R-Hadoop integration)
source("HRVFUNS.R")
require(caret)
require(rmr2)
require(AMORE)

## **** could wo with a single readLines or in blocks
con <- file("part-00000", open = "r")
line <- readLines(con, n = 1, warn = FALSE)
data = unlist(fromJSON(line))
features = names(data)
data = t(matrix(as.numeric(unlist(fromJSON(line)))))
while (length(line <- readLines(con, n = 1, warn = FALSE)) > 0) {
   data = rbind(data, t(matrix(as.numeric(unlist(fromJSON(line))))))
}
close(con)

    data <- data.frame(data)
    colnames(data) <- features
    drops <- c("startTime","endTime", "SDANN", "SDNNIDX", "ApEn")
    data <- data[, !(names(data) %in% drops)]
#    print(length(data$label==0))
    data$label[data$label==0] <- -1 #factor(data$label)
    inTrain <- createDataPartition(y = data$label, p = .75, list = FALSE)
    features <- c("label", "HF", "LF", "VLF", "IRRR", "LFHF", "NN50", "SDSD", "pNN50", "meanHR", "meanRR")
#    features<-from.dfs("/data/feature_output")
#    features<-c("label", features$key[which(features$val>=0.9)])
    #features <- names(data)

    #features <- c("SDNN", "TINN", "LFHF", "LFnu", "SD2", "SD12", "TP", "ApEn", "type")
    training <- na.omit(data[inTrain, features])
    testing <- na.omit(data[-inTrain, features])

    set.seed(400)


