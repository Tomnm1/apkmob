package com.edu.apkmob

import java.io.Serializable

data class Trail(
    val name: String,
    val stages: List<String>,
    val description: String,
    val estimatedTimes: List<Int>
): Serializable
