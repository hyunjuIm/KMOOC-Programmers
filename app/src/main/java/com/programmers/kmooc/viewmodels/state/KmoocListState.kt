package com.programmers.kmooc.viewmodels.state

import com.programmers.kmooc.models.LectureList

sealed class KmoocListState {

    object Uninitialized : KmoocListState()

    object Loading : KmoocListState()

    data class Success(
        val result: LectureList
    ) : KmoocListState()

}