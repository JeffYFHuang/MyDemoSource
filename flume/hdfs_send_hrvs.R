library(RCurl)
library(rjson)
library(parallel)

send_one_hrv <- function (data, subject = "x01", addr="172.18.161.100", port=44448) {
    payload = paste(data, collapse=" ")
    headers = format(Sys.time(), paste('"headers":{"m_user":"', subject , '","m_year":"%Y","m_month":"%m","m_day":"%d", "m_hour":"%H"}', sep=""))

    http_content = paste('[{', headers, ',"body":', "'", subject, '\t', payload, "'", '}]', sep="")
    httpheader <- c(Accept="application/json; charset=UTF-8", "Content-Type"="application/json")
    postForm(paste("http://", addr, ":", port, sep=""), .opts=list(httpheader=httpheader, postfields=http_content))
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
while (count <= 4000) {
     next_sub = TRUE
     for (i in 1:length(lines)){
         val <- unlist(strsplit(lines[i], "\t"))
         if (curSubject == val[1]){
            if (next_sub) next
            hrvdata <- c(hrvdata, val[2])
         } else {
            if (!next_sub) {
               hrvdata <- generateHRV(hrvdata, 144)
               lapply (hrvdata, send_one_hrv, subject = paste(curSubject, "-YFnb1", count, sep=""), addr=flumeserver, port=port)
               print(paste(curSubject, "-", count, sep=""))
               count = count + 1
            }

            curSubject = val[1]
            next_sub = (rbinom(1, 1, 0.5) == 1)
            if (next_sub) next

            hrvdata <- val[2]
        }
    }
    #send_one_hrv(val[1], val[2], addr=flumeserver, port=port)
}
