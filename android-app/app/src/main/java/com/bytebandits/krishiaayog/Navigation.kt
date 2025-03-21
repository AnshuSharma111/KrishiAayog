package com.bytebandits.krishiaayog


import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bytebandits.krishiaayog.viewmodel.CameraViewModel
import com.bytebandits.krishiaayog.viewmodel.CropHealthScreenViewModel
import com.bytebandits.krishiaayog.viewmodel.HomeScreenViewModel
import com.bytebandits.krishiaayog.viewmodel.LivestockHealthScreenViewModel
import com.bytebandits.krishiaayog.viewmodel.SignUpViewModel


@Composable
fun Navigation(
    navController: NavHostController,
    cameraViewModel: CameraViewModel,
    homeScreenViewModel: HomeScreenViewModel,
    signUpViewModel: SignUpViewModel,
    cropHealthScreenViewModel: CropHealthScreenViewModel,
    livestockHealthScreenViewModel: LivestockHealthScreenViewModel
) {

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
    val token = sharedPreferences.getString("jwt_token", null)


    val startDestination = if (token.isNullOrEmpty()) "start" else "home"

    NavHost(navController = navController, startDestination = startDestination) {

        composable(route = "loading_screen") {
            LoadingScreen()
        }

        composable(route = "start") {
            StartPage(navController)
        }

        composable(route = "login") {
            LoginPage()
        }

        composable(route = "signup") {
            SignupPage(navController, signUpViewModel)
        }
        composable(route = "home") {
            LocationPermissions(onPermissionGranted = {
                homeScreenViewModel.onPermissionGranted()
                HomeScreen(navController, viewModel = homeScreenViewModel)
            })
        }

        composable(route = "crophealth") {
            CropHealthScreen(navController, cropHealthScreenViewModel)
        }

//        composable(route = "image_capture") {
//            Camera(navController, cameraViewModel)
//        }

        composable("camera/{resultRoute}") { backStackEntry ->
            val resultRoute = backStackEntry.arguments?.getString("resultRoute") ?: "home"
            Camera(navController, cameraViewModel, resultRoute)
        }

        composable(route = "crop_health_result") {
            CropHealthResultPage(navController, cameraViewModel)
        }

        composable(route = "livestockhealth") {
            LivestockDiseaseScreen(navController, livestockHealthScreenViewModel)
        }

        composable(route = "livestock_health_result") {
            LiveStockHealthResultPage(navController, cameraViewModel)
        }

    }
}

