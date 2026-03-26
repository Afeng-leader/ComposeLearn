package com.example.composelearn.ui.screens.advanced

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composelearn.viewmodel.CounterViewModel

/**
 * ViewModel 演示页面
 *
 * 【核心概念】ViewModel + Compose 集成
 *
 * 1. viewModel(): Compose 中获取 ViewModel 的方式
 *    - 自动绑定到最近的 ViewModelStoreOwner（通常是 Activity/NavBackStackEntry）
 *    - 相同 owner 下多次调用返回同一实例
 *
 * 2. collectAsStateWithLifecycle(): 将 StateFlow 转换为 Compose State
 *    - 比 collectAsState() 更好，能感知生命周期
 *    - 页面不可见时自动暂停收集，节省资源
 *
 * 3. ViewModel 的优势（对比 remember）:
 *    - remember: 只在 Composition 中存活
 *    - ViewModel: 在配置变更（旋转屏幕）后仍然存活
 *    - ViewModel: 可以持有业务逻辑、网络请求、数据库操作
 *    - ViewModel: 配合 StateFlow 实现响应式 UI
 */
@Composable
fun ViewModelScreen() {
    // viewModel() 获取或创建 CounterViewModel 实例
    val counterViewModel: CounterViewModel = viewModel()

    // collectAsStateWithLifecycle 将 StateFlow 收集为 Compose State
    // 当 StateFlow 的值变化时，引用此 State 的 Composable 自动 recompose
    val count by counterViewModel.count.collectAsStateWithLifecycle()
    val history by counterViewModel.history.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        // 固定顶部: 计数器展示
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "ViewModel 计数器",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "旋转屏幕后数值不会丢失 (ViewModel 比 Composition 活得更久)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "$count",
                    style = MaterialTheme.typography.displayLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledIconButton(onClick = { counterViewModel.decrement() }) {
                        Icon(Icons.Default.Remove, "减少")
                    }
                    OutlinedIconButton(onClick = { counterViewModel.reset() }) {
                        Icon(Icons.Default.Refresh, "重置")
                    }
                    FilledIconButton(onClick = { counterViewModel.increment() }) {
                        Icon(Icons.Default.Add, "增加")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Slider 设置任意值
                var sliderValue by remember { mutableFloatStateOf(count.toFloat()) }
                LaunchedEffect(count) { sliderValue = count.toFloat() }
                Text("拖动设置值: ${sliderValue.toInt()}")
                Slider(
                    value = sliderValue,
                    onValueChange = { sliderValue = it },
                    onValueChangeFinished = { counterViewModel.setCount(sliderValue.toInt()) },
                    valueRange = -50f..50f
                )
            }
        }

        // 对比说明
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("remember vs ViewModel", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(4.dp))
                ComparisonRow("存活范围", "Composition", "ViewModelStore")
                ComparisonRow("屏幕旋转", "丢失 ❌", "保持 ✓")
                ComparisonRow("业务逻辑", "不适合", "推荐")
                ComparisonRow("网络/DB", "不应该放", "推荐")
                ComparisonRow("简单 UI 状态", "推荐", "可以但没必要")
            }
        }

        // 操作历史
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("操作历史 (${history.size})", style = MaterialTheme.typography.titleSmall)
            if (history.isNotEmpty()) {
                TextButton(onClick = { counterViewModel.clearHistory() }) {
                    Text("清空")
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (history.isEmpty()) {
                item {
                    Text(
                        "还没有操作记录，试试点击上方按钮",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            items(history.reversed()) { entry ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(entry, modifier = Modifier.padding(12.dp))
                }
            }
        }
    }
}

@Composable
private fun ComparisonRow(feature: String, remember: String, viewModel: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(feature, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
        Text(remember, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center)
        Text(viewModel, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center)
    }
}
