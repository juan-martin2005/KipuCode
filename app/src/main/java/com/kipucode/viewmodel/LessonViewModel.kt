package com.kipucode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kipucode.domain.model.LessonDomain
import com.kipucode.domain.usecase.lesson.GetLessonByCourseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LessonViewModel @Inject constructor (
    private val getLessonUseCase : GetLessonByCourseUseCase
): ViewModel(){

    private val _lessonState = MutableStateFlow<LessonDomain?>(null)

    val lessonState : StateFlow<LessonDomain?> = _lessonState

    fun getLessonById(lessonId : String){

        viewModelScope.launch {
            getLessonUseCase.invoke(lessonId).collect { _lessonState.value = it}
        }
    }

}