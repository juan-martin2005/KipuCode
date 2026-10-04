package com.kipucode.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.kipucode.R

// Set of Material typography styles to start with

val Nunito = FontFamily(
Font(R.font.nunito_bold, FontWeight.Bold),
        Font(R.font.nunito_regular, FontWeight.Normal),
        Font(R.font.nunito_medium, FontWeight.Medium),
        Font(R.font.nunito_semibold, FontWeight.SemiBold),
        Font(R.font.nunito_extrabold, FontWeight.ExtraBold),
        Font(R.font.nunito_black, FontWeight.Black),
        Font(R.font.nunito_extralight, FontWeight.ExtraLight),
        Font(R.font.nunito_light, FontWeight.Light),
    )

val JetBrains = FontFamily(
    Font(R.font.jetbrainsmono_nf, FontWeight.Normal),
    )
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)

// ============================================================================================
//  TIPOGRAFÍAS ESTÁNDAR KIPUCODE (CENTRALIZADAS PARA LECCIONES Y COMPONENTES)
// ============================================================================================

val KipuH1 = TextStyle(
    fontFamily = Nunito,
    fontWeight = FontWeight.Black,
    fontSize = 26.sp,
    color = KipuTeal
)

val KipuH2 = TextStyle(
    fontFamily = Nunito,
    fontWeight = FontWeight.ExtraBold,
    fontSize = 20.sp,
    color = KipuTeal
)

val KipuH3 = TextStyle(
    fontFamily = Nunito,
    fontWeight = FontWeight.Bold,
    fontSize = 18.sp,
    color = KipuDarkBlue
)

val KipuH4 = TextStyle(
    fontFamily = Nunito,
    fontWeight = FontWeight.ExtraBold,
    fontSize = 18.sp,
    color = KipuDarkBlue
)

val KipuH5 = TextStyle(
    fontFamily = Nunito,
    fontWeight = FontWeight.Bold,
    fontSize = 16.sp,
    color = KipuDarkBlue
)

val KipuParagraph = TextStyle(
    fontFamily = Nunito,
    fontWeight = FontWeight.Medium,
    fontSize = 15.sp,
    lineHeight = 23.sp,
    color = KipuDarkBlue
)

val KipuCodeText = TextStyle(
    fontFamily = JetBrains,
    fontWeight = FontWeight.Normal,
    fontSize = 13.sp,
    color = KipuDarkBlue
)

val KipuQuoteText = TextStyle(
    fontFamily = Nunito,
    fontWeight = FontWeight.Normal,
    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
    fontSize = 15.sp,
    color = KipuDarkBlue.copy(alpha = 0.85f)
)

