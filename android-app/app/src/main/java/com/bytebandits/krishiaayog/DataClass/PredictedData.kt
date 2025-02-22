package com.bytebandits.krishiaayog.DataClass

data class PredictedData (
    val cause: String,
    val confidence: Double,
    val cure: String,
    val description: String,
    val predicted_class: String
)