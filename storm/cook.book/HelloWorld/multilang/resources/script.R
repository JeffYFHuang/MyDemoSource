source("Storm.R")

storm=Storm$new()

storm$lambda = function(s) {
#argument 's' is the Storm object.
#get the current Tuple object.
t = s$tuple;
#optional: acknowledge receipt of the tuple.
#s$ack(t);
#optional: log a message.
s$log(c("processing tuple=",t$id));
#create contrived tuples to illustrate output.
#create 1st tuple...
#t$output = vector(mode="character",length=1);
t$output[1] = t$input[1];
#...and emit it.
s$emit(t);
#alternative/optional: mark the tuple as failed.
s$fail(t);
};
#enter the main tuple-processing loop.
storm$run();
