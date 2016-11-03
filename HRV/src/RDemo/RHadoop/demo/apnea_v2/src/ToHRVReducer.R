#! /usr/bin/env Rscript

# reducer.R - Wordcount program in R
# script for Reducer (R-Hadoop integration)
library(rjson)

trimWhiteSpace <- function(line) gsub("(^ +)|( +$)", "", line)

splitLine <- function(line) {
    val <- unlist(strsplit(line, "\t"))
    list(word = val[1], HRV = val[2])
}

#env <- new.env(hash = TRUE)

con <- file("stdin", open = "r")
while (length(line <- readLines(con, n = 1, warn = FALSE)) > 0) {
    line <- trimWhiteSpace(line)
    split <- splitLine(line)
    word <- split$word
    #HRV <- fromJSON(split$HRV)
    #cat(paste(word, "\t", paste(split$HRV, collapse=" "), "\n", sep = ""))
    cat(split$HRV, "\n", sep = "")
}
close(con)
