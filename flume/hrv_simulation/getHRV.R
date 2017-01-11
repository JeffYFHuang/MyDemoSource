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
