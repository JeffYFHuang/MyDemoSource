#! /usr/bin/env Rscript

# mapper.R - Wordcount program in R
# script for Mapper (R-Hadoop integration)
require(rjson)

trimWhiteSpace <- function(line) gsub("(^ +)|( +$)", "", line)
splitIntoWords <- function(line) unlist(strsplit(line, "[[:space:]]+"))
filepath = Sys.getenv("map_input_file")
filename = tail(unlist(strsplit(filepath, split="/")), n=1)

#print(filepath)

## **** could wo with a single readLines or in blocks
con <- file("stdin", open = "r")
count <- 1
while (length(line <- readLines(con, n = 1, warn = FALSE)) > 0) {
    data <- fromJSON(line)
    ## **** can be done as cat(paste(words, "\t1\n", sep=""), sep="")
#    cat(filename, "\t", paste(line, collapse=" "), "\n", sep="")
#    print(data$uuid)
#    print(length(data$data))
    for (x in data$data) {
        cat(names(x), "\n")
        cat(unlist(x[which(names(x)!="hrm")]), "\n")

        for (hrm in x$hrm) {
            cat(names(hrm), "\n")
            cat(unlist(hrm), "\n")
        }
    }
    count <- count + 1
}

#cat(filepath, filename, count, "\n")
close(con)
