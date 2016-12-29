require(compiler)
compilePKGS(TRUE)
enableJIT(3)
require(RHRV)
require(rhdfs)
require(rjson)
require(rmr2)

#lapply <- cmpfun(lapply)
#mapply <- cmpfun(mapply)

SplitBeatsbyEpisodes <- function(HRVData) {
    if (HRVData$Verbose) {
        cat("** Splitting heart rate signal using episodes **\n")
    }
    if (is.null(HRVData$Episodes)) {
        stop("  --- Episodes not present\n    --- Quitting now!! ---\n")
    }
    if (is.null(HRVData$Beat)) {
        stop("  --- Beat Time not present\n    --- Quitting now!! ---\n")
    } 
     ActiveEpisodes = HRVData$Episodes

     Beg = ActiveEpisodes$InitTime
     End = ActiveEpisodes$InitTime + ActiveEpisodes$Duration
     npoints = length(HRVData$Beat$Time)
     first = head(HRVData$Beat$Time, 1)
     last = tail(HRVData$Beat$Time, 1)
#     x = seq(first, last, length.out = npoints)
     Aux = rep(0, times = npoints)
     for (i in 1:length(Beg)) {
         Aux[HRVData$Beat$Time >= Beg[i] & HRVData$Beat$Time <= End[i]] = 1
     }
     l = list(InEpisodes = HRVData$Beat$Time[Aux == 1], OutEpisodes = HRVData$Beat$Time[Aux == 0])
     if (HRVData$Verbose) {
        cat("   Inside episodes:", length(l$InEpisodes), "points\n")
        cat("   Outside episodes:", length(l$OutEpisodes), "points\n")
     }
     return(l)
}

#SplitBeatsbyEpisodes <- cmpfun(SplitBeatsbyEpisodes)

CaculateApEn <- function (HRVData, m = 2, r = 0.2, SDNN = 1) {
     return(phi(HRVData, m, r, SDNN) - phi(HRVData, m+1, r, SDNN))
}

#CaculateApEn <- cmpfun(CaculateApEn)

phi <- function (HRVData, m = 2, r = 0.2, SDNN = 1) {
     U = HRVData$Beat$RR
     N = length(U)
     dm = array(TRUE, c(N-m+1, N-m+1))
     for (k in 1:m) {
         end = N-m+(k-1)+1
         ViK = array(c(rep(U[k:end], N-m+1)), c(N-m+1, N-m+1))
         VjK = t(ViK)
         dm = dm & (abs(VjK - ViK) < r*SDNN)
     }
     return(sum(log(rowSums(dm, na.rm=TRUE)/(N-m+1)))/N-m+1)
}

#phi <- cmpfun(phi)

filterHR <- function (HRVData, minbpm = 25, maxbpm = 200, windowsize = 300) {
   if (length(HRVData$Beat$Time) == 0)
       return(NULL)

   headTime = head(HRVData$Beat$Time, n = 1)
   tailTime = tail(HRVData$Beat$Time, 1)

   if ((tailTime - headTime < windowsize/2))
      return(NULL)

   HRVData = BuildNIHR(HRVData)
   #HRVData = FilterNIHR(HRVData, long=50, last=10, minbpm=minbpm, maxbpm=maxbpm)
   niHR = HRVData$Beat$niHR
   BeatTime = HRVData$Beat$Time
   RR = HRVData$Beat$RR
   index = (1:length(niHR))[niHR<minbpm | niHR>maxbpm]
   if(length(index) == 0) return(HRVData$Beat)
   niHR = niHR[-index]
   BeatTime = BeatTime[-index]
   RR = RR[-index]
   HRVData$Beat = data.frame(Time=BeatTime, niHR, RR)
}

#filterHR <- cmpfun(filterHR)

SplitBeatDataByDuration <- function(HRVData) {
    beatLists = mapply(beatDataInDuration, HRVData$BeatTimeInterval$begin, HRVData$BeatTimeInterval$end, MoreArgs=list(beats=HRVData$Beat$Time))
    if (!is.list(beatLists))
       beatLists = list(beatLists)
    return(beatLists)
}

#SplitBeatDataByDuration <- cmpfun(SplitBeatDataByDuration)

beatDataInDuration <- function(beats, startTime, endTime) {
    beats = beats[beats >= startTime & beats < endTime]
    return(as.vector(beats))
}

#beatDataInDuration <- cmpfun(beatDataInDuration)

getWindowsBeatData <- function(beats, windowsize = 300, shift = 300) {
    if (length(beats) == 0)
       return(NULL)

    headTime = head(beats, n = 1)
    tailTime = tail(beats, n = 1)

    #if ((tailTime - headTime <= windowsize))
    #   return(list(beats))

    beginTimes = seq(headTime, tailTime, shift)
    beginTimes = beginTimes[-tail(beginTimes, n=1)]
    endTimes = beginTimes + windowsize
    winsBeatData = mapply(beatDataInDuration, beginTimes, endTimes, MoreArgs=list(beats=beats))
    if (!is.list(winsBeatData))
       winsBeatData = list(winsBeatData)
    return(winsBeatData)
}

#getWindowsBeatData <- cmpfun(getWindowsBeatData)

#BuildNIHR <- cmpfun(BuildNIHR)
#InterpolateNIHR <- cmpfun(InterpolateNIHR)
#CreateTimeAnalysis <- cmpfun(CreateTimeAnalysis)
#CreateFreqAnalysis <- cmpfun(CreateFreqAnalysis)
#CalculatePowerBand <- cmpfun(CalculatePowerBand)
#CreateNonLinearAnalysis <- cmpfun(CreateNonLinearAnalysis)
#PoincarePlot <- cmpfun(PoincarePlot)

calculateHRV <- function(beats, windowsize = 300, class = NULL) {
    result = list()

    tryCatch({
       headTime = head(beats, n = 1)
       tailTime = tail(beats, n = 1)

       hrv.data = NULL
       hrv.data = CreateHRVData()
       hrv.data = SetVerbose(hrv.data, FALSE)

       hrv.data$Beat$Time = beats
       hrv.data = BuildNIHR(hrv.data)
 #      hrv.data = FilterNIHR(hrv.data, long=50, last=10, minbpm=25, maxbpm=180)
 #      hrv.data$Beat = filterHR(hrv.data)
       hrv.data = InterpolateNIHR (hrv.data, freqhr = 4)
       hrv.data = CreateTimeAnalysis(hrv.data, size = 300, interval = 7.8125) #size = 100
       hrv.data = CreateFreqAnalysis(hrv.data)
 #   hrv.data = CalculatePowerBand( hrv.data , indexFreqAnalysis= 1, size = 60, shift = 30, sizesp = 2048, type = "fourier", ULFmin = 0, ULFmax = 0.03, VLFmin = 0.03, VLFmax = 0.05, LFmin = 0.05, LFmax = 0.15, HFmin = 0.15, HFmax = 0.4 )
       hrv.data = CalculatePowerBand( hrv.data , indexFreqAnalysis= 1, type = "wavelet", wavelet = "la8", bandtolerance = 0.01, relative = FALSE, ULFmin = 0, ULFmax = 0.03, VLFmin = 0.03, VLFmax = 0.05, LFmin = 0.05, LFmax = 0.15, HFmin = 0.15, HFmax = 0.4 )
       hrv.data = CreateNonLinearAnalysis(hrv.data, verbose=NULL)
    #   hrv.data = NonlinearityTests(hrv.data)
    #   hrv.data = SurrogateTest(hrv.data, significance = 0.05,
    #                   useFunction = timeAsymmetry2, tau=4, doPlot = FALSE)
       hrv.data = PoincarePlot(hrv.data, indexNonLinearAnalysis=1, timeLag=1,
                      confidenceEstimation = TRUE,confidence = 0.9, doPlot=FALSE)

       result$startTime = hrv.data$Beat$Time[1]
       result$endTime = hrv.data$Beat$Time[length(hrv.data$Beat$Time)]
       #Time Domain
       result$SDNN = hrv.data$TimeAnalysis[[1]]$SDNN
       #result$SDANN = hrv.data$TimeAnalysis[[1]]$SDANN
       result$SDNNIDX = hrv.data$TimeAnalysis[[1]]$SDNNIDX
       result$pNN50 = hrv.data$TimeAnalysis[[1]]$pNN50
       result$SDSD = hrv.data$TimeAnalysis[[1]]$SDSD
       result$rMSSD = hrv.data$TimeAnalysis[[1]]$rMSSD
       result$IRRR = hrv.data$TimeAnalysis[[1]]$IRRR
       result$MADRR = hrv.data$TimeAnalysis[[1]]$MADRR
       result$TINN = hrv.data$TimeAnalysis[[1]]$TINN
       result$HRVi = hrv.data$TimeAnalysis[[1]]$HRVi
       RRDiffs = diff(hrv.data$Beat$RR)
       result$NN50 = length(RRDiffs[abs(RRDiffs) > 50])
       result$meanRR = mean(hrv.data$Beat$RR)
       result$meanHR = mean(hrv.data$Beat$niHR)
       #Frequency Domain
       result$ULF = mean(hrv.data$FreqAnalysis[[1]]$ULF)
       result$VLF = mean(hrv.data$FreqAnalysis[[1]]$VLF)
       result$LF = mean(hrv.data$FreqAnalysis[[1]]$LF)
       result$HF = mean(hrv.data$FreqAnalysis[[1]]$HF)
       result$LFHF = mean(hrv.data$FreqAnalysis[[1]]$LFHF)
       result$TP = result$VLF + result$LF + result$HF
       result$LFnu = result$LF/(result$TP - result$VLF)
       result$HFnu = result$HF/(result$TP - result$VLF)
       #NonlinearAnalysis
       result$SD1 = hrv.data$NonLinearAnalysis[[1]]$PoincarePlot$SD1
       result$SD2 = hrv.data$NonLinearAnalysis[[1]]$PoincarePlot$SD2
       result$SD12 = result$SD1/result$SD2
       #Approxiation Entropy
       #result$ApEn = CaculateApEn(hrv.data, m = 2, r = 0.2, SDNN = result$SDNN)
       if (!is.null(class))
          result$inEpisodes = class
    }, error = function(e) {
       #conditionMessage(e) # 這就會是"demo error"
       return(NULL)
    })
 
    return(result)
}

#calculateHRV <- cmpfun(calculateHRV)

splitWindowData <- function(HRVData, windowsize = 300, shift = 300) {
    winsBeatData = getWindowsBeatData(HRVData, windowsize = 300, shift = 300)
    winsBeatData = winsBeatData[lengths(winsBeatData) > 120]
    return(winsBeatData)
}

#splitWindowData <- cmpfun(splitWindowData)

getSplitWindowData <- function(HRVData, windowsize = 300, shift = 300) {
    beatLists = SplitBeatDataByDuration(HRVData)
    data = lapply(beatLists, splitWindowData, windowsize = 300, shift = 300)
    if (is.list(unlist(data, recursive = FALSE)))
       data = unlist(data, recursive = FALSE)
    return(data)
}

#getSplitWindowData <- cmpfun(getSplitWindowData)
#filterHR <- cmpfun(filterHR)

createFilterHRVData <- function(data) {
  HRVData = CreateHRVData()
  HRVData = SetVerbose(HRVData, FALSE)
  HRVData$Beat$Time = sort(data)
  HRVData$Beat = filterHR(HRVData, minbpm = 25, maxbpm = 180)
  if (is.null(HRVData$Beat))
     next
  HRVData$BeatTimeInterval$begin = head(HRVData$Beat$Time, n = 1)
  HRVData$BeatTimeInterval$end = tail(HRVData$Beat$Time, 1)

  begindex = which(diff(HRVData$Beat$Time) > 30)
  beg = HRVData$Beat$Time[begindex]
  end = HRVData$Beat$Time[begindex + 1]

  if (length(begindex) > 0) {
     HRVData$BeatTimeInterval$begin = c(HRVData$BeatTimeInterval$begin, end)
     HRVData$BeatTimeInterval$end = c(beg, HRVData$BeatTimeInterval$end)
  }
  HRVData
}

createFilterHRVData <- cmpfun(createFilterHRVData)

outputBeatsHRV <- function(beats, subject) {
    mod.fit = NULL
    HRV = calculateHRV(beats)
    pos = charmatch(strsplit(subject, "[.]")[[1]][2], c("InEpisodes", "OutEpisodes"))

    HRV$label = NA

    if (!is.na(pos)) {
       if (pos == 1) {
          HRV$label = 1
       } else if (pos == 2){
          HRV$label = 0
       }
    }

    ## **** can be done as cat(paste(words, "\t1\n", sep=""), sep="")
    cat(strsplit(subject, "[.]")[[1]][1], "\t", toJSON(HRV), "\n", sep="")
}

beats2HRV <- function(beats, subject) {
    mod.fit = NULL
    HRV = calculateHRV(beats)
    pos = charmatch(strsplit(subject, "[.]")[[1]][2], c("InEpisodes", "OutEpisodes"))

    HRV$label = NA

    if (!is.na(pos)) {
       if (pos == 1) {
          HRV$label = 1
       } else if (pos == 2){
          HRV$label = 0
       }
    }

    HRV
}

#outputBeatsHRV <- cmpfun(outputBeatsHRV)

outputWindowBeat <- function(data, subject) {
  cat(subject, "\t", paste(unlist(data), collapse=" "), "\n", sep="")
} 

#outputWindowBeat <- cmpfun(outputWindowBeat)

getSplitWindowBeats<-function(data, windowsize = 300, shift = 300, toHRV = F) {
  if (length(data$Beat) < 120)
     return(NULL)

  headTime = head(data$Beat, n = 1)
  tailTime = tail(data$Beat, n = 1)

  if ((tailTime - headTime) <= windowsize)
     return(list(data$Beat))

  HRVData = createFilterHRVData(data$Beat)
  beatLists = getSplitWindowData(HRVData, windowsize = 300, shift = 300)

  HRV = list()
  #if (toHRV==2) HRV = lapply(beatLists, beats2HRV, subject = data$Subject)

  if (toHRV)
     HRV = lapply(beatLists, outputBeatsHRV, subject = data$Subject)
  else
     lapply(beatLists, outputWindowBeat, subject = data$Subject)

  HRV
}

#getSplitWindowBeats <- cmpfun(getSplitWindowBeats)

getHRVData <- function(beats, windowsize = 300, shift = 300, class = NULL) {
    winsBeatData = getWindowsBeatData(beats, windowsize = 300, shift = 300)
    winsBeatData = winsBeatData[lengths(winsBeatData) > 0]
    HRVData = lapply(winsBeatData, calculateHRV, windowsize = windowsize, class)
    return(HRVData)
}

getHRVFeatures <- function(HRVData, windowsize = 300, shift = 300, class = NULL) {
    beatLists = SplitBeatDataByDuration(HRVData)
    results = list()
    results$shift = shift
    results$windowsize = windowsize
    results$CalculatedData = NULL
    results$CalculatedData = lapply(beatLists, getHRVData, windowsize = 300, shift = 300, class)
    if (is.list(unlist(results$CalculatedData, recursive = FALSE)))
       results$CalculatedData = unlist(results$CalculatedData, recursive = FALSE)
    #results$CaculatedData = getHRVData(HRVData$Beat$Time)
    results$CalculatedData = results$CalculatedData[lengths(results$CaculatedData) > 0]
    return(results)
}

getHRVFeatures_old <- function(HRVData, startTime = 0, endTime = 0, windowsize = 300, overlap = 0, mod.fit = NULL) {
   headTime = head(HRVData$Beat$Time, n = 1)
   tailTime = tail(HRVData$Beat$Time, 1)
   if (startTime == 0 && endTime == 0) {
      startTime = headTime
      endTime = tailTime
   }

   if (!((startTime < endTime) && (startTime >= headTime && endTime <= tailTime))) {
      return(NULL)
   }

   results = list()
   results$overlap = overlap
   results$size = size
   results$CalculatedData = list()
 
   WindowIndex = 1
   for (i in 1:length(HRVData$BeatTimeInterval$begin)) {
   startTime = HRVData$BeatTimeInterval$begin[i]
   endTime = HRVData$BeatTimeInterval$end[i]
#   cat("time interval(", i , "):", startTime, "->", endTime, "\n")
   WindowMin = startTime
   WindowMax = WindowMin + size
   while (WindowMax < endTime) {
   #   print(WindowMin)
      hrv.data = NULL
      hrv.data = CreateHRVData()
      hrv.data = SetVerbose(hrv.data, FALSE)

      hrv.data$Beat$Time = HRVData$Beat$Time[HRVData$Beat$Time >= WindowMin & HRVData$Beat$Time < WindowMax]
      hrv.data = BuildNIHR(hrv.data)
   #   hrv.data = FilterNIHR(hrv.data, long=50, last=10, minbpm=25, maxbpm=180)
   #   hrv.data$Beat = filterHR(hrv.data)
      hrv.data = InterpolateNIHR (hrv.data, freqhr = 4)

      hrv.data = CreateTimeAnalysis(hrv.data, size = 100, interval = 7.8125)
      hrv.data = CreateFreqAnalysis(hrv.data)
      #hrv.data = CalculatePowerBand( hrv.data , indexFreqAnalysis= 1, size = 60, shift = 30, sizesp = 2048, type = "fourier", ULFmin = 0, ULFmax = 0.03, VLFmin = 0.03, VLFmax = 0.05, LFmin = 0.05, LFmax = 0.15, HFmin = 0.15, HFmax = 0.4 )
      
      hrv.data = CalculatePowerBand( hrv.data , indexFreqAnalysis= 1, type = "wavelet", wavelet = "la8", bandtolerance = 0.01, relative = FALSE, ULFmin = 0, ULFmax = 0.03, VLFmin = 0.03, VLFmax = 0.05, LFmin = 0.05, LFmax = 0.15, HFmin = 0.15, HFmax = 0.4 )

      hrv.data = CreateNonLinearAnalysis(hrv.data, verbose=NULL)
      hrv.data = NonlinearityTests(hrv.data)
      hrv.data = SurrogateTest(hrv.data, significance = 0.05,
                         useFunction = timeAsymmetry2, tau=4, doPlot = FALSE)
      hrv.data = PoincarePlot(hrv.data, indexNonLinearAnalysis=1, timeLag=1,
                        confidenceEstimation = TRUE,confidence = 0.9, doPlot=FALSE)

      result = list()
      result$startTime = hrv.data$Beat$Time[1]
      result$endTime = hrv.data$Beat$Time[length(hrv.data$Beat$Time)]

      #Time Domain
      result$SDNN = hrv.data$TimeAnalysis[[1]]$SDNN
      result$SDANN = hrv.data$TimeAnalysis[[1]]$SDANN
      result$SDNNIDX = hrv.data$TimeAnalysis[[1]]$SDNNIDX
      result$pNN50 = hrv.data$TimeAnalysis[[1]]$pNN50
      result$SDSD = hrv.data$TimeAnalysis[[1]]$SDSD
      result$rMSSD = hrv.data$TimeAnalysis[[1]]$rMSSD
      result$IRRR = hrv.data$TimeAnalysis[[1]]$IRRR
      result$MADRR = hrv.data$TimeAnalysis[[1]]$MADRR
      result$TINN = hrv.data$TimeAnalysis[[1]]$TINN
      result$HRVi = hrv.data$TimeAnalysis[[1]]$HRVi
      RRDiffs = diff(hrv.data$Beat$RR)
      result$NN50 = length(RRDiffs[abs(RRDiffs) > 50])
      result$meanRR = mean(hrv.data$Beat$RR)
      result$meanHR = mean(hrv.data$Beat$niHR)

      #Frequency Domain
      result$ULF = mean(hrv.data$FreqAnalysis[[1]]$ULF)
      result$VLF = mean(hrv.data$FreqAnalysis[[1]]$VLF)
      result$LF = mean(hrv.data$FreqAnalysis[[1]]$LF)
      result$HF = mean(hrv.data$FreqAnalysis[[1]]$HF)
      result$LFHF = mean(hrv.data$FreqAnalysis[[1]]$LFHF)
      result$TP = result$VLF + result$LF + result$HF
      result$LFnu = result$LF/(result$TP - result$VLF)
      result$HFnu = result$HF/(result$TP - result$VLF)

      #NonlinearAnalysis
      result$SD1 = hrv.data$NonLinearAnalysis[[1]]$PoincarePlot$SD1
      result$SD2 = hrv.data$NonLinearAnalysis[[1]]$PoincarePlot$SD2
      result$SD12 = result$SD1/result$SD2

      #Approxiation Entropy
      result$ApEn = CaculateApEn(hrv.data, m = 2, r = 0.2, SDNN = result$SDNN)
      if (!is.null(mod.fit))
         result$InEpisodes = predict(mod.fit, newdata = result)

      results$CalculatedData[[WindowIndex]] = result

      WindowMin = ifelse(overlap == 0, WindowMin + size, WindowMin + overlap)
      WindowMax = ifelse(overlap == 0, WindowMax + size, WindowMin + size)
      WindowIndex = WindowIndex + 1
   }
   }
   return(results)
}

getHRVJSONData<-function(data) {
  HRVData = CreateFilterHRVData()

  features = getHRVFeatures(HRVData)
  jsonData<-toJSON(features)
  jsonData
}

connectHDFSBeatsFile <- function (filePath = NULL) {
   if (is.null(filePath)) {
      stop("no beats hdfs file path!")
   }

   f = hdfs.file(filePath,"r")
   c = "";
   repeat {
      m = hdfs.read(f)
      if(!is.vector(m)) break

      c = paste(c, rawToChar(m), sep="")
     
      if ( is.null(c) ) break
   }
   return(textConnection(c))
}

writeJsonToHDFS <- function (jsonData = NULL, path = NULL, fileName = NULL) {
   if (is.null(path) || is.null(fileName)) {
      stop ("path or fileName is null!")
   }
   
   if (!is.null(path) && !hdfs.exists(path)) {
      hdfs.mkdir(path)
   }

   filePath = file.path(path, fileName)
   if (!is.null(fileName) && hdfs.exists(filePath)) {
      hdfs.rm(filePath)
   }

   hdfsFile <- hdfs.file(filePath, "w")
   hdfs.write(jsonData, hdfsFile)
   hdfs.close(hdfsFile)
   #hdfs.put(fileName, filePath)
}

readJsonFromHDFS <- function (path = NULL, fileName = NULL) {
   if (is.null(path) || is.null(fileName)) {
      stop ("path or fileName is null!")
   }

   if (!is.null(path) && !hdfs.exists(path)) {
      stop ("path is not exist!")
   }

   filePath = file.path(path, fileName)
   if (!is.null(fileName) && !hdfs.exists(filePath)) {
      stop ("filePath is not exist!")
   }

   f = hdfs.file(filePath, "r")

   c = NULL;
   repeat {
     m = hdfs.read(f)
     if(!is.vector(m)) break
     c = append(c, m)
     
     #if ( is.null(c) ) break
   }
 
   return(unserialize(c))
}

putJsonToHDFS <- function (jsonData = NULL, path = NULL, fileName = NULL) {
   if (is.null(path) || is.null(fileName)) {
      stop ("path or fileName is null!")
   }

   if (!is.null(path) && !hdfs.exists(path)) {
      hdfs.mkdir(path)
   }

   filePath = file.path(path, fileName)
   if (!is.null(fileName) && hdfs.exists(filePath)) {
      hdfs.rm(filePath)
   }

   # Write/creat a json file in local
   writeLines(jsonData, paste(fileName, ".hrv", sep=""))
   # Put local Json file into hdfs
   hdfs.put(fileName, filePath)
   # Remove local Json file
   if (file.exists(fileName)) 
      file.remove(fileName)
}

readJsonDataFromHDFS <- function (path = NULL, fileName = NULL) {
   if (is.null(path) || is.null(fileName)) {
      stop ("path or fileName is null!")
   }

   if (!is.null(path) && !hdfs.exists(path)) {
      stop ("path is not exist!")
   }

   filePath = file.path(path, fileName)
   if (!is.null(fileName) && !hdfs.exists(filePath)) {
      stop ("filePath is not exist!")
   }

   jsonText = hdfs.read.text.file(filePath)
   jsonData = JsonToDataFrame(jsonText)
   #json_data <- fromJSON(jsonText, collapse="")
   #size = length(unlist(json_data$CaculatedData))
   #row.size = length(unlist(json_data$CaculatedData[[1]]))
   #col.names = names(json_data$CaculatedData[[1]])
   #HRV.Features = t(array(as.numeric(unlist(json_data$CaculatedData)), c(row.size, size/row.size)))
   #HRV.Features = data.frame(HRV.Features)
   #colnames(HRV.Features) = col.names
}

json.from.dfs <- function (path = NULL, fileName = NULL) {
   if (is.null(path) || is.null(fileName)) {
      stop ("path or fileName is null!")
   }

   if (!is.null(path) && !hdfs.exists(path)) {
      stop ("path is not exist!")
   }

   filePath = file.path(path, fileName)
   if (!is.null(fileName) && !hdfs.exists(filePath)) {
      stop ("filePath is not exist!")
   }

   jsonText = from.dfs(filePath)$val
   jsonData = JsonToDataFrame(jsonText)
   #json_data <- fromJSON(jsonText, collapse="")
   #size = length(unlist(json_data$CaculatedData))
   #row.size = length(unlist(json_data$CaculatedData[[1]]))
   #col.names = names(json_data$CaculatedData[[1]])
   #HRV.Features = t(array(as.numeric(unlist(json_data$CaculatedData)), c(row.size, size/row.size)))
   #HRV.Features = data.frame(HRV.Features)
   #colnames(HRV.Features) = col.names
}

JsonToDataFrame <- function (jsonText = NULL) {
   jsonData <- fromJSON(jsonText)
   size = length(unlist(jsonData$CalculatedData))
   row.size = length(unlist(jsonData$CalculatedData[[1]]))
   col.names = names(jsonData$CalculatedData[[1]])
   HRV.Features = t(array(as.numeric(unlist(jsonData$CalculatedData)), c(row.size, size/row.size)))
   HRV.Features = data.frame(HRV.Features)
   colnames(HRV.Features) = col.names
   return(HRV.Features)
}

putBeatsDataToHDFS <- function (HRVData, path = NULL, fileName = NULL) {
   if (is.null(path) || is.null(fileName)) {
      stop ("path or fileName is null!")
   }

   if (!is.null(path) && !hdfs.exists(path)) {
      hdfs.mkdir(path)
   }

   fileName = paste(fileName, ".beats", sep = "")
   filePath = file.path(path, fileName)
   if (!is.null(fileName) && hdfs.exists(filePath)) {
      hdfs.rm(filePath)
   }

   # Write/creat a json file in local
   write(HRVData$Beat$Time, fileName, sep="\n")
   # Put local Json file into hdfs
   hdfs.put(fileName, filePath)
   # Remove local Json file
   if (file.exists(fileName))
      file.remove(fileName)
}

putEpisodesDataToHDFS <- function (HRVData, path = NULL, fileName = NULL) {
   if (is.null(path) || is.null(fileName)) {
      stop ("path or fileName is null!")
   }

   if (!is.null(path) && !hdfs.exists(path)) {
      hdfs.mkdir(path)
   }

   fileName = paste(fileName, ".apn", sep = "")
   filePath = file.path(path, fileName)
   if (!is.null(fileName) && hdfs.exists(filePath)) {
      hdfs.rm(filePath)
   }

   # Write/creat a json file in local
   write.table(HRVData$Episodes, fileName, sep=" ")
   # Put local Json file into hdfs
   hdfs.put(fileName, filePath)
   # Remove local Json file
   if (file.exists(fileName))
      file.remove(fileName)
}

readEpisodesDataFromHDFS <- function (path, fileName) {
   if (is.null(path) || is.null(fileName)) {
      stop ("path or fileName is null!")
   }

   if (!is.null(path) && !hdfs.exists(path)) {
      stop ("path is not exist!")
   }

   filePath = file.path(path, fileName)
   if (!is.null(fileName) && !hdfs.exists(filePath)) {
      stop ("filePath is not exist!")
   }

   table.data = hdfs.read.text.file(filePath)
   table = read.table(textConnection(table.data))
   return(table)
}
