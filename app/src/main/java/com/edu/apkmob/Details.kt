package com.edu.apkmob

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class Details() : ComponentActivity() {
    private var trail: Trail? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        trail = intent.getSerializableExtra("trail") as Trail
        setContent {
            val timerViewModel: TimerViewModel by viewModels()
            TimerScreenContent(trail!!, timerViewModel)
        }
    }
}

@Composable
fun TimerScreenContent(trail: Trail, timerViewModel: TimerViewModel) {
    val timerValue by timerViewModel.timer.collectAsState()

    DetailsLayout(
        trail= trail,
        timerValue = timerValue,
        onStartClick = { timerViewModel.startTimer() },
        onPauseClick = { timerViewModel.pauseTimer() },
        onStopClick = { timerViewModel.stopTimer() }
    )
}

fun Long.formatTime(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val remainingSeconds = this % 60
    return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
}
@Composable
fun DetailsLayout(trail: Trail,
                  timerValue: Long,
                  onStartClick: () -> Unit,
                  onPauseClick: () -> Unit,
                  onStopClick: () -> Unit) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = trail.name, modifier = Modifier.padding(16.dp))
        Text(text = trail.description, modifier = Modifier.padding(16.dp))
        Text(
            text = "Etapy:",
            style = TextStyle(
                fontSize = 24.sp,
                shadow = Shadow(
                    color = Color.Blue, offset = Offset(5.0f, 10.0f),
                    blurRadius = 3f,
                ))
        )
        trail.stages.forEachIndexed{index, stage ->
            //odpowiadające czasy dodać potem /indeks +1
            Text(text = "${index+1} ${stage}", modifier = Modifier.padding(16.dp))
        }
        Text("", modifier = Modifier.padding(32.dp))
        Text(text = timerValue.formatTime(), fontSize = 24.sp)

        Row (horizontalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)){
            IconButton(onClick = onStopClick ) {
                Icon(Icons.Outlined.Refresh,
                    contentDescription = "Refresh",
                    modifier = Modifier.size(48.dp))
            }
            IconButton(onClick = onStartClick ) {
                Icon(
                    Icons.Outlined.PlayArrow,
                    contentDescription = "Start",
                    modifier = Modifier.size(48.dp))
            }
            IconButton(onClick =  onPauseClick ) {
                Icon(
                    Icons.Outlined.Close,
                    contentDescription = "Stop",
                    modifier = Modifier.size(48.dp))
            }
        }
        Button(onClick = {
        }) {
            Text("Zapisane czasy")
        }


    }
}