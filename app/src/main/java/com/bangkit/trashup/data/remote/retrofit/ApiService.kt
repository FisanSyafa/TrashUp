package com.bangkit.trashup.data.remote.retrofit

import com.bangkit.trashup.data.remote.response.ArticlesResponse
import com.example.storyappdicoding.data.remote.response.LoginResponse
import com.example.storyappdicoding.data.remote.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>

    @Multipart
    @POST("stories")
    suspend fun uploadStories(
        @Part("description") description: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<RegisterResponse>

    @GET("stories")
    suspend fun getStories(
    ): Response<ArticlesResponse>
}