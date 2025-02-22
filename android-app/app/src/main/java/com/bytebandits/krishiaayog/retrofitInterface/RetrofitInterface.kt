package com.bytebandits.krishiaayog.retrofitInterface

import com.bytebandits.krishiaayog.DataClass.PredictedData
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
    @POST("/predict")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ):Response<PredictedData>

//    @GET("/api/prediction")
//    suspend fun getHistory():Response<HistoryData>
//
//    @GET("/api/prediction/{id}")
//    suspend fun getHistoryById(@Path("id") id:String):Response<HistoryDataItem>

    @GET("/api/image/{id}")
    suspend fun fetchImageById(@Path("id") id:String):Response<ResponseBody>


    companion object{
        private val client = OkHttpClient.Builder()
            .connectTimeout(4, TimeUnit.MINUTES) // 4-minute connection timeout
            .writeTimeout(4, TimeUnit.MINUTES)   // 4-minute write timeout
            .readTimeout(4, TimeUnit.MINUTES)    // 4-minute read timeout
            .build()
        val instance by lazy {
            Retrofit.Builder()
                .baseUrl("http://172.17.7.142:5000")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RetrofitInterface::class.java)
        }
    }
}


