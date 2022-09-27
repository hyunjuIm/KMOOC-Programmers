package com.programmers.kmooc.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.programmers.kmooc.repositories.KmoocRepository
import com.programmers.kmooc.viewmodels.state.KmoocDetailState
import kotlinx.coroutines.launch


class KmoocDetailViewModel(private val repository: KmoocRepository) : ViewModel() {

    val kmoocDetailLiveData = MutableLiveData<KmoocDetailState>().apply {
        value = KmoocDetailState.Uninitialized
    }

    fun detail(courseId: String) = viewModelScope.launch {
        kmoocDetailLiveData.value = KmoocDetailState.Loading
        repository.detail(courseId) { lecture ->
            kmoocDetailLiveData.postValue(
                KmoocDetailState.Success(lecture)
            )
        }
    }
}

class KmoocDetailViewModelFactory(private val repository: KmoocRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KmoocDetailViewModel::class.java)) {
            return KmoocDetailViewModel(repository) as T
        }
        throw IllegalAccessException("Unkown Viewmodel Class")
    }
}