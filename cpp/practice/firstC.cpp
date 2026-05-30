#include <iostream>
#include <algorithm>
#include <vector>

using namespace std;
using ll = long long;

int main() {
  struct Init { Init() { ios::sync_with_stdio(0); cin.tie(0); } }init;

  ll N, M;
  cin >> N >> M;
  vector<ll> a(N), b(M);
  for (ll i = 0; i < N; i++) {
    cin >> a[i];
  }
  for (ll i = 0; i < M; i++) {
    cin >> b[i];
  }
  sort(a.begin(), a.end(), std::greater<>()); // シャリを大きいものから順に
  sort(b.begin(), b.end(), std::greater<>()); // ネタを大きいものから順に
  // 軽いしゃり，重いネタが扱いづらい
  // 重いシャリから見ていって，載せられる中で一番重いネタを乗せていく
  // すると次のシャリでは，今まで見たネタは載せられないのでその次から見ていけばいい
  // ということは

  ll i, j, count = 0;
  while (i < N && j < M) { // 見ていないシャリまたはネタがある限り
// i 番目のシャリをみて，
// j 番目のネタを見て，
// 載せられたらOKでi++, j++
    if (b[j] <= 2 * a[i]) {
      count++;
      i++;
      j++;
    } else { // 載せられなかったら次に軽いネタ
      j++;
    }
  }
  
  cout << count << endl;
  return 0;
}