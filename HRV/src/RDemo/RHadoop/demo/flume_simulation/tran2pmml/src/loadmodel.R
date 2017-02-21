Sys.setenv("HADOOP_CMD"="/media/data/hadoop_ecosystem/hadoop/bin/hadoop")
Sys.setenv("HADOOP_STREAMING"="/media/data/hadoop_ecosystem/hadoop/share/hadoop/tools/lib/hadoop-streaming-2.6.0.jar")

require(rmr2)
require(caret)
require(r2pmml)
require(randomForest)

models.dfs.path = "/data/train_output"
path = dfs.ls(models.dfs.path)$path

#env <- new.env(hash = TRUE)
#env <- Sys.getenv()

rfs = NULL
#path <- path[2:3]
for (i in 2:5) { #length(path)) {
#    rfs = NULL
    print(path[i])
    rf = from.dfs(path[i])

    for (v in rf$val){
#        r2pmml(combine(list(v$mod)), "rf.pmml")
#        r2pmml(v$mod, paste("rf", i, ".pmml", sep=""))
        v$mod$votes = 1 #make combine work
        if (is.null(rfs))
           rfs = v$mod
        else
           rfs = combine(rfs, v$mod)
    }

#    if (!exists(f, envir = env, inherits = FALSE))
#       assign(f, rfs, envir = env)

#    print(get(f, envir = env))
#r2pmml(rf$val, paste("knn", i, ".pmml", sep=""))
}

#combine(list(rfs))
rfs$forest$nodepred=as.integer(rfs$forest$nodepred)
rfs
r2pmml(rfs, "rf.pmml")
#print(rfs)

#features<-from.dfs("/data/feature_output")
#features<-features$key[which(features$val>=0.9)]
