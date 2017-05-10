source("src/cassandraFuncs.R")

school.id <- getKeyspaces()
truncateKeyspacesTables(school.id)
