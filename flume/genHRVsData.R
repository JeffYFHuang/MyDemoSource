library(RCurl)
library(rjson)
library(parallel)

bindSubjectHrv <- function (data, subject = "x01", conn) {
    hrv = paste(subject, "\t", data, sep="")
    hrv
}

generateHRV <- function(hrvdata, reptimes = 144, replace=T) {
    hrvdata <- sample(hrvdata, reptimes, replace=T)
    hrvdata <- lapply(hrvdata, fromJSON)
    for (i in 1:length(hrvdata)){
        hrvdata[[i]]$startTime <-  times[i]
        hrvdata[[i]]$endTime <- times[i] + 300
    }

    hrvdata <- lapply(hrvdata, toJSON)
}

flumeserver = "172.18.161.1"
port = 44448
recordPath = "hrvdata"
conn = file.path(recordPath, "hrvdata.txt")

lines <-readLines(conn)

curSubject <- ""
hrvdata <- NULL
now <- as.numeric(Sys.time())
times <- now+seq(0, 300*144, 300)
count = 1
while (count <= 20000) {
     next_sub = TRUE
     for (i in 1:length(lines)){
         val <- unlist(strsplit(lines[i], "\t"))
         if (curSubject == val[1]){
            if (next_sub) next
            hrvdata <- c(hrvdata, val[2])
         } else {
            if (!next_sub) {
               hrvdata <- generateHRV(hrvdata, 144)
               hrvdata = lapply (hrvdata, bindSubjectHrv, subject = paste(curSubject, "-", count, sep=""))
               print(paste(curSubject, "-", count, sep=""))
               write(unlist(hrvdata), file.path(recordPath, paste(curSubject, "-", count, ".hrv", sep="")))
               count = count + 1
            }

            curSubject = val[1]
            next_sub = (rbinom(1, 1, 0.5) == 1)
            if (next_sub) next

            hrvdata <- val[2]
        }
    }
}
