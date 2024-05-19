@file:OptIn(ExperimentalMaterial3Api::class)

package com.edu.apkmob

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapp.MyDBHandler
import com.edu.apkmob.ui.theme.ApkmobTheme
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.math.max
import kotlin.math.min

@Composable
fun ParallaxImage(bitmap: Bitmap, scrollFraction: Float, modifier: Modifier = Modifier) {
    val parallaxOffset = remember { derivedStateOf { max(0f, min(scrollFraction * 0.5f, 200f)) } }

    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = null,
        modifier = modifier.offset(y = parallaxOffset.value.dp)
    )
}

class Details : ComponentActivity() {
    private lateinit var dbHandler: MyDBHandler
    private var trailId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHandler = MyDBHandler(this, null, null, 1)
        trailId = intent.getIntExtra("trail_id", -1)
        setContent {
            val timerViewModel: TimerViewModel by viewModels()
            var isDarkTheme by remember { mutableStateOf(false) }
            var backgroundColor by remember { mutableStateOf(Color.White) }
            ApkmobTheme(darkTheme = isDarkTheme) {
                TimerScreenContent(trailId, dbHandler, timerViewModel, backgroundColor, isDarkTheme, onToggleTheme = { isDarkTheme = !isDarkTheme })
            }
        }
    }
}

@Composable
fun TimerScreenContent(
    trailId: Int,
    dbHandler: MyDBHandler,
    timerViewModel: TimerViewModel,
    backgroundColor: Color,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val timerValue by timerViewModel.timer.collectAsState()
    val savedTimesState by timerViewModel.savedTimes.collectAsState()
    var trail by remember { mutableStateOf<Trail?>(null) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showSavedTimesDialog by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(trailId) {
        trail = dbHandler.findTrailById(trailId)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            if (drawerState.isOpen) {
                DrawerContent(onToggleTheme = onToggleTheme, backgroundColor = backgroundColor, closeDrawer = { scope.launch { drawerState.close() } })
            }
        }
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = backgroundColor
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Szczegóły") },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(imageVector = Icons.Outlined.Menu, contentDescription = "Menu")
                            }
                        },
//                        actions = {
//                            Button(onClick = onToggleTheme) {
//                                Text("Toggle Theme")
//                            }
//                        }
                    )
                }
            ) { paddingValues ->
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
                        onShowSavedTimesClick = { showSavedTimesDialog = true },
                        onToggleTheme = onToggleTheme,
                        modifier = Modifier.padding(paddingValues)
                    )
                } ?: run {
                    Text(text = "Loading...", modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp))
                }
            }
        }
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
    onShowSavedTimesClick: () -> Unit,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageBitmap = remember { mutableStateOf<Bitmap?>(null) }
    val listState = rememberLazyListState()

    LaunchedEffect(trail.id) {
        imageBitmap.value = loadImageFromAssets(context, "images/${trail.id}.webp")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            item {
                imageBitmap.value?.let { bitmap ->
                    val scrollFraction = listState.firstVisibleItemScrollOffset / (bitmap.height.toFloat())
                    ParallaxImage(bitmap = bitmap, scrollFraction = scrollFraction, modifier = Modifier.padding(16.dp))
                }
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

                Spacer(modifier = Modifier.height(32.dp))
                Text(text = timerValue.formatTime(), fontSize = 24.sp)
            }

            item {
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
            }

            item {
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
            }
        }

        FloatingActionButton(
            onClick = {
                Toast.makeText(context, "Tutaj powinien odpalić się aparat", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.AddCircle,
                contentDescription = "Odpal aparat"
            )

        }
    }
}


