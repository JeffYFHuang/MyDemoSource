#! /usr/bin/env Rscript

# reducer.R - Wordcount program in R
# script for Reducer (R-Hadoop integration)
#library(rhbase)
library(rjson)
require(randomForest)
#load(url("http://172.18.161.250:50070/webhdfs/v1/models/randomforest.mod?op=OPEN"))
load("./src/randomforest.mod")

master = "10.0.0.5"
trimWhiteSpace <- function(line) gsub("(^ +)|( +$)", "", line)

splitLine <- function(line) {
    val <- unlist(strsplit(line, "\t"))
    list(subject = val[1], HRV = val[2])
}

getHRVData <- function (line) {
    split <- splitLine(line)
    subject <- trimws(split$subject)

 #   tryCatch({
 #      tables = hb.list.tables()
 #      if (!(subject %in% names(tables))) {
 #         print(word)
 #         hb.new.table(word, "info")
 #      }
 #   }, error = function(e) {
       #conditionMessage(e) # 這就會是"demo error"
 #      hb.init(master, 9090)
 #   })
    data = fromJSON(split$HRV)
#    data$label = predict(mod.fit, data)

 #   fields = paste("info", c(names(data)), sep=":")
 #   print(data$label)
 #   hb.insert(word, list(list(data$startTime, fields, data)))

 #   cat(paste(subject, "\t", paste(toJSON(data), collapse=" "), "\n", sep = ""))
    data
}

#env <- new.env(hash = TRUE)

#hb.init(master, 9090)
#tables = hb.list.tables()

con <- file("stdin", open = "r")

system.time(while (length(lines <- readLines(con, n = 1, warn = FALSE)) > 0) {
    split <- splitLine(lines[1])
    subject <- trimws(split$subject)
    hrv.data = fromJSON(split$HRV)
 #   print(hrv.data)
    #hrv.data <- lapply(as.list(lines), getHRVData)
 #   lapply(hrv.data, predictHRV)
    df <- data.frame(t(matrix(unlist(hrv.data), length(hrv.data[[1]]))))
    names(df) <- names(hrv.data[[1]])
    df <- cbind(df, label=predict(mod.fit, df))
 #   print(df)
    cat(paste(subject, "\t", paste(toJSON(df), collapse=" "), "\n", sep = ""))
 #   cat(subject, "\n")
})

close(con)
