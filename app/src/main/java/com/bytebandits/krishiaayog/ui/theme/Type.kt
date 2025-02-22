package com.bytebandits.krishiaayog.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.bytebandits.krishiaayog.R

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)


val Lufga = FontFamily(
    Font(R.font.lufgabold, FontWeight.Bold),
    Font(R.font.lufgaextrabold, FontWeight.ExtraBold),
    Font(R.font.lufgaextralight, FontWeight.ExtraLight),
    Font(R.font.lufgalight, FontWeight.Light),
    Font(R.font.lufgamedium, FontWeight.Medium),
    Font(R.font.lufgaregular, FontWeight.Normal),
    Font(R.font.lufgasemibold, FontWeight.SemiBold),
    Font(R.font.lufgathin, FontWeight.Thin),
    )