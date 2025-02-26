package com.bytebandits.krishiaayog

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.location.LocationManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.bytebandits.krishiaayog.DataClass.currentWeather.CurrentWeatherDataClass
import com.bytebandits.krishiaayog.ui.theme.Lufga
import com.bytebandits.krishiaayog.ui.theme.darkgreencolour
import com.bytebandits.krishiaayog.ui.theme.greenColor
import com.bytebandits.krishiaayog.ui.theme.mainBgColour
import com.bytebandits.krishiaayog.viewmodel.HomeScreenViewModel
import kotlin.math.roundToInt


@Composable
fun HomeScreen(navController: NavController, viewModel: HomeScreenViewModel) {

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
    val username = sharedPreferences.getString("username", null)

    val gpsEnabled = isGPSEnabled(viewModel.getApplication())
    val weatherData = viewModel.weatherData.value

    if (weatherData != null ) {

        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {

            val (topBar, weatherCard, historyCard) = createRefs()

            if (username != null) {
                TopBar(modifier = Modifier
                    .padding(top = 40.dp)
                    .constrainAs(topBar) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(weatherCard.top)
                    }, username)
            }

            WeatherCard(modifier = Modifier.constrainAs(weatherCard) {
                top.linkTo(topBar.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }, weatherData = weatherData)
        }
    } else {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column {
                LoadingScreen()
                if (!gpsEnabled) Text(
                    "Oops, Looks like GPS is off. \uD83D\uDE34",
                    color = Color.Black,
                    fontFamily = Lufga,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp
                )
            }
        }
    }
}


@SuppressLint("NewApi")
@Composable
fun TopBar(modifier: Modifier, username:String) {

    Box(modifier = modifier
        .padding(top = 20.dp)
        .fillMaxWidth()) {
        Column {
            Text(
                text = username,
                fontFamily = Lufga,
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal
            )

            Text(
                text = greeting(),
                fontFamily = Lufga,
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun WeatherCard(modifier: Modifier, weatherData: CurrentWeatherDataClass?) {
    val image =
        rememberAsyncImagePainter(
            model = "https://openweathermap.org/img/wn/${
                weatherData?.weather?.get(
                    0
                )?.icon
            }@2x.png"
        )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 30.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(greenColor)
            .height(190.dp)
            .padding(15.dp)
    ) {

        Column {
            Row(modifier = Modifier.fillMaxWidth().padding(end = 30.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column() {


                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.PinDrop,
                            contentDescription = "Location",
                            modifier = Modifier.size(18.dp)
                        )
                        if (weatherData != null) {
                            Text(
                                weatherData.name,
                                fontFamily = Lufga,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                    if (weatherData != null) {
                        Text(
                            "${weatherData.main.temp.roundToInt()}°C",
                            fontFamily = Lufga,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            modifier = modifier.padding(top = 5.dp)
                        )
                    }
                    if (weatherData != null) {
                        Text(
                            text = weatherData.weather[0].description.split(' ')
                                .joinToString(" ") { it.replaceFirstChar(Char::uppercase) },
                            fontFamily = Lufga,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }

                Image(image, contentDescription = null, modifier = Modifier.size(100.dp))
            }


            HorizontalDivider(color = Color.Black)


            Row(
                modifier = Modifier
                    .padding(top = 7.dp)
                    .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Humidity",
                        fontFamily = Lufga,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Light
                    )

                    if (weatherData != null) {
                        Text(
                            text = "${weatherData.main.humidity}%",
                            fontFamily = Lufga,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Wind",
                        fontFamily = Lufga,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Light
                    )
                    if (weatherData != null) {
                        Text(
                            text = "${weatherData.wind?.speed?.let { Utils.mstokmh(it) }} km/h",
                            fontFamily = Lufga,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Pressure",
                        fontFamily = Lufga,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Light
                    )
                    if (weatherData != null) {
                        Text(
                            text = "${weatherData.main.pressure}mb",
                            fontFamily = Lufga,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}



fun isGPSEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}

@RequiresApi(Build.VERSION_CODES.O)
fun greeting(): String {
    //GoodMorning, GoodAfternoon, GoodEvening
    val currentHour = java.time.LocalTime.now().hour
    val greeting = when {

        currentHour in 4..11 -> "Good Morning"
        currentHour in 12..17 -> "Good Afternoon"

        else -> "Good Evening"
    }

    return greeting

}
