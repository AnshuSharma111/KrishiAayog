package com.bytebandits.krishiaayog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.bytebandits.krishiaayog.ui.theme.Lufga
import com.bytebandits.krishiaayog.ui.theme.darkgreencolour
import com.bytebandits.krishiaayog.ui.theme.greenColor
import com.bytebandits.krishiaayog.ui.theme.mainBgColour
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CropHealthScreen(navController: NavController) {

//    LaunchedEffect(Unit) {
//        viewModel.getPredictedHistory()
//    }

//    val user_history = viewModel.PredictedHistory.value

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
                text = "Welcome Back! 👋",
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 50.dp),
                fontFamily = Lufga,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp)
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
                Button(
                    onClick = { navController.navigate("image_capture") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        darkgreencolour
                    )
                ) {
                    Text(
                        "Diagnose", fontFamily = Lufga,
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
                .height(360.dp)
                .padding(top = 40.dp)
                .constrainAs(RecentDiagnosesCard) {
                    top.linkTo(ScanCard.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }, colors = CardDefaults.cardColors(Color.White)
        ) {
            Column(modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
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
            }

//            LazyColumn {
//                if (user_history != null) {
//                    items(user_history.toList()){item ->
//                        LaunchedEffect(item._id) {
//                            if(item.image == null){
//                                viewModel.getImageForId(item._id)
//                            }
//                        }
//
//                        Row {
//                            val imageStream  = viewModel.getImageForId(item._id)
//                            if(imageStream!=null){
//                                Image(rememberAsyncImagePainter(imageStream), null)
//                            }
//                            Text(item.predicted_class, fontFamily = Poppins, fontSize = 12.sp, fontWeight = FontWeight.Normal)
//                        }
//                    }
//                }
//            }

        }

    }
}