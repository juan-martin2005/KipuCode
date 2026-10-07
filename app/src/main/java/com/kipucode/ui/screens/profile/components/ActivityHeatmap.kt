package com.kipucode.ui.screens.profile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.R
import com.kipucode.domain.model.DailyActivityDomain
import com.kipucode.ui.theme.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

private data class HeatmapDay(
    val date: LocalDate,
    val isCurrentYear: Boolean
)

private data class HeatmapWeek(
    val monthLabel: String?,
    val days: List<HeatmapDay?> // Tamaño 7 (Lunes = 0 a Domingo = 6)
)

@Composable
fun ActivityHeatmapCard(
    activities: List<DailyActivityDomain>,
    modifier: Modifier = Modifier,
    year: Int = LocalDate.now().year
) {
    // Mapa rápido por fecha "YYYY-MM-DD" -> O(1) acceso
    val activityMap = remember(activities) {
        activities.associateBy { it.date }
    }

    val totalActiveDays = remember(activities) {
        activities.count { it.exercisesCount > 0 }
    }
    val totalExercisesYear = remember(activities) {
        activities.sumOf { it.exercisesCount }
    }
    val totalXpYear = remember(activities) {
        activities.sumOf { it.xpEarned }
    }

    val today = remember { LocalDate.now() }
    var selectedDate by remember { mutableStateOf<LocalDate?>(today) }

    // Generar semanas del año
    val weeks = remember(year) {
        generateWeeksForYear(year)
    }

    // Auto-scroll a la semana actual
    val currentWeekIndex = remember(weeks) {
        val idx = weeks.indexOfFirst { week ->
            week.days.any { it?.date == today }
        }
        if (idx >= 0) (idx - 6).coerceAtLeast(0) else 0
    }
    val lazyListState = rememberLazyListState()
    LaunchedEffect(currentWeekIndex) {
        lazyListState.scrollToItem(currentWeekIndex)
    }

    // Densidad con fontScale fijo: solo se aplica a la cuadrícula (dp fijos),
    // el resto de la tarjeta sigue respetando el tamaño de fuente del sistema.
    val systemDensity = LocalDensity.current
    val gridDensity = remember(systemDensity.density) {
        Density(density = systemDensity.density, fontScale = 1f)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, BorderLightGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // --- ENCABEZADO ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(KipuCyan.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_calendar),
                            contentDescription = null,
                            tint = KipuTeal,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Mi Actividad en $year",
                            fontFamily = Nunito,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = KipuDarkBlue
                        )
                        Text(
                            text = "$totalActiveDays días activos • $totalExercisesYear ejercicios • $totalXpYear XP",
                            fontFamily = Nunito,
                            fontSize = 12.sp,
                            color = Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // --- CUADRÍCULA DE MAPA DE CALOR (fontScale bloqueado) ---
            CompositionLocalProvider(LocalDensity provides gridDensity) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    // Columna izquierda: Etiquetas de días (Lun, Mié, Vie)
                    // Alineada 1:1 con las casillas de la semana
                    Column(
                        modifier = Modifier.padding(end = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        // Espacio equivalente a la fila de los meses de arriba
                        Spacer(modifier = Modifier.height(16.dp))

                        listOf("Lun", "", "Mié", "", "Vie", "", "").forEach { dayLabel ->
                            Box(
                                modifier = Modifier
                                    .width(24.dp)
                                    .height(12.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                if (dayLabel.isNotEmpty()) {
                                    Text(
                                        text = dayLabel,
                                        fontFamily = Nunito,
                                        fontSize = 9.sp,
                                        lineHeight = 9.sp,
                                        color = Gray,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        softWrap = false,
                                        overflow = TextOverflow.Visible,
                                        modifier = Modifier.wrapContentSize(
                                            align = Alignment.CenterEnd,
                                            unbounded = true
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Cuadrícula deslizable horizontalmente
                    LazyRow(
                        state = lazyListState,
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        items(weeks.size) { weekIdx ->
                            val week = weeks[weekIdx]
                            Column(
                                verticalArrangement = Arrangement.spacedBy(3.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Etiqueta del mes sobre la semana (16.dp)
                                Box(
                                    modifier = Modifier
                                        .height(16.dp)
                                        .width(14.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    if (week.monthLabel != null) {
                                        Text(
                                            text = week.monthLabel,
                                            fontFamily = Nunito,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = KipuTealDark,
                                            maxLines = 1,
                                            softWrap = false,
                                            overflow = TextOverflow.Visible,
                                            modifier = Modifier.wrapContentWidth(
                                                align = Alignment.Start,
                                                unbounded = true
                                            )
                                        )
                                    }
                                }

                                // 7 celdas de la semana (Lunes a Domingo)
                                week.days.forEach { day ->
                                    if (day != null && day.isCurrentYear) {
                                        val dateStr = day.date.toString()
                                        val activity = activityMap[dateStr]
                                        val exercisesCount = activity?.exercisesCount ?: 0
                                        val isSelected = selectedDate == day.date
                                        val cellColor = getIntensityColor(exercisesCount)

                                        Box(
                                            modifier = Modifier
                                                .size(12.dp)
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(cellColor)
                                                .then(
                                                    if (isSelected) {
                                                        Modifier.border(
                                                            width = 1.dp,
                                                            color = KipuDarkBlue.copy(alpha = 0.4f),
                                                            shape = RoundedCornerShape(3.dp)
                                                        )
                                                    } else Modifier
                                                )
                                                .clickable {
                                                    selectedDate = day.date
                                                }
                                        )
                                    } else {
                                        // Espacio vacío fuera del año
                                        Spacer(modifier = Modifier.size(12.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // --- LEYENDA (Menos -> Más) con KipuTeal y escala de Alpha ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Menos", fontFamily = Nunito, fontSize = 11.sp, color = Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    listOf(
                        Color(0xFFE5E7EB),
                        KipuTeal.copy(alpha = 0.25f),
                        KipuTeal.copy(alpha = 0.50f),
                        KipuTeal.copy(alpha = 0.75f),
                        KipuTeal
                    ).forEach { color ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 2.dp)
                                .size(10.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(color)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Más", fontFamily = Nunito, fontSize = 11.sp, color = Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- TARJETA DE DETALLE AL HACER TAP ---
            selectedDate?.let { date ->
                val dateStr = date.toString()
                val selectedActivity = activityMap[dateStr]
                DayDetailCard(date = date, activity = selectedActivity)
            }
        }
    }
}

@Composable
private fun DayDetailCard(
    date: LocalDate,
    activity: DailyActivityDomain?
) {
    val formatter = remember {
        DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM, yyyy", Locale.forLanguageTag("es-ES"))
    }
    val formattedDate = remember(date) {
        date.format(formatter).replaceFirstChar { it.uppercase() }
    }

    val exercises = activity?.exercisesCount ?: 0
    val correct = activity?.correctCount ?: 0
    val incorrect = activity?.incorrectCount ?: 0
    val xp = activity?.xpEarned ?: 0
    val lessons = activity?.lessonsCount ?: 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BackgroundGray)
            .border(1.dp, BorderLightGray, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Text(
            text = formattedDate,
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = KipuDarkBlue
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (exercises > 0 || lessons > 0 || xp > 0) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                // Fila 1: Aciertos (Buenas) y Fallos (Malas)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricChip(
                        iconRes = R.drawable.ic_check,
                        label = if (correct == 1) "$correct correcta" else "$correct correctas",
                        iconTint = Green
                    )
                    MetricChip(
                        iconRes = R.drawable.ic_incorrect,
                        label = if (incorrect == 1) "$incorrect error" else "$incorrect errores",
                        iconTint = Red
                    )
                }

                // Fila 2: Lecciones y XP
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricChip(
                        iconRes = R.drawable.ic_code,
                        label = if (lessons == 1) "$lessons lección" else "$lessons lecciones",
                        iconTint = KipuTeal
                    )
                    MetricChip(
                        iconRes = R.drawable.ic_star,
                        label = "+$xp XP",
                        iconTint = Orange
                    )
                }
            }
        } else {
            Text(
                text = "Sin actividad registrada este día. ¡Completa ejercicios para encenderlo!",
                fontFamily = Nunito,
                fontSize = 12.sp,
                color = Gray
            )
        }
    }
}

@Composable
private fun RowScope.MetricChip(
    iconRes: Int,
    label: String,
    iconTint: Color
) {
    Row(
        modifier = Modifier
            .weight(1f)
            .clip(RoundedCornerShape(8.dp))
            .background(White)
            .border(1.dp, BorderLightGray, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontFamily = Nunito,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = KipuDarkBlue,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Escala de color con base KipuTeal y opacidad gradual (Alpha).
 */
private fun getIntensityColor(exercisesCount: Int): Color {
    return when (exercisesCount) {
        0 -> BorderLightGray.copy(alpha = 0.6f)
        in 1..4 -> KipuTeal.copy(alpha = 0.25f)
        in 5..9 -> KipuTeal.copy(alpha = 0.50f)
        in 10..14 -> KipuTeal.copy(alpha = 0.75f)
        else -> KipuTeal
    }
}

private fun generateWeeksForYear(year: Int): List<HeatmapWeek> {
    val startDate = LocalDate.of(year, 1, 1)
    val endDate = LocalDate.of(year, 12, 31)

    // Ajustar al lunes de la primera semana
    val startMonday = startDate.minusDays((startDate.dayOfWeek.value - 1).toLong())
    val endSunday = endDate.plusDays((7 - endDate.dayOfWeek.value).toLong())

    val totalDays = ChronoUnit.DAYS.between(startMonday, endSunday) + 1
    val totalWeeks = (totalDays / 7).toInt()

    val monthNames = arrayOf("Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic")
    val weeks = mutableListOf<HeatmapWeek>()
    var currentDate = startMonday
    var lastLabeledMonth = -1

    for (w in 0 until totalWeeks) {
        val daysInWeek = mutableListOf<HeatmapDay?>()
        var weekMonthLabel: String? = null

        repeat(7) {
            val isCurrentYear = currentDate.year == year
            daysInWeek.add(HeatmapDay(date = currentDate, isCurrentYear = isCurrentYear))

            // Si es el primer día de un mes (o el inicio de año) y aún no lo rotulamos
            if (isCurrentYear && (currentDate.dayOfMonth == 1 || (w == 0 && currentDate.monthValue != lastLabeledMonth))) {
                if (currentDate.monthValue != lastLabeledMonth) {
                    weekMonthLabel = monthNames[currentDate.monthValue - 1]
                    lastLabeledMonth = currentDate.monthValue
                }
            }

            currentDate = currentDate.plusDays(1)
        }
        weeks.add(HeatmapWeek(monthLabel = weekMonthLabel, days = daysInWeek))
    }

    return weeks
}

// ============================================================================================
//  PREVIEWS
// ============================================================================================
@Preview(showBackground = true, backgroundColor = 0xFFF5F7FA, name = "Heatmap")
@Composable
private fun ActivityHeatmapCardEmptyPreview() {
    Box(modifier = Modifier.padding(16.dp)) {
        ActivityHeatmapCard(
            activities = emptyList(),
            year = LocalDate.now().year
        )
    }
}
