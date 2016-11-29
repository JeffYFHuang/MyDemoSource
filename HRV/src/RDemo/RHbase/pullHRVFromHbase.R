require(rhbase)
require(rjson)

master = "10.0.0.8"
unlistData <- function (x) {
   y <- as.numeric(t(unlist(x[[3]])))
   y
}

ml <- function (data) {
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
    ctrl <- trainControl(method="repeatedcv", number = 10, repeats = 3) #,classProbs=TRUE,summaryFunction = twoClassSummary)
    mod.fit <- train(label ~ ., data = training, method = "knn", trControl = ctrl, preProcess = c("center","scale"), na.action = na.omit)
    save(mod.fit, file="knn.mod")
    knnPredict <- predict(mod.fit,newdata = testing )
    #Get the confusion matrix to see accuracy value and other parameter values
    confusionMatrix(knnPredict, testing$label )
}

hb.init(master, 9090)
fields = c('startTime','endTime','SDNN','SDANN','SDNNIDX','pNN50','SDSD','rMSSD','IRRR','MADRR','TINN','HRVi','NN50','meanRR','meanHR','ULF','VLF','LF','HF','LFHF','TP','LFnu','HFnu','SD1','SD2','SD12','ApEn','label')
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
       #conditionMessage(e) # 這就會是"demo error"
       hb.init(master, 9090)
    })  
}

if (length(data)<=0)
   stop()

features = strsplit(unlist(data[[1]][[2]]), ":")
features <- matrix(unlist(features), 2)[2,]
xlist = lapply(data, unlistData)
df <- data.frame(matrix(unlist(xlist), ncol=28, byrow=T))
colnames(df) <- features
drops <- c("starttime","endtime")
df <- df[, !(names(df) %in% drops)]
results <- ml(df)
print(results)
