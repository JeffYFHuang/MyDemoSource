   Sys.setenv("HADOOP_CMD"="/usr/local/hadoop/bin/hadoop")
   source("HRVFUNS.R")

   ##supply default values
   startTime = 0
   endTime = 0
   size=300 #seconds
   overlap = 0 #seconds: 0 -> non-overlap;
   folder = "/HRVData/physiobank/"
   #fileName = "example.beats"
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
   # hdfs.rm(paste(folder, "*.hrv", sep=""))
   beatFiles = hdfs.ls(paste(folder, "*.beats", sep=""))
   beatFiles = unlist(strsplit(beatFiles$file, folder))
   beatFiles = beatFiles[grep(".beats", beatFiles)]
   #beatFiles = beatFiles[!beatFiles %in% c("b04.beats", "c01.beats", "c01er.beats", "c04.beats", "b05.beats", "x14.beats", "x17.beats", "x18.beats", "x24.beats", "x29.beats")]
   for (fileName in beatFiles) {
      filePath = paste(folder, fileName, ".hrv", sep="")
      if (!hdfs.exists(filePath)) {
         hdfs.close(hdfs.file(filePath, "w"))      
         print(fileName)
         beatsPath = paste(folder, fileName, sep="")
         hdfsBeatsConn = textConnection(hdfs.read.text.file(beatsPath)) #connectHDFSBeatsFile(beatsPath)
         HRVData = CreateHRVData()
         HRVData = SetVerbose(HRVData, FALSE)
         HRVData = LoadBeatAscii(HRVData, hdfsBeatsConn, RecordPath = ".")
         HRVData$Beat = filterHR(HRVData, minbpm = 25, maxbpm = 180)

         # get valid beat time intervals
         HRVData$BeatTimeInterval$begin = head(HRVData$Beat$Time, n = 1)
         HRVData$BeatTimeInterval$end = tail(HRVData$Beat$Time, 1)

         begindex = which(diff(HRVData$Beat$Time) > 30)
         beg = HRVData$Beat$Time[begindex]
         end = HRVData$Beat$Time[begindex + 1]

         if (length(begindex) > 0) {
            HRVData$BeatTimeInterval$begin = c(HRVData$BeatTimeInterval$begin, end)
            HRVData$BeatTimeInterval$end = c(beg, HRVData$BeatTimeInterval$end)
         }

         # Calculate HRV features
         features = getHRVFeatures(HRVData, startTime, endTime, size, overlap)

         jsonData<-toJSON(features)

         # write json data to hdfs
         writeJsonToHDFS(jsonData, folder, paste(fileName, ".hrv", sep=""))
      }
   }
