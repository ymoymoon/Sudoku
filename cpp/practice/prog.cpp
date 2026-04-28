#include <iostream>
#include "ex.h"

using namespace std;

int main() {
    Ex obj;
    int num;
    cout << "please" << endl;
    cin >> num;
    
    obj.inc(num);
    int x = obj.get();
    cout << x << endl;

    return 0;
    
}