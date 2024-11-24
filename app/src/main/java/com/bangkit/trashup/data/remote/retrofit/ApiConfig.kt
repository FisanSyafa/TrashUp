package com.bangkit.trashup.data.remote.retrofit

import android.content.Context
import android.util.Log
import com.bangkit.trashup.data.pref.UserPreference
import com.bangkit.trashup.data.pref.dataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        fun getApiService(context: Context): ApiService {
            val userPreference = UserPreference.getInstance(context.dataStore)

            val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

            val authInterceptor = Interceptor { chain ->
                val token = runBlocking {
                    userPreference.getSession().firstOrNull()?.token.orEmpty()
                }

                if (token.isNotEmpty()) {
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                    Log.d("Auth Header", request.headers["Authorization"].toString())
                    chain.proceed(request)
                } else {
                    chain.proceed(chain.request())
                }
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://story-api.dicoding.dev/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}