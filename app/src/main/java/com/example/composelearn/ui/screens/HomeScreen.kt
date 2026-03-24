package com.example.composelearn.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.composelearn.navigation.*

/**
 * 首页 - 学习导航目录
 *
 * 【学习要点】
 * - LazyColumn: 高性能可滚动列表，只渲染可见区域的 item（类似 RecyclerView）
 * - Card: Material Design 卡片容器，自带阴影和圆角
 * - Modifier.clickable: 为任意 Composable 添加点击事件
 * - 函数参数传递 lambda: onNavigate 是回调，由父组件提供具体实现
 */
@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 分组标题 - 基础篇
        item { SectionHeader("🟢 基础篇", "Compose 核心概念入门") }
        items(basicScreens) { screen ->
            NavigationCard(screen = screen, icon = getIconForScreen(screen), onClick = { onNavigate(screen.route) })
        }

        // 分组标题 - 中级篇
        item { Spacer(modifier = Modifier.height(8.dp)) }
        item { SectionHeader("🟡 中级篇", "常用组件与交互模式") }
        items(intermediateScreens) { screen ->
            NavigationCard(screen = screen, icon = getIconForScreen(screen), onClick = { onNavigate(screen.route) })
        }

        // 分组标题 - 高级篇
        item { Spacer(modifier = Modifier.height(8.dp)) }
        item { SectionHeader("🔴 高级篇", "架构模式与数据层集成") }
        items(advancedScreens) { screen ->
            NavigationCard(screen = screen, icon = getIconForScreen(screen), onClick = { onNavigate(screen.route) })
        }
    }
}

/** 分组标题组件 */
@Composable
private fun SectionHeader(title: String, description: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = title, style = MaterialTheme.typography.headlineSmall)
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** 导航卡片组件 - 每个学习主题一张卡片 */
@Composable
private fun NavigationCard(screen: Screen, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = screen.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = screen.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "进入",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** 根据页面类型返回对应的 Material Icon */
private fun getIconForScreen(screen: Screen): ImageVector = when (screen) {
    Screen.BasicLayout -> Icons.Default.GridView
    Screen.TextDemo -> Icons.Default.TextFields
    Screen.ButtonDemo -> Icons.Default.SmartButton
    Screen.ImageDemo -> Icons.Default.Image
    Screen.StateDemo -> Icons.Default.Cached
    Screen.ListDemo -> Icons.AutoMirrored.Filled.ViewList
    Screen.FormDemo -> Icons.Default.EditNote
    Screen.ScaffoldDemo -> Icons.Default.WebAsset
    Screen.AnimationDemo -> Icons.Default.Animation
    Screen.ThemeDemo -> Icons.Default.Palette
    Screen.DialogDemo -> Icons.Default.ChatBubble
    Screen.ViewModelDemo -> Icons.Default.AccountTree
    Screen.NetworkDemo -> Icons.Default.Cloud
    Screen.PersistenceDemo -> Icons.Default.Storage
    Screen.SideEffectDemo -> Icons.Default.Bolt
    Screen.GestureDemo -> Icons.Default.TouchApp
    Screen.PagerCanvasDemo -> Icons.Default.ViewCarousel
    else -> Icons.Default.Circle
}
