package com.bytebandits.krishiaayog.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.bytebandits.krishiaayog.DataClass.AuthResponse
import com.bytebandits.krishiaayog.DataClass.SignUpRequest
import com.bytebandits.krishiaayog.retrofitInterface.provideLoginSignupService
import kotlinx.coroutines.launch
import retrofit2.Response

class SignUpViewModel (application: Application) : AndroidViewModel(application) {

    private val _signupResult = MutableLiveData<String?>()
    val signupResult: LiveData<String?> get() = _signupResult

    fun signup(user: SignUpRequest, context: Context, navController: NavController) {
        viewModelScope.launch {
            try {

                val response: Response<AuthResponse> = provideLoginSignupService().signup(user)
                if (response.isSuccessful) {
                    response.body()?.let {
                        println(response.body()!!.token)
                        saveToken(context, it.token , it.username)
                        navController.navigate("home"){popUpTo("start")}
                    }
                } else {
                    println("Failed")
                    _signupResult.postValue("Signup failed!")
                }
            } catch (e: Exception) {
                _signupResult.postValue("Error: ${e.message}")
            }
        }
    }

    private fun saveToken(context: Context, token: String, username: String) {
        val sharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("jwt_token", token).putString("username", username).apply()
    }

}

class SignupPageViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignUpViewModel::class.java)) {
            return SignUpViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

