   source("HRVFUNS.R")

   ##supply default values
   folder = "/HRVData/physiobank/"
   fileName = "a03"
   args=(commandArgs(TRUE))
 
   if(length(args) != 0){
      for(i in 1:length(args)){
        eval(parse(text=args[[i]]))
      }
   }
   
   # connect to hdfs file system to get beats file
   hdfs.init()

   # read json data from hdfs
   jsonData = readJsonFromHDFS(folder, paste(fileName, ".beats.hrv", sep=""))
   df = JsonToDataFrame(jsonData)
   print(df)

readEpisodesDataToHDFS <- function (path, fileName) {
   if (is.null(path) || is.null(fileName)) {
      stop ("path or fileName is null!")
   }

   if (!is.null(path) && !hdfs.exists(path)) {
      stop ("path is not exist!")
   }

   filePath = paste(path, fileName, sep = "")
   if (!is.null(fileName) && !hdfs.exists(filePath)) {
      stop ("filePath is not exist!")
   }

   table.data = hdfs.read.text.file(filePath)
   table = read.table(textConnection(table.data))
   return(table)
}

#   apnData = readEpisodesDataToHDFS(folder, paste(fileName, ".apn", sep=""))
#   print(apnData)
