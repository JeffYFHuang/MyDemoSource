require(Storm)

storm = Storm$new(); 
storm$lambda = function(s) { 
  t = s$tuple; 
  t$output = vector(mode="character",length=1);
  clicks = as.numeric(t$input[1]); 
  views = as.numeric(t$input[2]); 
  t$output[1] = rbeta(1, clicks, views - clicks); 
  s$emit(t); 
  #alternative: mark the tuple as failed. 
  s$fail(t);  
} 
storm$run();
