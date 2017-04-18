require(rmr2)

path <- dfs.ls("/data/phydummy")["path"]
r <- unlist(lapply(path, strsplit, '/'))
school.id <- paste("elm", matrix(r, 4)[4, ], sep="")

# Script for creating keysapces via school id.

for (sid in school.id) {
    cat (paste("CREATE KEYSPACE IF NOT EXISTS", sid, "WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 3 };\n"))
}

# Script for creating Tables for school id.
tables = c("step", "sleep", "context", "hrm")
for (sid in school.id) {
    cat (paste("CREATE TABLE IF NOT EXISTS", paste(sid, ".step", sep=""), "(uuid varchar, timestamp bigint, type int, count int, distance int, cal int, PRIMARY KEY (uuid, timestamp));\n"))
    cat (paste("CREATE TABLE IF NOT EXISTS", paste(sid, ".sleep", sep=""), "(uuid varchar, timestamp bigint, status int, duration int, PRIMARY KEY (uuid, timestamp));\n"))
    cat (paste("CREATE TABLE IF NOT EXISTS", paste(sid, ".context", sep=""), "(uuid varchar, timestamp bigint, situation int, duration int, hrm_avg int, PRIMARY KEY (uuid, timestamp));\n"))
    cat (paste("CREATE TABLE IF NOT EXISTS", paste(sid, ".hrm", sep=""), "(uuid varchar, timestamp bigint, hrm_report int, hr_peak_rate int, PRIMARY KEY (uuid, timestamp));\n"))
}
