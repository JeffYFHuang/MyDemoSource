#startTime = 0
#endTime = 0
#size=300 #seconds
#overlap = 0 #seconds: 0 -> non-overlap;
#folder = "/HRVData/"
#fileName = "example.beats"
rm *.Rout
R CMD BATCH --no-save --no-restore "--args fileName=\"$1\"" preprocessOneBeatDataOnHdFS.R preprocessOneBeatDataOnHdFS.Rout
R CMD BATCH --no-save --no-restore "--args fileName=\"$1\"" readHRVOnHDFS.R readHRVOnHDFS.Rout
