#! /usr/bin/env Rscript

# reducer.R - Wordcount program in R
# script for Reducer (R-Hadoop integration)
#library(rhbase)
library(rjson)
require(randomForest)
#load(url("http://172.18.161.250:50070/webhdfs/v1/models/randomforest.mod?op=OPEN"))

splitLine <- function(line) {
    val <- unlist(strsplit(line, "\t"))
    list(subject = val[1], HRV = val[2])
}

getHRVData <- function (line) {
    split <- splitLine(line)
    subject <- trimws(split$subject)

    data = fromJSON(split$HRV)
    data
}

con <- file("stdin", open = "r")

system.time(while (length(lines <- readLines(con, n = 144, warn = FALSE)) > 0) {
    split <- splitLine(lines[1])
    subject <- trimws(split$subject)
    hrv.data <- lapply(as.list(lines), getHRVData)
    df <- data.frame(t(matrix(unlist(hrv.data), length(hrv.data[[1]]))))
    names(df) <- names(hrv.data[[1]])
    df <- cbind(df, label=predict(mod.fit, df))
 #   print(df)
    cat(paste(subject, "\t", paste(toJSON(df), collapse=" "), "\n", sep = ""))
})

close(con)
