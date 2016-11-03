source("HRVFUNS.R")
startTime = 0
endTime = 0
size=300 #seconds
overlap = 0 #seconds: 0 -> non-overlap;
hdfs.root <- '/HRVData'
hdfs.data <- file.path(hdfs.root, 'physiobank')
hdfs.out <- file.path(hdfs.root, 'out')

## map function
map <- function(k,lines) {
  fileName = Sys.getenv("map_input_file")
  words.list <- strsplit(lines, '\\s') 
  words <- unlist(words.list)
  fileName = tail(unlist(strsplit(fileName, split="/")), n=1)
  #rmr.str(fileName)
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
  #print(jsonData)
  # write json data to hdfs
  #hdfs.init()
  #rmr.str(fileName)
  #writeJsonToHDFS(jsonData,  file.path(hdfs.data, paste(fileName, ".hrv", sep="")))#folder, paste(fileName, ".hrv", sep=""))
  to.dfs(jsonData, file.path(hdfs.data, paste(fileName, ".hrv", sep="")))
  #kv
  keyval(fileName, jsonData)
}

wordcount <- function (input, output=NULL) { 
  mapreduce(input=input, output=output, input.format="text", 
            map=map, reduce=reduce)
}


## delete previous result if any
hdfs.init()
if (hdfs.exists(hdfs.out)) {
   hdfs.rm(hdfs.out)
}
hdfs.rm(file.path(hdfs.data, "/*.hrv"))

#rmr.options.env = new.env(parent=emptyenv())

#rmr.options(backend = 'hadoop')
rmr.options(
  backend = "hadoop", 
  hdfs.tempdir = "tmp", #file.path("/home", system("whoami", TRUE),"tmp-rmr2", basename(tempdir())),
  backend.parameters = list(D = "mapreduce.map.java.opts=-Xmx1024M", 
        D = "mapreduce.reduce.java.opts=-Xmx1024M",
        D = "mapreduce.map.memory.mb=-Xmx1024M",
        D = "mapreduce.reduce.memory.mb=-Xmx1024M",
        D = "mapred.reduce.tasks=3"))
#rmr.options.env$backend.parameters =  
#  list(
#    hadoop = 
#      list(
#        D = "mapreduce.map.java.opts=-Xmx1024M", 
#        D = "mapreduce.reduce.java.opts=-Xmx1024M",
#        D = "mapreduce.map.memory.mb=4096",
#        D = "mapreduce.reduce.memory.mb=4096",
#        D = "mapred.reduce.tasks=2"))

## Submit job
out <- wordcount(hdfs.data, hdfs.out)

## Fetch results from HDFS
#results <- from.dfs(out)

## check top 30 frequent words
#results.df <- as.data.frame(results, stringsAsFactors=F) 
#colnames(results.df) <- c('word', 'count') 
#head(results.df[order(results.df$count, decreasing=T), ], 30)
