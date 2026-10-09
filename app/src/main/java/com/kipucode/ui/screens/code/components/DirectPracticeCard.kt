package com.kipucode.ui.screens.code.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.R
import com.kipucode.ui.theme.Gray
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.Nunito

enum class DirectPracticeType {
    FLASHCARD,
    UNIQUE_CHOICE,
    COMPLETE_CODE
}

@Composable
fun DirectPracticeCard(
    type: DirectPracticeType,
    totalCount: Int,
    dueCount: Int,
    isPracticed: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val style = when (type) {
        DirectPracticeType.FLASHCARD -> PracticeCardStyle(
            iconRes = R.drawable.ic_flashcards,
            iconBgColor = Color(0xFFE0F7FA),
            iconBorderColor = Color(0xFFB2EBF2),
            iconTint = KipuTeal,
            title = "Tarjetas de memoria",
            countLabel = "$totalCount tarjetas"
        )
        DirectPracticeType.UNIQUE_CHOICE -> PracticeCardStyle(
            iconRes = R.drawable.ic_multiple_choice,
            iconBgColor = Color(0xFFEDE7F6),
            iconBorderColor = Color(0xFFD1C4E9),
            iconTint = Color(0xFF5E35B1),
            title = "Opción múltiple",
            countLabel = "$totalCount preguntas"
        )
        DirectPracticeType.COMPLETE_CODE -> PracticeCardStyle(
            iconRes = R.drawable.ic_code,
            iconBgColor = Color(0xFFFFF3E0),
            iconBorderColor = Color(0xFFFFE0B2),
            iconTint = Color(0xFFE65100),
            title = "Completar bloques",
            countLabel = "$totalCount ejercicios"
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Contenedor redondeado del ícono
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(style.iconBgColor, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = style.iconRes),
                contentDescription = null,
                tint = style.iconTint,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        // Título y Conteo
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = style.title,
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = KipuDarkBlue
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = style.countLabel,
                fontFamily = Nunito,
                fontWeight = FontWeight.Medium,
                fontSize = 12.5.sp,
                color = Gray
            )
        }

        // Insignia de estado (Badge FSRS)
        when {
            dueCount > 0 -> {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFFF3E0)
                ) {
                    Text(
                        text = "$dueCount para repasar",
                        fontSize = 11.sp,
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            isPracticed -> {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFE8F5E9)
                ) {
                    Text(
                        text = "Al día",
                        fontSize = 11.sp,
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        // Flecha de navegación a la derecha
        Icon(
            painter = painterResource(id = R.drawable.ic_arrow_filled),
            contentDescription = null,
            tint = Gray.copy(alpha = 0.5f),
            modifier = Modifier.size(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Direct Practice Card - Para Repasar")
@Composable
private fun DirectPracticeCardDuePreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        DirectPracticeCard(
            type = DirectPracticeType.FLASHCARD,
            totalCount = 12,
            dueCount = 4,
            isPracticed = true,
            onClick = {}
        )
        DirectPracticeCard(
            type = DirectPracticeType.UNIQUE_CHOICE,
            totalCount = 8,
            dueCount = 2,
            isPracticed = true,
            onClick = {}
        )
        DirectPracticeCard(
            type = DirectPracticeType.COMPLETE_CODE,
            totalCount = 8,
            dueCount = 2,
            isPracticed = true,
            onClick = {}
        )

    }
}

@Preview(showBackground = true, name = "Direct Practice Card - Al Día")
@Composable
private fun DirectPracticeCardUpToDatePreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        DirectPracticeCard(
            type = DirectPracticeType.FLASHCARD,
            totalCount = 10,
            dueCount = 0,
            isPracticed = true,
            onClick = {}
        )
        DirectPracticeCard(
            type = DirectPracticeType.UNIQUE_CHOICE,
            totalCount = 6,
            dueCount = 0,
            isPracticed = false,
            onClick = {}
        )
        DirectPracticeCard(
            type = DirectPracticeType.COMPLETE_CODE,
            totalCount = 6,
            dueCount = 0,
            isPracticed = false,
            onClick = {}
        )
    }
}

private data class PracticeCardStyle(
    val iconRes: Int,
    val iconBgColor: Color,
    val iconBorderColor: Color,
    val iconTint: Color,
    val title: String,
    val countLabel: String
)
