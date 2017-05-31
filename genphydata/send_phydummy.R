library(RCurl)
library(rjson)
source("genActData_v2.R")

send_phydummy <- function (sid, data, time, addr="10.0.0.5", port=44448) {
    payload = toJSON(data)
    headers = format(as.POSIXct(time, origin="1970-01-01", tz="GMT"), paste('"headers":{"m_year":"%Y","m_month":"%m","m_day":"%d", "m_hour":"%H", "m_school": "', sid, '"}', sep=""))

    http_content = paste('[{', headers, ',"body":', "'", payload, "'", '}]', sep="")
    httpheader <- c(Accept="application/json; charset=UTF-8", "Content-Type"="application/json")
    result = postForm(paste("http://", addr, ":", port, sep=""), .opts=list(httpheader=httpheader, postfields=http_content))
}

args=(commandArgs(TRUE))
bdate = NULL
ip = NULL
port = NULL
range = NULL
hours = NULL
args=(commandArgs(TRUE))

if (length(args) != 0){
  for (i in 1:length(args)){
     eval(parse(text=args[[i]]))
  }
}

execution = "Exe. Rscript send_phydummy.R \"ip='10.0.0.5'\" bdate=\"'2017-05-01'\" port=44448 range='1:50' hours=12";
if (is.null(ip))
   stop(paste("please provide flume server address!", execution))
if (is.null(port))
   stop(paste("please provide port of flume service!", execution))
if (is.null(bdate))
   stop(paste("please provide date!", execution))
if (is.null(range))
   stop(paste("please provide the range of uuid! (1:5000)", execution))
if (is.null(hours))
   stop(paste("please provide the number of hours!", execution))

flumeserver = ip

e1 <- read.csv("e1_new.csv", header=T)
e1.Kaohsiung <- e1[regexpr("高雄市", e1[,3]) != -1, 1]
set.seed(100)
school.ids <- sample(e1.Kaohsiung, 50, replace = F)
uids.sids <- sample(school.ids, 20000, replace = T)[range] #length(range), replace = T)

uids = array()
count = 1
for (num in range) {
   set.seed(num)
   uids[count] = uuid()
   count = count + 1
}

#for (h in 1:4) {
#    time = now()
#    beginDate <- floor_date(as.Date(date(now())) - 1, "months")
    time = as.POSIXlt(paste(bdate, 8), format="%Y-%m-%d %H", tz="GMT")
#    print(time)
#    time = time + (h - 1) * hours*60*60

set.seed(as.integer(as.numeric(time)))
i <- 1
for (sid in unique(uids.sids)) {
    print(uids[which(uids.sids == sid)])
    for (uid in uids[which(uids.sids == sid)]) {
       cat(i, sid, " ", uid, "\n")
       data <- genActDataV2(uid, time, hours*60*60, win = 6*60*60)
       #print(data)
       for (x in data) {
           x$sid = paste("elm", sid, sep="")
           send_phydummy (x$sid, x, x$data[[1]]$timestamp, addr = flumeserver)
       }
       i <- i + 1
    }
}

#}
