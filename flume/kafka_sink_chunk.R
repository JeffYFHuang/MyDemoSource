options( java.parameters = "-Xmx3g" )
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

getHRV <- function (idx) {
   begin = Sys.time()
   file = paste0("/tmp/chunk", idx, ".Rdata")
   load(file)
   unlink(file)
   #print(chunk)
   y = splitLine(chunk)
   jsonData = getHRVJSONData(y$Beat)
   df = JsonToDataFrame(jsonData)
   df = na.omit(df)
   df$label = predict(mod.fit, df)
   cat(y$Subject, ": ", Sys.time() - begin, ", mem used: ", mem_used(), "\n", sep="")
}

## Save the chunks
saveChunks <- function (data) {
   for (idx in 1:length(data)) {
       chunk <- data[[idx]]
       output <- paste0("/tmp/chunk", idx, ".Rdata")
       save(chunk, file = output)
   }
}

#cs = rkafka.createConsumer("master:2181", "beats", consumerTimeoutMs = "3000", groupId = "flume-sink-group")
while (T) {
    tryCatch({
      cs = rkafka.createConsumer("172.18.161.250:2181", "beats", consumerTimeoutMs = "1000", groupId = "flume-sink-group")
      begin = Sys.time()
      cat("begin polling, mem used: ", mem_used(), "\n", sep="")
      data = rkafka.readPoll(cs)
      cat("polling finished, mem used: ", mem_used(), ", data length: ", length(data), "\n", sep="")
#      lapply(data, getHRV)
      idx = 1: length(data)
      names(idx) = idx
      saveChunks(data)
      rm(data)
      mclapply(idx, getHRV, mc.cores = 8)
      rkafka.closeConsumer(cs)
      cat("collapse time:", Sys.time() - begin, ", mem used after deleting data: ", mem_used(), "\n", sep="")
      gc()
    }, error = function(e) {
       conditionMessage(e) # 這就會是"demo error"
       gc()
       rkafka.closeConsumer(cs)
    })
}
#rkafka.closeConsumer(cs)
