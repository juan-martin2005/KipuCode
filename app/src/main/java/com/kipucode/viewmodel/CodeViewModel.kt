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
    AVAILABLE
}

data class ModuleItemUiModel(
    val course: CourseDomain,
    val lessons: List<LessonDomain>,
    val status: ModuleItemStatus,
    val masteryPercentage: Int = 0
)

enum class PracticeFilter {
    ALL,
    FOR_REVIEW,
    UP_TO_DATE
}

data class CodeUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val modules: List<ModuleItemUiModel> = emptyList(),
    val currentLessonId: String = "",
    val dueExercises: List<DueExerciseDomain> = emptyList(),
    val masteryOverview: CognitiveMasteryOverview = CognitiveMasteryOverview(),
    val selectedModuleId: String? = null,
    val selectedFilter: PracticeFilter = PracticeFilter.ALL
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
    private val _selectedModuleId = MutableStateFlow<String?>(null)
    private val _selectedFilter = MutableStateFlow(PracticeFilter.ALL)

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
        _isRefreshing,
        _selectedModuleId,
        _selectedFilter
    ) { domainData, isRefreshing, selectedModuleId, selectedFilter ->
        buildCodeUiState(domainData, isRefreshing, selectedModuleId, selectedFilter)
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

    fun selectModule(moduleId: String) {
        _selectedModuleId.value = moduleId
    }

    fun selectFilter(filter: PracticeFilter) {
        _selectedFilter.value = filter
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
        isRefreshing: Boolean,
        selectedModuleId: String?,
        selectedFilter: PracticeFilter
    ): CodeUiState {
        val progress = domainData.userProgress
        val currentLessonId = progress?.lastVisitedLessonId.orEmpty()

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

        val visibleModules = trackCourses.map { item ->
            val courseOrder = item.course.orderIndex
            val status = when {
                courseOrder < currentCourseOrder -> ModuleItemStatus.COMPLETED
                courseOrder == currentCourseOrder -> ModuleItemStatus.CURRENT
                else -> ModuleItemStatus.AVAILABLE
            }

            val masteryPct = domainData.masteryOverview.courseMastery[item.course.id]?.percentage ?: 0

            ModuleItemUiModel(
                course = item.course,
                lessons = item.lessons.sortedBy { it.orderIndex },
                status = status,
                masteryPercentage = masteryPct
            )
        }

        val resolvedSelectedModuleId = selectedModuleId
            ?: activeCourse?.course?.id
            ?: visibleModules.firstOrNull()?.course?.id

        return CodeUiState(
            isLoading = false,
            isRefreshing = isRefreshing,
            modules = visibleModules,
            currentLessonId = currentLessonId,
            dueExercises = domainData.dueExercises,
            masteryOverview = domainData.masteryOverview,
            selectedModuleId = resolvedSelectedModuleId,
            selectedFilter = selectedFilter
        )
    }
}
