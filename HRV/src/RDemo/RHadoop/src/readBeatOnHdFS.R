   source("HRVFUNS.R")

   ##supply default values
   folder = "/HRVData/"
   fileName = "example.beats.hrv"
   args=(commandArgs(TRUE))
 
   if(length(args) != 0){
      for(i in 1:length(args)){
        eval(parse(text=args[[i]]))
      }
   }
   
   # connect to hdfs file system to get beats file
   hdfs.init()
   beatsPath = paste(folder, fileName, sep="")

   # read json data from hdfs
   jsonData = readJsonFromHDFS(folder, paste(fileName, ".json", sep=""))
   df = JsonToDataFrame(jsonData)
   print(df)

