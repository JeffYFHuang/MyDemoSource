library(RCurl)
library(rjson)
#library(parallel)

send_one_data <- function (data, subject = "x01", addr="172.18.161.100", port=44448) {
    payload = data
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
args=(commandArgs(TRUE))

if (length(args) != 0){
  for (i in 1:length(args)){
     eval(parse(text=args[[i]]))
  }
}

execution = "Exe. Rscript hdfs_send_beats.R \"ip='10.0.0.1'\" port=44448 num=1000";
if (is.null(ip))
   stop(paste("please provide flume server address!", execution))
if (is.null(port))
   stop(paste("please provide port of flume service!", execution))
if (is.null(port))
   stop(paste("please provide the number of messages to send!", execution))

flumeserver = ip
nodename = Sys.info()["nodename"]
pid = Sys.getpid()

recordPath = "hrmdata"

conn = file.path(recordPath, "beatsdata.txt")
lines <-readLines(conn)
curSubject <- ""
hrvdata <- NULL
count = 1
while (count <= num) {
    data <- sample(lines, 1)
    val <- unlist(strsplit(data, "\t"))
    subject = paste(val[1], "-", nodename, "-", pid, "-", count, sep="")
    send_one_data(val[2], subject, addr=flumeserver, port=port)
   #lapply (hrvdata, send_one_hrv, subject = paste(curSubject, "-YFnb1", count, sep=""), addr=flumeserver, port=port)
    print(subject)
    count = count + 1
}
