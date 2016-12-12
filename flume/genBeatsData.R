#!/usr/bin/env Rscript
require(RHRV)
require(rjson)

recordPath = "/media/Data/wfdbData/apnea-ecg/"
recordNames = list.files(path=recordPath, pattern=".qrs")
recordNames = unlist(strsplit(recordNames, '.qrs'))

for (recordName in recordNames) {
    data = list()
    hrv.wfdb = CreateHRVData()
    hrv.wfdb = SetVerbose(hrv.wfdb, FALSE)

    hrv.wfdb = LoadBeatWFDB(hrv.wfdb, recordName, recordPath, annotator = "qrs")
    Beat = hrv.wfdb$Beat$Time
    TailBeat = tail(Beat, 1)
    data$Beat = c(Beat, TailBeat + Beat[Beat < 172800 - TailBeat])
    
    if (file.exists(paste(recordPath, recordName, ".apn", sep=""))) {
       hrv.wfdb = LoadApneaWFDB(hrv.wfdb, recordName, Tag="Apnea", recordPath)
       a = hrv.wfdb$Episodes
       b = a
       b[,1] = b[,1]+TailBeat
       data$Episodes = rbind(a, b)
    }
    cat(recordName, "\t", toJSON(data), "\n", sep="")
}
