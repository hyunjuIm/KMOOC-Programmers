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

     val hasNextPage = MutableLiveData<LectureList>().apply { LectureList.EMPTY }

    fun list() = viewModelScope.launch {
        kmoocListLiveData.value = KmoocListState.Loading
        repository.list { lectureList ->
            kmoocListLiveData.postValue(KmoocListState.Success(lectureList))
            hasNextPage.postValue(lectureList)
        }
    }

    fun next() = viewModelScope.launch {
        kmoocListLiveData.value = KmoocListState.Loading
        repository.next(hasNextPage.value!!) { lectureList ->
            kmoocListLiveData.postValue(KmoocListState.Success(lectureList))
            hasNextPage.postValue(lectureList)
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