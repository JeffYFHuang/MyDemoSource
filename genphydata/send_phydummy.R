library(RCurl)
library(rjson)
source("genActData_v2.R")

send_phydummy <- function (sid, data, time, addr="10.0.0.5", port=44448) {
    payload = toJSON(data)
    headers = format(as.POSIXct(time, origin="1970-01-01"), paste('"headers":{"m_year":"%Y","m_month":"%m","m_day":"%d", "m_hour":"%H", "m_school": "', sid, '"}', sep=""))

    http_content = paste('[{', headers, ',"body":', "'", payload, "'", '}]', sep="")
    httpheader <- c(Accept="application/json; charset=UTF-8", "Content-Type"="application/json")
    result = postForm(paste("http://", addr, ":", port, sep=""), .opts=list(httpheader=httpheader, postfields=http_content))
}

args=(commandArgs(TRUE))
ip = NULL
port = NULL
num = NULL
hours = NULL
args=(commandArgs(TRUE))

if (length(args) != 0){
  for (i in 1:length(args)){
     eval(parse(text=args[[i]]))
  }
}

execution = "Exe. Rscript send_phydummy.R \"ip='10.0.0.5'\" port=44448 num=10 hours=12";
if (is.null(ip))
   stop(paste("please provide flume server address!", execution))
if (is.null(port))
   stop(paste("please provide port of flume service!", execution))
if (is.null(num))
   stop(paste("please provide the number of uuid!", execution))
if (is.null(hours))
   stop(paste("please provide the number of hours!", execution))

flumeserver = ip

e1 <- read.csv("e1_new.csv", header=T)
e1.Kaohsiung <- e1[regexpr("高雄市", e1[,3]) != -1, 1]
set.seed(100)
school.ids <- sample(e1.Kaohsiung, 50, replace = F)
uids.sids <- sample(school.ids, 20000, replace = T)

set.seed(100)
uids = array()
count = 1
while (count <= num) {
   uids[count] = uuid()
   count = count + 1
}

#for (h in 1:4) {
    time = now()
    time = as.POSIXlt(paste(date(time), 8), format="%Y-%m-%d %H")
#    time = time + (h - 1) * hours*60*60

set.seed(runif(1, 0, 10000000))
i <- 1
for (sid in school.ids) {
    print(uids[which(uids.sids == sid)])    
    for (uid in uids[which(uids.sids == sid)]) {
#       cat(i, sid, " ", uids[1], "\n")
       data <- genActDataV2(uids[1], time, hours*60*60, win = 6*60*60)
#       print(data)   
       for (x in data) {
           x$sid <- paste("elm", sid, sep="")
           send_phydummy (sid, x, x$data[[1]]$timestamp, addr = flumeserver)
           stop()
       }
       i <- i + 1
    }
}
#}
