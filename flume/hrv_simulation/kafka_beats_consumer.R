options( java.parameters = "-Xmx1g" )
require(rkafka)
source("src/HRVFUNS.R")
require(caret)
require(parallel)
#load(url("http://172.18.161.250:50070/webhdfs/v1/models/randomforest.mod?op=OPEN"))
load("../models/randomforest.mod")

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
   print(confusionMatrix(df$label, df$predlabel))
   kv <- keyval(y$Subject, toJSON(list(startTime=df$startTime[1], origLabel=df$label, predLabel=as.numeric(df$predlabel)-1)))
   #to.dfs(kv, output=dfs.path, format="text")
   kv
   #cat(y$Subject, ": ", Sys.time() - begin, "\n", sep="")
}

#cs = rkafka.createConsumer("master:2181", "beats", consumerTimeoutMs = "3000", groupId = "flume-sink-group")
while (T) {
    tryCatch({
      cs = rkafka.createConsumer("172.18.161.250:2181", "beats", consumerTimeoutMs = "3000", groupId = "flume-sink-group")
      begin = Sys.time()
      data = rkafka.readPoll(cs)
      if (length(data)>0) {
         dfs.path = "/data/predicted_label"#paste("/data/predicted_", begin, sep="")
         if (dfs.exists(dfs.path))
            dfs.rmr(dfs.path)
         to.dfs(c.keyval(lapply(data, getHRV)), output=dfs.path, format="text")
      }
      #mcmapply(getHRV, data, mc.cores = 4)
      cat(length(data), " beats seq: ", Sys.time() - begin, "\n", sep="")
      rkafka.closeConsumer(cs)
    }, error = function(e) {
       conditionMessage(e) # 這就會是"demo error"
    })
}
#rkafka.closeConsumer(cs)
