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
    fields <- c("situation", "ts", "min", "max", "count", "duration", "hrmcount", "activeindex", "met", "type", "count", "distance", "cal", "status", "duration")

    colnames <- names(values)
    values[which(colnames != "uuid")] <- as.numeric(values[which(colnames != "uuid")])
    values[colnames %in% fields] <- as.integer(round(as.numeric(values[colnames %in% fields])))
    return(values[!is.na(values)])
}

getInsertCqlCmd <- function(tblname, values) {
    if (length(values) <= 0) return(NULL)

    #values <- RoundValues(values)

    cqlcmd <- paste("INSERT INTO", tblname)
    colnames <- names(values)
    cqlcmd <- paste(cqlcmd, "(", paste(colnames[which(colnames!='uuid')], collapse=", "), ",", colnames[which(colnames=='uuid')], ")")
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

#ClosePyCasDriver <- function () {
#    python.exec("session.shutdown()")
#}

date <- NULL
phytype <- 'context'
ptype <- 'date'

args=(commandArgs(TRUE))

if (length(args) != 0){
  for (i in 1:length(args)){
     eval(parse(text=args[[i]]))
  }
}

execution = "Exe. Rscript src/processCassPhyDateData.R date=\"'2017-04-26'\" phytype=\"'context'\" ptype=\"'date'\"";
if (is.null(date))
   stop(paste("please provide date!", execution))
if (is.null(phytype))
   stop(paste("please provide a phytype of physical activity!", execution))
if (is.null(ptype))
   stop(paste("please provide a ptype of period (date, week, month)!", execution))

in.table.map <- list(context = "context_hour", step = "step_hour", sleep = "sleep_hour", hrm = "hrm_hour")

# default is date
out.table.map <- list(context = "context_date", step = "step_date", sleep = "sleep_date", hrm = "hrm_date")
beginDate <- date
ndays <- 1
if (ptype == 'week') {
   beginDate <- floor_date(as.Date(date) - 1, "weeks")
   ndays <- 7
   in.table.map <- list(context = "context_date", step = "step_date", sleep = "sleep_date", hrm = "hrm_date")
   out.table.map <- list(context = "context_week", step = "step_week", sleep = "sleep_week", hrm = "hrm_week")
}

if (ptype == 'month') {
   full.date <- as.POSIXct(date, tz="GMT")
   beginDate <- ymd(format(full.date, "%Y-%m-01"))
   ndays <- days_in_month(full.date)
   in.table.map <- list(context = "context_date", step = "step_date", sleep = "sleep_date", hrm = "hrm_date")
   out.table.map <- list(context = "context_month", step = "step_month", sleep = "sleep_month", hrm = "hrm_month")
}

in.tableName <- in.table.map[phytype]
out.tableName <- out.table.map[phytype]
print(in.tableName)
print(out.tableName)

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

        time.shift = 0
        if (phytype == "sleep")
           time.shift = 43200 #12*60*60

        bTime <- getbTime(beginDate)
        eTime <- geteTime(beginDate, ndays) 
 
        getData <- function (keyspace) {
            cqlcmd <- paste("SELECT column_name FROM system_schema.columns WHERE keyspace_name = '", keyspace, 
                            "' AND table_name = '", tableName,"'", sep="")
            columns <- python.call("cqlexec", cqlcmd)
            cqlcmd <- paste("SELECT", paste(columns, collapse=","), "FROM", paste(keyspace, ".", tableName, sep=""), 
                             "WHERE ts >=", bTime + time.shift, "AND ts <=", eTime + time.shift, "ALLOW FILTERING")
            data <- python.call("cqlexec", cqlcmd)
            data <- Null2NA(data)
            data <- data.frame(matrix(unlist(data), ncol=length(columns), byrow=T))
            colnames(data) <- columns
            data$ts <- factor(bTime)
     #       data$keyspace <- rep(keyspace, nrow(data))
           # data
            keyval(keyspace, data)
        }

        #df <- NULL
        #for (i in 1:length(input)) {
        #    df <- rbind(df, getData(input[i]))
        #}

        #uuids <- unique(df$uuid)

        #groupbyuuid <- function(uuid) {
        #    keyval(uuid, df[which(df$uuid==uuid), ])
        #}

        #gc()
        # here is where we generate the actual sampled data
        #c.keyval(lapply(uuids, groupbyuuid))
        c.keyval(lapply(as.list(input), getData))
        #keyval(input, eTime)
     }

     as.numeric.factor <- function(x) {as.numeric(levels(x))[x]}

     processData <- function (data, phytype) {
        if (is.null(data)) return(data)
        data <- data.table(data)
        #uuid | ts   | situation | activeindex | avghrm    | duration | hrmcount | met
        #uuid | ts | situation | activeindex | avghrm | duration | hrmcount | met
        data <- na.omit(data)

        d <- NULL
        if (ptype == "date") {
           if (phytype == 'context') {
               d <- data[, list(
                            duration = as.integer(round(sum(as.numeric.factor(duration)))),
                            activeindex = as.integer(round(sum(as.numeric.factor(activeindex)))),
                            avghrm = as.integer(round(sum(as.numeric.factor(avghrm) * as.numeric.factor(hrmcount))/sum(as.numeric.factor(hrmcount)))),
                            hrmcount = as.integer(round(sum(as.numeric.factor(hrmcount)))),
                            met = as.integer(round(sum(as.numeric.factor(met))))),
                            by = list(uuid, ts = as.numeric.factor(ts), situation = as.numeric.factor(situation))]
           }

           if (phytype == 'step') {
           # todo
            # uuid | date | type | cal | count | distance
               d <- data[, list(
                            cal = as.integer(round(sum(as.numeric.factor(cal)))),
                            count = as.integer(round(sum(as.numeric.factor(count)))),
                            distance = as.integer(round(sum(as.numeric.factor(distance))))),
                            by = list(uuid, ts = as.numeric.factor(ts), type = as.numeric.factor(type))]
           }

           if (phytype == 'hrm') {
           # todo
               d <- data[, list(
                            min = min(as.numeric.factor(min)),
                            max = max(as.numeric.factor(max)),
                            mean = as.integer(round(sum(as.numeric.factor(mean) * as.numeric.factor(count))/sum(as.numeric.factor(count)))),
                            sd = sqrt(sum(as.numeric.factor(sd)^2 * as.numeric.factor(count))/sum(as.numeric.factor(count))),
                            count = as.integer(round(sum(as.numeric.factor(count))))),
                            by = list(uuid, ts = as.numeric.factor(ts), situation = as.numeric.factor(situation))]
           }

           if (phytype == 'sleep') {
              #  uuid | date | status | duration | ratio
              d <- data[, list(duration = duration, status = as.numeric.factor(status), ts = as.numeric.factor(ts), ratio = as.numeric.factor(duration)/sum(as.numeric.factor(duration))), by = list(uuid)]
              d <- d[, list(duration = as.integer(round(sum(as.numeric.factor(duration)))), ratio = sum(ratio)), by = list(uuid, ts, status)][order(uuid, status)]
           }
        } else {
           if (phytype == 'context') {
               d <- data[, list(
                            duration = as.integer(round(mean(as.numeric.factor(duration)))),
                            activeindex = as.integer(round(mean(as.numeric.factor(activeindex)))),
                            avghrm = as.integer(round(sum(as.numeric.factor(avghrm) * as.numeric.factor(hrmcount))/sum(as.numeric.factor(hrmcount)))),
                            hrmcount = as.integer(round(sum(as.numeric.factor(hrmcount)))),
                            met = as.integer(round(mean(as.numeric.factor(met))))),
                            by = list(uuid, ts = as.numeric.factor(ts), situation = as.numeric.factor(situation))]
           }

           if (phytype == 'step') {
           # todo
            # uuid | date | type | cal | count | distance
               d <- data[, list(
                            cal = as.integer(round(mean(as.numeric.factor(cal)))),
                            count = as.integer(round(mean(as.numeric.factor(count)))),
                            distance = as.integer(round(mean(as.numeric.factor(distance))))),
                            by = list(uuid, ts = as.numeric.factor(ts), type = as.numeric.factor(type))]
           }

           if (phytype == 'hrm') {
           # todo
               d <- data[, list(
                            min = min(as.numeric.factor(min)),
                            max = max(as.numeric.factor(max)),
                            mean = as.integer(round(sum(as.numeric.factor(mean) * as.numeric.factor(count))/sum(as.numeric.factor(count)))),
                            sd = sqrt(sum(as.numeric.factor(sd)^2 * as.numeric.factor(count))/sum(as.numeric.factor(count))),
                            count = as.integer(round(sum(as.numeric.factor(count))))),
                            by = list(uuid, ts = as.numeric.factor(ts), situation = as.numeric.factor(situation))]
           }

           if (phytype == 'sleep') {
              #  uuid | date | status | duration | ratio
              d <- data[, list(duration = duration, status = as.numeric.factor(status), ts = as.numeric.factor(ts), ratio = as.numeric.factor(duration)/sum(as.numeric.factor(duration))), by = list(uuid)]
              d <- d[, list(duration = as.integer(round(mean(as.numeric.factor(duration)))), ratio = sum(ratio)), by = list(uuid, ts, status)][order(uuid, status)]
           }
        }

        #n <- colnames(d)
        #if (ptype == 'week') {
        #   n[which(n=='date')] <- 'wdate'
        #}
        #if (ptype == 'month') {
        #   n[which(n=='date')] <- 'mdate'
        #}
        #colnames(d) <- n
        #ClosePyCasDriver()

        return(d)
     }

     reducer.process.data <- function(key, df) {
        SetupPyCasDriver()
        #uuid | datehour   | situation | activeindex | avghrm    | duration | hrmcount | met
     #   keyspace <- df$keyspace[1]
        df <- data.frame(processData(df, phytype))
        nrows <- nrow(df)
        for (i in 1:nrows) {
            cqlcmd <- getInsertCqlCmd(paste(key, ".", out.tableName, sep=""), df[i, ])
            python.call("cqlexec", cqlcmd)
            #keyval(key, df)
        }

        rm(df)
        gc()
        #ClosePyCasDriver()

        keyval(key, 1)#df[1,])
     }

     backend.parameters = list(hadoop=list(D='mapreduce.job.maps=25', D='mapreduce.job.reduces=10',
#                                      D='yarn.scheduler.minimum-allocation-mb=2048',
                                      D='mapreduce.map.memory.mb=6144',
                                      D='mapreduce.reduce.memory.mb=6144',
#                                      D='mapreduce.map.java.opts=-Xmx2048m',
                                      D='mapreduce.reduce.java.opts=-Xmx2048m'#,
#                                      D='mapreduce.child.java.opts=-Xmx3072m'
                                      ))

     a <- mapreduce(input=input,
#          input.format="text", #make.input.format("csv", sep = "\t"),
#          output.format="text", #make.input.format("csv", sep = "\t"),
          map=mapper.retrieve.data.cas,
          reduce=reducer.process.data,
          output=NULL, backend.parameters=backend.parameters)

     return(a)
}

a <- ProcessContextTable(in.tableName, beginDate, ndays)
#print(from.dfs(a))
