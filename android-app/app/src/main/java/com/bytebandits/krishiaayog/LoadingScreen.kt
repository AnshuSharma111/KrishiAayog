package com.bytebandits.krishiaayog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bytebandits.krishiaayog.ui.theme.Lufga

@Composable
fun LoadingScreen() {
        LinearProgressIndicator(color = Color.Black)
        Spacer(Modifier.height(10.dp))
        Text(
            "LOADING...",
            fontFamily = Lufga,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

}