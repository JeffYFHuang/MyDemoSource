options( java.parameters = "-Xmx1g" )
source("../src/HRVFUNS.R")
require(caret)
#load(url("http://172.18.161.250:50070/webhdfs/v1/models/randomforest.mod?op=OPEN"))

trimWhiteSpace <- function(line) gsub("(^ +)|( +$)", "", line)
splitIntoWords <- function(line) unlist(strsplit(line, "[[:space:]]+"))
splitLine <- function(line) {
    val <- unlist(strsplit(line, "\t"))
    val[2]<- trimWhiteSpace(val[2])
    #HRV <- as.numeric(splitIntoWords(val[2]))
    list(Subject = val[1], data = val[2])
}

beat2HRV <- function(subject, hr.data) {
    HRV = NULL
    if (!is.null(hr.data$Episodes)) {
      hr.data<-SplitBeatsbyEpisodes2(hr.data)

      for (i in 1:2) {
          if (length(hr.data[[i]]) < 120)
             next
          name = paste(subject, ".", names(hr.data[i]), sep="")
          data <- list()
          data$Beat <- hr.data[[i]]
          data$Subject <- name
          HRV <- c(HRV, getSplitWindowBeats(data, toHRV=T), recursive = F)
      }
    } else {
      data <- list()
      data$Beat <- hr.data$Beat
      data$Subject <- subject
      HRV <- getSplitWindowBeats(data, toHRV=T)
    }

    HRV[which(lapply(HRV, length)!=1)]
}

HRV2DF <- function(jsonData){
   #jsonData <- fromJSON(data)
   size = length(unlist(jsonData))
   row.size = length(unlist(jsonData[[1]][[1]]))
   col.names = names(jsonData[[1]][[1]])
   df = t(array(as.numeric(unlist(jsonData)), c(row.size, size/row.size)))
   df = data.frame(df)
   colnames(df) = col.names
   df
}

getHRV <- function (x) {
   begin = Sys.time()
   y = splitLine(x)
   HRV = NULL
   data <- fromJSON(y$data)
   if (!is.null(data$Beat)) {
      # beats to HRV
      HRV = beat2HRV(y$Subject, data)
   }
#   cat(paste(y$Subject, "\t", toJSON(HRV), "\n", sep=""))
   HRV
}

train.model <- function(v) {
  drops <- c("startTime","endTime")
  v <- v[, !(names(v) %in% drops)]
  v$label=factor(v$label)
  inTrain <- createDataPartition(y = v$label, p = .5, list = FALSE)
  training <- v[inTrain,]
  testing <- na.omit(v[-inTrain,])

  set.seed(400)
  ctrl <- trainControl(method="repeatedcv", number=10, repeats = 5)
  mod.fit <- train(label ~ ., data = training, method = "rf", trControl = ctrl, ntree=20, preProcess = c("center","scale"), na.action=na.omit, trace=FALSE)
  p <- predict(mod.fit, newdata = testing)
  #Get the confusion matrix to see accuracy value and other parameter values
  print(confusionMatrix(p, testing$label))
  mod.fit
}

con <- file("stdin", open = "r")
lines <- readLines(con, warn = FALSE)
HRV <- na.omit(HRV2DF(lapply(lines, getHRV)))
#print(HRV)
mod.fit<-train.model(HRV)
save(mod.fit, file = "randomforest.mod")

close(con)
