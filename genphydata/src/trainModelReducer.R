#! /usr/bin/env Rscript

# reducer.R - Wordcount program in R
# script for Reducer (R-Hadoop integration)
library(rhbase)
library(rjson)

trimWhiteSpace <- function(line) gsub("(^ +)|( +$)", "", line)

splitLine <- function(line) {
    val <- unlist(strsplit(line, "\t"))
    list(word = val[1], HRV = val[2])
}

#env <- new.env(hash = TRUE)

con <- file("stdin", open = "r")
line <- readLines(con, n = 1, warn = FALSE)
line <- trimWhiteSpace(line)

while (length(line <- readLines(con, n = 1, warn = FALSE)) > 0) {
    print(line)
    line <- trimWhiteSpace(line)
    split <- splitLine(line)
    word <- trimws(split$word)

    data = fromJSON(split$HRV)
}
close(con)
