import 'package:flutter/material.dart';
import 'package:fmin/fmin.dart';
import 'package:pedometer/math_utils.dart' as utils;
import 'package:pedometer/tuples.dart';
import 'package:scidart/numdart.dart';
import 'package:scidart/scidart.dart';
import 'package:sensors_plus/sensors_plus.dart';
import 'package:syncfusion_flutter_charts/charts.dart';

void main() {
  runApp(const PedometerApp());
}

class PedometerApp extends StatelessWidget {
  const PedometerApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      title: 'Pedometer',
      home: Counter(),
    );
  }
}

class Counter extends StatefulWidget {
  const Counter({super.key});

  @override
  State<Counter> createState() => _CounterState();
}

class _CounterState extends State<Counter> {
  double x = 0,
      y = 0,
      z = 0;
  String direction = "none";
  int stepCount = 0;
  List<TimestampedMetrics> axisData = List.empty(growable: true);

  @override
  void initState() {
    gyroscopeEvents.listen((GyroscopeEvent event) {
      x = event.x;
      y = event.y;
      z = event.z;
      axisData.add(TimestampedMetrics(DateTime.now(), x, y, z));

      _calculateSteps();

      setState(() {});
    });
    super.initState();
  }

  int _calculateSteps() {
    int samplingFreq = 100;
    int windowSize = 320;
    double duration = 1.25;
    double resolution = samplingFreq / windowSize;
    double length = duration * samplingFreq;
    
    int i = 0;
    while (i + windowSize < axisData.length) {
      var subAxisData = axisData.sublist(i, i + windowSize);
      Tuple3<double, double, double> meanOfAxis = subAxisData
          .map((e) => e.map((p0) => p0.abs()))
          .reduce((value, element) => value + element) /
          axisData.length;
    
      int maxAxis = utils.max(meanOfAxis);
      Iterable<double> maxAxisData = subAxisData.map((t) => t[maxAxis]);
    
      List<double> s = arrayComplexAbs(
          fft(arrayToComplexArray(Array(maxAxisData.toList()))))
          .map((element) => element * 2)
          .toList();

      var yPolyAxis = Array(s.sublist(2, 7));
      var xPolyAxis = Array([1, 2, 3, 4, 5]);

      var w0 = mean(Array(s.sublist(0, 2)));
      var wc = mean(yPolyAxis);
    
      var coefficients = PolyFit(xPolyAxis, yPolyAxis, 4);
      double f(List<double> x) => coefficients.coefficient(x.first.toInt());
      var max = 0;
      if (wc > w0 && wc > 10) {
        var fw = resolution * (max + 1);
        var c = duration * fw;
        stepCount += (stepCount + c).toInt();
      }

      i += length as int;
    }

    return 0;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text("Gyroscope Sensor in Flutter"),
        backgroundColor: Colors.redAccent,
      ),
      body: Container(
          alignment: Alignment.center,
          padding: const EdgeInsets.all(30),
          child: Column(children: [
            Text(
              "Step Count: $stepCount",
              style: const TextStyle(fontSize: 30),
            )
          ])),
    );
  }
}
