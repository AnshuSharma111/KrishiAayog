package com.bytebandits.krishiaayog

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale

//Permissions Code Below

@SuppressLint("PermissionLaunchedDuringComposition")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissions(onPermissionGranted: @Composable () -> Unit) {
    val locationPermissionState = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION

        )
    )

    val permissionsGranted = locationPermissionState.permissions.any { it.status.isGranted }
    val shouldShowRationale =
        locationPermissionState.permissions.any { it.status.shouldShowRationale }

    if (permissionsGranted) {
        onPermissionGranted()

    } else {

        PermissionScreen(
            showRationale = shouldShowRationale,
            onRequestPermissions = { locationPermissionState.launchMultiplePermissionRequest() })
    }
}
