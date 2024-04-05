package com.edu.apkmob

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class Details() : ComponentActivity() {
    val asd = intent.extras?.get("trail") as Trail
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DetailsLayout(asd)
        }
    }
}

@Composable
fun DetailsLayout(trail: Trail) {
    val message = "To jest nowa aktywność!"
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = trail.name, modifier = Modifier.padding(16.dp))
            Text(text = trail.description, modifier = Modifier.padding(16.dp))

        }
}