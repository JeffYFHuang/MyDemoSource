source("genActData_v2.R")
source("src/cassandraFuncs.R")

getInsertCqlCmd <- function(tblname, values) {
    if (length(values) <= 0) return(NULL)

    #values <- RoundValues(values)

    cqlcmd <- paste("INSERT INTO", tblname)
    colnames <- c("sid", "uuid")
    cqlcmd <- paste(cqlcmd, "(sid, uuid)")
    cqlcmd <- paste(cqlcmd, " VALUES ('", paste(values, collapse="', '"), "')", sep="")

    return(cqlcmd)
}

number <- 2000
e1 <- read.csv("e1_new.csv", header=T)
e1.Kaohsiung <- e1[regexpr("高雄市", e1[,3]) != -1, 1]
set.seed(100)
school.ids <- sample(e1.Kaohsiung, 50, replace = F)

keyspace_name <- "schoolsinfo"
table_name <- "sidsuuids"
CreateSchoolsInfo(keyspace_name)

uids.sids <- sample(school.ids, number, replace = T) #This shall be modified for new project
#uids.sids <- c(uids.sids, uids.sids, uids.sids, uids.sids)

for (sid in school.ids) {
   sid <- paste("elm", sid, sep="")
   cqlcmd <- paste("INSERT INTO schoolsinfo.schools (sid) VALUES ('", sid, "')", sep="")
   cat(cqlcmd, "\n")
   python.call("cqlexec", cqlcmd)
}

for (num in 1:number) {
   set.seed(num)
   sid <- paste("elm", uids.sids[num], sep="")

   cqlcmd <- getInsertCqlCmd(paste(keyspace_name, ".sidsuuids", sep=""), list(sid <- sid, uid <- uuid()))
   cat(cqlcmd, "\n")
   python.call("cqlexec", cqlcmd)
}
