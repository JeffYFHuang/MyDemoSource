library(RCurl)
library(rjson)
library(parallel)

send_one_data <- function (data, subject = "x01", addr="172.18.161.100", port=44448) {
    payload = data
    headers = format(Sys.time(), paste('"headers":{"m_user":"', subject , '","m_year":"%Y","m_month":"%m","m_day":"%d", "m_hour":"%H"}', sep=""))

    http_content = paste('[{', headers, ',"body":', "'", subject, '\t', payload, "'", '}]', sep="")
    httpheader <- c(Accept="application/json; charset=UTF-8", "Content-Type"="application/json")
    postForm(paste("http://", addr, ":", port, sep=""), .opts=list(httpheader=httpheader, postfields=http_content))
}

send_one_data_mt <- function (data, addr="172.18.161.100", port=44448) {
    pid = Sys.getpid()
    val <- unlist(strsplit(data, "\t"))
    subject = paste(nodename, "-", pid, "-", val[1], sep="")
    send_one_data(val[2], subject, addr, port)
    print(subject) 
}

args=(commandArgs(TRUE))
ip = NULL
port = NULL
num = NULL
numcores = 100
sourcefile = "beatsdata.txt"
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

print(numcores)
flumeserver = ip
nodename = Sys.info()["nodename"]

recordPath = "hrmdata"

conn = file.path(recordPath, sourcefile)
lines <-readLines(conn)
curSubject <- ""
hrvdata <- NULL

data <- sample(lines, num, replace=TRUE)
#data <- paste(1:length(data), data)
mclapply(data, send_one_data_mt, addr=flumeserver, port=port, mc.cores = numcores)
