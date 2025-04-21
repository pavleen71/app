package com.example.expensetrackerapp.data.api
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
object RetrofitInstance {
    val api: AuthApi by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/api/") // Replace with your IP
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }
}