library(RCurl)

send_beats <- function (subject = "x01", path = "xbeats",  addr="172.18.161.250", port=44448, iter = i) {
    file = file.path(path, paste(subject, ".beats", sep=""))
    beats = readLines(file)
    payload = paste(beats, collapse=" ")
    headers = format(Sys.time(), paste('"headers":{"m_user":"subject_', iter , '","m_year":"%Y","m_month":"%m","m_day":"%d", "m_hour":"%H", "key":"', iter, '", "topic": "beats-', iter%%4, '"}', sep=""))

    http_content = paste('[{', headers, ',"body":"subject_jeffnb_', iter, "\t", payload, '"}]', sep="")
    httpheader <- c(Accept="application/json; charset=UTF-8", "Content-Type"="application/json")
    postForm(paste("http://", addr, ":", port, sep=""), .opts=list(httpheader=httpheader, postfields=http_content))
}

flumeserver = "172.18.161.250"
port = 44448
recordPath = "xbeats"
recordNames = list.files(path=recordPath, pattern=".beats")
recordNames = unlist(strsplit(recordNames, '.beats'))

for (i in 1:900) {
for (subject in recordNames) {
    print(paste(subject, "_", i, sep=""))
    send_beats(subject, addr=flumeserver, port=port, iter = (i-1)*length(recordNames) + which(recordNames==subject))
}
}
