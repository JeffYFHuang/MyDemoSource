options( java.parameters = "-Xmx1536m" )
require(pryr)
require(rkafka)
source("src/HRVFUNS.R")
require(caret)
require(parallel)
load(url("http://172.18.161.250:50070/webhdfs/v1/models/randomforest.mod?op=OPEN"))

trimWhiteSpace <- function(line) gsub("(^ +)|( +$)", "", line)
splitIntoWords <- function(line) unlist(strsplit(line, "[[:space:]]+"))
splitLine <- function(line) {
    val <- unlist(strsplit(line, "\t"))
    val[2]<- trimWhiteSpace(val[2])
    Beat <- as.numeric(splitIntoWords(val[2]))
    list(Subject = val[1], Beat = Beat)
}

getHRV <- function (idx, env) {
   begin = Sys.time()
   x <- get("data", envir = env)[[idx]]
   y = splitLine(x)
   jsonData = getHRVJSONData(y$Beat)
   df = JsonToDataFrame(jsonData)
   df = na.omit(df)
   df$label = predict(mod.fit, df)
   cat(y$Subject, ": ", Sys.time() - begin, ", mem used: ", mem_used(), "\n", sep="")
}

my.env <- new.env()
#cs = rkafka.createConsumer("master:2181", "beats", consumerTimeoutMs = "3000", groupId = "flume-sink-group")
while (T) {
    tryCatch({
      cs = rkafka.createConsumer("172.18.161.250:2181", "beats", consumerTimeoutMs = "3000", groupId = "flume-sink-group")
      begin = Sys.time()
      cat("begin polling, mem used: ", mem_used(), "\n", sep="")
      data = rkafka.readPoll(cs)
      assign("data", data, envir = my.env)
      cat("polling finished, mem used: ", mem_used(), ", data length: ", length(data), "\n", sep="")
#      lapply(data, getHRV)
      idx = 1: length(data)
      names(idx) = idx
      mclapply(idx, getHRV, env = my.env, mc.cores = 6)
      rkafka.closeConsumer(cs)
      rm(data)
      cat("collapse time:", Sys.time() - begin, ", mem used after deleting data: ", mem_used(), "\n", sep="")
#      gc()
    }, error = function(e) {
       conditionMessage(e) # 這就會是"demo error"
    })
}
#rkafka.closeConsumer(cs)
