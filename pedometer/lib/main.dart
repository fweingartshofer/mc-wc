import 'package:flutter/material.dart';
import 'package:pedometer/math_utils.dart';
import 'package:pedometer/tuples.dart';
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
  double x = 0, y = 0, z = 0;
  String direction = "none";
  List<TimestampedMetrics> axisData =
      List.empty(growable: true);

  @override
  void initState() {
    gyroscopeEvents.listen((GyroscopeEvent event) {
      x = event.x;
      y = event.y;
      z = event.z;
      axisData.add(TimestampedMetrics(DateTime.now(), x, y, z));

      int samplingFreq = 100;
      int windowSize = 320;
      double duration = 1.25;
      double resolution = samplingFreq / windowSize;
      double length = duration * samplingFreq;

      int i = 0;
      while (i + windowSize < axisData.length) {
        Tuple3<num, num, num> mean = axisData
            .map((e) => e.map((p0) => p0.abs()))
            .reduce((value, element) => value + element) / axisData.length;
        int maxAxis = max(mean);
      }

      /*
      if (x > 0) {
        direction = "back";
      } else if (x < 0) {
        direction = "forward";
      }
      if (y > 0) {
        direction += " left";
      } else if (y < 0) {
        direction += " right";
      }*/

      setState(() {});
    });
    super.initState();
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
              direction,
              style: const TextStyle(fontSize: 30),
            )
          ])),
    );
  }
}
