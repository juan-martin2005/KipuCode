package com.kipucode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kipucode.domain.model.Course
import com.kipucode.domain.model.Response
import com.kipucode.domain.usecase.course.GetCourseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoursesViewModel @Inject constructor(
    private val getAllCourseUseCase: GetCourseUseCase
) : ViewModel(){

    private val _courseState = MutableStateFlow<Response<List<Course>>?>(null)
    val courseState : StateFlow<Response<List<Course>>?> = _courseState

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
    }
}