require(rhbase)
require(rjson)
require(caret)

master = "10.0.0.8"
unlistData <- function (x) {
   x[[3]][x[[3]]=="Y"] = 1
   x[[3]][x[[3]]=="N"] = 0
   y <- t(as.numeric(unlist(x[[3]])))
   y
}

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

hb.init(master, 9090)
fields = c('SDNN','SDNNIDX','pNN50','SDSD','rMSSD','IRRR','MADRR','TINN','HRVi','NN50','meanRR','meanHR','ULF','VLF','LF','HF','LFHF','TP','LFnu','HFnu','SD1','SD2','SD12','label') #remove "SDANN", "ApEn"
fields = paste("info", fields, sep=":")

tables = names(hb.list.tables())
data = NULL
numTables = length(tables)
indexTable = 1
while (indexTable <= numTables) {
    tryCatch({
       hbscan = hb.scan(tables[indexTable], 1, colspec = fields)
       while (length(x <- hbscan$get(100))) {
          data <- c(data, x)
       }
       hbscan$close()
       print(tables[indexTable])
       indexTable <- indexTable + 1
    }, error = function(e) {
       #conditionMessage(e)
       hb.init(master, 9090)
    })  
}

if (length(data)<=0)
   stop()

features = strsplit(unlist(data[[1]][[2]]), ":")
features <- matrix(unlist(features), 2)[2,]
xlist = lapply(data, unlistData) #, labelcolnum = which(features=="label"))
df <- data.frame(matrix(unlist(xlist), ncol=length(data[[1]][[3]]), byrow=T))
colnames(df) <- features
results <- ml(df, "rf")
print(results)
