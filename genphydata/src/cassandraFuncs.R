require(rPython)

python.load("src/funcs.py", get.exception = T)

# get keyspaces for schools
getKeyspaces <- function () {
    keyspaces <- CqlExec('SELECT keyspace_name FROM system_schema.keyspaces')
    keyspaces <- keyspaces[grepl("elm", keyspaces)]
    return (keyspaces)
}

CreateKeySpacesAndTables <- function (school.ids) {
    for (sid in school.ids) {
        CreateKeySpaceAndTables(sid)
    }
}

CreateKeySpaceAndTables <- function (sid) {
    CreateKeySpace(sid)
    CreateHourTable(sid)
    CreateDailyTable(sid)
    CreateWeeklyTable(sid)
    CreateMonthlyTable(sid)
}

CreateSchoolsInfo <- function (keyspace_name) {
    CreateKeySpace(keyspace_name)
    cmd <- paste("CREATE TABLE IF NOT EXISTS", paste(keyspace_name, ".schools", table_name, sep=""), "(sid varchar, PRIMARY KEY (sid))")
    CqlExec(cmd)

    cmd <- paste("CREATE TABLE IF NOT EXISTS", paste(keyspace_name, ".sidsuuids", sep=""), "(sid varchar, uuid varchar, PRIMARY KEY (sid, uuid))")
    CqlExec(cmd)
}

CreateKeySpace <- function (sid) {
    cmd <- paste("CREATE KEYSPACE IF NOT EXISTS", sid, "WITH REPLICATION = { 'class' : 'NetworkTopologyStrategy', 'datacenter1' : 3 }")
    CqlExec(cmd)
}

CqlExec <- function (cmd) {
    cat (cmd, ";\n")
    result <- tryCatch({
                          python.call("cqlexec", cmd)
                       }, warning = function(war) {
                          print(paste("MY_WARNING:  ",war))
                          return(war)
                       }, error = function(err) {
                          print(err)
                          # error handler picks up where error was generated
                          return(err)
                       }, finally = {
                          print("final")
                          # NOTE:  Finally is evaluated in the context of of the inital
                          # NOTE:  tryCatch block and 'e' will not exist if a warning
                          # NOTE:  or error occurred.
                          #print(paste("e =",e))
                      })
    return (result)
}

CreateHourTable <- function (sid) {
    cmd <- paste("CREATE TABLE IF NOT EXISTS", paste(sid, ".step_hour", sep=""), "(uuid varchar, ts bigint, type int, count int, distance int, cal int, PRIMARY KEY (uuid, ts, type))")
    CqlExec(cmd)

    cmd <- paste("CREATE TABLE IF NOT EXISTS", paste(sid, ".sleep_hour", sep=""), "(uuid varchar, ts bigint, status int, duration int, ratio float, PRIMARY KEY (uuid, ts, status))")
    CqlExec(cmd)

    cmd <- paste("CREATE TABLE IF NOT EXISTS", paste(sid, ".context_hour", sep=""), "(uuid varchar, ts bigint, situation int, duration int, avghrm float, hrmcount int, activeindex int, met int, PRIMARY KEY (uuid, ts, situation))")
    CqlExec(cmd)

    cmd <- paste("CREATE TABLE IF NOT EXISTS", paste(sid, ".hrm_hour", sep=""), "(uuid varchar, ts bigint, situation int, min int, max int, mean float, sd float, count int, PRIMARY KEY (uuid, ts, situation))")
    CqlExec(cmd)
}

CreateDailyTable <- function (sid) {
    cmd <- paste("CREATE TABLE IF NOT EXISTS", paste(sid, ".step_date", sep=""), "(uuid varchar, ts bigint, type int, count int, distance int, cal int, PRIMARY KEY (uuid, ts, type))")
    CqlExec(cmd)

    cmd <- paste("CREATE TABLE IF NOT EXISTS", paste(sid, ".sleep_date", sep=""), "(uuid varchar, ts bigint, status int, duration int, ratio float, PRIMARY KEY (uuid, ts, status))")
    CqlExec(cmd)

    cmd <- paste("CREATE TABLE IF NOT EXISTS", paste(sid, ".context_date", sep=""), "(uuid varchar, ts bigint, situation int, duration int, avghrm int, hrmcount int, activeindex int, met float, PRIMARY KEY (uuid, ts, situation))")
    CqlExec(cmd)

    cmd <- paste("CREATE TABLE IF NOT EXISTS", paste(sid, ".hrm_date", sep=""), "(uuid varchar, ts bigint, situation int, min int, max int, mean int, sd float, count int, PRIMARY KEY (uuid, ts, situation))")
    CqlExec(cmd)
}

CreateWeeklyTable <- function (sid) {
    cmd <- paste("CREATE TABLE IF NOT EXISTS", paste(sid, ".step_week", sep=""), "(uuid varchar, ts bigint, type int, count int, distance int, cal int, PRIMARY KEY (uuid, ts, type))")
    CqlExec(cmd)

    cmd <- paste("CREATE TABLE IF NOT EXISTS", paste(sid, ".sleep_week", sep=""), "(uuid varchar, ts bigint, status int, duration int, ratio float, PRIMARY KEY (uuid, ts, status))")
    CqlExec(cmd)

    cmd <- paste("CREATE TABLE IF NOT EXISTS", paste(sid, ".context_week", sep=""), "(uuid varchar, ts bigint, situation int, duration int, avghrm int, hrmcount int, activeindex int, met float, PRIMARY KEY (uuid, ts, situation))")
    CqlExec(cmd)

    cmd <- paste("CREATE TABLE IF NOT EXISTS", paste(sid, ".hrm_week", sep=""), "(uuid varchar, ts bigint, situation int, min int, max int, mean int, sd float, count int, PRIMARY KEY (uuid, ts, situation))")
    CqlExec(cmd)
}

CreateMonthlyTable <- function (sid) {
    cmd <- paste("CREATE TABLE IF NOT EXISTS", paste(sid, ".step_month", sep=""), "(uuid varchar, ts bigint, type int, count int, distance int, cal int, PRIMARY KEY (uuid,ts, type))")
    CqlExec(cmd)

    cmd <- paste("CREATE TABLE IF NOT EXISTS", paste(sid, ".sleep_month", sep=""), "(uuid varchar, ts bigint, status int, duration int, ratio float, PRIMARY KEY (uuid, ts, status))")
    CqlExec(cmd)

    cmd <- paste("CREATE TABLE IF NOT EXISTS", paste(sid, ".context_month", sep=""), "(uuid varchar, ts bigint, situation int, duration int, avghrm int, hrmcount int, activeindex int, met float, PRIMARY KEY (uuid, ts, situation))")
    CqlExec(cmd)

    cmd <- paste("CREATE TABLE IF NOT EXISTS", paste(sid, ".hrm_month", sep=""), "(uuid varchar, ts bigint, situation int, min int, max int, mean int, sd float, count int, PRIMARY KEY (uuid, ts, situation))")
    CqlExec(cmd)
}

DropKeySpace <- function (sid) {
    cmd <- paste("DROP KEYSPACE IF EXISTS", sid)
    CqlExec(cmd)
}

DropKeySpaces <- function (school.ids) {
    for (sid in school.ids) {
        DropKeySpace (sid)
    }
}

DropTable <- function (sid, tablename) {
    cmd <- paste("DROP TABLE IF EXISTS ", sid, ".", tablename, sep="")
    CqlExec(cmd)
}

DropKeySpacesTableColumn <- function (school.ids, tablename, column) {
    for (sid in school.ids) {
        DropTableColumn (sid, tablename, column)
    }
}

DropTableColumn <- function (sid, tablename, column) {
    cmd <- paste("ALTER TABLE ", sid, ".", tablename, " DROP ", column, sep="")
    CqlExec(cmd)
}

truncateKeyspacesTables <- function (school.id) {
    tables <- c("context", "step", "sleep", "hrm")
    tails <- c("hour", "date", "week", "month")

    for (sid in school.id) {
        for (tbl in tables) {
            tblNames <- paste(tbl, "_", tails, sep = "")
            for (tn in tblNames) {
                truncateKeyspaceTable (sid, tn)
            }
        }
    }
}

truncateKeyspaceTable <- function (sid, tableName) {
    cmd <- paste("TRUNCATE ", sid, ".", tableName, sep="")
    CqlExec(cmd)
}

AddTableColumn <- function (sid, tablename, column, type) {
    #ALTER TABLE elm533611.sleep_month ADD ratio float ;
    cmd <- paste("ALTER TABLE ", sid, ".", tablename, " ADD ", column, " ", type, sep="")
    CqlExec(cmd)
}

AddKeySpacesTableColumn <- function (school.ids, tablename, column, type) {
    for (sid in school.ids) {
        AddTableColumn (sid, tablename, column, type)
    }
}

RenameTableColumn <- function (sid, tablename, oldname, newname) {
    #ALTER TABLE elm533611.sleep_month ADD ratio float ;
    cmd <- paste("ALTER TABLE ", sid, ".", tablename, " RENAME ", oldname, " TO ", newname, sep="")
    CqlExec(cmd)
}

RenameKeySpacesTableColumn <- function (school.ids, tablename, oldname, newname) {
    for (sid in school.ids) {
        RenameTableColumn (sid, tablename, oldname, newname)
    }
}
