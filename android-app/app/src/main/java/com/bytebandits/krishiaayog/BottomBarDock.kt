package com.bytebandits.krishiaayog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.bytebandits.krishiaayog.ui.theme.darkgreencolour
import com.bytebandits.krishiaayog.ui.theme.greenColor

@Composable
fun BottomBarDock(navController: NavController, modifier: Modifier = Modifier) {
    val selectedTab = remember { mutableStateOf("home") }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(horizontal = 60.dp)
            .shadow(elevation = 8.dp, shape = RoundedCornerShape(50.dp))
            .clip(RoundedCornerShape(50.dp))
            .background(darkgreencolour),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 5.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {

            BottomBarButton(icon = R.drawable.home, navController = navController, route = "home", selectedTab = selectedTab, size = Dp(25F))

            BottomBarButton(icon = R.drawable.corn, navController = navController, route = "crophealth", selectedTab = selectedTab, size = Dp(35F))

            BottomBarButton(icon = R.drawable.cow, navController = navController, route = "livestockhealth", selectedTab = selectedTab, size = Dp(35F))
        }

    }
}


@Composable
fun BottomBarButton(icon: Int, navController: NavController, route: String, selectedTab: MutableState<String>, size: Dp) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val isSelected = selectedTab.value == route
//    val isSelected = currentRoute?.startsWith("crophealth") == true


    Box(
        modifier = Modifier
            .size(width = 60.dp, height = 60.dp)
            .clip(CircleShape)
            .background( if(isSelected) greenColor else Color.White)
            .border(1.dp, greenColor, CircleShape)
            .clickable{
                if(currentRoute != route){
                    selectedTab.value = route
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Image(painter = painterResource(icon), contentDescription = null, colorFilter = ColorFilter.tint(Color.Black), modifier = Modifier.size(size))
    }

}