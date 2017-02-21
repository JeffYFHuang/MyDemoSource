predictHRV <- function (x) {
   begin = Sys.time()
   y = splitLine(x)
   df = NULL
   #return(keyval(y$Subject, path))
   data <- fromJSON(y$data)

   if (!is.null(data$Beat)) {
      # beats to HRV
   #   df = getHRVfromJSON(beat2HRV(y$Subject, data))
      jsonData = getHRVJSONData(data$Beat)
      df = JsonToDataFrame(jsonData)
   } else {
      # just HRV
      df = getHRVfromJSON(data)
   }
   df = na.omit(df)
   #print(rfs)

   predict_inner <- function (rf, df) {
      predlabel <- predict(rf, df[, features], type = "class") #which(names(df)!="label")], type = "class")
      as.numeric(predlabel) - 1
   }

   predict_all <- function (rfs, df) {
      label_list <- lapply(rfs, predict_inner, df)
      n.row = length(label_list)
      n.col = length(label_list[[1]])
      m <- t(matrix(unlist(label_list), n.col, n.row))
      round(colSums(m)/n.row)
#      m
   }

#   models.dfs.path = "/data/train_output"
#   path = dfs.ls(models.dfs.path)$path

#   predict_path <- function (df) {
#      m = NULL
#      for (f in path[2:3]) {
#          rfs = NULL
#          rf = from.dfs(f)
#          for (v in rf$val){
#              rfs = c(rfs, list(v$mod))
#          }
#          m = predict_all(rfs, df)
#      }
#      round(colSums(m)/n.row)
#   }

   df$predlabel = predict_all(rfs, df) #lapply(rfs, predict_inner, df)
   #print(df$predlabel)
   accuracy = NULL
   if (!is.null(df$label)) {
   #   print(confusionMatrix(df$label, df$predlabel))
      accuracy = length(which(df$label==df$predlabel))/length(df$predlabel)
   }
   kv <- keyval(y$Subject, toJSON(list(startTime=df$startTime[1], origLabel=df$label, predLabel=as.numeric(df$predlabel), accuracy=accuracy)))
#   cat(unlist(kv), "\n")
   kv
}

predictLabel <- function(key, input) {

   getData <- function(line) {
      split <- splitLine(line)
      hrv.data = fromJSON(split$data)
      df <- data.frame(t(matrix(unlist(hrv.data), length(hrv.data[[1]]))))
      df <- cbind(split$Subject, df)
      names(df) <- c("subject", names(hrv.data[[1]]))
      df
   }

#   data <- NULL
#   for (i in 1:length(input)) {
#      data <- rbind(data, getData(input[i]))
#   }

  # predictHRV(data)
   c.keyval(lapply(input, predictHRV))
}
