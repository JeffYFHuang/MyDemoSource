#! /usr/bin/env Rscript

# mapper.R - Wordcount program in R
# script for Mapper (R-Hadoop integration)
require(compiler)
enableJIT(3)
loadcmp("HRVFUNS.Rc")

trimWhiteSpace <- function(line) gsub("(^ +)|( +$)", "", line)
splitIntoWords <- function(line) unlist(strsplit(line, "[[:space:]]+"))

SplitBeatsbyEpisodes <- function(hr.data, Verbose=FALSE) {
    if (Verbose) {
        cat("** Splitting heart rate signal using episodes **\n")
    }
    if (is.null(hr.data$Episodes)) {
        stop("  --- Episodes not present\n    --- Quitting now!! ---\n")
    }
    if (is.null(hr.data$Beat)) {
        stop("  --- Beat Time not present\n    --- Quitting now!! ---\n")
    }

    ActiveEpisodes = na.omit(data.frame(hr.data$Episodes))

    Beg = ActiveEpisodes$InitTime
    End = ActiveEpisodes$InitTime + ActiveEpisodes$Duration
    npoints = length(hr.data$Beat)
    first = head(hr.data$Beat, 1)
    last = tail(hr.data$Beat, 1)

    Aux = rep(0, times = npoints)
    for (i in 1:length(Beg)) {
        Aux[hr.data$Beat >= Beg[i] & hr.data$Beat <= End[i]] = 1
    }
    l = list(InEpisodes = hr.data$Beat[Aux == 1], OutEpisodes = hr.data$Beat[Aux == 0])
    if (Verbose) {
       cat("   Inside episodes:", length(l$InEpisodes), "points\n")
       cat("   Outside episodes:", length(l$OutEpisodes), "points\n")
    }
    return(l)
}

is.compile <- function(func)
{
	# this function lets us know if a function has been byte-coded or not
	#If you have a better idea for how to do this - please let me know...
    if(class(func) != "function") stop("You need to enter a function")
    last_2_lines <- tail(capture.output(func),2)
    any(grepl("bytecode:", last_2_lines)) # returns TRUE if it finds the text "bytecode:" in any of the last two lines of the function's print
}

# Compiled versions
SplitBeatsbyEpisodes_c <- cmpfun(SplitBeatsbyEpisodes)
fromJSON<-cmpfun(fromJSON)
#calculateHRV<-cmpfun(calculateHRV)
getSplitWindowBeats_c <- cmpfun(getSplitWindowBeats)
#is.compile(CaculateApEn)

beat2HRV <- function(k, input) {

  toHRV <- function(input) {
    val <- unlist(strsplit(input, "\t"))
    hr.data <- fromJSON(val[2])

    HRV = NULL
    if (!is.null(hr.data$Episodes)) {
 #      hr.data<-SplitBeatsbyEpisodes(hr.data)
      hr.data<-SplitBeatsbyEpisodes_c(hr.data)

       for (i in 1:2) {
          if (length(hr.data[[i]]) < 120)
             next
          name = paste(val[1], ".", names(hr.data[i]), sep="")
          data <- list()
          data$Beat <- hr.data[[i]]
          data$Subject <- name
          HRV <- c(HRV, getSplitWindowBeats_c(data, toHRV=T))
       }
    } else {
      data <- list()
      data$Beat <- hr.data$Beat
      data$Subject <- val[1]
      HRV <- getSplitWindowBeats_c(data, toHRV=T)
    }

    keyval(val[1], toJSON(HRV[which(lapply(HRV, length)!=1)]))
  }

  c.keyval(lapply(as.list(input), toHRV))
}

#input = "/data/beatsdata"
#output = "/data/beats2hrv"

backend.parameters = list(hadoop=list(D='mapreduce.map.tasks=15', D='mapreduce.reduce.tasks=4', 
                                      D='mapreduce.map.java.opts=-Xmx256m',
                                      D='mapreduce.reduce.java.opts=-Xmx256m',
                                      D='mapreduce.map.memory.mb=256', 
                                      D='mapreduce.reduce.memory.mb=256',
                                      D='mapreduce.child.java.opts=-Xmx256m'
                                      ))

a <- mapreduce(input=input,
          input.format="text", #make.input.format("csv", sep = "\t"),
          output.format="text",
          map=beat2HRV,
          reduce=function(k, v) {keyval(k, v)},
          output=output, backend.parameters=backend.parameters)
