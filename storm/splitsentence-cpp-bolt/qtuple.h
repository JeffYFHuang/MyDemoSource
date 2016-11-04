#ifndef QTUPLE_H
#define QTUPLE_H

#include <QObject>
#include <QJsonValue>

class QTuple
{

private:
    QString id;
    QString component;
    QString stream;
    int task;
    QJsonValue* value;
    void init(QJsonValue &value)
    {
        if(value.isArray())
            this->value = new QJsonValue(value);
        else
        {
            QJsonArray array;
            array.append(value);
            this->value = new QJsonValue(array);
        }
    }
public:
    ~QTuple(){
        delete value;
    }
    QTuple(QString id, QString component, QString stream, int task, QJsonValue value):
         id(id), component(component),stream(stream),task(task)
    {
        init(value);
    }

    QTuple(QJsonValue value):id(QString("")),component(QString("")),stream(QString("")),task(-1)
    {
        init(value);
    }
    
    QString getId(){return id;}
    QString getComponent(){return component;}
    QString getStream(){return stream;}
    int getTask(){return task;}
    QJsonValue* getValue(){return value;}
    QJsonValue getIdAsValue(){return QJsonValue(id);}



signals:
    
public slots:
    
};

#endif // QTUPLE_H
