#! /usr/bin/env Rscript

# reducer.R - Wordcount program in R
# script for Reducer (R-Hadoop integration)
library(rjson)
require(rPython)

trimWhiteSpace <- function(line) gsub("(^ +)|( +$)", "", line)

RoundValues <- function (values) {
    fields <- c("situation", "datehour", "min", "max", "count", "duration", "hrmcount", "activeindex", "met", "type", "count", "distance", "cal", "status", "duration")

    values[which(names(values) != "uuid")] <- as.numeric(values[which(names(values) != "uuid")])
    values[names(values) %in% fields] <- round(as.numeric(values[names(values) %in% fields]))
    return(values[!is.na(values)])
}

getInsertCqlCmd <- function(tblname, values) {
    if (length(values) <= 0) return(NULL)

    values <- RoundValues(values)

    cqlcmd <- paste("INSERT INTO", tblname)
    colnames <- names(values)
    cqlcmd <- paste(cqlcmd, "(", paste(colnames, collapse=", "), ")")
    cqlcmd <- paste(cqlcmd, " VALUES (", paste(values[which(colnames != "uuid")], collapse=", "))
    cqlcmd <- paste(cqlcmd, ", '", values$uuid, "')", sep="")

    return(cqlcmd) 
}

#env <- new.env(hash = TRUE)
python.load("src/funcs.py", get.exception = T)

con <- file("stdin", open = "r")
while (length(line <- readLines(con, n = 1, warn = FALSE)) > 0) {
    data <- fromJSON(line)

    keyspace <- data$keyspace
    for (n in names(data)[-c(1, 2)]) {
        for (d in data[[n]]) {
           d$uuid <- data$uuid
           # for hour table
           cqlcmd <- getInsertCqlCmd(paste(keyspace, ".", n, "_hour", sep=""), d)
           #cat(cqlcmd, "\n")
           python.call("cqlexec", cqlcmd)
        }
    }    
    cat(line, "\n")
}
close(con)
