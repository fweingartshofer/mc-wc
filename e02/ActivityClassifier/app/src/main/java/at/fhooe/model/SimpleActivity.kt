package at.fhooe.model;

import com.opencsv.CSVReader
import java.io.InputStream;
import java.io.InputStreamReader

class SimpleActivity(file: InputStream) : Activity(file) {
    private val reader = CSVReader(InputStreamReader(file))

    init {
        reader.readNext()
    }

    override fun dataEntry(): DoubleArray {
        val out = arrayOf<String>(* reader.readNext())
        val line = out.sliceArray(IntRange(3, out.size - 1))
        return line.map { s -> s.toDouble() }.toDoubleArray()
    }
}
