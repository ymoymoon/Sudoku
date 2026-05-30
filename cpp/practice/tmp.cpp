#include <iostream>
#include <algorithm>
#include <vector>
#include <tuple>

using namespace std;
using ll = long long;

int main() {
  struct Init { Init() { ios::sync_with_stdio(0); cin.tie(0); } }init;

  ll H, W;
  cin >> H >> W;
  vector<string> s(H);
  vector<tuple<ll, ll>> black = {};
  for (ll i = 0; i < H; i++) {
    cin >> s[i];
    for (ll j = 0; j < W; j++) {
      if (s[i][j] == '#') {
        black.push_back({i, j});
      }
    }
  }

  if (black.empty()) {
    for (ll i = 0; i < H; i++) {
    for (ll j = 0; j < W; j++) {
      cout << '.';
    }
    cout << endl;
    }
  }
  

  // 奇数回めに黒だったら，最後は白
  // 偶数回目に黒だったら，最後は黒
  // 全て白だったら全て白
  // １つでも黒があれば，いつかは黒になる
  // いつ？→一番近い黒との距離
  // 各ますについて，一番近い黒との距離が偶数（０も）→最後黒，奇数→白
  // (a, b), (i, j) の距離＝ max(|a-i|, |b-j|)
  for (ll i = 0; i < H; i++) {
    for (ll j = 0; j < W; j++) {
      if (s[i][j] == '#') {
        black.push_back({i, j});
      }
    }
    cout << endl;
  }
  
  
  return 0;
}