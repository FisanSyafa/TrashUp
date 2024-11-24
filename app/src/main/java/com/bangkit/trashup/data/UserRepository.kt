@file:Suppress("PackageDirectoryMismatch")

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.bangkit.trashup.data.Result
import com.bangkit.trashup.data.pref.UserModel
import com.bangkit.trashup.data.pref.UserPreference
import com.bangkit.trashup.data.remote.response.ListStoryItem
import com.bangkit.trashup.data.remote.retrofit.ApiService
import com.example.storyappdicoding.data.remote.response.RegisterResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {

    fun register(name: String, email: String, password: String): LiveData<Result<String>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val response = apiService.register(name, email, password)
            if (response.isSuccessful) {
                val message = response.body()?.message.orEmpty()
                emit(Result.Success(message))
            } else {
                val errorBody = response.errorBody()?.string()
                val error = Gson().fromJson(errorBody, RegisterResponse::class.java)
                emit(Result.Error("Register Failed: ${error.message}"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred"))
        }
    }

    fun login(email: String, password: String): LiveData<Result<String>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password)
            if (response.isSuccessful) {
                val loginResult = response.body()?.loginResult
                val user = UserModel(
                    name = loginResult?.name.orEmpty(),
                    userId = loginResult?.userId.orEmpty(),
                    token = loginResult?.token.orEmpty()
                )
                userPreference.saveSession(user)
                emit(Result.Success("Login Successful"))
            } else {
                val errorBody = response.errorBody()?.string()
                val error = Gson().fromJson(errorBody, RegisterResponse::class.java)
                emit(Result.Error("Login Failed: ${error.message}"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred"))
        }
    }

    fun uploadStories(description: RequestBody, file: MultipartBody.Part): LiveData<Result<String>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val response = apiService.uploadStories(description, file)
            if (response.isSuccessful) {
                val message = response.body()?.message.orEmpty()
                emit(Result.Success(message))
            } else {
                val errorBody = response.errorBody()?.string()
                val error = Gson().fromJson(errorBody, RegisterResponse::class.java)
                emit(Result.Error("Upload Failed: ${error.message}"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred"))
        }
    }

    fun getStories(): LiveData<Result<List<ListStoryItem>>> = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val token = userPreference.getSession().firstOrNull()?.token.orEmpty()
            if (token.isEmpty()) {
                emit(Result.Error("Login first"))
                return@liveData
            }

            val response = apiService.getStories()
            if (response.isSuccessful) {
                val listStory = response.body()?.listStory.orEmpty().filterNotNull()
                emit(Result.Success(listStory))
            } else {
                val errorBody = response.errorBody()?.string()
                val error = Gson().fromJson(errorBody, RegisterResponse::class.java)
                emit(Result.Error("Error: ${error.message}"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred"))
        }
    }


    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logOut() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            pref: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(pref, apiService)
            }.also { instance = it }
    }
}
