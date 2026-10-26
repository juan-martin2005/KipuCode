package com.kipucode.ui.util

/**
 * Convierte un número entero en su representación compacta para la UI.
 * Ejemplos:
 *  - 50 -> "50"
 *  - 1250 -> "1.2k"
 *  - 11025 -> "11k"
 *  - 1250000 -> "1.2M"
 */
fun Int.toCompactFormat(): String {
    return when {
        this < 1_000 -> "$this"
        this < 10_000 -> {
            val decimal = (this % 1_000) / 100
            if (decimal == 0) "${this / 1_000}k" else "${this / 1_000}.${decimal}k"
        }
        this < 1_000_000 -> "${this / 1_000}k"
        this < 10_000_000 -> {
            val decimal = (this % 1_000_000) / 100_000
            if (decimal == 0) "${this / 1_000_000}M" else "${this / 1_000_000}.${decimal}M"
        }
        else -> "${this / 1_000_000}M"
    }
}
