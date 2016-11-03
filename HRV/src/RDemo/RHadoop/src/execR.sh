#startTime = 0
#endTime = 0
#size=300 #seconds
#overlap = 0 #seconds: 0 -> non-overlap;
#folder = "/HRVData/"
#fileName = "example.beats"

R CMD BATCH --no-save --no-restore '--args overlap= b=c(2,5,6)' test.R test.out &
