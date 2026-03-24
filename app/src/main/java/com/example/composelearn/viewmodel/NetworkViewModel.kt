package com.example.composelearn.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composelearn.data.model.Post
import com.example.composelearn.data.model.User
import com.example.composelearn.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 网络请求 ViewModel
 *
 * 【关键概念】
 * - viewModelScope: ViewModel 提供的 CoroutineScope
 *   当 ViewModel 被清除时，自动取消所有运行中的协程（防止内存泄漏）
 *
 * - UiState sealed interface: 用密封接口表示 UI 的所有可能状态
 *   Loading / Success / Error，Compose 通过 when 分支处理每种状态
 *
 * 【数据流】
 * 用户操作 → ViewModel.loadXxx() → viewModelScope.launch → Retrofit 请求
 *   → 更新 StateFlow(UiState) → Compose collectAsState → UI 响应式更新
 */
class NetworkViewModel : ViewModel() {

    /** UI 状态的密封接口 - 表示加载/成功/失败三种状态 */
    sealed interface UiState<out T> {
        data object Loading : UiState<Nothing>
        data class Success<T>(val data: T) : UiState<T>
        data class Error(val message: String) : UiState<Nothing>
    }

    private val _usersState = MutableStateFlow<UiState<List<User>>>(UiState.Loading)
    val usersState: StateFlow<UiState<List<User>>> = _usersState.asStateFlow()

    private val _postsState = MutableStateFlow<UiState<List<Post>>>(UiState.Loading)
    val postsState: StateFlow<UiState<List<Post>>> = _postsState.asStateFlow()

    init {
        loadUsers()
    }

    /** 加载用户列表 */
    fun loadUsers() {
        viewModelScope.launch {
            _usersState.value = UiState.Loading
            try {
                val users = RetrofitClient.apiService.getUsers()
                _usersState.value = UiState.Success(users)
            } catch (e: Exception) {
                _usersState.value = UiState.Error(e.message ?: "未知错误")
            }
        }
    }

    /** 加载文章列表 */
    fun loadPosts() {
        viewModelScope.launch {
            _postsState.value = UiState.Loading
            try {
                val posts = RetrofitClient.apiService.getPosts()
                _postsState.value = UiState.Success(posts)
            } catch (e: Exception) {
                _postsState.value = UiState.Error(e.message ?: "未知错误")
            }
        }
    }
}
