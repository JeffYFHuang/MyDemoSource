require(rmr2)

path <- dfs.ls("/data/phydummy")["path"]
r <- unlist(lapply(path, strsplit, '/'))
school.id <- paste("elm", matrix(r, 4)[4, ], sep="")

# Script for creating keysapces via school id.

for (sid in school.id) {
    cat (paste("DROP KEYSPACE", sid, ";\n"))
}
