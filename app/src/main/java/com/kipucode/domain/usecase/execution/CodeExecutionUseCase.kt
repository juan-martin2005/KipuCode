package com.kipucode.domain.usecase.execution

import com.kipucode.domain.model.excecution.CodeExecutionEvent
import com.kipucode.domain.model.excecution.CodeExecutionRequestDomain
import com.kipucode.domain.repository.CodeExecutionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CodeExecutionUseCase @Inject constructor(
    private val codeExecutionRepository: CodeExecutionRepository
) {
    operator fun invoke(request: CodeExecutionRequestDomain): Flow<CodeExecutionEvent> {
        return codeExecutionRepository.execute(request)
    }
}
