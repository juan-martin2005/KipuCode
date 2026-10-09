package com.kipucode.data.repository

import com.kipucode.data.mapper.toDomain
import com.kipucode.data.mapper.toDto
import com.kipucode.data.remote.judge0.service.Judge0Api
import com.kipucode.domain.model.Response
import com.kipucode.domain.model.ServerErrorType
import com.kipucode.domain.model.excecution.CodeExecutionEvent
import com.kipucode.domain.model.excecution.CodeExecutionRequestDomain
import com.kipucode.domain.model.excecution.ProgrammingLanguageDomain
import com.kipucode.domain.repository.CodeExecutionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class CodeExecutionRepositoryImpl @Inject constructor(
    private val judge0Api: Judge0Api
) : CodeExecutionRepository {

    override fun execute(request: CodeExecutionRequestDomain): Flow<CodeExecutionEvent> = flow {
        emit(CodeExecutionEvent.Running)

        try {
            val response = judge0Api.executeCode(request.toDto())

            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data
                if (data != null) {
                    val domainResult = data.toDomain()
                    when (domainResult.status) {
                        "SUCCESS" -> {
                            emit(CodeExecutionEvent.Success(domainResult))
                        }
                        "COMPILATION_ERROR" -> {
                            emit(
                                CodeExecutionEvent.Error(
                                    message = "Error de compilación",
                                    diagnostics = domainResult.diagnostics,
                                    rawOutput = domainResult.compileOutput ?: domainResult.stderr
                                )
                            )
                        }
                        "TIME_LIMIT_EXCEEDED" -> {
                            emit(
                                CodeExecutionEvent.Error(
                                    message = "Tiempo límite de ejecución superado",
                                    rawOutput = domainResult.stderr
                                )
                            )
                        }
                        else -> {
                            emit(
                                CodeExecutionEvent.Error(
                                    message = domainResult.stderr ?: domainResult.statusDescription ?: "Error de ejecución",
                                    diagnostics = domainResult.diagnostics,
                                    rawOutput = domainResult.stderr ?: domainResult.compileOutput
                                )
                            )
                        }
                    }
                } else {
                    emit(CodeExecutionEvent.Error(message = "Respuesta vacía del servidor de compilación"))
                }
            } else {
                val errorMessage = response.body()?.error?.message
                    ?: "Error del servidor (${response.code()})"
                emit(CodeExecutionEvent.Error(message = errorMessage))
            }
        } catch (e: Exception) {
            emit(
                CodeExecutionEvent.Error(
                    message = "No se pudo conectar con el servidor de compilación: ${e.message ?: "Error desconocido"}"
                )
            )
        }
    }

    override suspend fun getLanguages(): Response<List<ProgrammingLanguageDomain>> {
        return try {
            val response = judge0Api.getLanguages()
            if (response.isSuccessful && response.body()?.success == true) {
                val list = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                Response.Success(list)
            } else {
                Response.Error(response.body()?.error?.message ?: "Error al obtener lenguajes", ServerErrorType.FIRESTORE_ERROR)
            }
        } catch (e: Exception) {
            Response.Error(e.message ?: "Error de red", ServerErrorType.FIRESTORE_ERROR)
        }
    }
}