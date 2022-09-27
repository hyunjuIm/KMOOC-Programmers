package com.programmers.kmooc.viewmodels.state

import com.programmers.kmooc.models.Lecture

sealed class KmoocDetailState {

    object Uninitialized : KmoocDetailState()

    object Loading : KmoocDetailState()

    data class Success(
        val lecture: Lecture
    ) : KmoocDetailState()

}