#! /usr/bin/env Rscript

# mapper.R - Wordcount program in R
# script for Mapper (R-Hadoop integration)

source("HRVFUNS.R")
splitIntoWords <- function(line) unlist(strsplit(line, "\t"))

## **** could wo with a single readLines or in blocks
con <- file("apneahrv.dat", open = "r")
df.InEpisodes = NULL
df.OutEpisodes = NULL
while (length(line <- readLines(con, n = 1, warn = FALSE)) > 0) {
    words <- splitIntoWords(line)
    df = JsonToDataFrame(words[2])
    pos = charmatch(strsplit(words[1], "[.]")[[1]][2], c("beatsInEpisodes", "beatsOutEpisodes"))
    if (pos == 1) {
       df.InEpisodes <- rbind(df.InEpisodes, df)
    } else {
       df.OutEpisodes <- rbind(df.OutEpisodes, df)
    }
}
close(con)

    drops <- c("startTime","endTime")
    df.InEpisodes <- df.InEpisodes[, !(names(df.InEpisodes) %in% drops)]
    df.OutEpisodes <- df.OutEpisodes[, !(names(df.OutEpisodes) %in% drops)]
    print(df.InEpisodes)
    print(df.InEpisodes)
