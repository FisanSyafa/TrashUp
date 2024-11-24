package com.bangkit.trashup.di

import UserRepository
import android.content.Context
import com.bangkit.trashup.data.pref.UserPreference
import com.bangkit.trashup.data.pref.dataStore
import com.bangkit.trashup.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(context)
        return UserRepository.getInstance(pref, apiService)
    }
}