   source("HRVFUNS.R")

   ##supply default values
   startTime = 0
   endTime = 0
   size=300 #seconds
   overlap = 0 #seconds: 0 -> non-overlap;
   folder = "/HRVData/"
   fileName = "example.beats"

   ##First read in the arguments listed at the command line
   args=(commandArgs(TRUE))

   ##args is now a list of character vectors
   ## First check to see if arguments are passed.
   ## Then cycle through each element of the list and evaluate the expressions.
  
   if(length(args)>1){
      for(i in 1:length(args)){
        eval(parse(text=args[[i]]))
      }
   }
   
   #library(curl)
   #filecon = curl("http://hadoop:50070/webhdfs/v1/HRVData/example.beats?op=OPEN") #"example.beats"

   # connect to hdfs file system to get beats file
   hdfs.init()
   beatsPath = paste(folder, fileName, sep="")
   hdfsBeatsConn = textConnection(hdfs.read.text.file(beatsPath)) #connectHDFSBeatsFile(beatsPath)
   HRVData = CreateHRVData()
   HRVData = SetVerbose(HRVData, FALSE)
   HRVData = LoadBeatAscii(HRVData, hdfsBeatsConn, RecordPath = ".")
   features = getHRVFeatures(HRVData, startTime, endTime, size, overlap)

   jsonData<-toJSON(features)

   # write json data to hdfs
   writeJsonToHDFS(jsonData, folder, paste(fileName, ".json", sep=""))

   # read json data from hdfs
   jsonData = readJsonFromHDFS(folder, paste(fileName, ".json", sep=""))
   df = JsonToDataFrame(jsonData)
   print(df)

