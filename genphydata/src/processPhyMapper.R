#! /usr/bin/env Rscript

# mapper.R - Wordcount program in R
# script for Mapper (R-Hadoop integration)
require(lubridate)
require(rPython)
require(data.table)
require(rjson) #rjson lib shall be behind the lib data.table

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

GetCutHour <- function(t) {
    if (is.na(t)) return(NA)

    hour.time = hour(as.POSIXct(t, origin="1970-01-01", tz="GMT"))

    hour = 8 
    if ((hour.time >= 8) && (hour.time < 14))
       hour = 8
    else if ((hour.time >= 14) && (hour.time < 20))
       hour = 14
    else if (((hour.time >= 20) && (hour.time < 24)) || ((hour.time >= 0) && (hour.time < 2)))
       hour = 20
    else if ((hour.time >= 2) && (hour.time < 8))
       hour = 2

    return (hour)
}

CtxbelongDateHour <- function (t) {
   hour = GetCutHour(t)
   return(as.numeric(as.POSIXlt(date(as.POSIXct(t, origin="1970-01-01", tz="GMT")))) + hour * 60 * 60)
}

CtxbelongWeek <- function (t, index = 7) {
   return(as.POSIXlt(paste(year(t), week(t), index, sep="-"), format = "%Y-%U-%u"))
}

CtxbelongMonth <- function (t) {
   return(as.POSIXlt(paste(year(t), month(t), 1, sep="-"), format = "%Y-%m-%d", tz="GMT"))
}

SleepbelongDate <- function (t) {
   return(as.numeric(as.POSIXlt(date(as.POSIXct(t, origin="1970-01-01", tz="GMT")), tz="GMT")))
}

GetPredictedMaxHR <- function (age) {
   return(220 - age)
}

CtxDateSummary <- function (data, age = 12, weight = 40) {
   if (is.null(data)) return(data)
   predicted.maxHR <- GetPredictedMaxHR (age) 
   data <- data.table(data)
   colnames(data) <- c("timestamp", "situation", "duration", "hrmsum", "count")    
   #data$date <- lapply(data$timestamp, CtxbelongDate)

   d <- data[, list(duration = sum(duration), avghrm = sum(hrmsum)/sum(count), hrmcount = sum(count)), by = list(situation, CtxbelongDateHour(timestamp))]

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

   d.names <- colnames(d)
   d.names[which(d.names=="CtxbelongDateHour")] = "ts"
   colnames(d) = d.names
   return(d)
}

StepDateSummary <- function (data) {
   if (is.null(data)) return(data)
   data <- data.table(data)
   colnames(data) <- c("timestamp", "type", "count", "distance", "cal")
   #data$date <- lapply(data$timestamp, CtxbelongDate)
   d <- data[, list(count = sum(count), distance = sum(distance), cal = sum(cal)), by = list(type, CtxbelongDateHour(timestamp))][order(type)]

   d.names <- colnames(d)
   d.names[which(d.names=="CtxbelongDateHour")] = "ts"
   colnames(d) = d.names
   return(d)
}

HrmDateSummary <- function (data) {
   if (is.null(data)) return(data)
   data <- data.table(data)
   colnames(data) <- c("situation", "timestamp", "hrm_report", "hr_peak_rate")
   #data$date <- lapply(data$timestamp, CtxbelongDate)
   d <- data[, list(min = min(hrm_report), max = max(hrm_report), mean = mean(hrm_report), sd = sd(hrm_report), count=length(hrm_report)), by = list(situation, CtxbelongDateHour(timestamp))][order(situation)]
   #hr <- data$hrm_report
   #return(list(min = min(hr), max = max(hr), mean = mean(hr), median = median(hr), sd = sd(hr), count=length(hr)))

   d.names <- colnames(d)
   d.names[which(d.names=="CtxbelongDateHour")] = "ts"
   colnames(d) = d.names
   return(d)
}

sleepDateSummary <- function (data) {
   if (is.null(data)) return(data)
   data <- data.table(data)
   colnames(data) <- c("timestamp", "status", "duration")
   total.duration <- sum(data$duration)
   #w <- data.table(status = c(1, 2, 3, 4, 5), wi = c(0, 0, 1, 0, 0))
   d <- data[, list(duration = sum(duration), ratio = sum(duration)/total.duration), by = list(status, CtxbelongDateHour(timestamp))][order(status)]

   d.names <- colnames(d)
   d.names[which(d.names=="CtxbelongDateHour")] = "ts"
   colnames(d) = d.names
   return(d)
}

#filepath <- Sys.getenv("map_input_file")

#keyspace <- "elmtest"
#if (nchar(filepath) > 0) {
#   path <- unlist(strsplit(filepath, split="/"))
#   keyspace <- path[7]
#}

tables <- c("step", "sleep", "context", "hrm")
## **** could wo with a single readLines or in blocks
con <- file("stdin", open = "r")
count <- 1
while (length(line <- readLines(con, n = 1, warn = FALSE)) > 0) {
    data <- fromJSON(line)
    uuid <- data$uuid
    sid <- data$sid

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

    ctx.t <- CtxDateSummary(ctx.m)
    step.t <- StepDateSummary(step.m)
    hrm.t <- HrmDateSummary(hrm.m)
    sleep.t <- sleepDateSummary(sleep.m)

    unnametable <- function (tbl) {
       if (is.null(tbl)) return(tbl)
       return(unname(split(tbl, 1:nrow(tbl))))
    }

    data <- list(
                 keyspace = sid,
                 uuid = uuid,
                 context = unnametable(ctx.t),
                 step = unnametable(step.t),
                 sleep = unnametable(sleep.t),
                 hrm = unnametable(hrm.t)
            )

    cat(toJSON(data), "\n")
}

close(con)
