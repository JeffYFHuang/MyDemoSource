#! /usr/bin/env Rscript

# mapper.R - Wordcount program in R
# script for Mapper (R-Hadoop integration)

trimWhiteSpace <- function(line) gsub("(^ +)|( +$)", "", line)
splitIntoWords <- function(line) unlist(strsplit(line, "[[:space:]]+"))
filepath = Sys.getenv("map_input_file")
filename = tail(unlist(strsplit(filepath, split="/")), n=1)

## **** could wo with a single readLines or in blocks
con <- file("stdin", open = "r")
while (length(line <- readLines(con, n = 1, warn = FALSE)) > 0) {
    line <- trimWhiteSpace(line)
    #words <- as.double(splitIntoWords(line))
    ## **** can be done as cat(paste(words, "\t1\n", sep=""), sep="")
    cat(filename, "\t", line, "\n", sep="")
    #cat("filename", "\t", paste(words, collapse=" "), "\n", sep="")
}
close(con)
