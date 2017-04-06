library(RCurl)
library(rjson)
source("genActData_v2.R")

send_phydummy <- function (data, time, addr="10.0.0.5", port=44448) {
    payload = toJSON(data)
    headers = format(as.POSIXct(time, origin="1970-01-01"), paste('"m_year":"%Y","m_month":"%m","m_day":"%d", "m_hour":"%H"}', sep=""))

    http_content = paste('[{', headers, ',"body":', "'", payload, "'", '}]', sep="")
    httpheader <- c(Accept="application/json; charset=UTF-8", "Content-Type"="application/json")
    #print(http_content)
    postForm(paste("http://", addr, ":", port, sep=""), .opts=list(httpheader=httpheader, postfields=http_content))
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

execution = "Exe. Rscript send_phydummy.R \"ip='10.0.0.5'\" port=44448 num=10";
if (is.null(ip))
   stop(paste("please provide flume server address!", execution))
if (is.null(port))
   stop(paste("please provide port of flume service!", execution))
if (is.null(num))
   stop(paste("please provide the number of messages to send!", execution))

flumeserver = ip

set.seed(100)
uids = array()
count = 1
while (count <= num) {
   uids[count] = uuid()
   count = count + 1
}

time = now()
time = as.POSIXlt(paste(date(time), hour(time)), format="%Y-%m-%d %H")

for (uid in uids) {
   data <- genActDataV2(uid, time, 24*60*60)
   
   for (x in data) {
       #print(x$data[[1]]$timestamp)
       send_phydummy (x, x$data[[1]]$timestamp)
   }
}
