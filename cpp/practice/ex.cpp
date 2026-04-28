#include "ex.h"

Ex::Ex() : secret(1) {
}
Ex::~Ex() {
}

void Ex::inc (int n) {
    secret = secret + n;
}

int Ex::get () {
    return secret;
}