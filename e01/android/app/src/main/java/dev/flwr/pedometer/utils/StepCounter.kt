package dev.flwr.pedometer.utils

import org.apache.commons.math3.analysis.solvers.LaguerreSolver
import org.apache.commons.math3.complex.Complex
import org.apache.commons.math3.fitting.PolynomialCurveFitter
import org.apache.commons.math3.fitting.WeightedObservedPoints
import org.apache.commons.math3.transform.DftNormalization
import org.apache.commons.math3.transform.FastFourierTransformer
import org.apache.commons.math3.transform.TransformType
import kotlin.math.absoluteValue


class StepCounter {
    private val fft = FastFourierTransformer(DftNormalization.STANDARD)
    private val samplingFreq = 100
    private val windowSize = 512
    private val duration = 1.25
    private val resolution = samplingFreq / windowSize
    private val length = duration * samplingFreq
    private val axisData: MutableList<Triple<Double, Double, Double>> = ArrayList()
    private var steps: Int = 0

    fun countSteps(x: Double, y: Double, z: Double): Int {
        axisData.add(Triple(x, y, z))
        var i = 0
        while (i + windowSize < axisData.size) {
            val subAxisData = axisData.subList(i, i + windowSize)
            val axisSum = subAxisData.map { t ->
                tripleAbsolute(t)
            }.reduce { t1, t2 -> addTriple(t1, t2) }
            val axisMean = Triple(
                axisSum.first / subAxisData.size,
                axisSum.second / subAxisData.size,
                axisSum.third / subAxisData.size
            )
            val maxAxis =
                if (axisMean.first > axisMean.second) {
                    if (axisMean.first > axisMean.third) {
                        1
                    } else {
                        3
                    }
                } else {
                    2
                }
            val maxAxisData = subAxisData.map { t ->
                when (maxAxis) {
                    1 -> t.first
                    2 -> t.second
                    else -> t.third
                }
            }
            val s = fft.transform(maxAxisData.toDoubleArray(), TransformType.FORWARD).map { v: Complex -> v.abs() * 2 }
            val yPolyAxis = s.subList(2, 7)
            val xPolyAxis = doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0)
            val w0 = s.subList(0, 2).average()
            val wc = yPolyAxis.average()



            val obs = WeightedObservedPoints()
            for (point in xPolyAxis.zip(yPolyAxis) { xPoint, yPoint -> Pair(xPoint, yPoint) }) {
                obs.add(point.first, point.second)
            }
            val fitter = PolynomialCurveFitter.create(4)
            val coefficients = fitter.fit(obs.toList())
            val max = LaguerreSolver().solveComplex(coefficients, 0.0)
            if( wc > w0 && wc > 10) {
                val fw = resolution *(max.real + 1)
                val c = duration * fw
                steps += c.toInt()
            }
        }
        return steps
    }

    private fun tripleAbsolute(t: Triple<Double, Double, Double>): Triple<Double, Double, Double> {
        return Triple(
            t.first.absoluteValue,
            t.second.absoluteValue,
            t.third.absoluteValue
        )
    }

    private fun addTriple(
        t1: Triple<Double, Double, Double>,
        t2: Triple<Double, Double, Double>
    ): Triple<Double, Double, Double> {
        return Triple(t1.first + t2.first, t1.second + t2.second, t1.third + t2.third)
    }
}