Sys.setenv("HADOOP_CMD"="/media/data/hadoop_ecosystem/hadoop/bin/hadoop")
Sys.setenv("HADOOP_STREAMING"="/media/data/hadoop_ecosystem/hadoop/share/hadoop/tools/lib/hadoop-streaming-2.6.0.jar")

require(rmr2)
require(caret)

models.dfs.path = "/data/models"
len = length(dfs.ls(models.dfs.path))

rfs = NULL
for (i in 1:len) {
    filename = paste("part-0000", (i-1), sep="")
    print(filename)
    path = paste(models.dfs.path, "/", filename, sep="")
    rf = from.dfs(path)
    rfs = c(rfs, rf$val)
}

print(rfs)
