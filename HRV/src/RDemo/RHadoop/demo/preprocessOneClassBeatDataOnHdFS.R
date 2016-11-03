   source("HRVFUNS.R")

   ##supply default values
   startTime = 0
   endTime = 0
   size=300 #seconds
   overlap = 0 #seconds: 0 -> non-overlap;
   folder = "/HRVData/physiobank/"
   fileName = "a01"
   #outputFileName = paste(fileName, ".hrv", sep="")

   ##First read in the arguments listed at the command line
   args=(commandArgs(TRUE))

   ##args is now a list of character vectors
   ## First check to see if arguments are passed.
   ## Then cycle through each element of the list and evaluate the expressions.
  
   if(length(args) != 0){
      for(i in 1:length(args)){
        eval(parse(text=args[[i]]))
      }
   }
  
   #library(curl)
   #filecon = curl("http://hadoop:50070/webhdfs/v1/HRVData/example.beats?op=OPEN") #"example.beats"

   # connect to hdfs file system to get beats file
   hdfs.init()
   beatFile = paste(fileName, ".beats", sep="")
   beatsPath = paste(folder, beatFile, sep="")
   hdfsBeatsConn = textConnection(hdfs.read.text.file(beatsPath)) #connectHDFSBeatsFile(beatsPath)
   HRVData = CreateHRVData()
   HRVData = SetVerbose(HRVData, FALSE)
   HRVData = LoadBeatAscii(HRVData, hdfsBeatsConn, RecordPath = ".")
   HRVData$Episodes = readEpisodesDataFromHDFS("/HRVData/apnea", paste(fileName, ".apn", sep=""))
   Splitting.beat = SplitBeatsbyEpisodes(HRVData)
   for (i in 1:2) {
      HRVData = NULL
      HRVData = CreateHRVData()
      HRVData = SetVerbose(HRVData, FALSE)
      HRVData$Beat$Time = as.numeric(unlist(Splitting.beat[i]))
   #   HRVData$Beat = filterHR(HRVData, minbpm = 25, maxbpm = 180)
   #   if (is.null(HRVData$Beat))
   #      next
      #print(length(HRVData$Beat$Time))
   #   HRVData$BeatTimeInterval$begin = head(HRVData$Beat$Time, n = 1)
   #   HRVData$BeatTimeInterval$end = tail(HRVData$Beat$Time, 1)

   #   begindex = which(diff(HRVData$Beat$Time) > 30)
   #   beg = HRVData$Beat$Time[begindex]
   #   end = HRVData$Beat$Time[begindex + 1]

   #   if (length(begindex) > 0) {
   #      HRVData$BeatTimeInterval$begin = c(HRVData$BeatTimeInterval$begin, end)
   #      HRVData$BeatTimeInterval$end = c(beg, HRVData$BeatTimeInterval$end)
   #   }
   #   print(HRVData$BeatTimeInterval)

      #load("current.mod")
      features = getHRVFeatures(HRVData)
      jsonData<-toJSON(features)

      print(jsonData)
   # write json data to hdfs
      #writeJsonToHDFS(jsonData, folder, paste(fileName, ".hrv", sep=""))
   }
