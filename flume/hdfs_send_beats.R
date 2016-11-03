library(RCurl)

send_beats <- function (subject = "x01", path = "xbeats",  addr="172.18.161.100", port=44448, iter = i) {
    file = file.path(path, paste(subject, ".beats", sep=""))
    beats = readLines(file)
    payload = paste(beats, collapse=" ")
    headers = format(Sys.time(), paste('"headers":{"m_user":"', subject , '","m_year":"%Y","m_month":"%m","m_day":"%d", "m_hour":"%H"}', sep=""))

    http_content = paste('[{', headers, ',"body":"', subject, "\t", payload, '"}]', sep="")
    httpheader <- c(Accept="application/json; charset=UTF-8", "Content-Type"="application/json")
    postForm(paste("http://", addr, ":", port, sep=""), .opts=list(httpheader=httpheader, postfields=http_content))
}

flumeserver = "172.18.161.1"
port = 44448
recordPath = "beats"
recordNames = list.files(path=recordPath, pattern=".beats")
recordNames = unlist(strsplit(recordNames, '.beats'))
for (subject in recordNames) {
    print(subject)
    send_beats(subject, addr=flumeserver, port=port, path=recordPath, iter = which(recordNames==subject))
}
