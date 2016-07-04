//
// Created by Eric Dörheit on 03/05/16.
//

#ifndef CPP_PLAYGROUND_POINT_H
#define CPP_PLAYGROUND_POINT_H

#include <opencv2/opencv.hpp>

using namespace std;
using namespace cv;


class TestClass {
public:
    float x;
    float y;
    float z;
    float r;

    void setT(Vec3f position, Vec3f u, Vec3f v, Vec3f w);
    void printT();

    TestClass(float x, float y, float z, float r) : x(x), y(y), z(z), r(r) { }
    TestClass(float x) : x(x) { }

private:
    Mat T;
    Vec3f u;
    Vec3f* v;
};


#endif //CPP_PLAYGROUND_POINT_H
