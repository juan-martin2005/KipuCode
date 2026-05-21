package com.kipucode.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.R
import com.kipucode.ui.components.textfields.KipucodeText
import com.kipucode.ui.theme.Nunito
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
){
    // Animación
    val animationState = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        animationState.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
        delay(500)
        onSplashFinished()
    }

    // Fondo
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF038da9),
                        Color(0xFF07396e)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .alpha(animationState.value)
                .scale(animationState.value)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_kipu_logo_splashscreen),
                contentDescription = "Logo KipuCode",
                modifier = Modifier.height(250.dp)
            )

            KipucodeText()

            Spacer(modifier = Modifier.height(50.dp))

            Text(
                text = "Learn. Understand.\nProgram. Create.",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 20.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 32.sp,

                fontFamily = Nunito,
                fontWeight = FontWeight.SemiBold
            )
        }

        Image(
            painter = painterResource(id = R.drawable.img_kipu_two),
            contentDescription = "Loading Bar Kipu",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
                .padding(horizontal = 80.dp)

                .alpha(animationState.value)
                .scale(animationState.value)
        )

    }
}