require(rmr2)

source("src/cassandraFuncs.R")

path <- dfs.ls("/data/phydummy")["path"]
r <- unlist(lapply(path, strsplit, '/'))
school.id <- matrix(r, 4)[4, ]

CreateKeySpacesAndTables(school.id)
