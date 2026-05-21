package com.kipucode

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.kipucode.ui.navigation.AppNavigation
import com.kipucode.ui.theme.KipuCodeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // EnableEdgeToEdge nos da acceso a modificar la pantalla completa dándonos acceso al
        // Status y Navigation Bar

        //  ┌─────────────────┐
        //  │   Status Bar    │ tu app puede pintar aquí
        //  ├─────────────────┤
        //  │                 │
        //  │     Tu App      │
        //  │                 │
        //  ├─────────────────┤
        //  │ Navigation Bar  │ tu app puede pintar aquí
        //  └─────────────────┘

        enableEdgeToEdge(
            // Status Bar
            // System Style Dark -> Iconos de color Blanco por defecto al ser modo Oscuro.
            // Color #292929 (gris oscuro), ToArgb cambiar formato de Long a Int.
            statusBarStyle = SystemBarStyle.dark(Color(0xFF292929).toArgb()),
            // Navigation Bar
            navigationBarStyle = SystemBarStyle.dark(Color(0xFF292929).toArgb())
        )

        super.onCreate(savedInstanceState)

        setContent {
            KipuCodeTheme {
                AppNavigation()
            }
        }
    }
}