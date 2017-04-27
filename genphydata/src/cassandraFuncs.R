require(rPython)

python.load("src/funcs.py", get.exception = T)

CreateKeySpacesAndTables <- function (school.ids) {
    for (sid in school.ids) {
        CreateKeySpaceAndTables(sid)
    }
}

CreateKeySpaceAndTables <- function (sid) {
    CreateKeySpace(sid)
    CreateTimeTable(sid)
    CreateDailyTable(sid)
    CreateWeeklyTable(sid)
    CreateMonthlyTable(sid)
}

CreateKeySpace <- function (sid) {
    cmd <- paste("CREATE KEYSPACE IF NOT EXISTS", sid, "WITH REPLICATION = { 'class' : 'NetworkTopologyStrategy', 'datacenter1' : 3 }")
    CqlExec(cmd)
}

CqlExec <- function (cmd) {
    cat (cmd, "\n")
    python.call("cqlexec", cmd)
}

CreateTimeTable <- function (sid) {
    cmd <- paste("CREATE TABLE IF NOT EXISTS", paste(sid, ".step", sep=""), "(uuid varchar, timestamp bigint, type int, count int, distance int, cal int, PRIMARY KEY (uuid, timestamp))")
    CqlExec(cmd)

    cmd <- paste("CREATE TABLE IF NOT EXISTS", paste(sid, ".sleep", sep=""), "(uuid varchar, timestamp bigint, status int, duration int, PRIMARY KEY (uuid, timestamp))")
    CqlExec(cmd)

    cmd <- paste("CREATE TABLE IF NOT EXISTS", paste(sid, ".context", sep=""), "(uuid varchar, timestamp bigint, situation int, duration int, avghrm int, PRIMARY KEY (uuid, timestamp))")
    CqlExec(cmd)

    cmd <- paste("CREATE TABLE IF NOT EXISTS", paste(sid, ".hrm", sep=""), "(uuid varchar, timestamp bigint, hrm_report int, hr_peak_rate int, PRIMARY KEY (uuid, timestamp))")
    CqlExec(cmd)
}

CreateDailyTable <- function (sid) {
    cmd <- paste("CREATE TABLE IF NOT EXISTS", paste(sid, ".step_date", sep=""), "(uuid varchar, date bigint, type int, count int, distance int, cal int, PRIMARY KEY (uuid, date, type))")
    CqlExec(cmd)

    cmd <- paste("CREATE TABLE IF NOT EXISTS", paste(sid, ".sleep_date", sep=""), "(uuid varchar, date bigint, status int, duration int, PRIMARY KEY (uuid, date, status))")
    CqlExec(cmd)

    cmd <- paste("CREATE TABLE IF NOT EXISTS", paste(sid, ".context_date", sep=""), "(uuid varchar, date bigint, situation int, duration int, avghrm int, activeindex int, met float, PRIMARY KEY (uuid, date, situation))")
    CqlExec(cmd)

    cmd <- paste("CREATE TABLE IF NOT EXISTS", paste(sid, ".hrm_date", sep=""), "(uuid varchar, date bigint, min int, max int, mean int, median int, sd float, PRIMARY KEY (uuid, date))")
    CqlExec(cmd)
}

CreateWeeklyTable <- function (sid) {
    cmd <- paste("CREATE TABLE IF NOT EXISTS", paste(sid, ".step_week", sep=""), "(uuid varchar, year int, week int, type int, avgcount int, avgdistance int, avgcal int, PRIMARY KEY (uuid, year, week, type))")
    CqlExec(cmd)

    cmd <- paste("CREATE TABLE IF NOT EXISTS", paste(sid, ".sleep_week", sep=""), "(uuid varchar, year int, week int, status int, avgduration int, PRIMARY KEY (uuid, year, week, status))")
    CqlExec(cmd)

    cmd <- paste("CREATE TABLE IF NOT EXISTS", paste(sid, ".context_week", sep=""), "(uuid varchar, year int, week int, situation int, avgduration int, avghrm int, activeindex int, avgmet float, PRIMARY KEY (uuid, year, week, situation))")
    CqlExec(cmd)

    cmd <- paste("CREATE TABLE IF NOT EXISTS", paste(sid, ".hrm_week", sep=""), "(uuid varchar, year int, week int, min int, max int, mean int, median int, sd float, PRIMARY KEY (uuid, year, week))")
    CqlExec(cmd)
}

CreateMonthlyTable <- function (sid) {
    cmd <- paste("CREATE TABLE IF NOT EXISTS", paste(sid, ".step_week", sep=""), "(uuid varchar, year int, month int, type int, avgcount int, avgdistance int, avgcal int, PRIMARY KEY (uuid, year, month, type))")
    CqlExec(cmd)

    cmd <- paste("CREATE TABLE IF NOT EXISTS", paste(sid, ".sleep_week", sep=""), "(uuid varchar, year int, month int, status int, avgduration int, PRIMARY KEY (uuid, year, month, status))")
    CqlExec(cmd)

    cmd <- paste("CREATE TABLE IF NOT EXISTS", paste(sid, ".context_week", sep=""), "(uuid varchar, year int, month int, situation int, avgduration int, avghrm int, activeindex int, avgmet float, PRIMARY KEY (uuid, year, month, situation))")
    CqlExec(cmd)

    cmd <- paste("CREATE TABLE IF NOT EXISTS", paste(sid, ".hrm_week", sep=""), "(uuid varchar, year int, month int, min int, max int, mean int, median int, sd float, PRIMARY KEY (uuid, year, month))")
    CqlExec(cmd)
}

DropKeySpace <- function (sid) {
    cmd <- paste("DROP KEYSPACE", sid, "")
    CqlExec(cmd)
}

DropKeySpaces <- function (school.ids) {
    for (sid in school.ids) {
        DropKeySpace (sid)
    }
}

DropTable <- function (sid, tablename) {
    cmd <- paste("DROP KEYSPACE", sid, ".", tablename, "")
    CqlExec(cmd)
}
