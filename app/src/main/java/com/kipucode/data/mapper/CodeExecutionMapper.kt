package com.kipucode.data.mapper

import com.kipucode.data.remote.judge0.dto.CodeDiagnosticDto
import com.kipucode.data.remote.judge0.dto.LanguageDto
import com.kipucode.data.remote.judge0.dto.SubmissionRequestDto
import com.kipucode.data.remote.judge0.dto.SubmissionResultDto
import com.kipucode.domain.model.excecution.CodeDiagnosticDomain
import com.kipucode.domain.model.excecution.CodeExecutionRequestDomain
import com.kipucode.domain.model.excecution.CodeExecutionResultDomain
import com.kipucode.domain.model.excecution.ProgrammingLanguageDomain

fun CodeExecutionRequestDomain.toDto(): SubmissionRequestDto =
    SubmissionRequestDto(
        languageKey = languageKey,
        sourceCode = sourceCode,
        stdin = stdin
    )

fun CodeDiagnosticDto.toDomain(): CodeDiagnosticDomain =
    CodeDiagnosticDomain(
        line = line,
        column = column,
        severity = severity,
        code = code,
        message = message
    )

fun SubmissionResultDto.toDomain(): CodeExecutionResultDomain =
    CodeExecutionResultDomain(
        status = status,
        stdout = stdout,
        stderr = stderr,
        compileOutput = compileOutput,
        executionTimeSeconds = executionTimeSeconds,
        memoryKb = memoryKb,
        diagnostics = diagnostics.map { it.toDomain() },
        statusDescription = judge0StatusDescription
    )

fun LanguageDto.toDomain(): ProgrammingLanguageDomain =
    ProgrammingLanguageDomain(
        key = key,
        name = name,
        extension = extension,
        template = template
    )
