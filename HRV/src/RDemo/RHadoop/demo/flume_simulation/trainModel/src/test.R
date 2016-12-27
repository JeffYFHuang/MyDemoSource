require(rmr2)
require(rhdfs)

rmr.options(backend = 'hadoop')
small.ints = to.dfs(1:100)
mapr = mapreduce(input = small.ints, 
                 map = function(k,v) cbind(v,v^2)) 
result = from.dfs(mapr)
result
