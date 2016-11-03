#! /usr/bin/env Rscript

# mapper.R - Wordcount program in R
# script for Mapper (R-Hadoop integration)

source("HRVFUNS.R")
require(caret)
splitIntoWords <- function(line) unlist(strsplit(line, "\t"))

## **** could wo with a single readLines or in blocks
con <- file("apneahrv.dat", open = "r")
result = list()
while (length(line <- readLines(con, n = 1, warn = FALSE)) > 0) {
    words <- splitIntoWords(line)
    df = JsonToDataFrame(words[2])
    load("knn.mod")
    df$type <- predict(knnFit, newdata=df)
    jsonData<-toJSON(df, method="R")
    cat(words[1], "\t", jsonData, "\n", sep = "")
}
close(con)
