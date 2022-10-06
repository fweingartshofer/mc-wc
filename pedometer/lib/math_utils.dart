import 'package:pedometer/tuples.dart';

int max(Tuple3<num, num, num> t) {
  return t.d0 > t.d1 ? (t.d0 > t.d2 ? 0 : 2) : 1;
}