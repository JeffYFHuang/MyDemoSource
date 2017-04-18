from cassandra.cluster import Cluster
import json
cluster = Cluster(['10.0.0.44'])
session = cluster.connect();

#def selectAll(keyspace, tblname):
#        session.set_keyspace(keyspace)
#	rows = session.execute('SELECT * FROM ' + tblname)
#        return list(rows)

def cqlexec(keyspace, cqlcmd):
        session.set_keyspace(keyspace)
        result = session.execute(cqlcmd)
        return list(result)
