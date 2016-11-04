#include "qbasicbolt.h"

#include <QPair>
#include <QThread>
#include <QFile>
#include <QCoreApplication>
#include <QTextStream>



QBasicBolt::QBasicBolt(QObject *parent) :
    QObject(parent)
{
}

void QBasicBolt::start()
{
    QPair<QJsonValue*, QJsonValue*> conf_context = InitComponent();
    initialize(conf_context.first, conf_context.second);
    run();
}

void QBasicBolt::run()
{
    while(true){
        QTuple* tuple = readTuple();
        if(tuple != NULL)
        {
            anchor_tuple = tuple;
            ack(tuple->getId());
            process(tuple);
            delete tuple;
        } else
        {
            QThread::sleep(50);
        }
    }
}

void QBasicBolt::sendMsgToParent(QJsonValue &v)
{
    QJsonDocument *doc;
    if(v.isArray()){
        doc = new QJsonDocument(v.toArray());
    } else {
        doc = new QJsonDocument(v.toObject());
    }
    std::cout << doc->toJson().constData();
    std::cout << "end" << std::endl;
    delete doc;
}

QString QBasicBolt::readLine()
{
    bool readLine = false;
    std::string temp;
    getline(std::cin, temp).good();
    QString line = QString::fromStdString(temp);
    if (readLine && (line != "end"))
        line = line + "\n";
    return line;
}

QJsonValue* QBasicBolt::readMsg()
{
    QString msg, line;
    while(1)
    {
        line = readLine();
        if(line.isNull())
            QThread::msleep(50);
        if (line == "end")
            break;
        else
            msg += line;
    }
    QJsonParseError error;
    QJsonDocument doc = QJsonDocument::fromJson(msg.toLatin1(), &error);
    if(error.error == QJsonParseError::NoError)
    {
        if(doc.isArray())
            return new QJsonValue(doc.array());
        if(doc.isObject())
            return new QJsonValue(doc.object());
    }
    return NULL;
}

QJsonValue* QBasicBolt::patientReadMsg()
{
    QJsonValue* msg = readMsg();
    int count = 0;
    while((msg == NULL) && (count < 10))
    {
        QThread::msleep(100);
        count++;
        msg = readMsg();
    }
    return msg;
}

QJsonValue* QBasicBolt::readTaskIDs()
{
    if (!pending_taskids.empty())
    {
        QJsonValue* v = pending_taskids.dequeue();
        return v;
    }
    else
    {
        QJsonValue* msg = patientReadMsg();
        if(msg == NULL)
            return NULL;
        while (!msg->isArray())
        {
            pending_commands.enqueue(msg);
            msg = readMsg();
        }
        return msg;
    }
}

QJsonValue* QBasicBolt::readCommand()
{

    if (!pending_commands.empty())
    {
        QJsonValue* v = pending_commands.dequeue();
        return v;
    }
    else
    {
        QJsonValue* msg = readMsg();
        if(msg == NULL)
            return NULL;
        while (msg->isArray())
        {
            pending_taskids.enqueue(msg);
            msg = readMsg();
        }
        return msg;
    }
}

QTuple* QBasicBolt::readTuple()
{
    QJsonValue* msg = readCommand();
    if(msg == NULL)
        return NULL;
    if(msg->isObject()){
        QJsonObject obj = msg->toObject();
        return new QTuple(obj["id"].toString(), obj["comp"].toString(),
                obj["stream"].toString(), (int)obj["task"].toDouble(),
                obj.value("tuple"));
    }
    return NULL;
}

void QBasicBolt::sync()
{
    QJsonObject obj;
    obj.insert(QString("command"),QString("sync"));
    QJsonValue v(obj);
    sendMsgToParent(v);
}

void QBasicBolt::ack(const QString id)
{
    QJsonObject obj;
    obj.insert(QString("command"),QString("ack"));
    obj.insert(QString("id"),id);
    QJsonValue v(obj);
    sendMsgToParent(v);
}

void QBasicBolt::fail(const QString &id)
{
    QJsonObject obj;
    obj.insert(QString("command"),QString("fail"));
    obj.insert(QString("id"),id);

    QJsonValue v(obj);
    sendMsgToParent(v);
}

void QBasicBolt::log(const QString &msg)
{
    QJsonObject obj;
    obj.insert(QString("commdn"),QString("log"));
    obj.insert("msg",msg);
    QJsonValue v(obj);
    sendMsgToParent(v);
}

// Writes an empty file in dir_name whose name is the PID of the current
// process and sends a message to parent.  Getting the PID of the current
// process is platform-specific, and for now only work on unix systems.
void QBasicBolt::sendPid(const QString &dir_name)
{
    pid_t current_pid = getpid();
    QJsonObject obj;
    obj.insert("pid",QJsonValue((double)current_pid));
    QJsonValue v(obj);
    sendMsgToParent(v);
    QString full_name = dir_name + QString("") + QString(current_pid);
    std::ofstream temp(full_name.toLatin1().constData());
    temp.close();
}

void QBasicBolt::emitBolt(QTuple *tuple,
                          const QString &stream, int task)
{
    QJsonObject obj;
    obj.insert(QString("command"),QString("emit"));
    if (!stream.isEmpty())
        obj.insert(QString("stream"),stream);
    if (task != -1)
        obj.insert(QString("task"),task);
    obj.insert(QString("tuple"), *tuple->getValue());
    QJsonValue v(obj);
    sendMsgToParent(v);
}

QJsonValue* QBasicBolt::emitTupleWithOptions(QTuple *tuple,
                                             const QString &stream, int task)
{
    emitBolt(tuple, stream, task);
    return readTaskIDs();
}

QJsonValue* QBasicBolt::emitTuple(QTuple *tuple)
{
    QJsonValue* ret = emitTupleWithOptions(tuple,tuple->getStream(),tuple->getTask());
    return ret;
}

QPair<QJsonValue *, QJsonValue *> QBasicBolt::InitComponent()
{
    QJsonValue* setupInfo = readMsg();
    sendPid(setupInfo->toObject()["pidDir"].toString());
    QPair<QJsonValue*, QJsonValue*> ret(new QJsonValue(setupInfo->toObject()["conf"]), new QJsonValue(setupInfo->toObject()["context"]));
    return ret;
}
