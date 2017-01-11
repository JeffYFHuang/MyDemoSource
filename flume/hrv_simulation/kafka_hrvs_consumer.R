options( java.parameters = "-Xmx1g" )
require(rkafka)
source("../src/HRVFUNS.R")
require(caret)
require(parallel)
#load(url("http://172.18.161.250:50070/webhdfs/v1/models/randomforest.mod?op=OPEN"))
load("../models/randomforest.mod")

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
   #label = df$label
   #dfs.path = paste("/data/predicted/", y$Subject, sep="")
   #if (dfs.exists(dfs.path))
   #    dfs.rmr(dfs.path)
   df$predlabel = predict(mod.fit, df)
#   print(confusionMatrix(df$label, df$predlabel))
   kv <- keyval(y$Subject, toJSON(list(startTime=df$startTime[1], origLabel=df$label, predLabel=as.numeric(df$predlabel)-1)))
   #to.dfs(kv, output=dfs.path, format="text")
   kv
#   print(df$label)
#   print(confusionMatrix(df$label, df$predlabel))
   #cat(y$Subject, ": ", Sys.time() - begin, "\n", sep="")
}

args=(commandArgs(TRUE))
dfs.path = NULL #"/data/predicted_label"

if (length(args) != 0){
  for (i in 1:length(args)){
     eval(parse(text=args[[i]]))
  }
}

execution = "Exe. Rscript kafka_hrvs_consumer.R dfs.path=\"'/data/predicted_label'\"";
if (is.null(dfs.path))
   stop(paste("please provide dfs path to store results!", execution))

cs = rkafka.createConsumer("10.0.0.5:2181", "hrvs", consumerTimeoutMs = "500", groupId = "flume-sink-group")
while (T) {
    cs <- tryCatch({
       while (T) {
          begin = Sys.time()
          data = rkafka.readPoll(cs)
          tryCatch({
             if (length(data)>0) {
                #dfs.path = "/data/predicted_label"#paste("/data/predicted_", begin, sep="")
                if (dfs.exists(dfs.path))
                   dfs.rmr(dfs.path)
                to.dfs(c.keyval(lapply(data, getHRV)), output=dfs.path, format="text")
             }
             cat(length(data), " beats seq: ", Sys.time() - begin, "\n", sep="")
          }, error = function(e) {
             conditionMessage(e)
          })
       }
       return (cs)
    }, error = function(e) {
       rkafka.closeConsumer(cs)
       cs = rkafka.createConsumer("10.0.0.5:2181", "hrvs", consumerTimeoutMs = "500", groupId = "flume-sink-group")
       return (cs)
    })
}
rkafka.closeConsumer(cs)
