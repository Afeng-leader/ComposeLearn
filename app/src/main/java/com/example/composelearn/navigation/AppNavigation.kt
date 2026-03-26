package com.example.composelearn.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.composelearn.ui.screens.HomeScreen
import com.example.composelearn.ui.screens.basic.*
import com.example.composelearn.ui.screens.intermediate.*
import com.example.composelearn.ui.screens.advanced.*

/**
 * 定义应用的所有路由（Route）
 *
 * 【关键概念】sealed class 路由定义
 * - 每个页面对应一个 route 字符串，用于 NavController 导航
 * - sealed class 确保路由枚举在编译时完整、类型安全
 * - title 用于在 HomeScreen 和 TopBar 中显示中文标题
 */
sealed class Screen(val route: String, val title: String, val subtitle: String) {
    data object Home : Screen("home", "首页", "Compose 学习导航")

    // ===== 基础篇 =====
    data object BasicLayout : Screen("basic_layout", "基础布局", "Column / Row / Box / Modifier")
    data object TextDemo : Screen("text_demo", "文本样式", "Text / AnnotatedString / Selection")
    data object ButtonDemo : Screen("button_demo", "按钮类型", "Button / IconButton / FAB")
    data object ImageDemo : Screen("image_demo", "图片展示", "Image / Icon / AsyncImage")
    data object StateDemo : Screen("state_demo", "状态管理", "remember / mutableStateOf / 状态提升")

    // ===== 中级篇 =====
    data object ListDemo : Screen("list_demo", "列表与网格", "LazyColumn / LazyRow / LazyGrid")
    data object FormDemo : Screen("form_demo", "表单输入", "TextField / Checkbox / Switch / Slider")
    data object ScaffoldDemo : Screen("scaffold_demo", "Scaffold 脚手架", "TopBar / BottomBar / Drawer / FAB")
    data object AnimationDemo : Screen("animation_demo", "动画效果", "AnimatedVisibility / animateXAsState")
    data object ThemeDemo : Screen("theme_demo", "主题定制", "MaterialTheme / Dark Mode / 动态颜色")
    data object DialogDemo : Screen("dialog_demo", "对话框", "AlertDialog / BottomSheet / Snackbar")

    // ===== 高级篇 =====
    data object ViewModelDemo : Screen("viewmodel_demo", "ViewModel", "ViewModel + StateFlow + Compose")
    data object NetworkDemo : Screen("network_demo", "网络请求", "Retrofit + ViewModel + Compose")
    data object PersistenceDemo : Screen("persistence_demo", "数据持久化", "Room / DataStore / CRUD")
    data object SideEffectDemo : Screen("side_effect_demo", "副作用 API", "LaunchedEffect / DisposableEffect / snapshotFlow")
    data object GestureDemo : Screen("gesture_demo", "手势交互", "Tap / Drag / Transform / Swipe")
    data object PagerCanvasDemo : Screen("pager_canvas_demo", "翻页与绘制", "HorizontalPager / Canvas / 自定义绘制")
}

/** 所有演示页面的有序列表，分为三个级别 */
val basicScreens = listOf(
    Screen.BasicLayout, Screen.TextDemo, Screen.ButtonDemo,
    Screen.ImageDemo, Screen.StateDemo
)
val intermediateScreens = listOf(
    Screen.ListDemo, Screen.FormDemo, Screen.ScaffoldDemo,
    Screen.AnimationDemo, Screen.ThemeDemo, Screen.DialogDemo
)
val advancedScreens = listOf(
    Screen.ViewModelDemo, Screen.NetworkDemo, Screen.PersistenceDemo,
    Screen.SideEffectDemo, Screen.GestureDemo, Screen.PagerCanvasDemo
)

/**
 * 应用主导航图（Navigation Graph）
 *
 * 【关键概念】NavHost + composable
 * - NavHost 是导航容器，管理 back stack 和页面切换动画
 * - composable("route") 将一个 route 映射到一个 Composable 页面
 * - navController 控制页面跳转: navigate() 前进, popBackStack() 后退
 */
@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(onNavigate = { route -> navController.navigate(route) })
        }

        // 基础篇
        composable(Screen.BasicLayout.route) { BasicLayoutScreen() }
        composable(Screen.TextDemo.route) { TextScreen() }
        composable(Screen.ButtonDemo.route) { ButtonScreen() }
        composable(Screen.ImageDemo.route) { ImageScreen() }
        composable(Screen.StateDemo.route) { StateScreen() }

        // 中级篇
        composable(Screen.ListDemo.route) { ListScreen() }
        composable(Screen.FormDemo.route) { FormScreen() }
        composable(Screen.ScaffoldDemo.route) { ScaffoldScreen() }
        composable(Screen.AnimationDemo.route) { AnimationScreen() }
        composable(Screen.ThemeDemo.route) { ThemeScreen() }
        composable(Screen.DialogDemo.route) { DialogScreen() }

        // 高级篇
        composable(Screen.ViewModelDemo.route) { ViewModelScreen() }
        composable(Screen.NetworkDemo.route) { NetworkScreen() }
        composable(Screen.PersistenceDemo.route) { PersistenceScreen() }
        composable(Screen.SideEffectDemo.route) { SideEffectScreen() }
        composable(Screen.GestureDemo.route) { GestureScreen() }
        composable(Screen.PagerCanvasDemo.route) { PagerCanvasScreen() }
    }
}
