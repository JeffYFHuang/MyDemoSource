#! /usr/bin/env Rscript

# mapper.R - Wordcount program in R
# script for Mapper (R-Hadoop integration)

source("HRVFUNS.R")
require(caret)
splitIntoWords <- function(line) unlist(strsplit(line, "\t"))

## **** could wo with a single readLines or in blocks
con <- file("apneahrv.dat", open = "r")
df.InEpisodes = NULL
df.OutEpisodes = NULL
while (length(line <- readLines(con, n = 1, warn = FALSE)) > 0) {
    words <- splitIntoWords(line)
    df = JsonToDataFrame(words[2])
    pos = charmatch(strsplit(words[1], "[.]")[[1]][2], c("beatsInEpisodes", "beatsOutEpisodes"))
    if (pos == 1) {
       df.InEpisodes <- rbind(df.InEpisodes, df)
    } else {
       df.OutEpisodes <- rbind(df.OutEpisodes, df)
    }
}
close(con)

    drops <- c("startTime","endTime")
    df.InEpisodes <- df.InEpisodes[, !(names(df.InEpisodes) %in% drops)]
    df.OutEpisodes <- df.OutEpisodes[, !(names(df.OutEpisodes) %in% drops)]
    df.InEpisodes["type"] = "Y"
    df.OutEpisodes["type"] = "N"
    data = rbind(df.InEpisodes, df.OutEpisodes)
    data$type=factor(data$type)
    inTrain <- createDataPartition(y = data$type, p = .75, list = FALSE)
#    features <- c("VLF", "LFHF", "meanRR", "meanHR", "HF", "type")
    features <- names(data)
    #features <- c("SDNN", "TINN", "LFHF", "LFnu", "SD2", "SD12", "TP", "ApEn", "type")

    training <- data[inTrain, features]
    testing <- na.omit(data[-inTrain, features])

    #trainX <- training[,names(training) != "type"]
    #preProcValues <- preProcess(x = trainX,method = c("center", "scale"))
    set.seed(400)
    ctrl <- trainControl(method="repeatedcv", number = 10, repeats = 3, classProbs=TRUE)#,summaryFunction = twoClassSummary)
    mod.fit <- train(type ~ ., data = training, method = "knn", trControl = ctrl, tuneLength=20, na.action = na.omit)
#    save(mod.fit, file="knn.mod") 
    p <- predict(mod.fit$finalModel, newdata = testing[which(names(testing)!="type")], type = "class")
    #Get the confusion matrix to see accuracy value and other parameter values
    confusionMatrix(p, testing$type )
