package com.bytebandits.krishiaayog

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bytebandits.krishiaayog.ui.theme.Lufga


@Composable
fun PermissionScreen(showRationale: Boolean, onRequestPermissions: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(30.dp)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (!showRationale) {
                    Text(
                        text = "This app requires location permission to work properly",
                        fontFamily = Lufga,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 20.dp),
                        color = Color.Black
                    )

                    Button(
                        onClick = onRequestPermissions,
                        modifier = Modifier.padding(20.dp),
                        colors = ButtonDefaults.buttonColors(Color.Black)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Get Started", fontFamily = Lufga, fontWeight = FontWeight.Bold, color = Color.White)
                            Icon(
                                Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                                contentDescription = "Arrow Forward",
                                modifier = Modifier.padding(start = 10.dp),
                                tint = Color.White
                            )

                        }
                    }
                } else {
                    val context = LocalContext.current
                    Text(
                        text = "Open Settings to enable location permission",
                        fontFamily = Lufga,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 20.dp),
                        color = Color.Black
                    )
                    Button(
                        onClick = {
                            val intent =
                                Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                }
                            context.startActivity(intent)
                        },
                        modifier = Modifier.padding(20.dp),
                        colors = ButtonDefaults.buttonColors(Color.Black)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Open Settings",
                                fontFamily = Lufga,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                                contentDescription = "Arrow Forward",
                                modifier = Modifier.padding(start = 10.dp),
                                tint = Color.White
                            )

                        }
                    }
                }
            }
        }

    }
}
