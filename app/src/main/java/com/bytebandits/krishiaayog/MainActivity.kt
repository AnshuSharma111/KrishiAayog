package com.bytebandits.krishiaayog

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bytebandits.krishiaayog.ui.theme.KrishiAayogTheme
import com.bytebandits.krishiaayog.viewmodel.CameraViewModel
import com.bytebandits.krishiaayog.viewmodel.CameraViewModelFactory

class MainActivity : ComponentActivity() {

    lateinit var navController: NavHostController
    private lateinit var cameraViewModel: CameraViewModel

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
            true
        setContent {
            KrishiAayogTheme {
                navController = rememberNavController()
                cameraViewModel = viewModel(
                    factory = CameraViewModelFactory(application)
                )

                val currentRoute =
                    navController.currentBackStackEntryAsState().value?.destination?.route
                val hideBottomBarRoutes = listOf("image_capture") // Add more routes as needed

                Scaffold(bottomBar = {if (currentRoute !in hideBottomBarRoutes){
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 26.dp)
                    ) { BottomBarDock(navController) }
                }}) {
                    Navigation(navController = navController, cameraViewModel)
                }


            }
        }
    }
}

