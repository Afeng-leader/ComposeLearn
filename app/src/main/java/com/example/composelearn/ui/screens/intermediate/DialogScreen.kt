package com.example.composelearn.ui.screens.intermediate

import com.example.composelearn.ui.components.SectionTitle
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * 对话框演示页面
 *
 * 【核心概念】
 * - AlertDialog: Material3 标准对话框
 * - ModalBottomSheet: 底部弹出的模态面板
 * - Snackbar: 底部消息提示条
 *
 * 【对话框 vs 传统 Android】
 * - 传统: AlertDialog.Builder + FragmentDialog，需要处理生命周期
 * - Compose: 对话框也是 Composable，通过 Boolean State 控制显示/隐藏
 *   show/dismiss 只是修改 State 值，Compose 自动处理显示和回收
 */
@Composable
fun DialogScreen() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // 各种对话框的显示状态
    var showBasicDialog by remember { mutableStateOf(false) }
    var showIconDialog by remember { mutableStateOf(false) }
    var showCustomDialog by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var showInputDialog by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ===== 1. AlertDialog =====
            SectionTitle("1. AlertDialog - 标准对话框")
            Text("通过 Boolean State 控制显示/隐藏，无需 DialogFragment")
            Button(
                onClick = { showBasicDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Info, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("基础对话框")
            }

            // ===== 2. 带图标的对话框 =====
            Button(
                onClick = { showIconDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Warning, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("带图标的对话框")
            }

            // ===== 3. 输入对话框 =====
            SectionTitle("2. 自定义内容对话框")
            Text("AlertDialog 的 text 参数接受任意 Composable，可以放入输入框等")
            Button(
                onClick = { showInputDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("输入对话框")
            }

            // ===== 4. 完全自定义对话框 =====
            Button(
                onClick = { showCustomDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.DesignServices, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("完全自定义对话框")
            }

            // ===== 5. BottomSheet =====
            SectionTitle("3. ModalBottomSheet - 底部弹出面板")
            Text("从屏幕底部滑出的面板，适合展示操作菜单或详情")
            Button(
                onClick = { showBottomSheet = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.ExpandLess, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("底部面板")
            }

            // ===== 6. Snackbar =====
            SectionTitle("4. Snackbar - 消息提示条")
            Text("底部临时提示消息，可带操作按钮（如撤销）")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar("这是一条 Snackbar 消息")
                    }
                }) { Text("简单提示") }

                OutlinedButton(onClick = {
                    scope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = "文件已删除",
                            actionLabel = "撤销",
                            duration = SnackbarDuration.Long
                        )
                        // SnackbarResult 可以判断用户是否点击了操作按钮
                        if (result == SnackbarResult.ActionPerformed) {
                            snackbarHostState.showSnackbar("已撤销删除")
                        }
                    }
                }) { Text("带撤销") }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // ===== 对话框实现（声明在 Column 外部） =====

    // 基础 AlertDialog
    if (showBasicDialog) {
        AlertDialog(
            onDismissRequest = { showBasicDialog = false },
            title = { Text("标题") },
            text = { Text("这是一个基础的 AlertDialog，包含标题、内容和按钮。") },
            confirmButton = {
                TextButton(onClick = { showBasicDialog = false }) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBasicDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    // 带图标的 AlertDialog
    if (showIconDialog) {
        AlertDialog(
            onDismissRequest = { showIconDialog = false },
            icon = {
                Icon(Icons.Default.Warning, contentDescription = null,
                    tint = MaterialTheme.colorScheme.error)
            },
            title = { Text("警告", textAlign = TextAlign.Center) },
            text = {
                Text("确定要执行此操作吗？此操作不可撤销。", textAlign = TextAlign.Center)
            },
            confirmButton = {
                Button(
                    onClick = { showIconDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) { Text("确认删除") }
            },
            dismissButton = {
                OutlinedButton(onClick = { showIconDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    // 输入对话框
    if (showInputDialog) {
        var inputText by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showInputDialog = false },
            title = { Text("输入名称") },
            text = {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = { Text("名称") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showInputDialog = false
                        scope.launch {
                            snackbarHostState.showSnackbar("输入的内容: $inputText")
                        }
                    }
                ) { Text("提交") }
            },
            dismissButton = {
                TextButton(onClick = { showInputDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    // 完全自定义对话框
    if (showCustomDialog) {
        AlertDialog(
            onDismissRequest = { showCustomDialog = false },
            confirmButton = { },
            title = null,
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "操作成功！",
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "你的数据已成功保存",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showCustomDialog = false },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("好的") }
                }
            }
        )
    }

    // ModalBottomSheet
    if (showBottomSheet) {
        @OptIn(ExperimentalMaterial3Api::class)
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false }
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("选择操作", style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp))

                listOf(
                    Triple(Icons.Default.Share, "分享", "分享给朋友"),
                    Triple(Icons.Default.ContentCopy, "复制链接", "复制到剪贴板"),
                    Triple(Icons.Default.Edit, "编辑", "编辑内容"),
                    Triple(Icons.Default.Delete, "删除", "永久删除"),
                ).forEach { (icon, title, subtitle) ->
                    ListItem(
                        headlineContent = { Text(title) },
                        supportingContent = { Text(subtitle) },
                        leadingContent = { Icon(icon, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
