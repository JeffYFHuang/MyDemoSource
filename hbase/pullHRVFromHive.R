require(caret)

ml <- function (data, trainModel = "knn") {
    data$label=factor(data$label)
    inTrain <- createDataPartition(y = data$label, p = .75, list = FALSE)
    #features <- c("VLF", "LFHF", "meanRR", "meanHR", "HF", "label")
    features <- names(data)
    #features <- c("SDNN", "TINN", "LFHF", "LFnu", "SD2", "SD12", "TP", "ApEn", "label")

    training <- data[inTrain, features]
    testing <- na.omit(data[-inTrain, features])

    #trainX <- training[,names(training) != "label"]
    #preProcValues <- preProcess(x = trainX,method = c("center", "scale"))
    set.seed(400)
    ctrl <- trainControl(method="repeatedcv", number = 10, repeats = 3) #,summaryFunction = twoClassSummary)
    mod.fit = NULL
    switch(trainModel, 
      svm = {
         mod.fit <- train(label ~., data = training, method="svmRadial", trControl=ctrl, preProc = c("center","scale"), na.action=na.omit)
      },
      nnet = {
         mod.fit <- train(label ~ ., data = training, method = "nnet", trace = FALSE, trControl = ctrl, na.action=na.omit)
      },
      tree = {
         mod.fit <- train (label ~., data = training, method = "rpart", tuneLength=20, trControl = ctrl, na.action = na.omit)
      },
      rf = {
         mod.fit <- train(label ~ ., data = training, method = "rf", trControl = ctrl, preProcess = c("center","scale"), na.action=na.omit)
      },
      {
         mod.fit <- train(label ~ ., data = training, method = "knn", trControl = ctrl, preProcess = c("center","scale"), na.action = na.omit)
      }
    )
    save(mod.fit, file="mod.fit")
    knnPredict <- predict(mod.fit, newdata = testing )
    #Get the confusion matrix to see accuracy value and other parameter values
    confusionMatrix(knnPredict, testing$label )
}

require(RHive)
require(rjson)

master = "10.0.0.8"
user = "hduser"
passwd = "hduser"
rhive.init()
rhive.connect(master, user="hduser", password="hduser")

tableName = "hivetest_hrv"
data = rhive.load.table(tableName)

features = strsplit(names(data), "\\.")
features <- matrix(unlist(features), 2)[2,]
data = lapply(data, function(x){as.double(as.character(x))})
data = data.frame(data)

colnames(data) <- features
results <- ml(data, "rf")
print(results)
