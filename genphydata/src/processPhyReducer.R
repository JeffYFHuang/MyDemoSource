#! /usr/bin/env Rscript

# reducer.R - Wordcount program in R
# script for Reducer (R-Hadoop integration)
require(rjson)
require(rPython)

GetPredictedMaxHR <- function (age) {
   return(220 - age)
}

trimWhiteSpace <- function(line) gsub("(^ +)|( +$)", "", line)

RoundValues <- function (values) {
    fields <- c("situation", "ts", "min", "max", "count", "duration", "hrmcount", "activeindex", "met", "type", "count", "distance", "cal", "status", "duration")

    values[which(names(values) != "uuid")] <- as.numeric(values[which(names(values) != "uuid")])
    values[names(values) %in% fields] <- as.integer(round(as.numeric(values[names(values) %in% fields])))
    return(values[!is.na(values)])
}

getInsertCqlCmd <- function(tblname, values) {
    if (length(values) <= 0) return(NULL)

    values <- RoundValues(values)

    cqlcmd <- paste("INSERT INTO", tblname)
    colnames <- names(values)
    colnames[which(colnames=="datehour")] <- "ts"
    cqlcmd <- paste(cqlcmd, "(", paste(colnames[which(colnames!='uuid')], collapse=", "), ",", colnames[which(colnames=='uuid')], ")")
    cqlcmd <- paste(cqlcmd, " VALUES (", paste(values[which(colnames != "uuid")], collapse=", "))
    cqlcmd <- paste(cqlcmd, ", '", values$uuid, "')", sep="")

    return(cqlcmd) 
}

#env <- new.env(hash = TRUE)
python.load("src/funcs.py", get.exception = T)

con <- file("stdin", open = "r")
while (length(line <- readLines(con, n = 1, warn = FALSE)) > 0) {
    data <- NULL
    tryCatch({
               data <- fromJSON(line)
            }, warning = function(war) {
               return(NULL)
            }, error = function(err) {
               # error handler picks up where error was generated
               return(NULL)
            }, finally = {
               # NOTE:  Finally is evaluated in the context of of the inital
               # NOTE:  tryCatch block and 'e' will not exist if a warning
               # NOTE:  or error occurred.
               #print(paste("e =",e))
    })

    if(is.null(data)) next
    #data <- fromJSON(line)

    keyspace <- data$keyspace
    for (n in names(data)[-c(1, 2)]) {
        for (d in data[[n]]) {
           d$uuid <- data$uuid
           # for hour table
           cqlcmd <- NULL
           if (n != "abn") {
              cqlcmd <- getInsertCqlCmd(paste(keyspace, ".", n, "_hour", sep=""), d)
           } else {
              if (d$ab == TRUE) {
                 d$ab <- NULL
                 d$hr_peak_rate <- NULL
                 cqlcmd <- getInsertCqlCmd(paste(keyspace, ".", n, "_hrm", sep=""), d)
              }
           }
           #cat(cqlcmd, "\n")
           if(!is.null(cqlcmd)) 
           python.call("cqlexec", cqlcmd)
        }
    }    
    #cat(line, "\n")
}
close(con)
