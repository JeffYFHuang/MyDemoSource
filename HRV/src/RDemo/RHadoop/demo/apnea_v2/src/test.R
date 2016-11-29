source("HRVFUNS.R")
RecordPath="/media/Data/HRV/wfdbData/apnea-ecg/"
RecordName = "a03"
hrv.wfdb = CreateHRVData()
hrv.wfdb = LoadBeat(hrv.wfdb, fileType = "WFDB", RecordName, RecordPath, annotator = "qrs")
hrv.wfdb = SetVerbose(hrv.wfdb, TRUE)
hrv.wfdb = LoadApneaWFDB(hrv.wfdb, RecordName,Tag="Apnea",  RecordPath)
