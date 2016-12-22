#! /usr/bin/env Rscript

# mapper.R - Wordcount program in R
# script for Mapper (R-Hadoop integration)
#! /usr/bin/env Rscript

# reducer.R - Wordcount program in R
# script for Reducer (R-Hadoop integration)
Sys.setenv("HADOOP_CMD"="/media/Data/hadoop_ecosystem/hadoop/bin/hadoop")
Sys.setenv("HADOOP_STREAMING"="/media/Data/hadoop_ecosystem/hadoop/share/hadoop/tools/lib/hadoop-streaming-2.6.0.jar")

con <- file("stdin", open = "r")
while (length(line <- readLines(con, n = 1, warn = FALSE)) > 0) {
    cat(line)
}
close(con)
