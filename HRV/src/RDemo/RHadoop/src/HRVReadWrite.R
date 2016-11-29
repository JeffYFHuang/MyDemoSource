library(rhdfs)

hdfs.init()
f = hdfs.file("/HRVData/example.beats","r")

c = "";
repeat {
     m = hdfs.read(f)
     cat(is.vector(m))
     if(!is.vector(m)) break

     c = paste(c, rawToChar(m), sep="")
     
     if ( is.null(c) ) break
}

library(RHRV)
HRVData = HRVData = CreateHRVData()
HRVData = LoadBeatAscii(HRVData, textConnection(c), RecordPath = ".")

