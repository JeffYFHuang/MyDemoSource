library(RHRV)
library(rhdfs)
source("HRVFUNS.R")

   startTime = 0
   endTime = 0
   size=300 #seconds
   overlap = 0 #seconds: 0 -> non-overlap;
   folder = "/HRVData/physiobank/"
   fileName = "b05"
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
   fileName = paste(fileName, ".beats", sep="")
   cat(fileName)
   cat("\n")
   beatsPath = paste(folder, fileName, sep="")
   hdfsBeatsConn = textConnection(hdfs.read.text.file(beatsPath)) #connectHDFSBeatsFile(beatsPath)
   HRVData = CreateHRVData()
   HRVData = SetVerbose(HRVData, FALSE)
   #HRVData = LoadBeatAscii(HRVData, "example.beats", RecordPath = ".")
   HRVData = LoadBeatAscii(HRVData, hdfsBeatsConn, RecordPath = ".")
   HRVData$Beat = filterHR(HRVData, minbpm = 25, maxbpm = 180)
   # interval without beats detected!!
   begindex = which(diff(HRVData$Beat$Time) > 30)
   beg = HRVData$Beat$Time[begindex]
   end = HRVData$Beat$Time[begindex + 1]
   if (length(begindex) > 0) {
       HRVData$BeatTimeInterval$begin = c(headTime, end)
       HRVData$BeatTimeInterval$end = c(beg, tailTime)
   }
#   niHR = HRVData$Beat$niHR
#   BeatTime = HRVData$Beat$Time
#   RR = HRVData$Beat$RR
#   index = (1:length(niHR))[niHR<25]
#   niHR = niHR[-index]
#   BeatTime = BeatTime[-index]
#   RR = RR[-index]
#   HRVData$Beat = data.frame(Time=BeatTime, niHR, RR) 

   headTime = head(HRVData$Beat$Time, n = 1)
   tailTime = tail(HRVData$Beat$Time, 1)

   if (startTime == 0 && endTime == 0) {
      startTime = headTime
      endTime = tailTime
   }

   if (!((startTime < endTime) && (startTime >= headTime && endTime <= tailTime))) {
      return(NULL)
   }

   WindowMin = 7500.17#3150.06 #startTime
   WindowMax = WindowMin + size
   WindowIndex = 1
   hrv.data = NULL
   hrv.data = CreateHRVData()
   hrv.data = SetVerbose(hrv.data, FALSE)

   hrv.data$Beat$Time = HRVData$Beat$Time[HRVData$Beat$Time >= WindowMin & HRVData$Beat$Time < WindowMax]
#   hrv.data = BuildNIHR(hrv.data)
   hrv.data$Beat = filterHR(hrv.data, minbpm=25, maxbpm=180)
   hrv.data = InterpolateNIHR (hrv.data, freqhr = 4)

   hrv.data = CreateTimeAnalysis(hrv.data, size = 100, interval = 7.8125)
   hrv.data = CreateFreqAnalysis(hrv.data)
      #hrv.data = CalculatePowerBand( hrv.data , indexFreqAnalysis= 1, size = 60, shift = 30, sizesp = 2048, type = "fourier", ULFmin = 0, ULFmax = 0.03, VLFmin = 0.03, VLFmax = 0.05, LFmin = 0.05, LFmax = 0.15, HFmin = 0.15, HFmax = 0.4 )

   hrv.data = CalculatePowerBand( hrv.data , indexFreqAnalysis= 1, type = "wavelet", wavelet = "la8", bandtolerance = 0.01, relative = FALSE, ULFmin = 0, ULFmax = 0.03, VLFmin = 0.03, VLFmax = 0.05, LFmin = 0.05, LFmax = 0.15, HFmin = 0.15, HFmax = 0.4 )
