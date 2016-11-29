library(rmr2) 

## map function
map <- function(k,lines) {
  fileName = Sys.getenv("map_input_file")
  words.list <- strsplit(lines, '\\s') 
  words <- unlist(words.list)
  rmr.str(fileName)
  return( keyval(words, 1) )
}

## reduce function
reduce <- function(word, counts) { 
  keyval(word, sum(counts))
}

wordcount <- function (input, output=NULL) { 
  mapreduce(input=input, output=output, input.format="text", 
            map=map, reduce=reduce)
}


## delete previous result if any
system("/Users/hadoop/hadoop/bin/hadoop fs -rmr wordcount/out")

## Submit job
hdfs.root <- 'wordcount'
hdfs.data <- file.path(hdfs.root, 'data') 
hdfs.out <- file.path(hdfs.root, 'out') 
out <- wordcount(hdfs.data, hdfs.out)

## Fetch results from HDFS
results <- from.dfs(out)

## check top 30 frequent words
results.df <- as.data.frame(results, stringsAsFactors=F) 
colnames(results.df) <- c('word', 'count') 
head(results.df[order(results.df$count, decreasing=T), ], 30)
