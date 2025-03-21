package com.bytebandits.krishiaayog

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.bytebandits.krishiaayog.ui.theme.Lufga
import com.bytebandits.krishiaayog.ui.theme.darkgreencolour
import com.bytebandits.krishiaayog.ui.theme.greenColor
import com.bytebandits.krishiaayog.ui.theme.mainBgColour
import com.bytebandits.krishiaayog.viewmodel.CropHealthScreenViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CropHealthScreen(navController: NavController, viewModel: CropHealthScreenViewModel) {

    LaunchedEffect(Unit) {
        viewModel.getPrevCropHistory()
    }

    val user_history by viewModel.diseaseHistory.collectAsStateWithLifecycle()

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.plant))
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(mainBgColour)
            .padding(20.dp)
    ) {
        val (TopBar, ScanCard, RecentDiagnosesCard) = createRefs()

        Box(modifier = Modifier
            .constrainAs(TopBar) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(ScanCard.top)
            }
            .fillMaxWidth()) {
            Text(
                text = "Crop Health ",
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 50.dp),
                fontFamily = Lufga,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )

        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp)
                .constrainAs(ScanCard) {
                    top.linkTo(TopBar.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(RecentDiagnosesCard.top)
                }, colors = CardDefaults.cardColors(greenColor)
        ) {
            Column {
                Row(
                    modifier = Modifier.padding(top = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LottieAnimation(
                        composition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier.size(130.dp)
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Check Your Plant",
                            modifier = Modifier.align(Alignment.Start),
                            fontFamily = Lufga,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Take Photos, start diagnose disease, & get plant care tips !",
                            fontFamily = Lufga,
                            lineHeight = 16.sp,
                            modifier = Modifier
                                .padding(top = 5.dp)
                                .align(Alignment.Start),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
                val resultRoute = "crop_health_result"
                Button(
                    onClick = { navController.navigate("camera/$resultRoute") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        darkgreencolour
                    )
                ) {
                    Text(
                        "Diagnose",
                        fontFamily = Lufga,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    )
                    Image(
                        painterResource(R.drawable.scan),
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(start = 5.dp)
                    )
                }
            }
        }


        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(top = 40.dp)
                .constrainAs(RecentDiagnosesCard) {
                    top.linkTo(ScanCard.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }, colors = CardDefaults.cardColors(greenColor)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxSize()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Recent Diagnoses",
                        fontFamily = Lufga,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        "See All",
                        modifier = Modifier.clickable { },
                        color = darkgreencolour,
                        fontFamily = Lufga,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Light,
                        textAlign = TextAlign.Right
                    )
                }
                Spacer(Modifier.height(6.dp))
                HorizontalDivider(color = Color(0xFFA4A4A4))

                val history_list = user_history

                if (history_list?.history?.isNotEmpty() == true) {
                    LazyColumn(modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxSize()) {

                        items(history_list.history.size) { item ->
                            val history = history_list.history[item]
                            val bitmap = decodeBase64ToBitmap(history.image)

                            HistoryBox(history.prediction, history.timestamp, bitmap)
                        }

                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                    ) {
                        Text("No History Found", textAlign = TextAlign.Center, fontSize = 16.sp,
                            fontWeight = FontWeight.Normal)
                    }
                }
            }
        }

    }

}

@Composable
fun HistoryBox(prediction: String, timestamp: String, bitmap: Bitmap) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            bitmap.asImageBitmap(), "", modifier = Modifier
                .width(40.dp)
                .height(50.dp)
                .clip(
                    RoundedCornerShape(5.dp)
                )
        )
        Column(modifier = Modifier.padding(start = 5.dp)) {
            Text(
                prediction,
                fontFamily = Lufga,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 2.sp
            )
            Text(timestamp, fontFamily = Lufga, fontSize = 12.sp, fontWeight = FontWeight.Light)
        }
    }
}


@OptIn(ExperimentalEncodingApi::class)
fun decodeBase64ToBitmap(base64String: String): Bitmap {
    val decodedBytes: ByteArray = Base64.decode(base64String)
    return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        ?: throw IllegalArgumentException("Failed to decode Base64 to Bitmap")
}

