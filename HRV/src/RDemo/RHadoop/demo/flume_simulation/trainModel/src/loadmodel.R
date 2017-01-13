Sys.setenv("HADOOP_CMD"="/media/data/hadoop_ecosystem/hadoop/bin/hadoop")
Sys.setenv("HADOOP_STREAMING"="/media/data/hadoop_ecosystem/hadoop/share/hadoop/tools/lib/hadoop-streaming-2.6.0.jar")

require(rmr2)
require(caret)

models.dfs.path = "/data/models"

path = dfs.ls(models.dfs.path)$path

rfs = NULL
for (f in path[-1]) {
    print(f)
    rf = from.dfs(f)
    rfs = c(rfs, rf$val)
}

