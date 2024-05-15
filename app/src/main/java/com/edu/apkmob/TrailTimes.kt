package com.edu.apkmob

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.sql.Time
import java.util.Date

class TrailTimes: ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrailTimesLayout()
        }
    }

    data class Time(
        val date: Date,
        val time: java.sql.Time,
    )

    val timeObject =
        Time(Date(System.currentTimeMillis()), Time(System.currentTimeMillis()))

    @Composable
    fun TrailTimesLayout(){
        val list = listOf(
            timeObject, timeObject, timeObject
        )
        TimesList(list)

        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.align(Alignment.BottomCenter)) {
                IconButton(onClick = { /* do something */ }) {
                    Icon(
                        Icons.Outlined.Close,
                        contentDescription = "Stop",
                        modifier = Modifier.size(48.dp))
                }
            }
        }
    }
    @Composable
    fun TimesList(times: List<Time>) {
        Column {
            times.forEach { (date, time) ->
                Row {
                    Text(text = date.toString())
                    Text(text = time.toString())
                }
            }
        }
    }
}