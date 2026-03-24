package com.example.composelearn.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 计数器 ViewModel - 最简单的 ViewModel 示例
 *
 * 【关键概念】ViewModel
 * - ViewModel 的生命周期比 Activity/Composable 更长
 * - 屏幕旋转等配置变更时，ViewModel 实例不会被销毁
 * - 非常适合保存 UI 状态（如计数值、列表数据、加载状态等）
 *
 * 【StateFlow vs LiveData】
 * - LiveData: 传统 Android 的可观察数据，需要 lifecycle-aware
 * - StateFlow: Kotlin 协程提供的状态流，更契合 Compose
 *   在 Compose 中通过 collectAsState() 收集为 State
 *
 * 【数据流方向】
 * ViewModel(StateFlow) → collectAsState() → Composable(State) → UI
 * UI Event → Composable → ViewModel.method() → 更新 StateFlow → 自动刷新 UI
 */
class CounterViewModel : ViewModel() {

    // _count: 内部可变状态，只有 ViewModel 自己可以修改
    private val _count = MutableStateFlow(0)
    // count: 对外暴露只读的 StateFlow
    val count: StateFlow<Int> = _count.asStateFlow()

    // 操作历史
    private val _history = MutableStateFlow<List<String>>(emptyList())
    val history: StateFlow<List<String>> = _history.asStateFlow()

    fun increment() {
        _count.value++
        addHistory("+1 → ${_count.value}")
    }

    fun decrement() {
        _count.value--
        addHistory("-1 → ${_count.value}")
    }

    fun reset() {
        _count.value = 0
        addHistory("重置 → 0")
    }

    fun setCount(value: Int) {
        _count.value = value
        addHistory("设置 → $value")
    }

    private fun addHistory(action: String) {
        _history.value = _history.value + action
    }

    fun clearHistory() {
        _history.value = emptyList()
    }
}
