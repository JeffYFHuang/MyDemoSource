source("src/cassandraFuncs.R")

school.id <- getKeyspaces()

DropKeySpaces(school.id)
