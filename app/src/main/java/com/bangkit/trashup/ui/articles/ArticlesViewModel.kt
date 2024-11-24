package com.bangkit.trashup.ui.articles

import UserRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.trashup.data.Result
import com.bangkit.trashup.data.remote.response.ListStoryItem
import kotlinx.coroutines.launch

class ArticlesViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _result = MediatorLiveData<Result<List<ListStoryItem>>>()
    val result: LiveData<Result<List<ListStoryItem>>> = _result

    private val _text = MutableLiveData<Result<String>>()
    val text: LiveData<Result<String>> = _text

    fun getStories() {
        _result.value = Result.Loading
        viewModelScope.launch {
            val results = userRepository.getStories()
            _result.addSource(results) { response ->
                _result.value = response
            }
        }
    }
}