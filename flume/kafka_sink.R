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

getHRV <- function (x) {
   begin = Sys.time()
   y = splitLine(x)
   jsonData = getHRVJSONData(y$Beat)
   df = JsonToDataFrame(jsonData)
   df = na.omit(df)
   df$label = predict(mod.fit, df)
   cat(y$Subject, ": ", Sys.time() - begin, "\n", sep="")
}

#cs = rkafka.createConsumer("master:2181", "beats", consumerTimeoutMs = "3000", groupId = "flume-sink-group")
while (T) {
    tryCatch({
      cs = rkafka.createConsumer("172.18.161.250:2181", "beats-0", consumerTimeoutMs = "3000", groupId = "flume-sink-group")
      begin = Sys.time()
      data = rkafka.readPoll(cs)
      lapply(data, getHRV)
      #mcmapply(getHRV, data, mc.cores = 4)
      cat(length(data), " beats seq: ", Sys.time() - begin, "\n", sep="")
      rkafka.closeConsumer(cs)
    }, error = function(e) {
       conditionMessage(e) # 這就會是"demo error"
    })
}
#rkafka.closeConsumer(cs)
