package com.example.composelearn.ui.screens.intermediate

import com.example.composelearn.ui.components.SectionTitle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.composelearn.ui.theme.ComposeLearnTheme

/**
 * 主题定制演示页面
 *
 * 【核心概念】Material3 主题系统
 *
 * 1. MaterialTheme: 全局主题容器
 *    - colorScheme: 颜色方案（primary, secondary, surface, background 等）
 *    - typography: 文字排版（displayLarge ~ labelSmall 共 15 级）
 *    - shapes: 形状（small, medium, large 三种圆角）
 *
 * 2. 颜色方案 (ColorScheme)
 *    - primary / onPrimary: 主色及其上的文字颜色
 *    - primaryContainer / onPrimaryContainer: 容器色及其上的文字
 *    - surface / onSurface: 表面色（Card、Dialog 背景）
 *    - 每种颜色都有 on* 对应色，保证对比度和可读性
 *
 * 3. Dynamic Color (Material You)
 *    - Android 12+ 自动从壁纸提取颜色生成主题
 *    - dynamicLightColorScheme / dynamicDarkColorScheme
 */
@Composable
fun ThemeScreen() {
    var isDarkMode by remember { mutableStateOf(false) }
    var useDynamicColor by remember { mutableStateOf(true) }

    // 用自定义主题包裹，实现局部主题切换演示
    ComposeLearnTheme(darkTheme = isDarkMode, dynamicColor = useDynamicColor) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ===== 1. 主题切换控制 =====
                SectionTitle("1. 主题切换")
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("深色模式")
                            Switch(
                                checked = isDarkMode,
                                onCheckedChange = { isDarkMode = it },
                                thumbContent = {
                                    Icon(
                                        if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                        contentDescription = null,
                                        modifier = Modifier.size(SwitchDefaults.IconSize)
                                    )
                                }
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("动态颜色 (Material You)")
                            Switch(
                                checked = useDynamicColor,
                                onCheckedChange = { useDynamicColor = it }
                            )
                        }
                    }
                }

                // ===== 2. ColorScheme 颜色展示 =====
                SectionTitle("2. ColorScheme - 当前主题颜色")
                Text("Material3 的 ColorScheme 包含完整的颜色对，每种颜色都有对应的 on* 色")
                val colorScheme = MaterialTheme.colorScheme
                val colors = listOf(
                    "primary" to colorScheme.primary,
                    "onPrimary" to colorScheme.onPrimary,
                    "primaryContainer" to colorScheme.primaryContainer,
                    "onPrimaryContainer" to colorScheme.onPrimaryContainer,
                    "secondary" to colorScheme.secondary,
                    "secondaryContainer" to colorScheme.secondaryContainer,
                    "tertiary" to colorScheme.tertiary,
                    "tertiaryContainer" to colorScheme.tertiaryContainer,
                    "error" to colorScheme.error,
                    "surface" to colorScheme.surface,
                    "surfaceVariant" to colorScheme.surfaceVariant,
                    "background" to colorScheme.background,
                    "outline" to colorScheme.outline,
                )

                colors.chunked(2).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { (name, color) ->
                            ColorSwatch(
                                name = name,
                                color = color,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (row.size < 2) Spacer(modifier = Modifier.weight(1f))
                    }
                }

                // ===== 3. Typography 排版 =====
                SectionTitle("3. Typography - 文字排版层级")
                Text("Material3 预设了 15 种文字样式，按 display > headline > title > body > label 递减")
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        val typography = MaterialTheme.typography
                        listOf(
                            "displaySmall" to typography.displaySmall,
                            "headlineMedium" to typography.headlineMedium,
                            "titleLarge" to typography.titleLarge,
                            "titleMedium" to typography.titleMedium,
                            "bodyLarge" to typography.bodyLarge,
                            "bodyMedium" to typography.bodyMedium,
                            "labelLarge" to typography.labelLarge,
                            "labelSmall" to typography.labelSmall,
                        ).forEach { (name, style) ->
                            Text(text = name, style = style)
                        }
                    }
                }

                // ===== 4. 使用主题颜色的组件 =====
                SectionTitle("4. 主题颜色在组件中的应用")
                Text("Material3 组件自动使用主题颜色，切换主题时所有组件跟着变化")

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { }) { Text("Button") }
                    ElevatedButton(onClick = { }) { Text("Elevated") }
                    OutlinedButton(onClick = { }) { Text("Outlined") }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = true, onClick = { }, label = { Text("Selected") })
                    FilterChip(selected = false, onClick = { }, label = { Text("Unselected") })
                    AssistChip(onClick = { }, label = { Text("Assist") })
                }

                // ===== 5. 自定义主题最佳实践 =====
                SectionTitle("5. 自定义主题最佳实践")
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Tips:", style = MaterialTheme.typography.titleSmall)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("• 使用 MaterialTheme.colorScheme.xxx 引用颜色，不要硬编码")
                        Text("• 文字颜色用 onPrimary / onSurface 等 on* 色")
                        Text("• 优先使用 dynamicColor 让用户有个性化体验")
                        Text("• 通过 Material Theme Builder 工具生成自定义配色方案")
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun ColorSwatch(name: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color)
        )
        Text(name, style = MaterialTheme.typography.labelSmall)
    }
}
