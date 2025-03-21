package com.bytebandits.krishiaayog.viewmodel

import android.annotation.SuppressLint
import com.bytebandits.krishiaayog.DataClass.CropHealthPredictedData
import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bytebandits.krishiaayog.DataClass.LivestockHealthPredictedData
import com.bytebandits.krishiaayog.retrofitInterface.RetrofitInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.IOException

class CameraViewModel(application: Application) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    val CropDiseaseData = mutableStateOf<CropHealthPredictedData?>(null)
    val LivestockDiseaseData = mutableStateOf<LivestockHealthPredictedData?>(null)

    val CapturedImage = mutableStateOf<Bitmap?>(null)


    fun getData(resultRoute:String, image: ByteArray) {
        viewModelScope.launch{
            try {

                //Storing the image in the viewmodel
                val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
                CapturedImage.value = bitmap

                val requestbody = image.toRequestBody("image/jpeg".toMediaType(), 0, image.size)
                val multipartBody =
                    MultipartBody.Part.createFormData("file", "image.jpg", requestbody)

                val apiService = RetrofitInterface.create(context)

                val dataResponse = if (resultRoute == "crop_health_result") {
                    apiService.uploadCropImage(multipartBody)
                } else {
                    apiService.uploadLivestockImage(multipartBody) // Corrected this line
                }
//                val dataResponse: Response<CropHealthPredictedData> = when (resultRoute) {
//                    "crop_health_result" -> RetrofitInterface.create(context).uploadCropImage(multipartBody)
//                    "livestock_health_result" -> fetchLivestockHealth()
//                    else ->
//                }


                withContext(Dispatchers.Main) {
                    println("Request Chole geche")

                    if (dataResponse.isSuccessful) {
                        if (resultRoute == "crop_health_result") {
                            CropDiseaseData.value = dataResponse.body() as CropHealthPredictedData?
                        } else {
                            LivestockDiseaseData.value = dataResponse.body() as LivestockHealthPredictedData?
                        }
                        println(dataResponse.body())
                    }else{
                        println("response nhi aya !")
                    }
                }
            } catch (e: Exception) {
                println(e)
                CropDiseaseData.value = null
            } catch (e: IOException) {
                println(e)
                CropDiseaseData.value = null
            }
        }
    }
}

class CameraViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CameraViewModel::class.java)) {
            return CameraViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}