package com.bytebandits.krishiaayog.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.bytebandits.krishiaayog.DataClass.CropDiseaseHistory
import com.bytebandits.krishiaayog.DataClass.DiseaseHistoryList
import com.bytebandits.krishiaayog.retrofitInterface.RetrofitInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CropHealthScreenViewModel(application: Application) : AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    val _diseaseHistory = MutableStateFlow<DiseaseHistoryList?>(null)
    val diseaseHistory: StateFlow<DiseaseHistoryList?> = _diseaseHistory.asStateFlow()


    fun getPrevCropHistory(){
        viewModelScope.launch {
            try {

                val retrofit = RetrofitInterface.create(context)
                val response = retrofit.getCropHistory()

                if (response.isSuccessful) {
                    _diseaseHistory.value = response.body()
                    println(_diseaseHistory.value)
                } else {
                    println("${response.errorBody()} + ${response.code()}")
                }
            } catch ( e : Exception){
                println(e.message)
            }
        }
    }
}

class CropHealthScreenViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CropHealthScreenViewModel::class.java)) {
            return CropHealthScreenViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

