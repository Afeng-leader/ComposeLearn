package com.example.composelearn.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 状态管理演示页面 - Compose 最核心的概念
 *
 * 【核心概念】
 * - State（状态）: Compose 通过"状态驱动 UI"，状态变化 → 自动 recomposition → UI 更新
 * - remember: 在 recomposition 时保持变量值（只在内存中，旋转屏幕会丢失）
 * - rememberSaveable: 在配置变更（如旋转屏幕）后仍保持值
 * - mutableStateOf: 创建可观察的可变状态，值变化时触发 recomposition
 * - State Hoisting（状态提升）: 将状态移到父组件，子组件通过参数接收值和回调
 *
 * 【关键区别：与传统 Android 的对比】
 * - 传统: findViewById → setText / setVisibility（命令式）
 * - Compose: 修改 State 值 → UI 自动更新（声明式）
 */
@Composable
fun StateScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ===== 1. 无状态 vs 有状态 =====
        SectionTitle("1. 无状态（不会更新 UI）")
        Text("如果不使用 remember，每次 recomposition 变量都会重置为初始值")
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = """
                    // ❌ 错误写法 - count 每次 recomposition 都重置为 0
                    var count = 0
                    Button(onClick = { count++ }) { ... }
                    """.trimIndent(),
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }
        }

        // ===== 2. remember + mutableStateOf =====
        SectionTitle("2. remember + mutableStateOf")
        Text(
            "remember 让变量在 recomposition 时保持值;\n" +
            "mutableStateOf 让 Compose 监听变量变化并自动刷新 UI"
        )

        // 【写法1】by 委托语法（推荐，更简洁）
        var count1 by remember { mutableIntStateOf(0) }
        CounterCard(
            title = "写法1: by 委托（推荐）",
            code = "var count by remember { mutableIntStateOf(0) }",
            count = count1,
            onIncrement = { count1++ },
            onDecrement = { count1-- },
            onReset = { count1 = 0 }
        )

        // 【写法2】直接使用 .value（等价写法）
        val count2 = remember { mutableIntStateOf(0) }
        CounterCard(
            title = "写法2: .intValue 访问",
            code = "val count = remember { mutableIntStateOf(0) }\n// 读: count.intValue  写: count.intValue = x",
            count = count2.intValue,
            onIncrement = { count2.intValue++ },
            onDecrement = { count2.intValue-- },
            onReset = { count2.intValue = 0 }
        )

        // ===== 3. rememberSaveable =====
        SectionTitle("3. rememberSaveable - 保存配置变更")
        Text(
            "remember 在屏幕旋转后值会丢失;\n" +
            "rememberSaveable 会自动序列化保存，旋转后恢复"
        )
        var savedCount by rememberSaveable { mutableIntStateOf(0) }
        CounterCard(
            title = "旋转屏幕后仍保持的计数器",
            code = "var count by rememberSaveable { mutableIntStateOf(0) }",
            count = savedCount,
            onIncrement = { savedCount++ },
            onDecrement = { savedCount-- },
            onReset = { savedCount = 0 }
        )

        // ===== 4. 多种状态类型 =====
        SectionTitle("4. 多种状态类型")
        Text("mutableStateOf 可以持有任何类型的值")

        // String 状态
        var name by remember { mutableStateOf("") }
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("输入你的名字") },
            modifier = Modifier.fillMaxWidth()
        )
        if (name.isNotEmpty()) {
            Text("你好, $name!", style = MaterialTheme.typography.titleMedium)
        }

        // Boolean 状态
        var isChecked by remember { mutableStateOf(false) }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = isChecked, onCheckedChange = { isChecked = it })
            Text(if (isChecked) "已勾选 ✓" else "未勾选")
        }

        // List 状态
        var items by remember { mutableStateOf(listOf("Item 1", "Item 2", "Item 3")) }
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("动态列表 (${items.size} 项)", style = MaterialTheme.typography.titleSmall)
                items.forEach { item ->
                    Text("• $item")
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // 注意: 不能用 items.add()，必须创建新 List 赋值才会触发 recomposition
                    Button(onClick = { items = items + "Item ${items.size + 1}" }) {
                        Text("添加")
                    }
                    OutlinedButton(
                        onClick = { if (items.isNotEmpty()) items = items.dropLast(1) }
                    ) {
                        Text("删除最后一项")
                    }
                }
            }
        }

        // ===== 5. State Hoisting 状态提升 =====
        SectionTitle("5. State Hoisting - 状态提升模式")
        Text(
            "最佳实践: 将状态放在调用者（父组件），子组件只接收值和回调\n" +
            "这让子组件变成'无状态'的，更容易复用和测试"
        )
        // 状态在此处（父组件）定义
        var rating by remember { mutableIntStateOf(0) }
        // RatingBar 是无状态组件，接收 rating 值和 onRatingChange 回调
        RatingBar(
            rating = rating,
            onRatingChange = { rating = it }
        )
        Text("当前评分: $rating / 5")

        // ===== 6. derivedStateOf =====
        SectionTitle("6. derivedStateOf - 派生状态")
        Text("当一个状态依赖其他状态时，用 derivedStateOf 避免不必要的 recomposition")
        var sliderValue by remember { mutableFloatStateOf(0f) }
        // derivedStateOf 只在 sliderValue 导致结果变化时才触发 recomposition
        val level by remember { derivedStateOf {
            when {
                sliderValue < 0.33f -> "低"
                sliderValue < 0.66f -> "中"
                else -> "高"
            }
        }}
        Slider(value = sliderValue, onValueChange = { sliderValue = it })
        Text("数值: ${"%.2f".format(sliderValue)} → 级别: $level")

        Spacer(modifier = Modifier.height(32.dp))
    }
}

/** 计数器卡片 - 演示状态的可视化展示 */
@Composable
private fun CounterCard(
    title: String,
    code: String,
    count: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onReset: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(
                text = code,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledIconButton(onClick = onDecrement) {
                    Icon(Icons.Default.Remove, contentDescription = "减少")
                }
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.width(60.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                FilledIconButton(onClick = onIncrement) {
                    Icon(Icons.Default.Add, contentDescription = "增加")
                }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = onReset) { Text("重置") }
            }
        }
    }
}

/**
 * 评分组件（State Hoisting 示例）
 *
 * 这是一个"无状态"组件 (Stateless Composable):
 * - 自身不持有任何状态
 * - rating: 由父组件传入的当前值
 * - onRatingChange: 由父组件提供的回调，用于通知状态变更
 * 好处: 可以在不同页面复用此组件，每处各自管理自己的评分状态
 */
@Composable
private fun RatingBar(
    rating: Int,
    onRatingChange: (Int) -> Unit,
    maxRating: Int = 5
) {
    Row {
        repeat(maxRating) { index ->
            IconButton(onClick = { onRatingChange(index + 1) }) {
                Icon(
                    imageVector = if (index < rating) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "评分 ${index + 1}",
                    tint = if (index < rating) Color(0xFFFFD700) else GrayColor,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}

private val GrayColor = androidx.compose.ui.graphics.Color(0xFF9E9E9E)
