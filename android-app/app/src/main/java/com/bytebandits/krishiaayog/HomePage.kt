package com.bytebandits.krishiaayog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import com.bytebandits.krishiaayog.ui.theme.Lufga
import com.bytebandits.krishiaayog.ui.theme.darkgreencolour
import com.bytebandits.krishiaayog.ui.theme.greenColor
import com.bytebandits.krishiaayog.ui.theme.mainBgColour


@Composable
fun HomeScreen() {

    ConstraintLayout(modifier = Modifier
        .fillMaxSize()
        .padding(20.dp)) {

        val (topBar, weatherCard, historyCard, bottomDock) = createRefs()

        TopBar(modifier = Modifier
            .padding(top = 40.dp)
            .constrainAs(topBar) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(weatherCard.top)
            })

        WeatherCard(modifier = Modifier.constrainAs(weatherCard){
            top.linkTo(topBar.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        })
    }


}


@Composable
fun TopBar(modifier: Modifier) {

    Box(modifier = modifier
        .padding(top = 20.dp)
        .fillMaxWidth()) {
        Column {
            Text(
                text = "Hi Amitrajeet !",
                fontFamily = Lufga,
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal
            )

            Text(
                text = "Good Morning",
                fontFamily = Lufga,
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }



}

@Composable
fun WeatherCard(modifier: Modifier) {

    Box(modifier = modifier
        .fillMaxWidth()
        .padding(top = 30.dp)
        .clip(RoundedCornerShape(22.dp))
        .background(greenColor)
        .height(170.dp)
        .padding(15.dp)) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.PinDrop, contentDescription = "Location", modifier = Modifier.size(18.dp) )
            Text("Lucknow", fontFamily = Lufga, fontSize = 16.sp, fontWeight = FontWeight.Normal)
        }
        Text(text = "29C", fontFamily = Lufga, fontSize = 18.sp, fontWeight = FontWeight.Normal, modifier = modifier.padding(top = 5.dp))
        Text(text = "Sunny", fontFamily = Lufga, fontSize = 14.sp, fontWeight = FontWeight.Normal)

        HorizontalDivider(color = Color.Black)

        Row(modifier = Modifier
            .padding(top = 7.dp)
            .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Humidity", fontFamily = Lufga, fontSize = 14.sp, fontWeight = FontWeight.Light)
                Text(text = "60%", fontFamily = Lufga, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)

            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Wind", fontFamily = Lufga, fontSize = 14.sp, fontWeight = FontWeight.Light)
                Text(text = "60%", fontFamily = Lufga, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)

            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Pressure", fontFamily = Lufga, fontSize = 14.sp, fontWeight = FontWeight.Light)
                Text(text = "60%", fontFamily = Lufga, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)

            }

        }
    }
    }

}

