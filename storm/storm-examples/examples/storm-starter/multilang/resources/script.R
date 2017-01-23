source("Storm.R")
storm = Storm$new()
storm$lambda<-function(s){
   tuple<-s$tuple;
   
   if (!is.character(tuple$input[1])) {
      words <- strsplit(tuple$input[1], " ")
      
      for (word in words) {
          tuple$output[1]<-as.list(word)
          s$emit(tuple)
      }
   }
}
storm$run()
