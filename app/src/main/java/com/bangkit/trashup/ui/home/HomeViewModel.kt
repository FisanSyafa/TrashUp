package com.bangkit.trashup.ui.home

import UserRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.bangkit.trashup.data.Result
import androidx.lifecycle.ViewModel
import com.bangkit.trashup.data.remote.response.ListStoryItem
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HomeViewModel(private val userRepository: UserRepository) : ViewModel() {

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
