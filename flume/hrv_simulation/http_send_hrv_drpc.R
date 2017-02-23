library(RCurl)
library(rjson)
library(parallel)

send_one_hrv <- function (data, subject = "x01", addr="172.18.161.100", port=44448, count) {
    payload = toJSON(data)
    headers = format(Sys.time(), paste('"headers":{"key":"', count, '", "topic": "hrvs"}', sep=""))
    #subject = "test"
    http_content = paste(subject, '\t', payload, sep="")
#    print(http_content)
    httpheader <- c(Accept="application/json; charset=UTF-8", "Content-Type"="application/json")
#    print(httpheader)
    result=postForm(paste("http://", addr, ":", port, "/drpc/drpcFunc", sep=""), .opts=list(httpheader=httpheader, postfields=http_content))
    print(result)
}

fromJSON2 <- function(hrv) {
    fromJSON(hrv[2])
}

combineHRV <- function(hrvdata) {
    #hrvdata <- sample(hrvdata, reptimes, replace=T)
    hrvdata <- lapply(hrvdata, fromJSON2)
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
sample_num = 144

if (length(args) != 0){
  for (i in 1:length(args)){
     eval(parse(text=args[[i]]))
  }
}

execution = "Exe. Rscript http_send_hrv_kafka.R \"ip='10.0.0.1'\" port=44448 num=1000 sample_num=144";
if (is.null(ip))
   stop(paste("please provide flume server address!", execution))
if (is.null(port))
   stop(paste("please provide port of flume service!", execution))
if (is.null(num))
   stop(paste("please provide the number of messages to send!", execution))

flumeserver = ip
nodename = Sys.info()["nodename"]

recordPath = "../hrmdata"

conn = file.path(recordPath, sourcefile)
lines <-readLines(conn)

pid = Sys.getpid()
curSubject <- ""
hrvdata <- NULL
now <- as.numeric(Sys.time())
times <- now+seq(0, 300*sample_num, 300)
count = 1
while (count <= num) {
     next_sub = TRUE
     samples = sample(lines, sample_num, replace=T)
     val <- strsplit(samples, "\t")
     #print(val)
     subject = paste(nodename, "-", pid,  "-", count, sep="")
     hrvdata <- combineHRV(val)
     send_one_hrv(hrvdata, subject, addr=flumeserver, port=port, count) 
     #lapply (hrvdata, send_one_hrv, subject = paste(curSubject, "-YFnb1", count, sep=""), addr=flumeserver, port=port)
     print(subject)
     count = count + 1
}
