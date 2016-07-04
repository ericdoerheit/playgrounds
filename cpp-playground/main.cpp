#include <iostream>
#include <iomanip>
#include <sstream>
#include <opencv2/opencv.hpp>
#include "TestClass.h"

using namespace std;
using namespace cv;

int main(int argc, char *argv[]) {

    Vec3f position(0.0f, 0.0f, 0.0f);
    Vec3f u(1.0f, 0.0f, 0.0f);
    Vec3f v(0.0f, 1.0f, 0.0f);
    Vec3f w(0.0f, 0.0f, 1.0f);

    TestClass p(1.0f);
    p.setT(position, u, v, w);
    p.printT();

    return 0;

    int i = 20;
    ostringstream oss;
    oss << setfill('0') << setw(6) << i;
    cout << oss.str() << endl;

    oss.str("");
    oss.clear();

    oss << "Bla";

    cout << oss.str() << endl;

    float f = atof("7.215377000000e+02");

    cout << f;

    return 0;
}