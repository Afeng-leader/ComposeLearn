package com.example.composelearn.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.composelearn.data.local.AppDatabase
import com.example.composelearn.data.local.PreferencesManager
import com.example.composelearn.data.model.TodoItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Todo ViewModel - 结合 Room + DataStore 的完整 CRUD 示例
 *
 * 【关键概念】AndroidViewModel
 * - 继承 AndroidViewModel 而非 ViewModel，可以获取 Application Context
 * - 需要 Context 来初始化 Room Database 和 DataStore
 *
 * 【数据流架构】
 * Room DB → Flow<List<TodoItem>> → ViewModel(StateFlow) → Compose(collectAsState) → UI
 * 用户操作 → Compose Event → ViewModel → Room DAO (suspend) → DB 更新 → Flow 自动通知
 *
 * 这就是 Compose 推荐的 "单向数据流" (Unidirectional Data Flow):
 * State 从 ViewModel 流向 UI，Event 从 UI 流向 ViewModel
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TodoViewModel(application: Application) : AndroidViewModel(application) {

    private val todoDao = AppDatabase.getDatabase(application).todoDao()
    val preferencesManager = PreferencesManager(application)

    /** 过滤模式 */
    enum class FilterMode { ALL, ACTIVE, COMPLETED }

    private val _filterMode = MutableStateFlow(FilterMode.ALL)
    val filterMode: StateFlow<FilterMode> = _filterMode.asStateFlow()

    /**
     * 根据过滤模式切换数据源
     * flatMapLatest: 当 _filterMode 变化时，自动切换到对应的 Flow
     */
    val todos: StateFlow<List<TodoItem>> = _filterMode.flatMapLatest { mode ->
        when (mode) {
            FilterMode.ALL -> todoDao.getAllTodos()
            FilterMode.ACTIVE -> todoDao.getActiveTodos()
            FilterMode.COMPLETED -> todoDao.getCompletedTodos()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setFilter(mode: FilterMode) {
        _filterMode.value = mode
    }

    fun addTodo(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            todoDao.insertTodo(TodoItem(title = title.trim()))
        }
    }

    fun toggleTodo(todo: TodoItem) {
        viewModelScope.launch {
            todoDao.updateTodo(todo.copy(isCompleted = !todo.isCompleted))
        }
    }

    fun deleteTodo(todo: TodoItem) {
        viewModelScope.launch {
            todoDao.deleteTodo(todo)
        }
    }

    fun deleteCompleted() {
        viewModelScope.launch {
            todoDao.deleteCompleted()
        }
    }

    fun saveUsername(name: String) {
        viewModelScope.launch {
            preferencesManager.saveUsername(name)
        }
    }
}
