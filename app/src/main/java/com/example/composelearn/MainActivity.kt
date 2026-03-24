package com.example.composelearn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.composelearn.navigation.*
import com.example.composelearn.ui.theme.ComposeLearnTheme

/**
 * 应用唯一的 Activity
 *
 * 【关键概念】Single Activity + Compose Navigation
 * - 传统 Android 用多个 Activity 管理页面
 * - Compose 推荐单 Activity 架构，所有页面都是 Composable，通过 Navigation 切换
 * - setContent {} 是 Activity 与 Compose 的桥梁
 * - enableEdgeToEdge() 启用全面屏（内容延伸到状态栏和导航栏下方）
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeLearnTheme {
                ComposeLearnApp()
            }
        }
    }
}

/**
 * 应用根 Composable
 *
 * 【关键概念】rememberNavController / Scaffold
 * - rememberNavController() 创建并记忆 NavController 实例，生命周期跟随 Composition
 * - currentBackStackEntryAsState() 将当前路由转换为 State，路由变化时自动 recompose
 * - Scaffold 提供 Material Design 的页面骨架: topBar / bottomBar / floatingActionButton / content
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeLearnApp() {
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // 根据当前 route 查找对应的 Screen 信息
    val allScreens = basicScreens + intermediateScreens + advancedScreens + listOf(Screen.Home)
    val currentScreen = allScreens.find { it.route == currentRoute } ?: Screen.Home
    val isHome = currentRoute == Screen.Home.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(currentScreen.title) },
                navigationIcon = {
                    // 非首页时显示返回按钮
                    if (!isHome) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "返回"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        // innerPadding 包含 topBar / bottomBar 的高度，避免内容被遮挡
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AppNavGraph(navController = navController)
        }
    }
}
