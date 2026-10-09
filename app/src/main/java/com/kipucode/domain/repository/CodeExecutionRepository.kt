package com.kipucode.domain.repository

import com.kipucode.domain.model.Response
import com.kipucode.domain.model.excecution.CodeExecutionEvent
import com.kipucode.domain.model.excecution.CodeExecutionRequestDomain
import com.kipucode.domain.model.excecution.ProgrammingLanguageDomain
import kotlinx.coroutines.flow.Flow

interface CodeExecutionRepository {
    fun execute(request: CodeExecutionRequestDomain): Flow<CodeExecutionEvent>
    suspend fun getLanguages(): Response<List<ProgrammingLanguageDomain>>
}