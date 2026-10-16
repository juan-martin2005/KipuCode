package com.kipucode.ui.components.navbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kipucode.R
import com.kipucode.ui.theme.KipuCodeTheme
import com.kipucode.ui.theme.KipuDarkBlue
import com.kipucode.ui.theme.Nunito

@Composable
fun KipuTopBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        // Botón
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(40.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_left),
                contentDescription = "Volver",
                tint = KipuDarkBlue,
                modifier = Modifier.size(30.dp)
            )
        }

        // Título
        Text(
            text = title,
            fontSize = 18.sp,
            fontFamily = Nunito,
            fontWeight = FontWeight.ExtraBold,
            color = KipuDarkBlue,
            lineHeight = 19.sp,
            maxLines = 2,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 48.dp)
        )
    }
}

@Preview(showBackground = true, name = "TopBar - Default")
@Composable
private fun TopBarPreview() {
    KipuCodeTheme {
        KipuTopBar(
            title = "Pensamiento Computacional y Entornos Base",
            onBackClick = {}
        )
    }
}