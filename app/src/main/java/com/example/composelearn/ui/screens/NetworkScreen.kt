package com.example.composelearn.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composelearn.viewmodel.NetworkViewModel
import com.example.composelearn.viewmodel.NetworkViewModel.UiState

/**
 * 网络请求演示页面
 *
 * 【核心概念】Retrofit + ViewModel + Compose 的完整数据流
 *
 * 1. 数据层: Retrofit ApiService 定义接口 → RetrofitClient 创建实例
 * 2. ViewModel: viewModelScope.launch 发起请求 → UiState 管理状态
 * 3. UI 层: collectAsStateWithLifecycle 收集 → when(state) 处理三种状态
 *
 * 【UiState 模式】
 * sealed interface UiState {
 *   Loading → 显示加载指示器
 *   Success → 显示数据
 *   Error   → 显示错误信息 + 重试按钮
 * }
 * 这保证了 UI 状态的穷尽性（exhaustive），不会遗漏任何情况
 */
@Composable
fun NetworkScreen() {
    val viewModel: NetworkViewModel = viewModel()
    val usersState by viewModel.usersState.collectAsStateWithLifecycle()
    val postsState by viewModel.postsState.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("用户列表") },
                icon = { Icon(Icons.Default.People, null) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = {
                    selectedTab = 1
                    // 切换到 Posts tab 时才加载
                    if (postsState is UiState.Loading) viewModel.loadPosts()
                },
                text = { Text("文章列表") },
                icon = { Icon(Icons.AutoMirrored.Filled.Article, null) }
            )
        }

        when (selectedTab) {
            0 -> UsersTab(
                state = usersState,
                onRetry = { viewModel.loadUsers() }
            )
            1 -> PostsTab(
                state = postsState,
                onRetry = { viewModel.loadPosts() }
            )
        }
    }
}

/** 用户列表 Tab */
@Composable
private fun UsersTab(
    state: UiState<List<com.example.composelearn.data.model.User>>,
    onRetry: () -> Unit
) {
    // when 穷尽处理所有 UiState 分支
    when (state) {
        is UiState.Loading -> LoadingContent()
        is UiState.Error -> ErrorContent(message = state.message, onRetry = onRetry)
        is UiState.Success -> {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        "成功加载 ${state.data.size} 个用户",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                items(state.data, key = { it.id }) { user ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(user.name, style = MaterialTheme.typography.titleMedium)
                            Text(
                                "@${user.username}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Email, null, modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(user.email, style = MaterialTheme.typography.bodySmall)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Phone, null, modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(user.phone, style = MaterialTheme.typography.bodySmall)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Language, null, modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(user.website, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

/** 文章列表 Tab */
@Composable
private fun PostsTab(
    state: UiState<List<com.example.composelearn.data.model.Post>>,
    onRetry: () -> Unit
) {
    when (state) {
        is UiState.Loading -> LoadingContent()
        is UiState.Error -> ErrorContent(message = state.message, onRetry = onRetry)
        is UiState.Success -> {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        "成功加载 ${state.data.size} 篇文章",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                items(state.data, key = { it.id }) { post ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "#${post.id} · userId: ${post.userId}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                post.title,
                                style = MaterialTheme.typography.titleSmall,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                post.body,
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

/** 加载中占位 */
@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("加载中...")
        }
    }
}

/** 错误状态 + 重试按钮 */
@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("加载失败", style = MaterialTheme.typography.titleMedium)
            Text(message, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Icon(Icons.Default.Refresh, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("重试")
            }
        }
    }
}
