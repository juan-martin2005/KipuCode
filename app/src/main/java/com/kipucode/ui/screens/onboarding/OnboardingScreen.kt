package com.kipucode.ui.screens.onboarding

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.R
import com.kipucode.ui.component.button.FilledButton
import com.kipucode.ui.component.button.OutlineButton
import com.kipucode.ui.theme.Nunito

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
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(50.dp))

        Image(
            painter = painterResource(id = R.drawable.img_kipucode_logo),
            contentDescription = stringResource(id = R.string.cd_banner),
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.CenterHorizontally)
        )
        
        Text(
            text = stringResource(id = R.string.banner),
            fontFamily = Nunito,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 32.sp,
            textAlign = TextAlign.Center,
            lineHeight = 40.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
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
                modifier = Modifier.fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = page.descriptionRes),
                    color = Color(0xFF4B5563),
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp)
                )
            }
        }

        Image(
            painter = painterResource(id = R.drawable.img_kipu),
            contentDescription = stringResource(id = R.string.cd_kipu),
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp)
        )

        PagerIndicator(
            pageCount = onboardingPages.size,
            currentPage = pagerState.currentPage,
        )

        Spacer(modifier = Modifier.height(24.dp))

        FilledButton(
            stringResource(id = R.string.logIn),
            {onNavigateToLogin()},
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlineButton(
            stringResource(id = R.string.register),
            { onNavigateToRegister()},
            modifier = Modifier.padding(horizontal = 24.dp)

        )
    }
}

@Preview(showBackground = true, name = "Pantalla de Onboarding")
@Composable
fun OnboardingPreview() {
    OnboardingScreen(
        onNavigateToLogin = {},
        onNavigateToRegister = {}
    )
}