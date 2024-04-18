package com.edu.apkmob

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.edu.apkmob.ui.theme.ApkmobTheme
import androidx.compose.ui.Alignment
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApkmobTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainActivityLayout(context = this)
                }
            }
        }
    }

    @Composable
    fun MainActivityLayout(context: Context) {
        val trails = listOf(
            Trail(
                "Szlak Górski",
                listOf("Przełęcz", "Szczyt", "Schronisko"),
                "Opis szlaku górskiego...",
                listOf(60, 90, 45)
            ),
            Trail(
                "Szlak Nadmorski",
                listOf("Plaża", "Klif", "Latarnia"),
                "Opis szlaku nadmorskiego...",
                listOf(30, 45, 20)
            )
        )

        Column(modifier = Modifier
            .fillMaxSize()) {
            TrailList(trails)
            Spacer(modifier = Modifier.weight(1f))
            FloatingActionButton(
                onClick = {
                    // Tutaj należy dodać kod do uruchamiania aparatu
                    Toast.makeText(context, "Tutaj powinien odpalić się aparat", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.AddCircle,
                    contentDescription = "Odpal aparat"
                )
            }
        }
    }
    @Composable
    fun DisplayTrail(x: Trail){
        Surface(onClick = {
            val intent = Intent(this, Details::class.java)
            intent.putExtra("trail", x)
            startActivity(intent)
        }) {
            Text(
                text = x.name,
                modifier = Modifier
                    .padding(16.dp),
                textAlign = TextAlign.Center,
            )
        }

    }

    @Composable
    fun TrailList(trails: List<Trail>){
        LazyColumn {
            items(trails.size){
                trails.forEach{trail ->
                    DisplayTrail(trail)
                }
            }

        }
    }
}

