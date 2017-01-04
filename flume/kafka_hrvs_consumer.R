options( java.parameters = "-Xmx1g" )
require(rkafka)
source("src/HRVFUNS.R")
require(caret)
require(parallel)
#load(url("http://172.18.161.250:50070/webhdfs/v1/models/randomforest.mod?op=OPEN"))
load("models/randomforest.mod")

trimWhiteSpace <- function(line) gsub("(^ +)|( +$)", "", line)
splitIntoWords <- function(line) unlist(strsplit(line, "[[:space:]]+"))
splitLine <- function(line) {
    val <- unlist(strsplit(line, "\t"))
    val[2]<- trimWhiteSpace(val[2])
    #HRV <- as.numeric(splitIntoWords(val[2]))
    list(Subject = val[1], HRV = val[2])
}

getHRV <- function (x) {
   begin = Sys.time()
   y = splitLine(x)
   jsonData <- fromJSON(y$HRV)
   size = length(unlist(jsonData))
   row.size = length(unlist(jsonData[[1]]))
   col.names = names(jsonData[[1]])
   df = t(array(as.numeric(unlist(jsonData)), c(row.size, size/row.size)))
   df = data.frame(df)
   colnames(df) = col.names
   df = na.omit(df)
   label = df$label
   df$label = predict(mod.fit, df)
#   print(df$label)
   print(confusionMatrix(df$label, label))
   #cat(y$Subject, ": ", Sys.time() - begin, "\n", sep="")
}

#cs = rkafka.createConsumer("master:2181", "beats", consumerTimeoutMs = "3000", groupId = "flume-sink-group")
while (T) {
    tryCatch({
      cs = rkafka.createConsumer("10.0.0.5:2181", "hrvs", consumerTimeoutMs = "3000", groupId = "flume-sink-group")
      begin = Sys.time()
      data = rkafka.readPoll(cs)
      lapply(data, getHRV)
      #mcmapply(getHRV, data, mc.cores = 4)
      cat(length(data), " beats seq: ", Sys.time() - begin, "\n", sep="")
      rkafka.closeConsumer(cs)
    }, error = function(e) {
       rkafka.closeConsumer(cs)
       conditionMessage(e) # 這就會是"demo error"
    })
}
#rkafka.closeConsumer(cs)
