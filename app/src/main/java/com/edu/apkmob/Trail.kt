package com.edu.apkmob

import java.io.Serializable

data class Trail(
    var id: Int,
    var name: String,
    var distance: String,
    var level: String,
    var desc: String
): Serializable
