#include "SplitSentence.h"
#include "testsplitsentence.cpp"

#include <QCoreApplication>

#include <iostream>

int main(int argc, char *argv[])
{
    QCoreApplication app(argc, argv);
    /*TestSplitSentence test;
    QTest::qExec(&test, argc, argv);*/

    SplitSentence b;
    b.start();
    return QCoreApplication::exec();
}
