package com.kipucode.unit

import com.kipucode.data.local.model.LearningProgressEntity
import com.kipucode.data.mapper.toDomain
import com.kipucode.data.mapper.toEntity
import com.kipucode.domain.model.LearningProgressDomain
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Prueba unitaria de Mappers y Transformación de Modelos (PU-10).
 * Verifica que las conversiones bidireccionales entre Room Entity y Domain conserven todos los atributos del motor FSRS bajo el patrón AAA.
 */
class ModelMappingUnitTest {

    /**
     * PU-10: Verifies that converting LearningProgressEntity to domain preserves all FSRS fields.
     */
    @Test
    fun learningProgressEntityToDomainPreservesAllFsrsAttributes() {
        // Arrange
        val entity = LearningProgressEntity(
            exerciseId = "ex_csharp_01",
            userId = "user_test_999",
            difficulty = 4.85,
            stability = 12.35,
            reps = 3,
            lapses = 1,
            state = 2, // State.REVIEW
            dueDate = 1791823200000L,
            lastReviewed = 1791736800000L
        )

        // Act
        val domain: LearningProgressDomain = entity.toDomain()

        // Assert
        assertEquals(entity.exerciseId, domain.exerciseId)
        assertEquals(entity.userId, domain.userId)
        assertEquals(entity.difficulty, domain.difficulty, 0.0001)
        assertEquals(entity.stability, domain.stability, 0.0001)
        assertEquals(entity.reps, domain.reps)
        assertEquals(entity.lapses, domain.lapses)
        assertEquals(entity.state, domain.state)
        assertEquals(entity.dueDate, domain.dueDate)
        assertEquals(entity.lastReviewed, domain.lastReviewed)
    }

    /**
     * PU-10: Verifies that domain to entity roundtrip conversion preserves precision without data loss.
     */
    @Test
    fun learningProgressDomainToEntityPerformsLosslessRoundtrip() {
        // Arrange
        val originalDomain = LearningProgressDomain(
            exerciseId = "ex_csharp_02",
            userId = "user_test_888",
            difficulty = 6.12345,
            stability = 24.98765,
            reps = 7,
            lapses = 0,
            state = 1, // State.LEARNING
            dueDate = 1800000000000L,
            lastReviewed = 1799000000000L
        )

        // Act
        val entity = originalDomain.toEntity()
        val roundtripDomain = entity.toDomain()

        // Assert
        assertEquals(originalDomain, roundtripDomain)
    }
}
