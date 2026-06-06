package com.kipucode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kipucode.domain.model.CourseWithLessonsDomain
import com.kipucode.domain.model.Response
import com.kipucode.domain.usecase.course.GetCourseWithLessonsUseCase
import com.kipucode.domain.usecase.course.RefreshCoursesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoursesViewModel @Inject constructor(
    private val getCourseWithLessonsUseCase: GetCourseWithLessonsUseCase,
    private val refreshCoursesUseCase: RefreshCoursesUseCase
) : ViewModel() {
    private val _coursesWithLessonsState = MutableStateFlow<List<CourseWithLessonsDomain>>(emptyList())
    val coursesWithLessonsState: StateFlow<List<CourseWithLessonsDomain>> = _coursesWithLessonsState

    private val _refreshState = MutableStateFlow<Response<Unit>?>(null)
    val refreshState: StateFlow<Response<Unit>?> = _refreshState

    init {
        startObservingCourses()
    }

    private fun startObservingCourses() {
        viewModelScope.launch {
            getCourseWithLessonsUseCase.invoke().collect { data ->
                _coursesWithLessonsState.value = data
            }
        }
    }

    fun swipeToRefresh() {
        viewModelScope.launch {
            _refreshState.value = Response.Loading
            val result = refreshCoursesUseCase.invoke()

            _refreshState.value = result
        }
    }

    fun resetRefreshState() {
        _refreshState.value = null
    }
}