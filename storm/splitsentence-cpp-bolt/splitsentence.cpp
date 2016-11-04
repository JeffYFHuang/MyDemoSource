#include "splitsentence.h"

#include <QStringList>

SplitSentence::SplitSentence(QObject *parent) :
    QBasicBolt(parent)
{
}

void SplitSentence::process(QTuple *tuple)
{
    QJsonArray val = tuple->getValue()->toArray();
    QString s = val.at(0).toString();
    QStringList tokens = s.split(" ");
    for (QStringList::Iterator i = tokens.begin(); i != tokens.end(); ++i)
    {
        QJsonValue value(*i);
        QTuple* t = new QTuple(value);
        QJsonValue* result = emitTuple(t);
        if(result != NULL)
            delete result;
        delete t;
    }
}
