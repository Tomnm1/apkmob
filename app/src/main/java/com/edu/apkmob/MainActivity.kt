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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.edu.apkmob.ui.theme.ApkmobTheme
import com.example.myapp.MyDBHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Strona główna", "Łatwy", "Średni", "Trudny", "B. Trudny")
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

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
                Column(modifier = Modifier.padding(paddingValues)) {
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = {
                                    Text(
                                        text = title,
                                        color = if (selectedTabIndex == index) Color.Gray else Color.Gray
                                    )
                                }
                            )
                        }
                    }
                    when (selectedTabIndex) {
                        0 -> HomeScreen()
                        1 -> TrailsScreen(dbHandler, "EASY")
                        2 -> TrailsScreen(dbHandler, "MEDIUM")
                        3 -> TrailsScreen(dbHandler, "HARD")
                        4 -> TrailsScreen(dbHandler, "VERY HARD")
                    }
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
fun TrailsScreen(dbHandler: MyDBHandler, difficulty: String) {
    var trails by remember { mutableStateOf(emptyList<Trail>()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            trails = dbHandler.getTrailsByDifficulty(difficulty)
        }
    }

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

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Witamy w aplikacji Szlaki!",
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Nasza aplikacja pozwala na przeglądanie szlaków turystycznych o różnym stopniu trudności. " +
                    "Kliknij odpowiednią zakładkę, aby zobaczyć dostępne trasy."
        )
    }
}
