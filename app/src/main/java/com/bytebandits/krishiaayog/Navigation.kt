package com.bytebandits.krishiaayog


import android.app.Application
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bytebandits.krishiaayog.viewmodel.CameraViewModel


@Composable
fun Navigation(navController: NavHostController, cameraViewModel: CameraViewModel) {

    NavHost(navController = navController, startDestination = "home") {
        composable(route = "home") {
            HomeScreen()
        }

        composable(route = "crophealth") {
            CropHealthScreen(navController)
        }

        composable(route = "image_capture") {
            Camera(navController, cameraViewModel)
        }

    }
}

