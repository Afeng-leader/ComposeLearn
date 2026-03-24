package com.example.composelearn.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// Dark Theme 的 ColorScheme
private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

// Light Theme 的 ColorScheme
private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

/**
 * 应用主题入口
 *
 * @param darkTheme 是否启用暗色主题，默认跟随系统设置
 * @param dynamicColor 是否启用 Material You 动态取色（Android 12+），默认开启
 *
 * 【关键概念】
 * - MaterialTheme 是 Compose 中的主题容器，包含 colorScheme / typography / shapes
 * - dynamicColor 使用壁纸颜色自动生成配色，仅 Android 12 (API 31) 以上支持
 * - 子 Composable 通过 MaterialTheme.colorScheme 等访问当前主题值
 */
@Composable
fun ComposeLearnTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Android 12+ 支持从壁纸提取动态颜色
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
