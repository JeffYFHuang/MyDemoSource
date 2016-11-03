require(RHive)
require(rjson)

master = "10.0.0.8"
user = "hduser"
passwd = "hduser"
rhive.init()
rhive.connect(master, user="hduser", password="hduser")

con <- file("part-00000", open = "r")
line <- readLines(con, n = 1, warn = FALSE)
data = fromJSON(line)
fields = names(data)

tableName = "hivetest_hrv"
#create hrv table
if (!rhive.exist.table(tableName)) {
   create_tb = paste("CREATE TABLE ", tableName, " (", paste(fields[-28], "string", collapse=", "), ", ", fields[28], " int)", sep="")
   rhive.query(create_tb)
}

values = paste("(", paste("'", as.numeric(data), "'", collapse=", ", sep=""), ")", sep="")

while (length(line <- readLines(con, n = 1, warn = FALSE)) > 0) {
   data = fromJSON(line)
   values = c(values, paste("(", paste("'", as.numeric(data), "'", collapse=", ", sep=""), ")", sep=""))
}
close(con)
insert_data = paste("INSERT INTO TABLE ", tableName, " VALUES ", paste(values, collapse=", ", sep=""), sep="")
rhive.query(insert_data)
