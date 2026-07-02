package com.kipucode.ui.screens.auth.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.R
import com.kipucode.ui.components.KipuTopBar
import com.kipucode.ui.components.button.FilledButton
import com.kipucode.ui.theme.BackgroundGray
import com.kipucode.ui.theme.Gray
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.LightGray
import com.kipucode.ui.theme.MoonFrost
import com.kipucode.ui.theme.Nunito
import com.kipucode.ui.theme.White

@Composable
fun CourseChoices(
    modifier: Modifier = Modifier,
    selectedCourse: String,
    onCourseSelected: (String) -> Unit,
    onConfirm: () -> Unit,
    onBack: () -> Unit,
    isLoading: Boolean
) {
    val courses = listOf(
        Pair("python", "Programación en Python"),
        Pair("java", "Desarrollo en Java")
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundGray),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Botón de retroceder
        KipuTopBar(
            title = stringResource(id = R.string.register_choice_back),
            onBackClick = { onBack() },
            modifier = Modifier.padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(id = R.string.register_choice_title),
            fontSize = 28.sp,
            fontFamily = Nunito,
            fontWeight = FontWeight.ExtraBold,
            color = KipuDarkBlue,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)

        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.register_choice_desc),
            fontSize = 16.sp,
            fontFamily = Nunito,
            fontWeight = FontWeight.SemiBold,
            color = Gray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // LISTA
        courses.forEach { (courseId, courseName) ->
            val isSelected = selectedCourse == courseId

            Card(
                onClick = { onCourseSelected(courseId) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp, horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MoonFrost else White
                ),
                border = if (isSelected) BorderStroke(1.5.dp, KipuTeal) else
                    BorderStroke(1.dp, LightGray)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        painter = painterResource(
                            id = when (courseId) {
                                "python" -> R.drawable.ic_python
                                "java" -> R.drawable.ic_java
                                else -> R.drawable.ic_python
                            }
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = Color.Unspecified
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = courseName,
                        fontSize = 18.sp,
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Bold,
                        color = KipuDarkBlue
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        FilledButton(
            modifier = Modifier.padding(horizontal = 16.dp),
            textButton = stringResource(id = R.string.register),
            onClickFilledButton = onConfirm,
            isLoading = isLoading,
            enabled = selectedCourse.isNotBlank()
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun CourseChoicesPreview() {
    CourseChoices(
        selectedCourse = "python",
        onCourseSelected = {},
        onConfirm = {},
        onBack = {},
        isLoading = false
    )
}