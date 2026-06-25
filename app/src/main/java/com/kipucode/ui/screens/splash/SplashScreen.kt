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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.R
import com.kipucode.ui.components.text_field.KipucodeText
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.KipuTeal
import com.kipucode.ui.theme.Nunito
import com.kipucode.ui.theme.White
import com.kipucode.viewmodel.AuthViewModel
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SplashScreen(
    authViewModel: AuthViewModel,
    onSplashFinished: (isUserLogged: Boolean) -> Unit
){
    val animationState = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        animationState.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
        delay(800.milliseconds)

        val isLogged = authViewModel.isUserLoggedIn()
        onSplashFinished(isLogged)
    }

    SplashContent(
        animationScale = animationState.value
    )
}

@Composable
fun SplashContent(
    animationScale: Float = 1f
) {
    // Fondo
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        KipuTeal,
                        KipuDarkBlue
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .alpha(animationScale)
                .scale(animationScale)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_kipu_logo_splashscreen),
                contentDescription = stringResource(R.string.cd_logo),
                modifier = Modifier.height(210.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Components -> TextFields
            KipucodeText()

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(id = R.string.subline),
                color = White.copy(alpha = 0.8f),
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
                lineHeight = 32.sp,
                fontFamily = Nunito,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(48.dp))
        }

        Image(
            painter = painterResource(id = R.drawable.img_kipu),
            contentDescription = stringResource(id = R.string.cd_kipu),
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
                .alpha(animationScale)
                .scale(animationScale)
        )
    }
}

// --- PREVIEW ---
@Preview(showBackground = true, showSystemUi = true, name = "Splash Inicial")
@Composable
fun SplashScreenPreview() {
    SplashContent(animationScale = 1f)
}