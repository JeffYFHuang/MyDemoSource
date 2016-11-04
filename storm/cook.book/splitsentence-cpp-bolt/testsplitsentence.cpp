#include <QtTest/QtTest>
#include <QJsonObject>
#include <QJsonDocument>
#include <QQueue>
#include "splitsentence.h"

#include <iostream>

using namespace std;

class TestSplitSentence: public QObject
{
    Q_OBJECT
private slots:
    void basicInteraction();
};

class MocSplitSentence : public SplitSentence
{
public:
    QQueue<QString> currentInputLines;
    QQueue<QString> currentOutputLines;
protected:
    /*void afterRun()
    {

    }*/
    QString readLine()
    {
        if(currentInputLines.isEmpty())
            return NULL;
        return currentInputLines.dequeue();
    }

    void sendMsgToParent(QJsonValue &v)
    {
        QJsonDocument *doc;
        if(v.isArray()){
            doc = new QJsonDocument(v.toArray());
        } else {
            doc = new QJsonDocument(v.toObject());
        }
        cout << doc->toJson().constData() << endl;
    }
};



void TestSplitSentence::basicInteraction()
{
    MocSplitSentence bolt;

    QJsonObject* setupInfo = new QJsonObject();
    setupInfo->insert("pidDir",QCoreApplication::applicationDirPath());
    setupInfo->insert("conf",QJsonObject());
    setupInfo->insert("context",QJsonObject());
    QJsonDocument* setup = new QJsonDocument(*setupInfo);
    bolt.currentInputLines.enqueue(setup->toJson());
    bolt.currentInputLines.enqueue(QString("end"));

    QJsonObject* input = new QJsonObject();
    input->insert("id",QString("1"));
    input->insert("comp",QString("test"));
    input->insert("stream",QString("sentence"));
    input->insert("task",QString("split"));
    QJsonArray tuple;
    QJsonValue sentence(QString("this is a test sentence"));
    tuple.append(sentence);
    input->insert("tuple",tuple);
    QJsonDocument* doc = new QJsonDocument(*input);
    bolt.currentInputLines.enqueue(doc->toJson());
    bolt.currentInputLines.enqueue(QString("end"));

    for(int i = 0; i < 5;++i){
        QJsonArray* ids = new QJsonArray();
        ids->append(QJsonValue(i+1));
        QJsonDocument* idsDoc = new QJsonDocument(*ids);
        bolt.currentInputLines.enqueue(idsDoc->toJson());
        bolt.currentInputLines.enqueue(QString("end"));
    }

    bolt.start();

}

#include "testsplitsentence.moc"
