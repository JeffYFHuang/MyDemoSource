#! /usr/bin/env Rscript

# mapper.R - Wordcount program in R
# script for Mapper (R-Hadoop integration)
require(rjson)
require(rPython)

trimWhiteSpace <- function(line) gsub("(^ +)|( +$)", "", line)
splitIntoWords <- function(line) unlist(strsplit(line, "[[:space:]]+"))
getInsertCqlCmd <- function(tblname, colnames, values) {
    if (length(colnames) != length(na.omit(values))) return(NULL)
    cqlcmd <- paste("INSERT INTO", tblname)
    cqlcmd <- paste(cqlcmd, "(", paste(colnames, collapse=", "), ")")

    if (length(which(colnames == "uuid")) == 1)
       cqlcmd <- paste(cqlcmd, " VALUES ('", values[which(colnames == "uuid")], "',", sep = "")

    values[which(colnames != "uuid")] <- round(as.numeric(values[which(colnames != "uuid")]))
    cqlcmd <- paste(cqlcmd, paste(values[which(colnames != "uuid")], collapse=", "), ")")

    return(cqlcmd) 
}

filepath <- Sys.getenv("map_input_file")

keyspace <- "elmtest"
if (nchar(filepath) > 0) {
   path <- unlist(strsplit(filepath, split="/"))
   keyspace <- path[6] 
}

#cat(keyspace, "\n")
python.load("src/funcs.py", get.exception = T)
python.call("setkeyspace", keyspace)

tables <- c("step", "sleep", "context", "hrm")
## **** could wo with a single readLines or in blocks
con <- file("stdin", open = "r")
count <- 1
while (length(line <- readLines(con, n = 1, warn = FALSE)) > 0) {
    data <- fromJSON(line)
    uuid <- data$uuid
    for (x in data$data) {
        xx <- x[which(names(x)!="hrm")]
 
        if (xx["context"] != 5 && xx["context"] != 0) {
           colnames <- c("uuid", "timestamp", "situation", "duration", "avghrm")

           hrm_avg = 0
           if (length(which(names(x) == 'hrm') > 0))
              hrm_avg = mean(matrix(unlist(x$hrm), 3)[2, ])

           values <- unlist(c(uuid, xx["timestamp"], xx["context"], xx["duration"], hrm_avg))
           cqlcmd <- getInsertCqlCmd("context", colnames, values)
#           cat(cqlcmd, "\n")
           python.call("cqlexec", cqlcmd)
           if (xx["context"] == 2 || xx["context"] == 3) {
              colnames = c("uuid", "timestamp", "type", "count", "distance", "cal")
              values = unlist(c(uuid, xx["timestamp"], xx["context"], xx["count"], xx["distance"], xx["cal"]))
              cqlcmd <- getInsertCqlCmd("step", colnames, values)
#              cat(cqlcmd, "\n")
              python.call("cqlexec", cqlcmd)
           }
        } else if (xx["context"] == 5) {
           colnames = c("uuid", "timestamp", "status", "duration")
           values = unlist(c(uuid, xx["timestamp"], xx["status"], xx["duration"]))
           cqlcmd <- getInsertCqlCmd("sleep", colnames, values)
#           cat(cqlcmd, "\n")
           python.call("cqlexec", cqlcmd)
        }

        if (length(which(names(x) == 'hrm') > 0)) {
           for (hrm in x$hrm) {
               colnames <- c("uuid", names(hrm))
               values <- unlist(c(uuid, round(unlist(hrm))))
               cqlcmd <- getInsertCqlCmd("hrm", colnames, values)
#               cat(cqlcmd, "\n")
               python.call("cqlexec", cqlcmd)
           }
        }
    }
    cat(uuid, "\n");
}

close(con)
