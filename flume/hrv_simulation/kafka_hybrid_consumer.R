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

getHRVfromJSON <- function(jsonData){
   #jsonData <- fromJSON(data)
   size = length(unlist(jsonData))
   row.size = length(unlist(jsonData[[1]]))
   col.names = names(jsonData[[1]])
   df = t(array(as.numeric(unlist(jsonData)), c(row.size, size/row.size)))
   df = data.frame(df)
   colnames(df) = col.names
   df
}

getHRV <- function (x) {
   begin = Sys.time()
   y = splitLine(x)
   df = NULL
   data <- fromJSON(y$data)
   if (!is.null(data$Beat)) {
      # beats to HRV
      df = getHRVfromJSON(beat2HRV(y$Subject, data))
      print(df$label)
      #jsonData = getHRVJSONData(data$Beat)
      #df = JsonToDataFrame(jsonData)
   } else {
      # just HRV
      df = getHRVfromJSON(data)
   }
   df = na.omit(df)
#   print(df)
   df$predlabel = predict(mod.fit, df)
   accuracy = NULL
   if (!is.null(df$label)) {
      print(confusionMatrix(df$label, df$predlabel))
      accuracy = length(which(df$label==df$predlabel))/length(df$predlabel)
   }
   kv <- keyval(y$Subject, toJSON(list(startTime=df$startTime[1], origLabel=df$label, predLabel=as.numeric(df$predlabel)-1, accuracy=accuracy)))
   kv
}

while (T) {
    tryCatch({
      cs = rkafka.createConsumer("10.0.0.5:2181", "hrvs", consumerTimeoutMs = "3000", groupId = "flume-sink-group")
      begin = Sys.time()
      data = rkafka.readPoll(cs)
      if (length(data)>0) {
         dfs.path = "/data/predicted_label"
         if (dfs.exists(dfs.path))
            dfs.rmr(dfs.path)
         to.dfs(c.keyval(lapply(data, getHRV)), output=dfs.path, format="text")
      }
      #mcmapply(getHRV, data, mc.cores = 4)
      cat(length(data), " beats seq: ", Sys.time() - begin, "\n", sep="")
      rkafka.closeConsumer(cs)
    }, error = function(e) {
       rkafka.closeConsumer(cs)
       conditionMessage(e) # 這就會是"demo error"
    })
}
