library(methods);
library(rjson);

    buf = "";

    x.stdin = file("stdin");
    open(x.stdin);

    cat(paste('{"pid": ',Sys.getpid(),'}',"\nend\n",sep=""));
    cat('{"command": "emit", "anchors": [], "tuple": ["JEFF bolt initializing"]}\nend\n');

    while (TRUE) {
      rl = as.character(readLines(con=x.stdin,n=1,warn=FALSE));
      if (length(rl) == 0) {
        close(x.stdin);
        break;
      }
      if (rl == "end" || rl == "END") {
        print(buf);
        t=fromJSON(buf);
        if (is.list(t)) {
           if (!is.null(t$tuple))
              cat(paste('{"command": "emit", "anchors": [], "tuple": ["',t$tuple,'"]}\nend\n'));
        }
        buf = "";
      }
      else {
        buf = paste(buf,rl,"\n",sep="");
      }
    }
