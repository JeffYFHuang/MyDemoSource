options( java.parameters = "-Xmx1g" )
require(pryr)
require(rkafka)
source("src/HRVFUNS.R")
require(caret)
require(parallel)
require(randomForest)
load(url("http://172.18.161.250:50070/webhdfs/v1/models/randomforest.mod?op=OPEN"))

trimWhiteSpace <- function(line) gsub("(^ +)|( +$)", "", line)
splitIntoWords <- function(line) unlist(strsplit(line, "[[:space:]]+"))
splitLine <- function(line) {
    val <- unlist(strsplit(line, "\t"))
    val[2]<- trimWhiteSpace(val[2])
    Beat <- as.numeric(splitIntoWords(val[2]))
    list(Subject = val[1], Beat = Beat)
}

getHRV <- function (x) {
#   for(i in 1:length(data)) {
      begin = Sys.time()
      y = splitLine(x)
      jsonData = getHRVJSONData(y$Beat)
      df = JsonToDataFrame(jsonData)
      df = na.omit(df)
      df$label = predict(mod.fit, df)
      cat(y$Subject, ": ", Sys.time() - begin, ", mem used: ", mem_used(), "\n", sep="")
      gc()
#   }
}

cs = rkafka.createConsumer("master:2181", "beats", consumerTimeoutMs = "3000", groupId = "flume-sink-group")
while (T) {
    tryCatch({
      #cs = rkafka.createConsumer("172.18.161.250:2181", "beats", consumerTimeoutMs = "3000", groupId = "flume-sink-group")
      begin = Sys.time()
      cat("begin polling, mem used: ", mem_used(), "\n", sep="")

      max_length = 980
      data = list()
      while (length((seq = rkafka.read(cs))) > 0 ) {
          data[[length(data)+1]] = seq
          if (length(data) >= max_length) break
      }

      cat("polling finished, mem used: ", mem_used(), ", data length: ", length(data), "\n", sep="")
      core_num = 4
      #partition_size = length(data)/core_num + ifelse(max_length%%core_num==0, 0, 1)
      #datasplit = split(data, rep(1:core_num, each=partition_size))
      #rm(data)
#      lapply(data, getHRV)
      mclapply(data, getHRV, mc.cores = core_num)
      rm(data)
      gc()
      #rkafka.closeConsumer(cs)
      cat("collapse time:", Sys.time() - begin, ", mem used after deleting data: ", mem_used(), "\n", sep="")
    }, error = function(e) {
       conditionMessage(e) # 這就會是"demo error"
       rm(data)
       rkafka.closeConsumer(cs)
       cs = rkafka.createConsumer("172.18.161.250:2181", "beats", consumerTimeoutMs = "3000", groupId = "flume-sink-group")
    })
}
rkafka.closeConsumer(cs)
