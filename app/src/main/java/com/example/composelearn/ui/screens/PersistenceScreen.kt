package com.example.composelearn.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composelearn.data.model.TodoItem
import com.example.composelearn.viewmodel.TodoViewModel

/**
 * 数据持久化演示页面 - Room + DataStore
 *
 * 【核心概念】本地数据持久化
 *
 * 1. Room (SQLite ORM):
 *    - Entity: 数据库表结构 (@Entity)
 *    - DAO: 数据访问接口 (@Dao, @Query, @Insert, @Update, @Delete)
 *    - Database: 数据库定义 (@Database)
 *    - Flow<List<T>>: DAO 返回 Flow，数据变化时自动通知 UI
 *
 * 2. DataStore (替代 SharedPreferences):
 *    - 类型安全的键 (stringPreferencesKey, booleanPreferencesKey 等)
 *    - 基于 Flow 的异步读取
 *    - 事务性写入 (edit {})
 *
 * 【数据流】
 * Room DB → Flow → ViewModel(StateFlow) → collectAsState → UI
 * 用户操作 → ViewModel → DAO(suspend) → DB 更新 → Flow 自动触发 UI 刷新
 */
@Composable
fun PersistenceScreen() {
    val viewModel: TodoViewModel = viewModel()
    val todos by viewModel.todos.collectAsStateWithLifecycle()
    val filterMode by viewModel.filterMode.collectAsStateWithLifecycle()
    val username by viewModel.preferencesManager.username.collectAsStateWithLifecycle(initialValue = "未设置")

    var selectedTab by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 },
                text = { Text("Room Todo") }, icon = { Icon(Icons.Default.Checklist, null) })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 },
                text = { Text("DataStore") }, icon = { Icon(Icons.Default.Settings, null) })
        }

        when (selectedTab) {
            0 -> TodoTab(
                todos = todos,
                filterMode = filterMode,
                onFilterChange = { viewModel.setFilter(it) },
                onAdd = { viewModel.addTodo(it) },
                onToggle = { viewModel.toggleTodo(it) },
                onDelete = { viewModel.deleteTodo(it) },
                onDeleteCompleted = { viewModel.deleteCompleted() }
            )
            1 -> DataStoreTab(
                username = username,
                onSaveUsername = { viewModel.saveUsername(it) }
            )
        }
    }
}

/** Room Todo CRUD Tab */
@Composable
private fun TodoTab(
    todos: List<TodoItem>,
    filterMode: TodoViewModel.FilterMode,
    onFilterChange: (TodoViewModel.FilterMode) -> Unit,
    onAdd: (String) -> Unit,
    onToggle: (TodoItem) -> Unit,
    onDelete: (TodoItem) -> Unit,
    onDeleteCompleted: () -> Unit
) {
    var newTodoText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        // 输入区
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newTodoText,
                onValueChange = { newTodoText = it },
                label = { Text("新建待办") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            FilledIconButton(
                onClick = {
                    onAdd(newTodoText)
                    newTodoText = ""
                },
                enabled = newTodoText.isNotBlank()
            ) {
                Icon(Icons.Default.Add, "添加")
            }
        }

        // 过滤器
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = filterMode == TodoViewModel.FilterMode.ALL,
                onClick = { onFilterChange(TodoViewModel.FilterMode.ALL) },
                label = { Text("全部") }
            )
            FilterChip(
                selected = filterMode == TodoViewModel.FilterMode.ACTIVE,
                onClick = { onFilterChange(TodoViewModel.FilterMode.ACTIVE) },
                label = { Text("未完成") }
            )
            FilterChip(
                selected = filterMode == TodoViewModel.FilterMode.COMPLETED,
                onClick = { onFilterChange(TodoViewModel.FilterMode.COMPLETED) },
                label = { Text("已完成") }
            )
            Spacer(modifier = Modifier.weight(1f))
            TextButton(onClick = onDeleteCompleted) {
                Text("清除已完成", style = MaterialTheme.typography.labelSmall)
            }
        }

        // 列表
        if (todos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Text(
                        "暂无待办事项\n添加一个试试吧!",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(todos, key = { it.id }) { todo ->
                    TodoItemCard(
                        todo = todo,
                        onToggle = { onToggle(todo) },
                        onDelete = { onDelete(todo) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TodoItemCard(
    todo: TodoItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = todo.isCompleted, onCheckedChange = { onToggle() })
            Text(
                text = todo.title,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (todo.isCompleted) TextDecoration.LineThrough else null,
                color = if (todo.isCompleted)
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                else MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "删除",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/** DataStore 偏好设置 Tab */
@Composable
private fun DataStoreTab(
    username: String,
    onSaveUsername: (String) -> Unit
) {
    var inputName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionTitle("DataStore 偏好设置")
        Text("DataStore 是 SharedPreferences 的替代方案，基于 Flow 的异步读写")

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("当前保存的用户名", style = MaterialTheme.typography.labelMedium)
                Text(
                    username,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        OutlinedTextField(
            value = inputName,
            onValueChange = { inputName = it },
            label = { Text("输入新用户名") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                onSaveUsername(inputName)
                inputName = ""
            },
            enabled = inputName.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Save, null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("保存到 DataStore")
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("DataStore vs SharedPreferences", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))
                CompareRow("API 模式", "异步 (Flow)", "同步 (可能阻塞)")
                CompareRow("类型安全", "是 ✓", "否 ❌")
                CompareRow("事务写入", "是 ✓", "否 (apply异步)")
                CompareRow("错误处理", "Flow catch", "无")
                CompareRow("推荐使用", "是 ✓", "已弃用")
            }
        }
    }
}

@Composable
private fun CompareRow(feature: String, dataStore: String, sp: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(feature, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
        Text(dataStore, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
        Text(sp, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
    }
}
