package com.dam.kipucode.ui.screens.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dam.kipucode.R
import com.dam.kipucode.ui.theme.Nunito

@Composable
fun OnboardingScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFf6f7f9))
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(50.dp))

        Image(
            painter = painterResource(id = R.drawable.img_kipucode_logo),
            contentDescription = "Logo KipuCode",
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.CenterHorizontally),
        )
        
        Text(
            text = "Learn to code with clarity, practice and purpose",
            fontFamily = Nunito,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 32.sp,
            textAlign = TextAlign.Center,
            lineHeight = 40.sp,
            modifier = Modifier
                .fillMaxWidth(),
            color = Color(0xFF081c40),

            )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { position ->
            val page = onboardingPages[position]

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = page.description,
                    color = Color(0xFF4B5563),
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp)
                        .fillMaxSize()
                )
            }
        }

        Image(
            painter = painterResource(id = R.drawable.img_kipu_two),
            contentDescription = "Loading Bar Kipu",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
                .padding(bottom = 30.dp)
        )

        PagerIndicator(
            pageCount = onboardingPages.size,
            currentPage = pagerState.currentPage,
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                onNavigateToLogin()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0293a8))
        ) {
            Text(
                text = "Log In",
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = {
                onNavigateToRegister()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(width = 2.dp, color = Color(0xFF0293a8)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF0293a8))
        ) {
            Text(
                text = "Register",
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF0293a8)
            )
        }
    }
}

//@Preview(showBackground = true, name = "Pantalla de Onboarding")
//@Composable
//fun OnboardingPreview() {
//    OnboardingScreen {
//    }
//}