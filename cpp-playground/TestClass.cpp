//
// Created by Eric Dörheit on 03/05/16.
//

#include "TestClass.h"


void TestClass::setT(Vec3f position, Vec3f u, Vec3f v, Vec3f w) {

    /*float t[16] = { u.val[0],    v.val[0],   w.val[0],   position.val[0],
                    u.val[1],    v.val[1],   w.val[1],   position.val[1],
                    u.val[2],    v.val[2],   w.val[2],   position.val[2],
                    0,           0,          0,          1};*/

    float t[16] = { 1,    0,   0,   0,
                    0,    1,   0,   0,
                    0,    0,   1,   0,
                    0,    0,   0,   1};

    Mat T(4, 4, CV_32F, t);
    cout << "Transformation Matrix" << endl << T << endl;
    t[0] = 2;
    cout << "Transformation Matrix" << endl << T << endl;

    T.copyTo(this->T);

    cout << "Transformation Matrix" << endl << this->T << endl;

    t[0] = 3;
    cout << "Transformation Matrix" << endl << T << endl;
    cout << "Transformation Matrix" << endl << this->T << endl;

    cout << endl;
}


void TestClass::printT() {
    cout << this->T << endl;
    cout << endl;
}



