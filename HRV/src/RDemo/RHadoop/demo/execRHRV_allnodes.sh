#hadoop fs -rm /HRVData/physiobank/*.hrv
#R CMD BATCH --no-save --no-restore preprocessBeatOnHdFS.R preprocessBeatOnHdFS2.Rout
hadoop fs -rm /HRVData/physiobank/*.hrv
scp preprocessBeatOnHdFS.R data1:/home/hduser/RDemo/RHadoop/demo/
scp preprocessBeatOnHdFS.R data2:/home/hduser/RDemo/RHadoop/demo/
#ssh hduser@master "cd RDemo/RHadoop/demo;R CMD BATCH --no-save --no-restore preprocessBeatOnHdFS.R preprocessBeatOnHdFS.Rout" &
ssh hduser@data1 "cd RDemo/RHadoop/demo;R CMD BATCH --no-save --no-restore preprocessBeatOnHdFS.R preprocessBeatOnHdFS.Rout" &
ssh hduser@data2 "cd RDemo/RHadoop/demo;R CMD BATCH --no-save --no-restore preprocessBeatOnHdFS.R preprocessBeatOnHdFS.Rout" &
