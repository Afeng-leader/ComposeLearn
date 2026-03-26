package com.example.composelearn.ui.screens.advanced

import com.example.composelearn.ui.components.SectionTitle
import android.util.Log
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * 副作用 API 演示页面
 *
 * 【核心概念】Side Effects（副作用）
 * Compose 是声明式的：UI = f(State)，不应该在 Composable 函数体内执行"副作用"操作。
 * 副作用是指：改变应用状态、写日志、启动协程、注册回调等。
 *
 * Compose 提供了一系列 Effect API 来安全地管理副作用：
 *
 * 1. LaunchedEffect: 在 Composable 进入组合时启动协程，key 变化时重新启动
 * 2. DisposableEffect: 需要清理的副作用（注册→注销模式）
 * 3. SideEffect: 每次成功 recomposition 后执行（非挂起）
 * 4. rememberUpdatedState: 在长期运行的 effect 中引用最新的值
 * 5. snapshotFlow: 将 Compose State 转换为 Flow
 */
@Composable
fun SideEffectScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ===== 1. LaunchedEffect - 自动计时器 =====
        SectionTitle("1. LaunchedEffect - 协程副作用")
        Text(
            "LaunchedEffect(key) 在进入组合时启动协程，key 变化时取消并重启。\n" +
            "适用场景：定时刷新、一次性数据加载、延迟操作"
        )

        LaunchedEffectDemo()

        // ===== 2. DisposableEffect - 注册/注销 =====
        SectionTitle("2. DisposableEffect - 可清理的副作用")
        Text(
            "DisposableEffect(key) 在进入组合时执行，离开时调用 onDispose {} 清理。\n" +
            "适用场景：注册/注销监听器、绑定/解绑服务"
        )

        DisposableEffectDemo()

        // ===== 3. SideEffect - 每次 recomposition 后执行 =====
        SectionTitle("3. SideEffect - 同步副作用")
        Text(
            "SideEffect {} 在每次成功 recomposition 后同步执行（非挂起）。\n" +
            "适用场景：向非 Compose 系统同步状态（如 Analytics 日志）"
        )

        SideEffectDemo()

        // ===== 4. rememberUpdatedState =====
        SectionTitle("4. rememberUpdatedState - 引用最新值")
        Text(
            "在长期运行的 LaunchedEffect 中，lambda 捕获的值可能过时。\n" +
            "rememberUpdatedState 确保始终引用最新的值。"
        )

        RememberUpdatedStateDemo()

        // ===== 5. snapshotFlow =====
        SectionTitle("5. snapshotFlow - State 转 Flow")
        Text(
            "snapshotFlow {} 将 Compose State 读取转换为 Flow。\n" +
            "当 State 值变化时，Flow 发射新值。可使用 Flow 的操作符（如 distinctUntilChanged、filter）。"
        )

        SnapshotFlowDemo()

        // ===== 6. 对比总结 =====
        SectionTitle("6. Side Effect API 对比")
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                EffectCompareRow("LaunchedEffect", "进入组合时", "自动取消协程", "异步操作/定时器")
                EffectCompareRow("DisposableEffect", "进入组合时", "onDispose 手动清理", "监听器注册/注销")
                EffectCompareRow("SideEffect", "每次 recomposition", "无需清理", "同步状态到外部系统")
                EffectCompareRow("rememberUpdatedState", "-", "-", "在 Effect 中引用最新值")
                EffectCompareRow("snapshotFlow", "-", "-", "State → Flow 转换")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

/** LaunchedEffect 演示：自动计时器 */
@Composable
private fun LaunchedEffectDemo() {
    var timerRunning by remember { mutableStateOf(false) }
    var seconds by remember { mutableIntStateOf(0) }

    // key = timerRunning：当 timerRunning 变化时，取消旧协程并重新启动
    // 如果 timerRunning = false，LaunchedEffect 进入时不做任何循环
    LaunchedEffect(timerRunning) {
        if (timerRunning) {
            while (true) {
                delay(1000L)
                seconds++
            }
        }
        // 当 timerRunning 变为 false，协程被取消（因为 key 变了）
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("计时器: ${seconds}s", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { timerRunning = true }, enabled = !timerRunning) {
                    Icon(Icons.Default.PlayArrow, null)
                    Spacer(Modifier.width(4.dp))
                    Text("开始")
                }
                OutlinedButton(onClick = { timerRunning = false }, enabled = timerRunning) {
                    Icon(Icons.Default.Pause, null)
                    Spacer(Modifier.width(4.dp))
                    Text("暂停")
                }
                TextButton(onClick = { timerRunning = false; seconds = 0 }) {
                    Text("重置")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "代码: LaunchedEffect(timerRunning) { while(true) { delay(1000); seconds++ } }",
                style = MaterialTheme.typography.bodySmall,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * DisposableEffect 演示：模拟生命周期监听
 *
 * 典型使用场景：注册/注销 BroadcastReceiver、添加/移除 OnBackPressedCallback、
 * 绑定/解绑 Service、添加/移除 MapView 标注等。
 *
 * 注意：logMessages 在 effect 中被修改，这里是演示目的；
 * 实际项目中不建议在 DisposableEffect 内直接修改 UI State，
 * 应通过 ViewModel 或 callback 通知 UI 层。
 */
@Composable
private fun DisposableEffectDemo() {
    var showChild by remember { mutableStateOf(false) }
    var logMessages by remember { mutableStateOf(listOf<String>()) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("显示子组件")
                Switch(checked = showChild, onCheckedChange = { showChild = it })
            }

            if (showChild) {
                // key = Unit: 仅在进入/离开组合时触发，不会因 recomposition 而重复注册
                DisposableEffect(Unit) {
                    val msg = "✅ 进入组合 - 注册监听器"
                    logMessages = logMessages + msg
                    Log.d("SideEffectDemo", msg)

                    // onDispose 是必须提供的清理回调，类似 Activity.onDestroy
                    onDispose {
                        val disposeMsg = "❌ 离开组合 - 注销监听器"
                        logMessages = logMessages + disposeMsg
                        Log.d("SideEffectDemo", disposeMsg)
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        "我是子组件 (DisposableEffect 已注册)",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("生命周期日志:", style = MaterialTheme.typography.labelMedium)
            logMessages.takeLast(6).forEach { msg ->
                Text(msg, style = MaterialTheme.typography.bodySmall)
            }
            if (logMessages.isNotEmpty()) {
                TextButton(onClick = { logMessages = emptyList() }) {
                    Text("清空日志")
                }
            }
        }
    }
}

/**
 * SideEffect 演示
 *
 * SideEffect {} 在每次成功 recomposition 后同步执行，且不可挂起。
 * 典型用途：将 Compose State 同步到非 Compose 管理的对象（如 Analytics SDK、
 * 第三方 View 系统、全局日志系统）。
 *
 * 与 LaunchedEffect 的区别：
 * - LaunchedEffect: 协程作用域，可挂起，有 key 控制生命周期
 * - SideEffect: 非挂起，没有 key，每次 recomposition 都执行
 */
@Composable
private fun SideEffectDemo() {
    var count by remember { mutableIntStateOf(0) }
    // 使用普通对象包装计数器，避免 State 变更触发 recomposition 形成死循环。
    // 如果用 mutableIntStateOf，SideEffect 修改 → 触发 recomposition → SideEffect 再次修改 → 无限循环
    val recomposeCounter = remember { intArrayOf(0) }
    var displayedRecomposeCount by remember { mutableIntStateOf(0) }

    SideEffect {
        recomposeCounter[0]++
        Log.d("SideEffectDemo", "Recomposition #${recomposeCounter[0]}, count = $count")
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("点击次数: $count")
            Text("Recomposition 次数: $displayedRecomposeCount")
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { count++ }) {
                    Text("点击 (+1)")
                }
                OutlinedButton(onClick = { displayedRecomposeCount = recomposeCounter[0] }) {
                    Text("刷新计数")
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "点击按钮触发 recomposition → SideEffect 执行 → 非 State 计数器++\n" +
                "点「刷新计数」将内部计数器同步到 UI（避免 State 变更导致无限 recomposition）",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * rememberUpdatedState 演示
 *
 * 问题背景：LaunchedEffect(key) 只在 key 变化时重新启动。如果在 Effect 内部引用了
 * 外部 State 变量，而该变量在 Effect 运行期间发生了变化，Effect 中引用的闭包仍然是旧值。
 *
 * rememberUpdatedState 的作用：创建一个始终指向最新值的 State 引用。
 * Effect 通过读取该引用（而非直接捕获值）来获取最新值。
 * 内部实现等价于：val ref = remember { mutableStateOf(value) }.also { it.value = value }
 */
@Composable
private fun RememberUpdatedStateDemo() {
    var message by remember { mutableStateOf("初始消息") }
    var showResult by remember { mutableStateOf(false) }
    var capturedMessage by remember { mutableStateOf("") }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("修改消息") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // currentMessage 始终指向最新的 message 值，即使 LaunchedEffect 不重启
            val currentMessage by rememberUpdatedState(message)

            LaunchedEffect(showResult) {
                if (showResult) {
                    delay(3000L)
                    // 如果这里直接用 message 而非 currentMessage，捕获到的会是 3 秒前的旧值
                    capturedMessage = currentMessage
                    showResult = false
                }
            }

            Button(
                onClick = { showResult = true; capturedMessage = "" },
                enabled = !showResult
            ) {
                Text(if (showResult) "3 秒后显示..." else "3 秒后捕获当前消息")
            }

            if (capturedMessage.isNotEmpty()) {
                Text(
                    "3 秒后捕获到: \"$capturedMessage\"",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                "在 3 秒等待期间修改消息，捕获到的一定是最新值（感谢 rememberUpdatedState）",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * snapshotFlow 演示
 *
 * snapshotFlow { } 的工作原理：
 * 1. lambda 中读取 Compose State（如 sliderValue）
 * 2. 当这些 State 变化时，lambda 重新执行并发射新值到 Flow
 * 3. 可以对 Flow 使用所有 kotlinx.coroutines.flow 操作符
 *
 * 本例演示 snapshotFlow + map + distinctUntilChanged 的组合：
 * Slider 每帧都在变化值，但通过 map（将连续值映射为离散分类）+
 * distinctUntilChanged（去重），Flow 只在分类真正切换时才发射。
 */
@Composable
private fun SnapshotFlowDemo() {
    var sliderValue by remember { mutableFloatStateOf(0f) }
    var category by remember { mutableStateOf("无") }
    var flowEmitCount by remember { mutableIntStateOf(0) }

    // key = Unit: 整个生命周期只启动一次，snapshotFlow 内部自动追踪 State 变化
    LaunchedEffect(Unit) {
        snapshotFlow { sliderValue }
            .map { value ->
                when {
                    value < 0.25f -> "极低"
                    value < 0.50f -> "偏低"
                    value < 0.75f -> "偏高"
                    else -> "极高"
                }
            }
            .distinctUntilChanged()
            .collect { newCategory ->
                category = newCategory
                flowEmitCount++
            }
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("拖动滑块，分类只在跨越阈值时才更新（Flow distinctUntilChanged）:")
            Spacer(modifier = Modifier.height(8.dp))
            Slider(value = sliderValue, onValueChange = { sliderValue = it })
            Text("数值: ${"%.2f".format(sliderValue)}")
            Text(
                "当前分类: $category",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text("Flow 发射次数: $flowEmitCount（远少于 Slider 变化次数）")
        }
    }
}

@Composable
private fun EffectCompareRow(name: String, trigger: String, cleanup: String, useCase: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            name,
            modifier = Modifier.width(140.dp),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
        )
        Column {
            if (trigger != "-") Text("触发: $trigger", style = MaterialTheme.typography.bodySmall)
            if (cleanup != "-") Text("清理: $cleanup", style = MaterialTheme.typography.bodySmall)
            Text("场景: $useCase", style = MaterialTheme.typography.bodySmall)
        }
    }
}
