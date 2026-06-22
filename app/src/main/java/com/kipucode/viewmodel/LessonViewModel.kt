package com.kipucode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kipucode.domain.model.LessonDomain
import com.kipucode.domain.usecase.GetLessonByCourseUseCase
import com.kipucode.domain.usecase.RefreshExercisesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LessonViewModel @Inject constructor (
    private val getLessonUseCase : GetLessonByCourseUseCase,
    private val refreshExercisesUseCase: RefreshExercisesUseCase
): ViewModel(){

    private val _lessonState = MutableStateFlow<LessonDomain?>(null)

    val lessonState : StateFlow<LessonDomain?> = _lessonState

    fun getLessonById(lessonId: String) {
        viewModelScope.launch {
            getLessonUseCase.invoke(lessonId).collect { lesson ->
                _lessonState.value = lesson
                if (lesson != null) {
                    viewModelScope.launch {
                        refreshExercisesUseCase(lesson.courseId, lesson.id)
                    }
                }
            }
        }
    }

}