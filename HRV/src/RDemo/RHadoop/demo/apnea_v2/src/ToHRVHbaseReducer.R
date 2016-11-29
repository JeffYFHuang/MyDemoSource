#! /usr/bin/env Rscript

# reducer.R - Wordcount program in R
# script for Reducer (R-Hadoop integration)
library(rhbase)
library(rjson)

master = "10.0.0.8"
trimWhiteSpace <- function(line) gsub("(^ +)|( +$)", "", line)

splitLine <- function(line) {
    val <- unlist(strsplit(line, "\t"))
    list(word = val[1], HRV = val[2])
}

#env <- new.env(hash = TRUE)

con <- file("stdin", open = "r")
line <- readLines(con, n = 1, warn = FALSE)
line <- trimWhiteSpace(line)
hb.init(master, 9090)
#tables = hb.list.tables()

while (length(line <- readLines(con, n = 1, warn = FALSE)) > 0) {
    line <- trimWhiteSpace(line)
    split <- splitLine(line)
    word <- trimws(split$word)

    tryCatch({
       tables = hb.list.tables()
       if (!(word %in% names(tables))) {
          hb.new.table(word, "info")
       }
    }, error = function(e) {
       #conditionMessage(e) # 這就會是"demo error"
       hb.init(master, 9090)
    })

    data = fromJSON(split$HRV)
    fields = paste("info", names(data), sep=":")
    hb.insert(word, list(list(data$startTime, fields, data)))
    #cat(paste(word, "\t", paste(split$HRV, collapse=" "), "\n", sep = ""))
    #cat(split$HRV, "\n", sep = "")
}
close(con)
