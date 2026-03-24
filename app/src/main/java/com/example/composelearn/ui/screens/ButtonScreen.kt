package com.example.composelearn.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 按钮类型演示页面
 *
 * 【核心概念】Material3 按钮体系
 * - Button (Filled): 最高优先级的操作按钮
 * - ElevatedButton: 带阴影的按钮，次要操作
 * - FilledTonalButton: 浅色填充按钮
 * - OutlinedButton: 带边框的按钮，三级操作
 * - TextButton: 纯文字按钮，最低优先级
 * - IconButton: 图标按钮
 * - FloatingActionButton: 悬浮操作按钮
 *
 * 【状态管理预览】
 * 这里用到 remember + mutableStateOf 来记录点击次数
 * 详细解释见 StateScreen
 */
@Composable
fun ButtonScreen() {
    // remember 使变量在 recomposition 时保持值
    // mutableIntStateOf 是 mutableStateOf<Int> 的优化版本
    var clickCount by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 点击计数器展示区
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Text(
                text = "累计点击次数: $clickCount",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }

        // ===== 1. Filled Button (默认按钮) =====
        SectionTitle("1. Button (Filled) - 主操作按钮")
        Text("最高视觉优先级，用于页面的主要操作")
        Button(onClick = { clickCount++ }) {
            Text("Filled Button")
        }
        // 带图标的按钮
        Button(onClick = { clickCount++ }) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
            Text("带图标的按钮")
        }

        // ===== 2. ElevatedButton =====
        SectionTitle("2. ElevatedButton - 带阴影按钮")
        Text("有阴影效果，视觉优先级次于 Filled Button")
        ElevatedButton(onClick = { clickCount++ }) {
            Text("Elevated Button")
        }

        // ===== 3. FilledTonalButton =====
        SectionTitle("3. FilledTonalButton - 浅色填充")
        Text("使用 secondaryContainer 颜色填充，介于 Filled 和 Outlined 之间")
        FilledTonalButton(onClick = { clickCount++ }) {
            Text("Filled Tonal Button")
        }

        // ===== 4. OutlinedButton =====
        SectionTitle("4. OutlinedButton - 边框按钮")
        Text("只有边框没有填充色，适合次要操作")
        OutlinedButton(onClick = { clickCount++ }) {
            Text("Outlined Button")
        }

        // ===== 5. TextButton =====
        SectionTitle("5. TextButton - 纯文字按钮")
        Text("最低视觉优先级，适合对话框中的取消操作")
        TextButton(onClick = { clickCount++ }) {
            Text("Text Button")
        }

        // ===== 6. IconButton =====
        SectionTitle("6. IconButton - 图标按钮")
        Text("只包含图标，常用于 TopBar 中的操作")
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { clickCount++ }) {
                Icon(Icons.Default.Favorite, contentDescription = "收藏")
            }
            IconButton(onClick = { clickCount++ }) {
                Icon(Icons.Default.Share, contentDescription = "分享")
            }
            // FilledIconButton: 有背景色的图标按钮
            FilledIconButton(onClick = { clickCount++ }) {
                Icon(Icons.Default.Add, contentDescription = "添加")
            }
            // OutlinedIconButton: 有边框的图标按钮
            OutlinedIconButton(onClick = { clickCount++ }) {
                Icon(Icons.Default.Search, contentDescription = "搜索")
            }
        }

        // ===== 7. FloatingActionButton =====
        SectionTitle("7. FloatingActionButton - 悬浮按钮")
        Text("通常在 Scaffold 中使用，这里单独演示几种变体")
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 小型 FAB
            SmallFloatingActionButton(onClick = { clickCount++ }) {
                Icon(Icons.Default.Add, contentDescription = "添加")
            }
            // 标准 FAB
            FloatingActionButton(onClick = { clickCount++ }) {
                Icon(Icons.Default.Edit, contentDescription = "编辑")
            }
            // 大型 FAB
            LargeFloatingActionButton(onClick = { clickCount++ }) {
                Icon(Icons.Default.Create, contentDescription = "创建")
            }
        }
        // 扩展型 FAB（带文字）
        ExtendedFloatingActionButton(
            onClick = { clickCount++ },
            icon = { Icon(Icons.Default.Navigation, contentDescription = null) },
            text = { Text("导航") }
        )

        // ===== 8. 按钮状态: enabled / disabled =====
        SectionTitle("8. 按钮状态控制")
        Text("enabled = false 时按钮不可点击，颜色变为灰色")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { }, enabled = true) { Text("可用") }
            Button(onClick = { }, enabled = false) { Text("禁用") }
        }

        // ===== 9. 重置按钮 =====
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        OutlinedButton(
            onClick = { clickCount = 0 },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("重置计数器")
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
