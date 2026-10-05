package com.kipucode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kipucode.domain.model.LessonDomain
import com.kipucode.domain.usecase.GetLessonByCourseUseCase
import com.kipucode.domain.usecase.UpdateLastViewedLessonUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LessonViewModel @Inject constructor (
    private val getLessonUseCase: GetLessonByCourseUseCase,
    private val updateLastViewedLessonUseCase: UpdateLastViewedLessonUseCase
): ViewModel() {

    private val _lessonState = MutableStateFlow<LessonDomain?>(null)

    val lessonState: StateFlow<LessonDomain?> = _lessonState

    fun getLessonById(lessonId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            updateLastViewedLessonUseCase(lessonId)
            getLessonUseCase.invoke(lessonId).collect { lesson ->
                _lessonState.value = lesson
            }
        }
    }
}