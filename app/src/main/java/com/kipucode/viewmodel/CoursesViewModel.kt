package com.kipucode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kipucode.domain.model.Course
import com.kipucode.domain.model.Response
import com.kipucode.domain.model.UserProgress
import com.kipucode.domain.usecase.course.GetCourseUseCase
import com.kipucode.domain.usecase.user.GetUserProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoursesViewModel @Inject constructor(
    private val getAllCourseUseCase: GetCourseUseCase,
    private val getUseProgressUseCase: GetUserProgressUseCase
) : ViewModel(){

    private val _courseState = MutableStateFlow<Response<List<Course>>?>(null)
    private val _userProgressState = MutableStateFlow<Response<UserProgress>?>(null)
    val courseState : StateFlow<Response<List<Course>>?> = _courseState
    val userProgressState : StateFlow<Response<UserProgress>?> = _userProgressState

    init {
        getAllCourses()
    }

    fun getAllCourses(){
        viewModelScope.launch {
            _courseState.value = Response.Loading
            val result = getAllCourseUseCase.invoke()
            _courseState.value = result
        }
    }

    fun resetState(){
        _courseState.value = null
        _userProgressState.value = null
    }
}