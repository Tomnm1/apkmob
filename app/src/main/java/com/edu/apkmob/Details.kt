package com.edu.apkmob

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapp.MyDBHandler

class Details : ComponentActivity() {
    private lateinit var dbHandler: MyDBHandler
    private var trailId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHandler = MyDBHandler(this, null, null, 1)
        trailId = intent.getIntExtra("trail_id", -1)
        setContent {
            val timerViewModel: TimerViewModel by viewModels()
            TimerScreenContent(trailId, dbHandler, timerViewModel)
        }
    }
}

@Composable
fun TimerScreenContent(trailId: Int, dbHandler: MyDBHandler, timerViewModel: TimerViewModel) {
    val timerValue by timerViewModel.timer.collectAsState()
    val savedTimesState by timerViewModel.savedTimes.collectAsState()
    var trail by remember { mutableStateOf<Trail?>(null) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showSavedTimesDialog by remember { mutableStateOf(false) }

    LaunchedEffect(trailId) {
        trail = dbHandler.findTrailById(trailId)
    }

    trail?.let {
        if (showSaveDialog) {
            SaveTimeDialog(
                onConfirm = {
                    dbHandler.saveTrailTime(trailId, timerValue)
                    timerViewModel.saveTimer()
                    showSaveDialog = false
                },
                onDismiss = { showSaveDialog = false }
            )
        }
        if (showSavedTimesDialog) {
            SavedTimesDialog(
                times = dbHandler.getTrailTimes(trailId).map { it.formatTime() },
                onDismiss = { showSavedTimesDialog = false }
            )
        }
        DetailsLayout(
            trail = it,
            timerValue = timerValue,
            savedTimes = savedTimesState,
            onStartClick = { timerViewModel.startTimer() },
            onPauseClick = { timerViewModel.pauseTimer() },
            onStopClick = { timerViewModel.stopTimer() },
            onSaveClick = { showSaveDialog = true },
            onShowSavedTimesClick = { showSavedTimesDialog = true }
        )
    } ?: run {
        Text(text = "Loading...", modifier = Modifier.fillMaxSize().padding(16.dp))
    }
}

@Composable
fun SaveTimeDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Save Time") },
        text = { Text("Do you want to save the current time?") },
        confirmButton = {
            Button(onClick = onConfirm) { Text("Yes") }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("No") }
        }
    )
}

@Composable
fun SavedTimesDialog(times: List<String>, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Saved Times") },
        text = {
            Column {
                times.forEach { time ->
                    Text(text = time)
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("OK") }
        }
    )
}

fun Long.formatTime(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val remainingSeconds = this % 60
    return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
}

@Composable
fun DetailsLayout(
    trail: Trail,
    timerValue: Long,
    savedTimes: List<Long>,
    onStartClick: () -> Unit,
    onPauseClick: () -> Unit,
    onStopClick: () -> Unit,
    onSaveClick: () -> Unit,
    onShowSavedTimesClick: () -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = trail.name, modifier = Modifier.padding(16.dp), fontSize = 28.sp)
        Text(text = "Distance: ${trail.distance} km", modifier = Modifier.padding(16.dp), fontSize = 20.sp)
        Text(text = "Level: ${trail.level}", modifier = Modifier.padding(16.dp), fontSize = 20.sp)
        Text(text = trail.desc, modifier = Modifier.padding(16.dp), fontSize = 18.sp)

        Text(
            text = "Timer:",
            style = TextStyle(
                fontSize = 24.sp,
                shadow = Shadow(
                    color = Color.Blue, offset = Offset(5.0f, 10.0f),
                    blurRadius = 3f,
                )
            )
        )

        Text("", modifier = Modifier.padding(32.dp))
        Text(text = timerValue.formatTime(), fontSize = 24.sp)

        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            IconButton(onClick = onStopClick) {
                Icon(
                    Icons.Outlined.Refresh,
                    contentDescription = "Stop",
                    modifier = Modifier.size(48.dp)
                )
            }
            IconButton(onClick = onStartClick) {
                Icon(
                    Icons.Outlined.PlayArrow,
                    contentDescription = "Start",
                    modifier = Modifier.size(48.dp)
                )
            }
            IconButton(onClick = onPauseClick) {
                Icon(
                    Icons.Outlined.Close,
                    contentDescription = "Pause",
                    modifier = Modifier.size(48.dp)
                )
            }
        }
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            Button(onClick = onShowSavedTimesClick) {
                Text("Saved times")
            }
            IconButton(onClick = onSaveClick) {
                Icon(
                    Icons.Outlined.FavoriteBorder,
                    contentDescription = "Save",
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        FloatingActionButton(
            onClick = {
                Toast.makeText(context, "Camera should start here", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .align(Alignment.End)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.AddCircle,
                contentDescription = "Start Camera"
            )
        }
    }
}