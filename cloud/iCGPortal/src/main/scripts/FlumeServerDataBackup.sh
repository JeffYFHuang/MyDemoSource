#!/bin/sh

#Purpose of this Script is to Backup of Important Data from Flume Server

SRCDIR=/complete/source/data/path # Location of Important Data Directory (Source of backup).
DESDIR=/var/lib/flume/backup/	  # Destination of backup file.

ADMINEMAILID="sarat.moosinada@aricent.com"

MYSQLDBHOST="172.31.157.58"

MYSQLDBNAME="ext_icgcloud"

MYSQLDBPORT=3306

MYSQLDBUSER="root"

MYSQLDBPWD="MySq&23DF1$%@#^!"

DATE=`date +%Y%m%d%H%M%S`

#START
FILENAME=backup-$DATE.tar.gz    # Here i define Backup file name format.
tar -cpzf $DESDIR/$FILENAME $SRCDIR 
#END

echo "Message Body" | mail -s "Message Subject" $ADMINEMAILID

SQLQUERY="INSERT INTO activity_log (activity_log_id, name, username, user_role, created_date, level, action, summary, ipaddress) VALUES(NULL, 'CRON Jobs', 'auto', 'super_admin', NOW(), 'info', 'DatabaseBackup', 'Flume Data Backup '. $DESDIR, '')"

#You must have DB Access permission for the below script to execute.
#mysql -h$MYSQLDBHOST -u$MYSQLDBUSER -p$MYSQLDBPWD -P$MYSQLDBPORT -D$MYSQLDBNAME <<EOF 
#$SQL_QUERY
#EOF

echo "End of script"


