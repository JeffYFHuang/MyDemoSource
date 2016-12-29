library(RCurl)
library(rjson)
library(parallel)

send_one_hrv <- function (data, subject = "x01", addr="172.18.161.100", port=44448) {
    payload = toJSON(data)
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

    hrvdata # <- lapply(hrvdata, toJSON)
}

args=(commandArgs(TRUE))
ip = NULL
port = NULL
num = NULL
sourcefile = "hrvlabel.dat"
args=(commandArgs(TRUE))

if (length(args) != 0){
  for (i in 1:length(args)){
     eval(parse(text=args[[i]]))
  }
}

execution = "Exe. Rscript hdfs_send_hrv.R \"ip='10.0.0.1'\" port=44448 num=1000";
if (is.null(ip))
   stop(paste("please provide flume server address!", execution))
if (is.null(port))
   stop(paste("please provide port of flume service!", execution))
if (is.null(port))
   stop(paste("please provide the number of messages to send!", execution))

flumeserver = ip
nodename = Sys.info()["nodename"]

recordPath = "hrmdata"

conn = file.path(recordPath, sourcefile)
lines <-readLines(conn)

pid = Sys.getpid()
curSubject <- ""
hrvdata <- NULL
now <- as.numeric(Sys.time())
times <- now+seq(0, 300*144, 300)
count = 1
while (count <= num) {
     next_sub = TRUE
     for (i in 1:length(lines)){
         val <- unlist(strsplit(lines[i], "\t"))
         if (curSubject == val[1]){
            if (next_sub) next
            hrvdata <- c(hrvdata, val[2])
         } else {
            if (!next_sub) {
               subject = paste(nodename, "-", curSubject, "-", pid,  "-", count, sep="")
               hrvdata <- generateHRV(hrvdata, 144)
               send_one_hrv(hrvdata, subject, addr=flumeserver, port=port) 
               #lapply (hrvdata, send_one_hrv, subject = paste(curSubject, "-YFnb1", count, sep=""), addr=flumeserver, port=port)
               print(subject)
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
