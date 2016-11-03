library(RHRV)
library(rhdfs)
source("HRVFUNS.R")

hdfs.init()
recordPath = "/home/hduser/RDemo/wfdbData/www.physionet.org/physiobank/database/apnea-ecg/"
recordNames = list.files(path=recordPath, pattern=".qrs")
recordNames = unlist(strsplit(recordNames, '.qrs'))

for (recordName in recordNames) {
    hrv.wfdb = CreateHRVData()
    hrv.wfdb = SetVerbose(hrv.wfdb, TRUE)

    cat("recordName: ", recordName, "\n")    
    hrv.wfdb = LoadBeatWFDB(hrv.wfdb, recordName, recordPath, annotator = "qrs")
    putBeatsDataToHDFS(hrv.wfdb, "/HRVData/physiobank/", recordName)
    
#    if (file.exists(paste(recordPath, recordName, ".apn", sep=""))) {
#       cat("apn file exist!!!!!!!\n")
#       hrv.wfdb = LoadApneaWFDB(hrv.wfdb, recordName, Tag="Apnea", recordPath)
#       putEpisodesDataToHDFS(hrv.wfdb, "/HRVData/physiobank/", recordName)
#    }
}
