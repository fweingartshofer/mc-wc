package at.fhooe.model

import com.opencsv.CSVReader
import java.io.InputStream
import java.io.InputStreamReader

abstract class Activity(file: InputStream) {
    companion object {
        private val classes = arrayOf(
            "Downstairs",
            "Jogging",
            "Sitting",
            "Standing",
            "Upstairs",
            "Walking"
        )
    }

    fun classify(): String {
        val prediction = ActivityClassifier.predict(dataEntry());
        return classes[prediction.indexOfFirst { it == 1.0 }]
    }

    abstract fun dataEntry(): DoubleArray
}