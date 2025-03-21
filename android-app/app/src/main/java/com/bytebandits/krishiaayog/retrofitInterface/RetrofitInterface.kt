package com.bytebandits.krishiaayog.retrofitInterface

import android.content.Context
import com.bytebandits.krishiaayog.DataClass.CropDiseaseHistory
import com.bytebandits.krishiaayog.DataClass.CropHealthPredictedData
import com.bytebandits.krishiaayog.DataClass.DiseaseHistoryList
import com.bytebandits.krishiaayog.DataClass.HomePageAnnouncements
import com.bytebandits.krishiaayog.DataClass.LivestockHealthPredictedData
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

interface RetrofitInterface {

    @Multipart
    @POST("crop/predict")
    suspend fun uploadCropImage(
        @Part file: MultipartBody.Part
    ):Response<CropHealthPredictedData>

    @Multipart
    @POST("livestock/predict")
    suspend fun uploadLivestockImage(
        @Part file: MultipartBody.Part
    ):Response<LivestockHealthPredictedData>

    @GET("livestock/history")
    suspend fun getLivestockHistory():Response<DiseaseHistoryList>


    @GET("crop/history")
    suspend fun getCropHistory():Response<DiseaseHistoryList>

    @GET("daily")
    suspend fun getAnnouncements(): Response<HomePageAnnouncements>


    companion object {
        private fun provideOkHttpClient(context: Context): OkHttpClient {
            return OkHttpClient.Builder()
                .connectTimeout(4, TimeUnit.MINUTES)
                .writeTimeout(4, TimeUnit.MINUTES)
                .readTimeout(4, TimeUnit.MINUTES)
                .addInterceptor { chain ->
                    val sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                    val token = sharedPreferences.getString("jwt_token", null)
                    println("token" + token)

                    val request = chain.request().newBuilder().apply {
                        token?.let {
                            header("Authorization", "Bearer $it")  // Attach token to every request
                        }
                    }.build()

                    chain.proceed(request)
                }
                .build()
        }

        fun create(context: Context): RetrofitInterface {
            return Retrofit.Builder()
                .baseUrl("http://your-backend/api/")
                .client(provideOkHttpClient(context)) // Pass context to fetch JWT token
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RetrofitInterface::class.java)
        }
    }
}


