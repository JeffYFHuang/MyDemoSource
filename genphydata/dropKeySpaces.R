require(rmr2)

source("src/cassandraFuncs.R")

path <- dfs.ls("/data/physical/in")["path"]
r <- unlist(lapply(path, strsplit, '/'))
school.id <- matrix(r, 5)[5, ]

DropKeySpaces(school.id)
