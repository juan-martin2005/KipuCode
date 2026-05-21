package com.kipucode.ui.screens.onboarding

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kipucode.R

data class OnboardingPageInfo(
    val description: String
)

val onboardingPages = listOf(
    OnboardingPageInfo(
        description = "Build real programming skills through guided lessons and hands on experience"
    ),
    OnboardingPageInfo(
        description = "Master fundamental logic through structured and easy to follow learning paths"
    ),
    OnboardingPageInfo(
        description = "Transition from theory to execution with interactive coding challenges"
    ),
    OnboardingPageInfo(
        description = "Transform basic knowledge into advanced & professional engineering skills"
    ),
)

@Composable
fun PagerIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        repeat(pageCount) { iteration ->
            val color = if (currentPage == iteration) Color(0xFF0293a8) else Color(0xFFD9DBE1)

            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(8.dp)
                    .background(color, shape = CircleShape)
            )
        }
    }
}