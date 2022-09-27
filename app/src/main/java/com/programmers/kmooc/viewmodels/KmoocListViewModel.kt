package com.programmers.kmooc.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.programmers.kmooc.models.LectureList
import com.programmers.kmooc.repositories.KmoocRepository

class KmoocListViewModel(private val repository: KmoocRepository) : ViewModel() {

    fun list() {
        repository.list { lectureList ->
        }
    }

    fun next() {
        val currentLectureList = LectureList.EMPTY
        repository.next(currentLectureList) { lectureList ->
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