package com.edu.apkmob

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.myapp.MyDBHandler
import kotlinx.coroutines.launch
import com.edu.apkmob.ui.theme.ApkmobTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private lateinit var dbHandler: MyDBHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHandler = MyDBHandler(this, null, null, 1)
        setContent {
            val timerViewModel: TimerViewModel by viewModels()
            var isDarkTheme by remember { mutableStateOf(false) }
            var backgroundColor by remember { mutableStateOf(Color.White) }
            var isLoading by remember { mutableStateOf(true) }

            LaunchedEffect(Unit) {
                delay(2000) // Simulate a loading period
                isLoading = false
            }

            if (isLoading) {
                LoadingScreen()
            } else {
                ApkmobTheme(darkTheme = isDarkTheme) {
                    MainScreen(
                        dbHandler = dbHandler,
                        backgroundColor = backgroundColor,
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = { isDarkTheme = !isDarkTheme }
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    var rotationAngle by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            rotationAngle += 10f
            if (rotationAngle >= 360f) {
                rotationAngle = 0f
            }
            delay(16L) // roughly 60 frames per second
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.mipmap.logo),
                contentDescription = "Rotating Logo",
                modifier = Modifier
                    .size(100.dp)
                    .graphicsLayer(rotationZ = rotationAngle)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    dbHandler: MyDBHandler,
    backgroundColor: Color,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    var trails by remember { mutableStateOf(emptyList<Trail>()) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            trails = dbHandler.loadTrailsFromDB()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            if (drawerState.isOpen) {
                DrawerContent(
                    onToggleTheme = onToggleTheme,
                    backgroundColor = backgroundColor,
                    closeDrawer = { scope.launch { drawerState.close() } }
                )
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
                        title = { Text("Szlaki") },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(imageVector = Icons.Outlined.Menu, contentDescription = "Menu")
                            }
                        },
                    )
                },
                floatingActionButton = {
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
            ) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    TrailGrid(trails = trails)
                }
            }
        }
    }
}

@Composable
fun DrawerContent(onToggleTheme: () -> Unit, backgroundColor: Color, closeDrawer: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .background(backgroundColor)
    ) {
        Text(
            text = "Navigation",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Divider()
        DrawerItem(label = "Toggle Theme", onClick = {
            onToggleTheme()
            closeDrawer()
        })
    }
}

@Composable
fun DrawerItem(label: String, onClick: () -> Unit) {
    Text(
        text = label,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
    )
}

@Composable
fun TrailGrid(trails: List<Trail>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.padding(8.dp)
    ) {
        items(trails) { trail ->
            TrailItem(trail = trail)
        }
    }
}

@Composable
fun TrailItem(trail: Trail) {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                val intent = Intent(context, Details::class.java)
                intent.putExtra("trail_id", trail.id)
                context.startActivity(intent)
            }
    ) {
        val imageBitmap = loadImageFromAssets(context, "images/${trail.id}.webp")
        imageBitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = trail.name,
                modifier = Modifier
                    .requiredSize(180.dp)
                    .padding(2.dp)
            )
        }
        Text(text = trail.name, modifier = Modifier.padding(8.dp))
    }
}

fun loadImageFromAssets(context: Context, filePath: String): android.graphics.Bitmap? {
    return try {
        context.assets.open(filePath).use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
