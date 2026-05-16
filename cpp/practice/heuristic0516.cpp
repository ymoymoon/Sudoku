#include <iostream>
#include <tuple>
#include <string>

using namespace std;

tuple<int, int> search (int a[20][20], int num) {
  for (int i=0; i<20; i++) {
    for (int j=0; j<20; j++) {
      if (a[i][j] == num) {
        return forward_as_tuple(i,j);
      }
    }
  }
  return forward_as_tuple(0,0);
}

int main() {
  struct Init { Init() { ios::sync_with_stdio(0); cin.tie(0); } }init;

  int N;
  cin >> N;
  int a[20][20];
  for (int i=0; i<N; i++) {
    for (int j=0; j<N; j++) {
      cin >> a[i][j];
    }
  }
  cout << 11 << endl;

  for (int i=0; i<20;) {
    cout << 40 << " ";
    for (int j=0; j<20; j++) {
      cout << j << " " << i << " ";
    }
    i++;
    for (int j=19; j>-1; j--) {
      cout << j << " " << i << " ";
    }
    i++;
    cout << endl;
  }
  cout << 40 << " ";
  for (int j=0; j<20; j++) {
    cout << 0 << " " << j << " ";
  }
  for (int j=19; j>-1; j--) {
    cout << 1 << " " << j << " ";
  }
  cout << endl;

  int sum = 0;
  string s ="";
  for (int num = 0; num < 400; num++) {
    int i,j;
    tie(i, j) = search(a, num);

    int row = abs(10-j);
    sum += i + row + row;

    if (j%2 == 0) {
      for (int k = 0; k < i; k++) {
        s = s + to_string(j/2) + " -1 \n" ;
      }
    }
    else {
      for (int k = 0; k < i; k++) {
        s = s + to_string(j/2) + " 1 \n" ;
      }
    }
    if (j < 10) {
      for (int k = 0; k < row; k++) {
        s = s + "11 -1 \n" ;
      }
      for (int k = 0; k < row; k++) { // 戻す
        s = s + "11 1 \n" ;
      }
    }
    else {
      for (int k = 0; k < row; k++) {
        s = s + "11 1 \n" ;
      }
      for (int k = 0; k < row; k++) { // 戻す
        s = s + "11 -1 \n" ;
      }
    }
  }
  cout << sum << endl;
  cout << s;
  
  return 0;
}