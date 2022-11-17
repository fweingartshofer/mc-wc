package at.fhooe

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import at.fhooe.model.Activity
import at.fhooe.model.SimpleActivity
import at.fhooe.ui.theme.ActivityClassifierTheme
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {
    private lateinit var activity: Activity
    private lateinit var mainHandler: Handler
    private var activityState = MutableStateFlow("text")

    private val updateTextTask = object : Runnable {
        override fun run() {
            updateActivity()
            mainHandler.postDelayed(this, 1000)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ActivityClassifierTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ActivityText(activityState)
                }
            }
        }
        mainHandler = Handler(Looper.getMainLooper())
        activity = SimpleActivity(assets.open("WISDM_ar_v1.1_transformed.csv"))
    }

    fun updateActivity() {
        activityState.value = activity.classify()
    }

    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(updateTextTask)
    }

    override fun onResume() {
        super.onResume()
        mainHandler.post(updateTextTask)
    }
}

@Composable
fun ActivityText(activityState: MutableStateFlow<String>) {
    val text by activityState.collectAsState()
    Text(text)
}