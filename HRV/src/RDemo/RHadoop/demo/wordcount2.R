#!/usr/bin/env Rscript

#
# Example 1: wordcount
#
# Tally the number of occurrences of each word in a text
#
# from https://github.com/RevolutionAnalytics/RHadoop/blob/master/rmr2/docs/tutorial.md
#
source("HRVFUNS.R")
startTime = 0
endTime = 0
size=300 #seconds
overlap = 0 #seconds: 0 -> non-overlap;
hdfs.root <- '/HRVData'
hdfs.data <- file.path(hdfs.root, 'physiobank')
hdfs.out <- file.path(hdfs.root, 'out')
#hdfs.init()
#hdfs.rm(hdfs.out)
# Set "LOCAL" variable to T to execute using rmr2's local backend.
# Otherwise, use Hadoop (which needs to be running, correctly configured, etc.)

LOCAL=F

if (LOCAL)
{
	rmr.options(backend = 'local')
} else {
	rmr.options(backend = 'hadoop')
}

map.wc = function(k,lines) {
        fileName = Sys.getenv("map_input_file")
        fileName = tail(unlist(strsplit(fileName, split="/")), n=1)
	words.list = strsplit(lines, '\\s+')		# use '\\W+' instead to exclude punctuation
	words = unlist(words.list)

	return( keyval(fileName, 1) )
}

reduce.wc = function(word,counts) {
	
	return( keyval(word, sum(counts)) )
}

wordcount = function (input, output = NULL) {
	mapreduce(input = input ,
			  output = output,
			  input.format = "text",
			  map = map.wc,
			  reduce = reduce.wc,
			  combine = T)}

out = wordcount(hdfs.data, hdfs.out)

results = from.dfs( out )
results.df = as.data.frame(results, stringsAsFactors=F )
colnames(results.df) = c('word', 'count')

head(results.df)
