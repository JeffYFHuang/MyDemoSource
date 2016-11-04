#ifndef BASICBOLT_H
#define BASICBOLT_H

#include <QObject>
#include <QJsonArray>
#include <QJsonObject>
#include <QJsonDocument>
#include <QQueue>
#include <QJsonParseError>
#include <QLinkedList>
#include <QEvent>
#include <QCoreApplication>

#include <iostream>
#include <sstream>
#include <fstream>
#include <utility>
#include <iostream>
#include <stdexcept>
#include <sstream>
#include <string>

#include "qtuple.h"



class QBasicBolt : public QObject
{
    Q_OBJECT
public:
    explicit QBasicBolt(QObject *parent = 0);

    virtual void initialize(QJsonValue* conf, QJsonValue* context) = 0;
    virtual void process(QTuple *tuple) = 0;

    void start();

private:


    QQueue<QJsonValue*> pending_taskids;
    QQueue<QJsonValue*> pending_commands;
    QTuple *anchor_tuple;


protected:

    virtual QString readLine();

    virtual void sendMsgToParent(QJsonValue &v);

    QJsonValue *readMsg();
    QJsonValue *patientReadMsg();

    QJsonValue *readCommand();
    QJsonValue *readTaskIDs();

    QTuple* readTuple();

    void sync();

    void ack(const QString id);

    void fail(const QString &id);

    void log(const QString &msg);

    void sendPid(const QString &dir_name);

    void emitBolt(QTuple *tuple,
                const QString &stream, int task);

    QPair<QJsonValue*, QJsonValue*> InitComponent();

    QJsonValue *emitTupleWithOptions(QTuple *tuple,
                          const QString &stream = QString(""),
                          int task = -1);

    QJsonValue *emitTuple(QTuple *tuple);
    
signals:
    
public slots:

     void run();
    
};

#endif // BASICBOLT_H
