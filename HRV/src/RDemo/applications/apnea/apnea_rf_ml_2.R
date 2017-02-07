#! /usr/bin/env Rscript

# mapper.R - Wordcount program in R
# script for Mapper (R-Hadoop integration)
source("HRVFUNS.R")
require(caret)
require(r2pmml)

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
    data$label=factor(data$label)
    inTrain <- createDataPartition(y = data$label, p = .75, list = FALSE)
    #features <- c("VLF", "LFHF", "meanRR", "meanHR", "HF", "type")
    features <- names(data)
    
    #features <- c("SDNN", "TINN", "LFHF", "LFnu", "SD2", "SD12", "TP", "ApEn", "type")
    training <- data[inTrain, features]
    testing <- na.omit(data[-inTrain, features])

    set.seed(400)
    ctrl <- trainControl(method="repeatedcv",number=10, repeats = 3) #,classProbs=TRUE,summaryFunction = twoClassSummary)
    # Random forrest
    mod.fit <- train(label ~ ., data = training, method = "rf", trControl = ctrl, na.action = na.omit)#, preProcess = c("center","scale"), na.action=na.omit)
    # save(mod.fit, file = "randomforest.mod")
    # Export the model to PMML
    r2pmml(mod.fit, "rf.pmml")

#    testing$label=as.integer(testing$label) - 1
#    write.csv(testing, file = "input.csv", row.names = FALSE)
#    write.csv(testing, file = "input.csv", row.names = FALSE)
#    rfPredict <- predict(mod.fit,newdata = testing)
    #Get the confusion matrix to see accuracy value and other parameter values
#    confusionMatrix(rfPredict, testing$label)
