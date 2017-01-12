options( java.parameters = "-Xmx1g" )
require(rkafka)
source("../src/HRVFUNS.R")
require(caret)
require(parallel)
#load(url("http://172.18.161.250:50070/webhdfs/v1/models/randomforest.mod?op=OPEN"))
source("loadmodel.R")

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
      #jsonData = getHRVJSONData(data$Beat)
      #df = JsonToDataFrame(jsonData)
   } else {
      # just HRV
      df = getHRVfromJSON(data)
   }
   df = na.omit(df)
   #print(rfs)
   predict_inner <- function (rf, df) {
      predlabel<-predict(rf ,df)
      as.numeric(predlabel) - 1
   }

   predict_all <- function (rfs, df) {
      label_list <- lapply(rfs, predict_inner, df)
      n.row = length(label_list)
      n.col = length(label_list[1]$m)
      m <- t(matrix(unlist(label_list), n.col, n.row))
      round(colSums(m)/n.row)
   }

   df$predlabel = predict_all(rfs, df) #lapply(rfs, predict_inner, df)
   #print(df$predlabel)
   accuracy = NULL
   if (!is.null(df$label)) {
      print(confusionMatrix(df$label, df$predlabel))
      accuracy = length(which(df$label==df$predlabel))/length(df$predlabel)
   }
   kv <- keyval(y$Subject, toJSON(list(startTime=df$startTime[1], origLabel=df$label, predLabel=as.numeric(df$predlabel)-1, accuracy=accuracy)))
   kv
}

cs = rkafka.createConsumer("10.0.0.5:2181", "hrvs", consumerTimeoutMs = "3000", groupId = "flume-sink-group")
while (T) {
    cs <- tryCatch({
       while (T) {
          begin = Sys.time()
          data = rkafka.readPoll(cs)
          tryCatch ({
             if (length(data)>0) {
                source("getHRV.R")
                dfs.path = "/data/predicted_label"
                if (dfs.exists(dfs.path))
                   dfs.rmr(dfs.path)
                to.dfs(c.keyval(lapply(data, getHRV)), output=dfs.path, format="text")
             }
             #mcmapply(getHRV, data, mc.cores = 4)
             cat(length(data), " beats seq: ", Sys.time() - begin, "\n", sep="")
          }, error = function(e) {
             conditionMessage(e)
          })
       }
       return(cs)
    }, error = function(e) {
       rkafka.closeConsumer(cs)
       cs = rkafka.createConsumer("10.0.0.5:2181", "hrvs", consumerTimeoutMs = "3000", groupId = "flume-sink-group")
       return(cs)
       #conditionMessage(e) # 這就會是"demo error"
    })
}
rkafka.closeConsumer(cs)
