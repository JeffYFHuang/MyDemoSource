Sys.setenv("HADOOP_CMD"="/usr/local/hadoop/bin/hadoop")
Sys.setenv("HADOOP_STREAMING"="/usr/local/hadoop/share/hadoop/tools/lib/hadoop-streaming-2.6.0.jar")
source("HRVFUNS.R")
startTime = 0
endTime = 0
size=300 #seconds
overlap = 0 #seconds: 0 -> non-overlap;
folder = "/HRVData/test/"

## map function
map <- function(k,lines) {
  fileName = Sys.getenv("map_input_file")
  words.list <- strsplit(lines, '\\s') 
  words <- unlist(words.list)
  fileName = tail(unlist(strsplit(fileName, split="/")), n=1)
#  rmr.str(fileName)
  return( keyval(fileName, words) )
}

## reduce function
reduce <- function(fileName, words) {
  #rmr.str(sort(as.numeric(c(words))))
  HRVData = CreateHRVData()
  HRVData = SetVerbose(HRVData, FALSE)
  HRVData$Beat$Time = sort(as.numeric(c(words)))  
  HRVData$Beat = filterHR(HRVData, minbpm = 25, maxbpm = 180)

  HRVData$BeatTimeInterval$begin = head(HRVData$Beat$Time, n = 1)
  HRVData$BeatTimeInterval$end = tail(HRVData$Beat$Time, 1)

  begindex = which(diff(HRVData$Beat$Time) > 30)
  beg = HRVData$Beat$Time[begindex]
  end = HRVData$Beat$Time[begindex + 1]

  if (length(begindex) > 0) {
     HRVData$BeatTimeInterval$begin = c(HRVData$BeatTimeInterval$begin, end)
     HRVData$BeatTimeInterval$end = c(beg, HRVData$BeatTimeInterval$end)
  }

  features = getHRVFeatures(HRVData, startTime, endTime, size, overlap)
  jsonData<-toJSON(features)
  # write json data to hdfs
  #hdfs.init()
  #rmr.str(fileName)
  #writeJsonToHDFS(jsonData, folder, paste(fileName, ".hrv", sep=""))
  #to.dfs(kv, paste(folder, fileName, ".hrv", sep=""))
  #kv
  keyval(fileName, jsonData)
}

wordcount <- function (input, output=NULL) { 
  mapreduce(input=input, output=output, input.format="text", 
            map=map, reduce=reduce)
}


## delete previous result if any
system("hadoop fs -rmr /HRVData/out")
system(paste("hadoop fs -rm ", folder, "*.hrv", sep=""))

rmr.options.env = new.env(parent=emptyenv())

rmr.options(backend = 'hadoop')
rmr.options.env$backend.parameters =  
  list(
    hadoop = 
      list(
        D = "mapreduce.map.java.opts=-Xmx1024M", 
        D = "mapreduce.reduce.java.opts=-Xmx1024M",
        D = "mapred.reduce.tasks=2"))

## Submit job
hdfs.root <- '/HRVData'
hdfs.data <- file.path(hdfs.root, 'physiobank') 
hdfs.out <- file.path('', 'out') 
out <- wordcount(hdfs.data, NULL)

## Fetch results from HDFS
#results <- from.dfs(out)

## check top 30 frequent words
#results.df <- as.data.frame(results, stringsAsFactors=F) 
#colnames(results.df) <- c('word', 'count') 
#head(results.df[order(results.df$count, decreasing=T), ], 30)
