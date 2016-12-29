require(compiler)
enableJIT(3)

input <- NULL #"/data/hrvdata"
output <- NULL #"/data/train_output"

args=(commandArgs(TRUE))

if (length(args) != 0){
  for (i in 1:length(args)){
     eval(parse(text=args[[i]]))
  }
}

execution = "Exe. Rscript runRc.R input=\"'/data/beatsdata'\" output=\"'/data/beats2hrv'\"";
if (is.null(input))
   stop(paste("please provide input path!", execution))
if (is.null(output))
   stop(paste("please provide output path!", execution))

loadcmp("beats2HRV.Rc")
