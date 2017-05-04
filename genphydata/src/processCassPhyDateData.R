#! /usr/bin/env Rscript

# reducer.R - Wordcount program in R
# script for Reducer (R-Hadoop integration)
require(lubridate)
require(data.table)
require(rmr2)
require(rPython)
require(rjson)

trimWhiteSpace <- function(line) gsub("(^ +)|( +$)", "", line)

RoundValues <- function (values) {
    fields <- c("situation", "datehour", "min", "max", "count", "duration", "hrmcount", "activeindex", "met", "type", "count", "distance", "cal", "status", "duration")

    values[which(names(values) != "uuid")] <- as.numeric(values[which(names(values) != "uuid")])
    values[names(values) %in% fields] <- as.integer(round(as.numeric(values[names(values) %in% fields])))
    return(values[!is.na(values)])
}

getInsertCqlCmd <- function(tblname, values) {
    if (length(values) <= 0) return(NULL)

    values <- RoundValues(values)

    cqlcmd <- paste("INSERT INTO", tblname)
    colnames <- names(values)
    cqlcmd <- paste(cqlcmd, "(", paste(colnames, collapse=", "), ")")
    cqlcmd <- paste(cqlcmd, " VALUES (", paste(values[which(colnames != "uuid")], collapse=", "))
    cqlcmd <- paste(cqlcmd, ", '", values$uuid, "')", sep="")

    return(cqlcmd) 
}

Null2NA <- function (data) {
    listelm2NA <- function (x) {
        x[unlist(lapply(x, is.null))] <- NA
        return (x)
    }
    data <- lapply(data, listelm2NA)
    return(data)
}

SetupPyCasDriver <- function () {
    python.exec("from cassandra.cluster import Cluster")
    python.exec("import json")
    python.exec("cluster = Cluster(['172.18.161.100', '172.18.161.101'])")
    python.exec("session = cluster.connect();")
    python.exec("session.default_timeout = 3600")
    python.exec("def setkeyspace(keyspace):return session.set_keyspace(keyspace)")
    python.exec("def cqlexec(cqlcmd):result = session.execute(cqlcmd);return list(result)")
}

beginDate <- NULL
ndays <- 1  #1 day
type <- 'context'

args=(commandArgs(TRUE))

if (length(args) != 0){
  for (i in 1:length(args)){
     eval(parse(text=args[[i]]))
  }
}

execution = "Exe. Rscript runRc.R beginDate=\"'2017-04-26'\" ndays=1 type='context'";
if (is.null(beginDate))
   stop(paste("please provide beginDate!", execution))
if (is.null(ndays))
   stop(paste("please provide a num of days!", execution))
if (is.null(type))
   stop(paste("please provide tableName (context_hour, step_hour, sleep_hour, hrm_hour)!", execution))

table.map <- list(context = "context_hour", step = "step_hour", sleep = "sleep_hour", hrm = "hrm_hour")
tableName <- table.map[type]
print(tableName)
# load python cassandra driver
python.load("src/funcs.py", get.exception = T)

# get keyspaces for schools
keyspaces <- python.call("cqlexec", 'SELECT keyspace_name FROM system_schema.keyspaces')
keyspaces <- keyspaces[grepl("elm", keyspaces)]

# store schools keyspaces into hdfs to be input data.
input = to.dfs(keyspaces)

ProcessContextTable <- function (tableName, beginDate, ndays) {
     # MAP function. Retrieve data from cassandra.
     # Where data between 00:00:00 to 23:59:59 on a day.
     mapper.retrieve.data.cas <- function(., input) {
        SetupPyCasDriver()

        getbTime <- function (beginDate) {
           return (as.numeric(as.POSIXct(beginDate, origin="1970-01-01", tz="GMT")))
        }

        geteTime <- function (beginDate, ndays) {
           return (getbTime(beginDate) + ndays * 86400) #24*60*60
        }

        bTime <- getbTime(beginDate)
        eTime <- geteTime(beginDate, ndays) 
 
        getData <- function (keyspace) {
            cqlcmd <- paste("SELECT column_name FROM system_schema.columns WHERE keyspace_name = '", keyspace, 
                            "' AND table_name = '", tableName,"'", sep="")
            columns <- python.call("cqlexec", cqlcmd)
            columns <- paste(columns, collapse=",") 
            cqlcmd <- paste("SELECT", columns, "FROM", paste(keyspace, ".", tableName, sep=""), 
                             "WHERE datehour >= ", bTime, "AND datehour <=", eTime, "ALLOW FILTERING")
            data <- python.call("cqlexec", cqlcmd)
            data <- Null2NA(data)
            data <- data.frame(matrix(unlist(data), ncol=length(columns), byrow=T))
            colnames(data) <- columns
            keyval(keyspace, data)
        }

        c.keyval(lapply(as.list(input), getData))
        #keyval(input, eTime)
     }

     reducer.process.data <- function(key, df) {
        SetupPyCasDriver()

        keyval(key, df)
     }

     backend.parameters = list(hadoop=list(D=paste('mapreduce.job.maps=', 6, sep=""), D='mapreduce.job.reduces=4',
                                      D='mapreduce.map.java.opts=-Xmx2048m',
                                      D='mapreduce.reduce.java.opts=-Xmx3072m',
                                      D='mapreduce.map.memory.mb=2048',
                                      D='mapreduce.reduce.memory.mb=3072',
                                      D='mapreduce.child.java.opts=-Xmx3072m'
                                      ))

     a <- mapreduce(input=input,
#          input.format="text", #make.input.format("csv", sep = "\t"),
#          output.format="text", #make.input.format("csv", sep = "\t"),
          map=mapper.retrieve.data.cas,
          reduce=reducer.process.data,
          output=NULL, backend.parameters=backend.parameters)

     return(a)
}

print(from.dfs(ProcessContextTable(tableName, beginDate, ndays)))
