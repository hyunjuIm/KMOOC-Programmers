package com.programmers.kmooc.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.programmers.kmooc.models.LectureList
import com.programmers.kmooc.repositories.KmoocRepository
import com.programmers.kmooc.viewmodels.state.KmoocListState
import kotlinx.coroutines.launch

class KmoocListViewModel(private val repository: KmoocRepository) : ViewModel() {

    val kmoocListLiveData = MutableLiveData<KmoocListState>().apply {
        value = KmoocListState.Uninitialized
    }

    fun list() = viewModelScope.launch {
        kmoocListLiveData.value = KmoocListState.Loading
        repository.list { lectureList ->
            kmoocListLiveData.postValue(KmoocListState.Success(lectureList))
        }
    }

    fun next(hasNextPage: LectureList) = viewModelScope.launch {
        kmoocListLiveData.value = KmoocListState.Loading
        repository.next(hasNextPage) { lectureList ->
            kmoocListLiveData.postValue(KmoocListState.Success(lectureList))
        }
    }
}

class KmoocListViewModelFactory(private val repository: KmoocRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(KmoocListViewModel::class.java)) {
            return KmoocListViewModel(repository) as T
        }
        throw IllegalAccessException("Unkown Viewmodel Class")
    }
}