#! /usr/bin/env Rscript

# mapper.R - Wordcount program in R
# script for Mapper (R-Hadoop integration)

source("HRVFUNS.R")
require(caret)
require(r2pmml)
require(pmml)
require(XML)

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

#    features <- c("SDNN", "TINN", "LFHF", "LFnu", "SD2", "SD12", "TP", "ApEn", "type")
    training <- data[inTrain, features]
    testing <- data[-inTrain, features]

#    fitControl <- trainControl(method = "repeatedcv",
#                           number = 10,
#                           repeats = 3,
#                   classProbs = TRUE,
#                   summaryFunction = twoClassSummary)
    #CART1 machine learning
    set.seed(400)
    control <- trainControl(method="repeatedcv", number=10, repeats=3)#, classProbs = TRUE)
    mod.fit = train (type~.,
           training,
           method = "rpart",
           tuneLength=20,
#           preProcess = c("center","scale"),
#           metric="ROC",
           trControl = control)
    model.predict = predict(object = mod.fit$finalModel, newdata = testing[which(names(testing)!="type")], type = "class")
    saveXML(pmml(mod.fit$finalModel), "tree.pmml")
    confusionMatrix(testing$type, model.predict)

#   boxplot between two group
    features <- names(df.InEpisodes)
    pdf('1-8.pdf')
    par(mfrow=c(2,4))
    for (feature in features[1:8]) {
        boxplot(c(df.InEpisodes[feature], df.OutEpisodes[feature]), outline = FALSE,  names = c("InEpisodes", "OutEpisodes"), xlab=feature)
    }
    dev.off()
    pdf('9-16.pdf')
    par(mfrow=c(2,4))
    for (feature in features[9:16]) {
        boxplot(c(df.InEpisodes[feature], df.OutEpisodes[feature]), outline = FALSE,  names = c("InEpisodes", "OutEpisodes"), xlab=feature)
    }
    dev.off()
    pdf('17-25.pdf')
    par(mfrow=c(3,3))
    for (feature in features[17:25]) {
        boxplot(c(df.InEpisodes[feature], df.OutEpisodes[feature]), outline = FALSE,  names = c("InEpisodes", "OutEpisodes"), xlab=feature)
    }
    dev.off()
