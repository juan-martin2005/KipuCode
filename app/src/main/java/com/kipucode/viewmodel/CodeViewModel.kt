package com.kipucode.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kipucode.domain.model.CourseDomain
import com.kipucode.domain.model.CourseWithLessonsDomain
import com.kipucode.domain.model.DueExerciseDomain
import com.kipucode.domain.model.ExerciseDomain
import com.kipucode.domain.model.LessonDomain
import com.kipucode.domain.model.UserProgressDomain
import com.kipucode.domain.usecase.CognitiveMasteryOverview
import com.kipucode.domain.usecase.GetCognitiveMasteryOverviewUseCase
import com.kipucode.domain.usecase.GetCourseWithLessonsUseCase
import com.kipucode.domain.usecase.GetDueExercisesWithDetailsUseCase
import com.kipucode.domain.usecase.GetExercisesByLessonUseCase
import com.kipucode.domain.usecase.RefreshCoursesUseCase
import com.kipucode.domain.usecase.RefreshLearningProgressUseCase
import com.kipucode.domain.usecase.UpdateLastViewedLessonUseCase
import com.kipucode.domain.usecase.user.GetUserProgressUseCase
import com.kipucode.domain.usecase.user.RefreshUserProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ModuleItemStatus {
    COMPLETED,
    CURRENT,
    NEXT_LOCKED
}

data class ModuleItemUiModel(
    val course: CourseDomain,
    val lessons: List<LessonDomain>,
    val status: ModuleItemStatus,
    val masteryPercentage: Int = 0,
    val lockMessage: String? = null
)

data class CodeUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val modules: List<ModuleItemUiModel> = emptyList(),
    val currentLessonId: String = "",
    val dueExercises: List<DueExerciseDomain> = emptyList(),
    val masteryOverview: CognitiveMasteryOverview = CognitiveMasteryOverview()
)

private data class CodeDomainData(
    val dueExercises: List<DueExerciseDomain>,
    val coursesWithLessons: List<CourseWithLessonsDomain>,
    val userProgress: UserProgressDomain?,
    val masteryOverview: CognitiveMasteryOverview
)

@HiltViewModel
class CodeViewModel @Inject constructor(
    getDueExercisesWithDetailsUseCase: GetDueExercisesWithDetailsUseCase,
    private val refreshLearningProgressUseCase: RefreshLearningProgressUseCase,
    getCourseWithLessonsUseCase: GetCourseWithLessonsUseCase,
    private val refreshCoursesUseCase: RefreshCoursesUseCase,
    getUserProgressUseCase: GetUserProgressUseCase,
    private val refreshUserProgressUseCase: RefreshUserProgressUseCase,
    getCognitiveMasteryOverviewUseCase: GetCognitiveMasteryOverviewUseCase,
    private val getExercisesByLessonUseCase: GetExercisesByLessonUseCase,
    private val updateLastViewedLessonUseCase: UpdateLastViewedLessonUseCase
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)

    private val domainDataFlow: Flow<CodeDomainData> = combine(
        getDueExercisesWithDetailsUseCase(),
        getCourseWithLessonsUseCase(),
        getUserProgressUseCase(),
        getCognitiveMasteryOverviewUseCase()
    ) { dueList, coursesWithLessons, userProgress, masteryOverview ->
        CodeDomainData(
            dueExercises = dueList,
            coursesWithLessons = coursesWithLessons,
            userProgress = userProgress,
            masteryOverview = masteryOverview
        )
    }

    val uiState: StateFlow<CodeUiState> = combine(
        domainDataFlow,
        _isRefreshing
    ) { domainData, isRefreshing ->
        buildCodeUiState(domainData, isRefreshing)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CodeUiState(isLoading = true)
    )

    init {
        syncDataInBackground()
    }

    private fun syncDataInBackground() {
        viewModelScope.launch {
            refreshLearningProgressUseCase()
            refreshCoursesUseCase()
            refreshUserProgressUseCase()
        }
    }

    fun getExercisesForLesson(lessonId: String): Flow<List<ExerciseDomain>> {
        return getExercisesByLessonUseCase(lessonId)
    }

    fun onSelectLesson(lessonId: String) {
        viewModelScope.launch {
            updateLastViewedLessonUseCase(lessonId)
        }
    }

    fun swipeToRefresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                refreshLearningProgressUseCase()
                refreshCoursesUseCase()
                refreshUserProgressUseCase()
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private fun buildCodeUiState(
        domainData: CodeDomainData,
        isRefreshing: Boolean
    ): CodeUiState {
        val progress = domainData.userProgress
        val currentLessonId = progress?.currentLessonId.orEmpty()

        val activeCourse = domainData.coursesWithLessons.find { courseItem ->
            courseItem.lessons.any { it.id == currentLessonId }
        }
        val activeTrack = activeCourse?.course?.track

        val trackCourses = if (activeTrack != null) {
            domainData.coursesWithLessons.filter { it.course.track == activeTrack }
        } else {
            domainData.coursesWithLessons
        }.sortedBy { it.course.orderIndex }

        val currentCourseOrder = activeCourse?.course?.orderIndex ?: 1

        val visibleModules = trackCourses
            .filter { it.course.orderIndex <= currentCourseOrder + 1 }
            .map { item ->
                val courseOrder = item.course.orderIndex
                val status = when {
                    courseOrder < currentCourseOrder -> ModuleItemStatus.COMPLETED
                    courseOrder == currentCourseOrder -> ModuleItemStatus.CURRENT
                    else -> ModuleItemStatus.NEXT_LOCKED
                }
                val lockMsg = if (status == ModuleItemStatus.NEXT_LOCKED) {
                    "Módulo bloqueado · Completa el Módulo $currentCourseOrder para desbloquear el acceso a este contenido."
                } else null

                val masteryPct = domainData.masteryOverview.courseMastery[item.course.id]?.percentage ?: 0

                ModuleItemUiModel(
                    course = item.course,
                    lessons = item.lessons.sortedBy { it.orderIndex },
                    status = status,
                    masteryPercentage = masteryPct,
                    lockMessage = lockMsg
                )
            }

        return CodeUiState(
            isLoading = false,
            isRefreshing = isRefreshing,
            modules = visibleModules,
            currentLessonId = currentLessonId,
            dueExercises = domainData.dueExercises,
            masteryOverview = domainData.masteryOverview
        )
    }
}
