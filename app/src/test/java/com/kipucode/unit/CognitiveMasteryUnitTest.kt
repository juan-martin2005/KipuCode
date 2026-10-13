package com.kipucode.unit

import com.kipucode.domain.model.CognitiveMasteryDomain
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Data model for Cognitive Mastery data-driven test cases (PU-05).
 */
data class CognitiveMasteryTestCase(
    val inputPercentage: Int,
    val expectedPercentage: Int,
    val expectedTag: String,
    val scenarioDescription: String
) {
    override fun toString(): String = "[$inputPercentage%] -> exp: $expectedPercentage% ($expectedTag) - $scenarioDescription"
}

/**
 * Parameterized unit test suite for FSRS Cognitive Mastery Domain (PU-05).
 * Validates domain mastery thresholds, mnemonic tags, and boundaries clamping.
 */
@RunWith(Parameterized::class)
class CognitiveMasteryUnitTest(
    private val testCase: CognitiveMasteryTestCase
) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {0}")
        fun data(): Collection<Array<Any>> = listOf(
            // ====================================================================================
            // --- ZERO DOMAIN LEVEL ---
            // ====================================================================================
            arrayOf(CognitiveMasteryTestCase(0, 0, CognitiveMasteryDomain.TAG_ZERO, "Initial level without practice")),

            // ====================================================================================
            // --- INITIAL LEVEL ---
            // ====================================================================================
            arrayOf(CognitiveMasteryTestCase(1, 1, CognitiveMasteryDomain.TAG_INITIAL, "Lower boundary for starting topic")),
            arrayOf(CognitiveMasteryTestCase(15, 15, CognitiveMasteryDomain.TAG_INITIAL, "Early range for starting topic")),
            arrayOf(CognitiveMasteryTestCase(25, 25, CognitiveMasteryDomain.TAG_INITIAL, "Mid range for starting topic")),
            arrayOf(CognitiveMasteryTestCase(39, 39, CognitiveMasteryDomain.TAG_INITIAL, "Upper boundary for starting topic")),

            // ====================================================================================
            // --- CONSOLIDATING LEVEL ---
            // ====================================================================================
            arrayOf(CognitiveMasteryTestCase(40, 40, CognitiveMasteryDomain.TAG_CONSOLIDATING, "Lower boundary for consolidation")),
            arrayOf(CognitiveMasteryTestCase(50, 50, CognitiveMasteryDomain.TAG_CONSOLIDATING, "Mid range for consolidation")),
            arrayOf(CognitiveMasteryTestCase(65, 65, CognitiveMasteryDomain.TAG_CONSOLIDATING, "Advanced range for consolidation")),
            arrayOf(CognitiveMasteryTestCase(79, 79, CognitiveMasteryDomain.TAG_CONSOLIDATING, "Upper boundary for consolidation")),

            // ====================================================================================
            // --- MASTERED LEVEL ---
            // ====================================================================================
            arrayOf(CognitiveMasteryTestCase(80, 80, CognitiveMasteryDomain.TAG_MASTERED, "Lower boundary for consolidated mastery")),
            arrayOf(CognitiveMasteryTestCase(85, 85, CognitiveMasteryDomain.TAG_MASTERED, "Intermediate range for consolidated mastery")),
            arrayOf(CognitiveMasteryTestCase(95, 95, CognitiveMasteryDomain.TAG_MASTERED, "High mastery range")),
            arrayOf(CognitiveMasteryTestCase(100, 100, CognitiveMasteryDomain.TAG_MASTERED, "Maximum mastery reached")),

            // ====================================================================================
            // --- CLAMPING BOUNDARIES ---
            // ====================================================================================
            arrayOf(CognitiveMasteryTestCase(-25, 0, CognitiveMasteryDomain.TAG_ZERO, "Lower clamping (negative adjusted to 0)")),
            arrayOf(CognitiveMasteryTestCase(150, 100, CognitiveMasteryDomain.TAG_MASTERED, "Upper clamping (>100 adjusted to 100)"))
        )
    }

    /**
     * PU-05: Evaluates mastery percentage clamping and mnemonic tags across defined thresholds.
     */
    @Test
    fun evaluatePercentageAndStatusTagCorrectly() {
        // Arrange (Injected by JUnit Parameterized runner via testCase)

        // Act
        val mastery = CognitiveMasteryDomain.fromPercentage(percentage = testCase.inputPercentage)

        // Assert
        assertEquals(
            "Failure in percentage for: ${testCase.scenarioDescription}",
            testCase.expectedPercentage,
            mastery.percentage
        )
        assertEquals(
            "Failure in statusTag for: ${testCase.scenarioDescription}",
            testCase.expectedTag,
            mastery.statusTag
        )
    }
}
