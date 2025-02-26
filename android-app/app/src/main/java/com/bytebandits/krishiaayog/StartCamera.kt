package com.bytebandits.krishiaayog


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.bytebandits.krishiaayog.ui.theme.Lufga
import com.bytebandits.krishiaayog.ui.theme.greenColor
import com.bytebandits.krishiaayog.viewmodel.CameraViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Camera(navController: NavHostController, cameraViewModel: CameraViewModel, resultRoute: String) {
    BackHandler {
        navController.navigate("crophealth"){
            popUpTo(0){
                inclusive = false
            }
        }
    }
    val cameraPermission: PermissionState =
        rememberPermissionState(android.Manifest.permission.CAMERA)

    if (cameraPermission.status.isGranted) {
        CameraPreviewScreen(navController, cameraViewModel, resultRoute)
    } else {
        Column {
            if (cameraPermission.status.shouldShowRationale) {
                Text("Camera permission is required to take photos")
            } else {
                Text("Camera permission is permanently denied. You can enable it in the settings.")
            }
            Button(onClick = {
                cameraPermission.launchPermissionRequest()
            }) {
                Text("Request permission")
            }
        }
    }


}

@Composable
fun CameraPreviewScreen(navController: NavController, cameraViewModel: CameraViewModel ,resultRoute: String) {
    val lensFacing = CameraSelector.LENS_FACING_BACK
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val preview = Preview.Builder().build()
    val previewView = remember {
        PreviewView(context)
    }
    val cameraxSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
    val imageCapture = remember { ImageCapture.Builder().build() }



    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraxSelector, preview, imageCapture)
        preview.surfaceProvider = previewView.surfaceProvider
    }
    ConstraintLayout(
        modifier = Modifier
            .background(greenColor)
            .fillMaxSize()
    ) {

        val (TopBar, CameraPreview) = createRefs()
        Box(modifier = Modifier
            .padding(top = 140.dp)
            .fillMaxWidth()
            .constrainAs(TopBar) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(CameraPreview.top)
            }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Box(modifier = Modifier
                    .padding(start = 5.dp)
                    .background(Color(0xABFFFFFF), shape = CircleShape)
                    .clip(CircleShape)
                    .clickable {
                        navController.navigate("home") {
                            popUpTo(0) {
                                inclusive = false
                            }
                        }
                    }
                    .padding(8.dp)) {
                    Icon(
                        Icons.Rounded.ArrowBackIosNew,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    "Diagnose",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(10.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontFamily = Lufga,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Box(contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .constrainAs(CameraPreview) {
                    top.linkTo(TopBar.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
                .fillMaxSize()) {
            AndroidView(
                factory = { previewView }, modifier = Modifier.fillMaxHeight()
            )
            Image(painterResource(R.drawable.camerabutton),
                contentDescription = null,
                modifier = Modifier
                    .padding(bottom = 150.dp).clip(CircleShape)
                    .clickable {

                        captureImage(navController, imageCapture, context, cameraViewModel, resultRoute)

                        Toast
                            .makeText(context, "Successfully Uploaded", Toast.LENGTH_SHORT)
                            .show()

                    })


        }
    }
}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }

private fun captureImage(
    navController: NavController,
    imageCapture: ImageCapture,
    context: Context,
    cameraViewModel: CameraViewModel,
    resultRoute: String
) {


    imageCapture.takePicture(
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback() {

            override fun onCaptureSuccess(imageProxy: ImageProxy) {
                val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                val bitmap = imageProxy.toBitmap()
                imageProxy.close()
                val matrix = Matrix()
                matrix.postRotate(rotationDegrees.toFloat()) // Rotate the image by the rotation degrees
                val imageCorrectedBitmap =
                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

                val byteArray = bitmapToByteArray(imageCorrectedBitmap)

                try {
                    CoroutineScope(Dispatchers.Main).launch {
                        cameraViewModel.getData(resultRoute, byteArray)
                        navController.navigate(resultRoute)
                    }

                } catch (e: Exception) {
                    println(e)
                }
            }

            override fun onError(exception: ImageCaptureException) {
                println("Capture Failed with error - ${exception.message}")
            }
        })
}

private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
    val stream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    return stream.toByteArray()
}