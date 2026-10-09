package com.kipucode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kipucode.domain.model.LessonDomain
import com.kipucode.domain.model.PracticeMethod
import com.kipucode.domain.usecase.GetExercisesByLessonUseCase
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
    private val updateLastViewedLessonUseCase: UpdateLastViewedLessonUseCase,
    private val getExercisesByLessonUseCase: GetExercisesByLessonUseCase
): ViewModel() {

    private val _lessonState = MutableStateFlow<LessonDomain?>(null)
    val lessonState: StateFlow<LessonDomain?> = _lessonState

    private val _availableMethods = MutableStateFlow<List<PracticeMethod>>(emptyList())
    val availableMethods: StateFlow<List<PracticeMethod>> = _availableMethods

    fun getLessonById(lessonId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            updateLastViewedLessonUseCase(lessonId)
            getLessonUseCase.invoke(lessonId).collect { lesson ->
                _lessonState.value = lesson
            }
        }
        viewModelScope.launch(Dispatchers.IO) {
            getExercisesByLessonUseCase(lessonId).collect { exercises ->
                val existingTypes = exercises.map { it.type }.toSet()

                // Mapeo estricto 1 a 1 de los tipos existentes
                val methods = existingTypes.mapNotNull { type ->
                    when (type) {
                        "UNIQUE_CHOICE" -> PracticeMethod.UNIQUE_CHOICE
                        "DEFAULT_FLASHCARDS", "FLASHCARD" -> PracticeMethod.DEFAULT_FLASHCARDS
                        "FEYNMAN_FLASHCARDS" -> PracticeMethod.FEYNMAN_FLASHCARDS
                        else -> null
                    }
                }

                _availableMethods.value = methods
            }
        }
    }
}