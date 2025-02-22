package com.bytebandits.krishiaayog.viewmodel

import com.bytebandits.krishiaayog.DataClass.PredictedData
import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bytebandits.krishiaayog.retrofitInterface.RetrofitInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.IOException

class CameraViewModel(application: Application) : AndroidViewModel(application) {

    val DiseaseData = mutableStateOf<PredictedData?>(null)

    val CapturedImage = mutableStateOf<Bitmap?>(null)


    fun getData(image: ByteArray) {
        viewModelScope.launch{
            try {

                //Storing the image in the viewmodel
                val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)
                CapturedImage.value = bitmap

                val requestbody = image.toRequestBody("image/jpeg".toMediaType(), 0, image.size)
                val multipartBody =
                    MultipartBody.Part.createFormData("file", "image.jpg", requestbody)
                val dataResponse: Response<PredictedData> =
                    RetrofitInterface.instance.uploadImage(multipartBody)

                withContext(Dispatchers.Main) {

                    if (dataResponse.isSuccessful) {
                        DiseaseData.value = dataResponse.body()
                        println(DiseaseData.value)
                    }
                }
            } catch (e: Exception) {
                println(e)
                DiseaseData.value = null
            } catch (e: IOException) {
                println(e)
                DiseaseData.value = null
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