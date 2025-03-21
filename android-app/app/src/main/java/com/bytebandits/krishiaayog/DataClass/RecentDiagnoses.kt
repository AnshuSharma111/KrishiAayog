package com.bytebandits.krishiaayog.DataClass

data class CropDiseaseHistory (
    val prediction : String,
    val timestamp : String,
    val image : String
)


data class DiseaseHistoryList(
    val history : List<CropDiseaseHistory>

)