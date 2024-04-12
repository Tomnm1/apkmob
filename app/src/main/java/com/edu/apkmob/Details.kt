package com.edu.apkmob

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class Details() : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val asd = intent.getSerializableExtra("trail") as Trail
        setContent {
            DetailsLayout(asd)
        }
    }
}

@Composable
fun DetailsLayout(trail: Trail) {
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

            Row (horizontalArrangement = Arrangement.Center, ){
                IconButton(onClick = { /* do something */ }) {
                    Icon(Icons.Outlined.Refresh,
                        contentDescription = "Refresh",
                        tint = Color.Magenta,
                        modifier = Modifier.size(48.dp))
                }
                IconButton(onClick = { /* do something */ }) {
                    Icon(
                        Icons.Outlined.PlayArrow,
                        contentDescription = "Start",
                        tint = Color.Magenta,
                        modifier = Modifier.size(48.dp))
                }
                IconButton(onClick = { /* do something */ }) {
                    Icon(
                        Icons.Outlined.Close,
                        contentDescription = "Stop",
                        tint = Color.Magenta,
                        modifier = Modifier.size(48.dp))
                }

            }

        }
}
