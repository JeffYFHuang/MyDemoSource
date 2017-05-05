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

# store keyspaces of shcools into hdfs to be input data of mapreduce.
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
            cqlcmd <- paste("SELECT", paste(columns, collapse=","), "FROM", paste(keyspace, ".", tableName, sep=""), 
                             "WHERE datehour >= ", bTime, "AND datehour <=", eTime, "ALLOW FILTERING")
            data <- python.call("cqlexec", cqlcmd)
            data <- Null2NA(data)
            data <- data.frame(matrix(unlist(data), ncol=length(columns), byrow=T))
            colnames(data) <- columns
            data$datehour <- factor(bTime)
            keyval(keyspace, data)
        }

        c.keyval(lapply(as.list(input), getData))
        #keyval(input, eTime)
     }

     as.numeric.factor <- function(x) {as.numeric(levels(x))[x]}

     processData <- function (data, type) {
        if (is.null(data)) return(data)
        data <- data.table(data)
        #uuid | datehour   | situation | activeindex | avghrm    | duration | hrmcount | met
        #uuid | date | situation | activeindex | avghrm | duration | hrmcount | met
        data <- na.omit(data)

        d <- NULL
        if (type == 'context') {
            d <- data[, list(
                            duration = sum(as.numeric.factor(duration)), 
                            activeindex = sum(as.numeric.factor(activeindex)),            
                            avghrm = sum(as.numeric.factor(avghrm) * as.numeric.factor(hrmcount))/sum(as.numeric.factor(hrmcount)), 
                            hrmcount = sum(as.numeric.factor(hrmcount)),
                            met = sum(as.numeric.factor(met))), 
                            by = list(uuid, date = as.numeric.factor(datehour), situation)]
        }

        if (type == 'step') {
        # todo
            # uuid | date | type | cal | count | distance
            d <- data[, list(
                            cal = sum(as.numeric.factor(cal)),
                            count = sum(as.numeric.factor(count)),
                            distance = sum(as.numeric.factor(distance))),
                            by = list(uuid, date = as.numeric.factor(datehour), type)]
        }

        if (type == 'hrm') {
        # todo
        #     d <- data[, list(
        #                    min = min(as.numeric.factor(min)),
        #                    max = max(as.numeric.factor(max)),
        #                    mean = as.numeric.factor(mean) * as.numeric.factor(count)/sum(as.numeric.factor(count)),
        #                    sd = sqrt(as.numeric.factor(sd)^2 * as.numeric.factor(count)/sum(as.numeric.factor(count))),
        #                    count = sum(count)),
        #                    by = list(uuid, date = as.numeric.factor(datehour), situation)]
        }

        if (type == 'sleep') {
              #  uuid | date | status | duration | ratio
              d <- data[, list(duration = duration, status = status, date = datehour, ratio = as.numeric.factor(duration)/sum(as.numeric.factor(duration))), by = list(uuid)]
              d <- d[, list(ratio = sum(ratio)), by = list(uuid, status)][order(uuid, status)]
        }

        return(d)
     }

     reducer.process.data <- function(key, df) {
        SetupPyCasDriver()
        #uuid | datehour   | situation | activeindex | avghrm    | duration | hrmcount | met
        df <- data.frame(processData(df, type))
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
