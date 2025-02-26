package com.bytebandits.krishiaayog

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.bytebandits.krishiaayog.DataClass.CropHealthPredictedData
import com.bytebandits.krishiaayog.DataClass.LivestockHealthPredictedData
import com.bytebandits.krishiaayog.ui.theme.Lufga
import com.bytebandits.krishiaayog.ui.theme.greenColor
import com.bytebandits.krishiaayog.viewmodel.CameraViewModel
import java.util.Locale


@Composable
fun LiveStockHealthResultPage (navController: NavController, viewModel: CameraViewModel) {
    BackHandler {
        navController.navigate("crophealth") {
//            popUpTo(0) {
//                inclusive = false
//            }
        }
    }

    val diseaseData = viewModel.DiseaseData.value
    val capturedImage = viewModel.CapturedImage.value


    Surface(modifier = Modifier.fillMaxSize(), color = greenColor) {
        if (diseaseData == null) {
            DataLoadingAnimation()
        } else {
            Column {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp, start = 10.dp, end = 10.dp),
                    contentAlignment = Alignment.CenterStart

                ) {

                    Box(modifier = with(Modifier) {
                        padding(start = 5.dp)
                            .background(Color(0xABFFFFFF), shape = CircleShape)
                            .clip(CircleShape)
                            .clickable {
                                navController.navigate("home") {
                                    popUpTo(0) {
                                        inclusive = false
                                    }
                                }
                            }
                            .padding(8.dp)
                    }) {
                        Icon(
                            Icons.Rounded.ArrowBackIosNew,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }


                    Text(
                        "Results",
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(10.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontFamily = Lufga,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                }

                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 10.dp, end = 10.dp, top = 10.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    val (ImageCard, ResultsCards) = createRefs()

                    if (capturedImage != null) {
                        Card(
                            modifier = Modifier
                                .constrainAs(ImageCard) {
                                    top.linkTo(parent.top)
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                    bottom.linkTo(ResultsCards.top)
                                }
                                .fillMaxWidth()
                                .height(120.dp)
                                .padding(10.dp),
                            colors = CardDefaults.cardColors(Color(0xFF282828))
                        ) {
                            Row {

                                Image(
                                    capturedImage.asImageBitmap(),
                                    contentDescription = null,
                                    modifier = Modifier.aspectRatio(1f)
                                        .size(width = 50.dp, height = 120.dp)
                                        .padding(start = 10.dp, top = 10.dp, bottom = 10.dp).clip(
                                            RoundedCornerShape(5.dp)
                                        ),
                                    contentScale = ContentScale.Crop
                                )

                                Text(
                                    "${diseaseData.prediction.uppercase(Locale.ROOT)}", fontSize = 16.sp,
                                    fontFamily = Lufga,
                                    modifier = Modifier.fillMaxWidth().padding(5.dp).align(Alignment.CenterVertically),
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center, color = Color.White)
                            }
                        }
                    }
                    Cards(diseaseData, modifier = Modifier.constrainAs(ResultsCards) {
                        top.linkTo(ImageCard.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    })

                }
            }
        }
    }
}

@Composable
fun Cards(diseaseData: LivestockHealthPredictedData, modifier: Modifier) {
    Column(modifier = modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 30.dp)) {
        Card(
            modifier = Modifier.padding(top = 5.dp, bottom = 5.dp),
            colors = CardDefaults.cardColors(Color.White)
        ) {
            Column(modifier = Modifier.padding(15.dp)) {
                Text(
                    "Description", fontSize = 14.sp,
                    fontFamily = Lufga,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    "${diseaseData.description} ",
                    fontSize = 12.sp,
                    fontFamily = Lufga,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Justify,
                )
            }
        }
        Card(
            modifier = Modifier.padding(top = 5.dp, bottom = 5.dp),
            colors = CardDefaults.cardColors(Color.White)
        ) {
            Column(modifier = Modifier.padding(15.dp)) {
                Text(
                    "Cause", fontSize = 14.sp,
                    fontFamily = Lufga,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    "${diseaseData.cause} ",
                    fontSize = 12.sp,
                    fontFamily = Lufga,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Justify
                )
            }
        }

        Card(
            modifier = Modifier.padding(top = 5.dp, bottom = 5.dp),
            colors = CardDefaults.cardColors(Color.White)
        ) {
            Column(modifier = Modifier.padding(15.dp)) {

                Text(
                    "Cure", fontSize = 14.sp,
                    fontFamily = Lufga,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    "${diseaseData.cure} ",
                    fontSize = 12.sp,
                    fontFamily = Lufga,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Justify
                )
            }
        }
    }
}