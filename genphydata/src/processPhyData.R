#! /usr/bin/env Rscript

# mapper.R - Wordcount program in R
# script for Mapper (R-Hadoop integration)
require(lubridate)
require(rjson)
require(rPython)
require(data.table)

trimWhiteSpace <- function(line) gsub("(^ +)|( +$)", "", line)
splitIntoWords <- function(line) unlist(strsplit(line, "[[:space:]]+"))
getInsertCqlCmd <- function(tblname, colnames, values) {
    if (length(colnames) != length(na.omit(values))) return(NULL)
    cqlcmd <- paste("INSERT INTO", tblname)
    cqlcmd <- paste(cqlcmd, "(", paste(colnames, collapse=", "), ")")

    if (length(which(colnames == "uuid")) == 1)
       cqlcmd <- paste(cqlcmd, " VALUES ('", values[which(colnames == "uuid")], "',", sep = "")

    values[which(colnames != "uuid")] <- round(as.numeric(values[which(colnames != "uuid")]))
    cqlcmd <- paste(cqlcmd, paste(values[which(colnames != "uuid")], collapse=", "), ")")

    return(cqlcmd) 
}

CtxbelongDate <- function (timestamp) {
   #time = as.POSIXlt(date(timestamp), format="%Y-%m-%d %H")
   #minTime = as.numeric(startTime) + 7 * 60 * 60
   return(date(timestamp))
}

CtxbelongWeek <- function (timestamp, index = 7) {
   return(as.POSIXlt(paste(year(timestamp), week(timestamp), index, sep="-"), format = "%Y-%U-%u"))
}

CtxbelongMonth <- function (timestamp) {
   return(as.POSIXlt(paste(year(timestamp), month(timestamp), 1, sep="-"), format = "%Y-%m-%d"))
}

GetPredictedMaxHR <- function (age) {
   return(220 - age)
}

CtxDateSummary <- function (data, age = 12, weight = 65) {
   if (is.null(data)) return
   predicted.maxHR <- GetPredictedMaxHR (age) 
   data <- data.table(data)
   colnames(data) <- c("timestamp", "situation", "duration", "hrmsum", "count")    

   d <- data[, list(duration = sum(duration), sumhr = sum(hrmsum), hrmcount = sum(count)), by = situation]
   d <- setkey(d, situation)[d, avghrm := sumhr / hrmcount][order(situation)]

   # calculate active index
   isact <- data.table(situation = c(1, 2, 3, 4), isact = c(0, 0, 1, 1))
   d <- setkey(d, situation)[isact, activeindex := isact * (duration * avghrm * 100) / (predicted.maxHR * 60 * 60)][order(situation)]

   # calculate MET
   #context_situation = 1, static     (1 METs)
   #context_situation = 2, walk      (2.5 METs)
   #context_situation = 3, run        (10.0 METs)
   #context_situation = 4, bicycle (6.0 METs)
   met.tb <- data.table(situation = c(1, 2, 3, 4), met=c(1, 2.5, 10.0, 6.0))
   d <- setkey(d, situation)[met.tb, met := (duration * weight * met) / (60 * 60)][order(situation)]

   return(d)
}

StepDateSummary <- function (data) {
   if (is.null(data)) return
   data <- data.table(data)
   colnames(data) <- c("timestamp", "type", "count", "distance", "cal")
   d <- data[, list(count = sum(count), distance = sum(distance), cal = sum(cal)), by = type][order(type)]
   return(d)
}

HrmDateSummary <- function (data) {
   if (is.null(data)) return
   data <- data.table(data)
   colnames(data) <- c("situation", "timestamp", "hrm_report", "hr_peak_rate")
   d <- data[, list(min = min(hrm_report), max = max(hrm_report), mean = mean(hrm_report), median = median(hrm_report), sd = sd(hrm_report), count=length(hrm_report)), by = situation][order(situation)]
   #hr <- data$hrm_report
   #return(list(min = min(hr), max = max(hr), mean = mean(hr), median = median(hr), sd = sd(hr), count=length(hr)))
   return(d)
}

filepath <- Sys.getenv("map_input_file")

keyspace <- "elmtest"
if (nchar(filepath) > 0) {
   path <- unlist(strsplit(filepath, split="/"))
   keyspace <- path[6] 
}

#cat(keyspace, "\n")
#python.load("src/funcs.py", get.exception = T)
#python.call("setkeyspace", keyspace)

tables <- c("step", "sleep", "context", "hrm")
## **** could wo with a single readLines or in blocks
con <- file("stdin", open = "r")
count <- 1
while (length(line <- readLines(con, n = 1, warn = FALSE)) > 0) {
    data <- fromJSON(line)
    uuid <- data$uuid

    ctx.colnames <- c("timestamp", "situation", "duration", "hrmsum", "count")
    step.colnames <- c("timestamp", "type", "count", "distance", "cal")
    sleep.colnmaes <- c("timestamp", "status", "duration")
    hrm.colnames <- c("situation", "timestamp", "hrm_report", "hr_peak_rate")

    ctx.m <- NULL
    step.m <- NULL
    sleep.m <- NULL
    hrm.m <- NULL

    age = 12
    weight = 65
    for (x in data$data) {
        xx <- x[which(names(x)!="hrm")]
 
        if (xx["context"] != 5 && xx["context"] != 0) {
           hrmsum <- 0
           count <- 0
           if (length(which(names(x) == 'hrm') > 0)) {
              hrmsum <- sum(matrix(unlist(x$hrm), 3)[2, ])
              count <- length(matrix(unlist(x$hrm), 3)[2, ])
           }

           ctx.m <- rbind(ctx.m, unlist(c(xx["timestamp"], xx["context"], xx["duration"], hrmsum, count)))
#           cqlcmd <- getInsertCqlCmd("context", colnames, values)
#           cat(cqlcmd, "\n")
#           python.call("cqlexec", cqlcmd)
           if (xx["context"] == 2 || xx["context"] == 3) {
               step.m <- rbind(step.m, unlist(c(xx["timestamp"], xx["context"], xx["count"], xx["distance"], xx["cal"])))
#              cqlcmd <- getInsertCqlCmd("step", colnames, values)
#              cat(cqlcmd, "\n")
#              python.call("cqlexec", cqlcmd)
           }
        } else if (xx["context"] == 5) {
           sleep.m = rbind(sleep.m, unlist(c(xx["timestamp"], xx["status"], xx["duration"])))
#           cqlcmd <- getInsertCqlCmd("sleep", colnames, values)
#           cat(cqlcmd, "\n")
#           python.call("cqlexec", cqlcmd)
        }

        if (length(which(names(x) == 'hrm') > 0)) {
           for (hrm in x$hrm) {
               hrm.m <- rbind(hrm.m, unlist(c(xx["context"], round(unlist(hrm)))))
#               cqlcmd <- getInsertCqlCmd("hrm", colnames, values)
#               cat(cqlcmd, "\n")
#               python.call("cqlexec", cqlcmd)
           }
        }
    }
    print (CtxDateSummary(ctx.m))
    print (StepDateSummary(step.m))
    print (HrmDateSummary(hrm.m))
#    cat(uuid, "\n");
}

close(con)
