# ComposeLearn - Jetpack Compose 新手学习项目

一个面向 Android 传统开发者的 Jetpack Compose 学习项目，通过 **18 个独立演示页面** 从基础到进阶覆盖 Compose 核心知识点。

## 项目结构

```
ComposeLearn/
├── app/src/main/java/com/example/composelearn/
│   ├── MainActivity.kt                 # 应用入口，Scaffold + NavHost
│   ├── navigation/
│   │   └── AppNavigation.kt            # 路由定义 + 导航图
│   ├── ui/
│   │   ├── theme/                      # 主题（颜色/排版/Material3）
│   │   │   ├── Color.kt
│   │   │   ├── Theme.kt
│   │   │   └── Type.kt
│   │   ├── components/                 # 公共 UI 组件
│   │   │   └── SectionTitle.kt         # 统一章节标题样式
│   │   └── screens/                    # 18 个演示页面（按难度分组）
│   │       ├── HomeScreen.kt           # 首页导航目录
│   │       ├── basic/                  # 🟢 基础篇
│   │       │   ├── BasicLayoutScreen.kt    # 基础布局
│   │       │   ├── TextScreen.kt           # 文本样式
│   │       │   ├── ButtonScreen.kt         # 按钮类型
│   │       │   ├── ImageScreen.kt          # 图片展示
│   │       │   └── StateScreen.kt          # 状态管理 ⭐
│   │       ├── intermediate/           # 🟡 中级篇
│   │       │   ├── ListScreen.kt           # 列表与网格（A-Z 索引栏）
│   │       │   ├── FormScreen.kt           # 表单输入
│   │       │   ├── ScaffoldScreen.kt       # Scaffold + Drawer 脚手架
│   │       │   ├── AnimationScreen.kt      # 动画效果
│   │       │   ├── ThemeScreen.kt          # 主题定制
│   │       │   └── DialogScreen.kt         # 对话框
│   │       └── advanced/               # 🔴 高级篇
│   │           ├── ViewModelScreen.kt      # ViewModel ⭐
│   │           ├── NetworkScreen.kt        # 网络请求 ⭐
│   │           ├── PersistenceScreen.kt    # 数据持久化 ⭐
│   │           ├── SideEffectScreen.kt     # 副作用 API ⭐
│   │           ├── GestureScreen.kt        # 手势交互 ⭐
│   │           └── PagerCanvasScreen.kt    # 翻页与自定义绘制 ⭐
│   ├── viewmodel/
│   │   ├── CounterViewModel.kt         # 计数器 VM（StateFlow 入门）
│   │   ├── NetworkViewModel.kt         # 网络请求 VM（UiState 模式）
│   │   └── TodoViewModel.kt            # Todo VM（Room + DataStore）
│   └── data/
│       ├── model/Models.kt             # 数据模型（User/Post/TodoItem）
│       ├── remote/
│       │   ├── ApiService.kt           # Retrofit API 接口
│       │   └── RetrofitClient.kt       # Retrofit 客户端单例
│       └── local/
│           ├── AppDatabase.kt          # Room 数据库定义
│           ├── TodoDao.kt              # Room DAO
│           └── PreferencesManager.kt   # DataStore 偏好管理
└── build.gradle.kts                    # 依赖配置
```

## 学习路线

### 🟢 基础篇（从这里开始）

| # | 页面 | 核心知识点 | 对应传统 Android |
|---|------|-----------|-----------------|
| 1 | **基础布局** | Column, Row, Box, Modifier, weight, Arrangement | LinearLayout, FrameLayout |
| 2 | **文本样式** | Text, TextStyle, AnnotatedString, Typography | TextView, SpannableString |
| 3 | **按钮类型** | Button, ElevatedButton, OutlinedButton, FAB, IconButton | Button, ImageButton, FAB |
| 4 | **图片展示** | Image, Icon, clip, ContentScale, ColorFilter | ImageView, VectorDrawable |
| 5 | **状态管理** ⭐ | remember, mutableStateOf, rememberSaveable, State Hoisting, derivedStateOf | 无直接对应（命令式 vs 声明式的核心差异） |

### 🟡 中级篇

| # | 页面 | 核心知识点 | 对应传统 Android |
|---|------|-----------|-----------------|
| 6 | **列表与网格** | LazyColumn, LazyRow, LazyVerticalGrid, stickyHeader, items, A-Z 字母索引栏(awaitEachGesture) | RecyclerView, GridLayoutManager, SideIndex |
| 7 | **表单输入** | TextField, Checkbox, Switch, RadioButton, Slider, DropdownMenu | EditText, CheckBox, Switch, SeekBar |
| 8 | **Scaffold 脚手架** | Scaffold, TopAppBar, BottomBar, SnackbarHost, FAB, ModalNavigationDrawer | CoordinatorLayout, DrawerLayout, BottomNavigationView |
| 9 | **动画效果** | animateXAsState, AnimatedVisibility, Crossfade, InfiniteTransition, updateTransition | ObjectAnimator, TransitionManager |
| 10 | **主题定制** | MaterialTheme, ColorScheme, Typography, Dynamic Color, Dark Mode | styles.xml, themes.xml |
| 11 | **对话框** | AlertDialog, ModalBottomSheet, Snackbar | AlertDialog.Builder, BottomSheetDialogFragment |

### 🔴 高级篇

| # | 页面 | 核心知识点 | 对应传统 Android |
|---|------|-----------|-----------------|
| 12 | **ViewModel** ⭐ | ViewModel, StateFlow, collectAsStateWithLifecycle, viewModel() | ViewModel + LiveData + Observer |
| 13 | **网络请求** ⭐ | Retrofit, UiState sealed interface, viewModelScope, Loading/Success/Error | Retrofit + Callback/RxJava |
| 14 | **数据持久化** ⭐ | Room(Entity/DAO/Database), DataStore, Flow → UI 实时同步 | SQLiteOpenHelper, SharedPreferences |
| 15 | **副作用 API** ⭐ | LaunchedEffect, DisposableEffect, SideEffect, rememberUpdatedState, snapshotFlow | 无直接对应（Compose 特有的生命周期管理机制） |
| 16 | **手势交互** ⭐ | pointerInput, detectTapGestures, detectDragGestures, detectTransformGestures, draggable | OnTouchListener, GestureDetector, ScaleGestureDetector |
| 17 | **翻页与绘制** ⭐ | HorizontalPager, TabRow, Canvas, DrawScope, drawText, Path, rotate | ViewPager2, TabLayout, View.onDraw(Canvas) |

## 核心概念速查

### 声明式 UI vs 命令式 UI

```kotlin
// ❌ 命令式（传统 Android）
textView.text = "Hello"
button.setOnClickListener { textView.text = "Clicked" }

// ✅ 声明式（Compose）
var text by remember { mutableStateOf("Hello") }
Text(text)
Button(onClick = { text = "Clicked" }) { Text("Click") }
```

### 状态驱动 UI 的三步骤

```kotlin
// 1. 定义状态
var count by remember { mutableIntStateOf(0) }

// 2. UI 读取状态（自动订阅）
Text("Count: $count")

// 3. 事件修改状态 → 自动触发 recomposition → UI 更新
Button(onClick = { count++ }) { Text("+1") }
```

### ViewModel + Compose 数据流

```
┌─────────────┐    StateFlow     ┌──────────────┐    State    ┌──────┐
│  ViewModel  │ ──────────────→  │ collectAs... │ ─────────→  │  UI  │
│  (数据层)    │                  │  State()     │             │      │
└─────────────┘                  └──────────────┘             └──┬───┘
       ↑                                                         │
       └──────────── onClick / onValueChange ────────────────────┘
                     (Event 事件回调)
```

## 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Kotlin | 2.1.0 | 编程语言 |
| Compose BOM | 2024.12.01 | UI 框架 |
| Material3 | BOM 管理 | UI 组件库 |
| Navigation Compose | 2.8.5 | 页面导航 |
| ViewModel | 2.8.7 | 状态管理 |
| Retrofit | 2.11.0 | 网络请求 |
| Room | 2.6.1 | 本地数据库 |
| DataStore | 1.1.1 | 偏好存储 |
| Coil | 2.7.0 | 图片加载 |
| Coroutines | 1.9.0 | 异步编程 |

## 使用方式

1. 用 Android Studio (Ladybug 或更新版本) 打开项目
2. Sync Gradle 等待依赖下载完成
3. 连接设备或启动模拟器，运行项目
4. 从首页导航目录进入各个演示页面学习

## 建议学习顺序

1. 先理解 **状态管理** (StateScreen) —— 这是 Compose 的核心
2. 再学习 **基础布局** 和 **文本/按钮** 等 UI 组件
3. 然后进入 **列表** 和 **表单**，掌握常用交互模式
4. 接着学习 **动画** 和 **主题**，提升 UI 表现力
5. 学习 **ViewModel + 网络 + 持久化**，掌握完整架构
6. 深入 **副作用 API**，理解 Compose 的生命周期与副作用管理
7. 学习 **手势交互** 和 **Canvas 绘制**，掌握自定义交互与绘制能力

每个页面文件的顶部都有详细的中英文混合注释，解释核心概念和与传统 Android 的对比。
