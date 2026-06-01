package com.kipucode.ui.component.card

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import com.kipucode.ui.theme.Gray
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.Nunito
import com.kipucode.ui.theme.White

@Composable
fun HeadlineHome(
    userName: String,
    userXp: Int,
    userStreak: Int,
    modifier: Modifier = Modifier
){
    var showXp by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(10000)
            showXp = !showXp
        }
    }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // --- Usuario ---
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        KipuTeal,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_user),
                    contentDescription = "Avatar de Usuario",
                    tint = White.copy(alpha = 0.85f),
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // --- Textos ---
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stringResource(R.string.headline_home),
                    fontSize = 20.sp,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.ExtraBold,
                    color = KipuDarkBlue,
                    lineHeight = 24.sp
                )
                Text(
                    text = userName,
                    modifier = Modifier.padding(start = 2.dp),
                    fontSize = 13.sp,
                    fontFamily = Nunito,
                    color = Gray,
                )
            }

            // --- Bloque Dinámico XP <-> Racha ---
            AnimatedContent(
                targetState = showXp,
                transitionSpec = {
                    (slideInHorizontally { width -> width } + fadeIn())
                        .togetherWith(
                            slideOutHorizontally { width -> -width } + fadeOut()
                        )
                },

            ) { isShowingXp ->

                val bgColor = if (isShowingXp) Color(0xFFFFF8E1) else Color(0xFFFFEBEE)
                val iconRes = if (isShowingXp) R.drawable.ic_star else R.drawable.ic_fire
                val iconTint = if (isShowingXp) Color(0xFFFFC107) else Color(0xFFFF5722)

                val textValue = if (isShowingXp) userXp
                else userStreak

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .background(color = bgColor, shape = CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = if (isShowingXp) "XP" else "Racha",
                            tint = iconTint,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Column {
                        Text(
                            text = "$textValue",
                            color = KipuDarkBlue,
                            fontSize = 14.sp,
                            fontFamily = Nunito,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}

// --- VISTA PREVIA ---
//@Preview(showBackground = true)
//@Composable
//fun HeadlineHomePreview() {
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .background(Color(0xFFf6f7f9))
//            .padding(16.dp)
//    ) {
//        HeadlineHome(
//            userName = "Juan Martin Gonzales Sinarahua",
//            userXp = 1250,
//            userStreak = 365
//        )
//    }
//}