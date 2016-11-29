library(rjson)
json_data <- fromJSON(paste(readLines("example.beats.json"), collapse=""))
size = length(unlist(json_data$CaculatedData))
row.size = length(unlist(json_data$CaculatedData[[1]]))
col.names = names(json_data$CaculatedData[[1]])
HRV.Features = t(array(as.numeric(unlist(json_data$CaculatedData)), c(row.size, size/row.size)))
HRV.Features = data.frame(HRV.Features)
colnames(HRV.Features) = col.names
