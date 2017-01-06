#!/usr/bin/env Rscript
require(RHRV)
require(rjson)

recordPath = "/media/data/wfdbData/apnea-ecg/"
recordNames = list.files(path=recordPath, pattern=".qrs")
recordNames = unlist(strsplit(recordNames, '.qrs'))

for (recordName in recordNames) {
    data = list()
    hrv.wfdb = CreateHRVData()
    hrv.wfdb = SetVerbose(hrv.wfdb, FALSE)

    hrv.wfdb = LoadBeatWFDB(hrv.wfdb, recordName, recordPath, annotator = "qrs")
    data$Beat = hrv.wfdb$Beat$Time

    if (file.exists(paste(recordPath, recordName, ".apn", sep=""))) {
       print(recordName)
       hrv.wfdb = LoadApneaWFDB(hrv.wfdb, recordName, Tag="Apnea", recordPath)
       data$Episodes = hrv.wfdb$Episodes
    }

    len = length(data$Beat) #216000*5
    while (length(data$Beat) <= len) {
       TailBeat = tail(data$Beat, 1)

       if (length(data$Beat[data$Beat < len - TailBeat])==0) break

       data$Beat = c(data$Beat, TailBeat + data$Beat[data$Beat < len - TailBeat])

       b = hrv.wfdb$Episodes
       b[,1] = b[,1]+TailBeat
       data$Episodes = rbind(data$Episodes, b)
     }

#    cat(recordName, "\n")
    cat(recordName, "\t", toJSON(data), "\n", sep="")
}
