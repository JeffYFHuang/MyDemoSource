require(rhbase)
require(rjson)

hb.init("10.0.0.8", 9090)
hb.new.table("hrv_rhbase","info")

con <- file("part-00000", open = "r")
line <- readLines(con, n = 1, warn = FALSE)
data = fromJSON(line)
fields = paste("info", names(data), sep=":")
hb.insert("hrv_rhbase",list(list(data$startTime, fields, data)))
while (length(line <- readLines(con, n = 1, warn = FALSE)) > 0) {
   data = fromJSON(line)
   fields = paste("info", names(data), sep=":")
   hb.insert("hrv_rhbase",list(list(data$startTime, fields, data)))
}
close(con)


