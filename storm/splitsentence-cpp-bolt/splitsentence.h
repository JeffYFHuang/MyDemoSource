#ifndef SPLITSENTENCE_H
#define SPLITSENTENCE_H

#include <QObject>
#include "qbasicbolt.h"

class SplitSentence : public QBasicBolt
{
    Q_OBJECT
public:
    explicit SplitSentence(QObject *parent = 0);

    void initialize(QJsonValue* conf, QJsonValue* context) { }
    void process(QTuple *tuple);
    
signals:
    
public slots:
    
};

#endif // SPLITSENTENCE_H
