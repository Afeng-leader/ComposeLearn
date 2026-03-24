package com.example.composelearn.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * Scaffold 脚手架演示页面
 *
 * 【核心概念】Scaffold + ModalNavigationDrawer
 * - Scaffold 是 Material Design 的页面骨架组件
 * - 提供 topBar / bottomBar / floatingActionButton / snackbarHost 等插槽
 * - 自动处理各组件之间的布局关系（如 FAB 不被 BottomBar 遮挡）
 * - ModalNavigationDrawer 提供侧滑抽屉导航（从屏幕左侧滑出）
 *
 * 【SnackbarHost】
 * - Snackbar 的容器，通过 SnackbarHostState.showSnackbar() 显示
 * - 使用协程调用（因为 showSnackbar 是 suspend 函数）
 * - rememberCoroutineScope() 获取与 Composable 生命周期绑定的 CoroutineScope
 *
 * 【ModalNavigationDrawer】
 * - drawerState: 控制抽屉的打开/关闭状态
 * - drawerContent: 抽屉内部的内容（通常放 ModalDrawerSheet + NavigationDrawerItem）
 * - 通过 drawerState.open() / close() 控制（需要在协程中调用）
 * - 也可以通过手势从左边缘向右滑动打开
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldScreen() {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var selectedNavItem by remember { mutableIntStateOf(0) }
    val navItems = listOf(
        Triple("首页", Icons.Default.Home, "home"),
        Triple("搜索", Icons.Default.Search, "search"),
        Triple("收藏", Icons.Default.Favorite, "favorites"),
        Triple("我的", Icons.Default.Person, "profile"),
    )

    // DrawerState 类似 BottomSheetState，内部用 Animatable 管理滑动位移
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var selectedDrawerItem by remember { mutableIntStateOf(0) }
    val drawerItems = listOf(
        Triple("收件箱", Icons.Default.Inbox, 12),
        Triple("已发送", Icons.AutoMirrored.Filled.Send, 0),
        Triple("草稿箱", Icons.Default.Drafts, 3),
        Triple("垃圾箱", Icons.Default.Delete, 0),
        Triple("设置", Icons.Default.Settings, 0),
    )

    // ModalNavigationDrawer 必须在 Scaffold 外层，因为 Drawer 的遮罩层（scrim）
    // 需要覆盖 TopAppBar 和 BottomBar；如果放在 content 内部，则只遮盖主体区域
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Compose 学习者",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            "compose@learn.dev",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(modifier = Modifier.height(8.dp))

                drawerItems.forEachIndexed { index, (label, icon, badge) ->
                    NavigationDrawerItem(
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) },
                        // badge 使用双层 lambda: 外层 if 决定是否显示，内层 @Composable 渲染内容
                        badge = if (badge > 0) {{ Text("$badge") }} else null,
                        selected = selectedDrawerItem == index,
                        onClick = {
                            selectedDrawerItem = index
                            // 分两个 launch：close() 和 showSnackbar() 都是 suspend 函数，
                            // 并行执行可以让抽屉关闭动画和 Snackbar 显示同时进行
                            scope.launch { drawerState.close() }
                            scope.launch {
                                snackbarHostState.showSnackbar("选择了: $label")
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Scaffold 演示") },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "打开抽屉")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    message = "这是一个 Snackbar 通知",
                                    actionLabel = "撤销",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }) {
                            Icon(Icons.Default.Notifications, contentDescription = "通知")
                        }
                    }
                )
            },

            bottomBar = {
                NavigationBar {
                    navItems.forEachIndexed { index, (label, icon, _) ->
                        NavigationBarItem(
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label) },
                            selected = selectedNavItem == index,
                            onClick = { selectedNavItem = index }
                        )
                    }
                }
            },

            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("FAB 被点击了!")
                        }
                    }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "添加")
                }
            },

            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }

        ) { innerPadding ->
            // innerPadding 包含 topBar 的高度（top）和 bottomBar 的高度（bottom）。
            // 如果不应用此 padding，LazyColumn 的内容会被 TopAppBar/BottomBar 遮盖。
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        "当前标签: ${navItems[selectedNavItem].first}",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }

                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Scaffold 组件说明", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            ScaffoldInfo("topBar", "页面顶部的应用栏，支持标题、导航图标、操作按钮")
                            ScaffoldInfo("bottomBar", "页面底部的导航栏，通常放 NavigationBar")
                            ScaffoldInfo("floatingActionButton", "悬浮操作按钮，默认位于右下角")
                            ScaffoldInfo("snackbarHost", "Snackbar 消息的容器，点击右上角铃铛试试")
                            ScaffoldInfo("drawerContent", "侧滑抽屉导航，点击左上角菜单按钮或从左侧边缘滑入")
                            ScaffoldInfo("content", "主体内容区域，会接收 innerPadding")
                        }
                    }
                }

                // Drawer 说明卡片
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("ModalNavigationDrawer 用法", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "• ModalNavigationDrawer 包裹 Scaffold\n" +
                                "• drawerState 控制开关: open() / close()\n" +
                                "• ModalDrawerSheet 作为抽屉容器\n" +
                                "• NavigationDrawerItem 作为导航项（支持 badge 角标）\n" +
                                "• 支持手势: 从屏幕左边缘向右滑动打开"
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = { scope.launch { drawerState.open() } },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Menu, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("打开抽屉")
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("innerPadding 的作用", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Scaffold 的 content lambda 会传入 PaddingValues 参数，\n" +
                                "包含 topBar 和 bottomBar 的高度。\n\n" +
                                "必须将此 padding 应用到内容区域:\n" +
                                "Modifier.padding(innerPadding)\n\n" +
                                "否则内容会被 TopBar 和 BottomBar 遮挡。"
                            )
                        }
                    }
                }

                items(10) { index ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                navItems[selectedNavItem].second,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("${navItems[selectedNavItem].first} - 内容项 ${index + 1}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScaffoldInfo(name: String, description: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text("• ", color = MaterialTheme.colorScheme.primary)
        Text(name, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        Text(": $description", style = MaterialTheme.typography.bodySmall)
    }
}
