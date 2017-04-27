from cassandra.cluster import Cluster
import json
cluster = Cluster(['172.18.161.100', '172.18.161.101'])
session = cluster.connect();

#def selectAll(keyspace, tblname):
#        session.set_keyspace(keyspace)
#	rows = session.execute('SELECT * FROM ' + tblname)
#        return list(rows)

def setkeyspace(keyspace):
        return session.set_keyspace(keyspace)

def cqlexec(cqlcmd):
        result = session.execute(cqlcmd)
        return list(result)

def cqlexecwithkeyspace(keyspace, cqlcmd):
        session.set_keyspace(keyspace)
        result = session.execute(cqlcmd)
        return list(result)
