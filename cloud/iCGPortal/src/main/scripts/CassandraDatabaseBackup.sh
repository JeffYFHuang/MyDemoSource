#!/bin/sh

export PATH=/sbin:/bin:/usr/sbin:/usr/bin:/opt/java/bin

BACKUPDIRECTORY="/var/lib/cassandra/backup/"

ADMINEMAILID="sarat.moosinada@aricent.com"

MYSQLDBHOST="172.31.157.58"

MYSQLDBNAME="ext_icgcloud"

MYSQLDBPORT=3306

MYSQLDBUSER="root"

MYSQLDBPWD="MySq&23DF1$%@#^!"

DATE=`date +%Y%m%d%H%M%S`

SNAME="snapshot-$DATE"

if [ ! -d "$BACKUPDIRECTORY" ]; then
        echo "Directory $BACKUPDIRECTORY not found, creating..."
        mkdir $BACKUPDIRECTORY
fi

if [ ! -d "$BACKUPDIRECTORY" ]; then
        echo "Directory $BACKUPDIRECTORY not found, exit..."
        exit
fi

echo
echo "Snapshot name: $SNAME"
echo "Clear all snapshots"
nodetool -h 127.0.0.1 clearsnapshot

cd $BACKUPDIRECTORY
pwd
rm -rf *

echo "Taking snapshot"
nodetool -h 127.0.0.1 snapshot -t $SNAME
SFILES=`ls -1 -d /var/lib/cassandra/data/*/*/snapshots/$SNAME`
for f in $SFILES
do
        echo "Process snapshot $f"
        TABLE=`echo $f | awk -F/ '{print $(NF-2)}'`
        KEYSPACE=`echo $f | awk -F/ '{print $(NF-3)}'`

        if [ ! -d "$BACKUPDIRECTORY/$SNAME" ]; then
                mkdir $BACKUPDIRECTORY/$SNAME
        fi

        if [ ! -d "$BACKUPDIRECTORY/$SNAME/$KEYSPACE" ]; then
                mkdir $BACKUPDIRECTORY/$SNAME/$KEYSPACE
        fi

        mkdir $BACKUPDIRECTORY/$SNAME/$KEYSPACE/$TABLE
        find $f -maxdepth 1 -type f -exec mv -t $BACKUPDIRECTORY/$SNAME/$KEYSPACE/$TABLE/ {} +
done

echo "Clear Incremental Backups"
SFILES=`ls -1 -d /var/lib/cassandra/data/*/*/backups/`
for f in $SFILES
do
        echo "Clear $f"
        rm -f $f*
done

echo "Message Body" | mail -s "Message Subject" $ADMINEMAILID

##mysql -h$MYSQLDBHOST -u$MYSQLDBUSER -p$MYSQLDBPWD $MYSQLDBNAME -e INSERT INTO activity_lo(activity_log_id, name, username, user_role, created_date, level, action, summary, ipaddress) VALUES(NULL, 'CRON Jobs', 'auto', 'super_admin', NOW(), 'info', 'Create', 'Database backup '. $BACKUPDIRECTORY, '');

SQLQUERY="INSERT INTO activity_log (activity_log_id, name, username, user_role, created_date, level, action, summary, ipaddress) VALUES(NULL, 'CRON Jobs', 'auto', 'super_admin', NOW(), 'info', 'DatabaseBackup', 'Database backup '. $BACKUPDIRECTORY, '')"

mysql -h$MYSQLDBHOST -u$MYSQLDBUSER -p$MYSQLDBPWD -P$MYSQLDBPORT -D$MYSQLDBNAME <<EOF 
$SQL_QUERY
EOF

echo "End of script"


