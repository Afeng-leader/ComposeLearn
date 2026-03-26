package com.example.composelearn.ui.screens.intermediate

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

/**
 * 列表与网格演示页面
 *
 * 【核心概念】
 * - LazyColumn: 垂直懒加载列表（等价于 RecyclerView vertical）
 *   只渲染当前屏幕可见的 item，性能极高
 * - LazyRow: 水平懒加载列表
 * - LazyVerticalGrid: 网格布局
 * - items(): DSL 方法，从集合生成列表项
 * - stickyHeader: 吸顶分组标题
 *
 * 【与 Column 的区别】
 * - Column: 一次性渲染所有子元素（少量固定内容用）
 * - LazyColumn: 按需渲染可见区域（大量数据必须用）
 */
@Composable
fun ListScreen() {
    // 使用 Tab 切换不同的列表类型
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("LazyColumn", "LazyRow", "Grid", "分组列表")

    Column(modifier = Modifier.fillMaxSize()) {
        // TabRow: Material Design 的标签栏
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            maxLines = 1
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> LazyColumnDemo()
            1 -> LazyRowDemo()
            2 -> LazyGridDemo()
            3 -> GroupedListDemo()
        }
    }
}

/** LazyColumn 演示 - 垂直懒加载列表 */
@Composable
private fun LazyColumnDemo() {
    // 生成模拟数据
    val items = remember { (1..50).map { "列表项 #$it" } }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),          // 列表整体的内边距
        verticalArrangement = Arrangement.spacedBy(8.dp) // 每个 item 之间的间距
    ) {
        // item {} 添加单个元素（常用于头部/尾部）
        item {
            Text(
                "LazyColumn 共 ${items.size} 项",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // items() 从集合批量生成列表项
        // key 参数帮助 Compose 精确识别每项，提升 recomposition 性能
        items(items = items, key = { it }) { item ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.AutoMirrored.Filled.Article, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(item)
                }
            }
        }

        // itemsIndexed 提供 index
        item {
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text("↑ 以上使用 items()  |  ↓ 以下使用 itemsIndexed()")
        }
        // 遍历列表，只取前 5 条数据
        itemsIndexed(items.take(5)) { index, item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text(
                    "index=$index, value=$item",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

/** LazyRow 演示 - 水平懒加载列表 */
@Composable
private fun LazyRowDemo() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("水平卡片轮播", style = MaterialTheme.typography.titleMedium)
        Text("LazyRow 的用法和 LazyColumn 完全一致，只是方向变成水平")

        // 横向滚动的卡片列表
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(20) { index ->
                Card(
                    modifier = Modifier.size(width = 160.dp, height = 200.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(
                            red = (50 + index * 10) / 255f,
                            green = (100 + index * 8) / 255f,
                            blue = (200 - index * 5) / 255f
                        )
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("卡片 ${index + 1}", color = Color.White)
                    }
                }
            }
        }

        HorizontalDivider()
        Text("标签横向滚动", style = MaterialTheme.typography.titleMedium)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(15) { index ->
                AssistChip(
                    onClick = { },
                    label = { Text("标签 ${index + 1}") },
                    leadingIcon = {
                        Icon(Icons.Default.Tag, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                )
            }
        }
    }
}

/** LazyVerticalGrid 演示 - 网格布局 */
@Composable
private fun LazyGridDemo() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            "LazyVerticalGrid 网格布局",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )

        // GridCells.Fixed(n): 固定 n 列
        // GridCells.Adaptive(minSize): 根据可用宽度自适应列数
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 100.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(30) { index ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("项目 ${index + 1}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

/**
 * 分组列表演示 - stickyHeader + 右侧可拖拽字母索引栏 + 中央气泡
 *
 * 实现要点：
 * - LazyListState 控制列表的滚动位置
 * - 字母索引栏使用 pointerInput + detectVerticalDragGestures 实现触摸拖拽：
 *   手指在字母栏上滑动时，根据触摸 Y 坐标实时计算对应字母并跳转
 * - 拖拽期间屏幕中央显示放大字母气泡（AnimatedVisibility + fadeIn/scaleIn）
 * - weight(1f) 让 26 个字母均匀分布在右侧栏的可用高度内
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun GroupedListDemo() {

    val originNames = listOf(
        "Alice", "Bob", "Charlie", "David",
        "Ella", "Frank", "Grace", "Henry",
        "Iris", "Jack", "Katie", "Linda",
        "Mike", "Nancy", "Oscar", "Peter",
        "Queen", "Rose", "Sam", "Tom",
        "Una", "Vivian", "Wendy", "Yoyo", "Zoro", "Doro"
    )

    val groupedData = remember {
        originNames
            .sortedBy { it }
            .groupBy { name -> name.first().uppercase() }
            .toSortedMap()
    }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val letterIndexMap = remember(groupedData) {
        buildMap {
            var index = 1
            groupedData.forEach { (letter, names) ->
                put(letter, index)
                index += 1 + names.size
            }
        }
    }

    val alphabet = ('A'..'Z').toList()

    // 当前触摸/选中的字母（用于高亮 + 气泡显示）
    var activeLetter by remember { mutableStateOf<String?>(null) }
    // 是否正在触摸字母栏
    var isTouching by remember { mutableStateOf(false) }
    // 字母栏的实际像素高度，用于将触摸 Y 坐标映射到字母 index
    var sidebarSize by remember { mutableStateOf(IntSize.Zero) }

    // 根据触摸 Y 坐标计算对应字母，并跳转到列表对应位置
    fun scrollToLetterAtY(y: Float) {
        if (sidebarSize.height <= 0) return
        val clampedY = y.coerceIn(0f, sidebarSize.height.toFloat())
        val letterIndex = ((clampedY / sidebarSize.height) * alphabet.size)
            .toInt()
            .coerceIn(0, alphabet.lastIndex)
        val letter = alphabet[letterIndex].toString()
        activeLetter = letter
        val targetIndex = letterIndexMap[letter]
        if (targetIndex != null) {
            // scrollToItem（非 animate）更适合拖拽场景，响应更即时
            scope.launch { listState.scrollToItem(targetIndex) }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // ===== 左侧：分组列表 =====
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 28.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                Text(
                    "stickyHeader 吸顶分组",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
            groupedData.forEach { (letter, names) ->
                stickyHeader {
                    Text(
                        text = letter,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                items(names) { name ->
                    ListItem(
                        headlineContent = { Text(name) },
                        leadingContent = {
                            Icon(Icons.Default.Circle, contentDescription = null, modifier = Modifier.size(8.dp))
                        }
                    )
                }
            }
        }

        // ===== 右侧：A-Z 字母索引栏（支持触摸拖拽） =====
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(28.dp)
                .padding(vertical = 8.dp)
                .onSizeChanged { sidebarSize = it }
                // awaitEachGesture 比 detectVerticalDragGestures 更底层：
                // 按下瞬间就触发跳转（无 touch slop），同时支持点击和拖拽
                .pointerInput(Unit) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        isTouching = true
                        scrollToLetterAtY(down.position.y)

                        // 持续追踪手指移动，直到所有手指抬起
                        do {
                            val event = awaitPointerEvent()
                            event.changes.forEach { change ->
                                if (change.pressed) {
                                    change.consume()
                                    scrollToLetterAtY(change.position.y)
                                }
                            }
                        } while (event.changes.any { it.pressed })

                        isTouching = false
                        activeLetter = null
                    }
                },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            alphabet.forEach { letter ->
                val letterStr = letter.toString()
                val hasGroup = letterStr in groupedData
                val isActive = activeLetter == letterStr

                Text(
                    text = letterStr,
                    fontSize = 11.sp,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    color = when {
                        isActive -> MaterialTheme.colorScheme.onPrimary
                        hasGroup -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.outlineVariant
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .wrapContentHeight(Alignment.CenterVertically)
                        .then(
                            if (isActive) Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.primary)
                            else Modifier
                        )
                )
            }
        }

        // ===== 中央：字母气泡提示 =====
        AnimatedVisibility(
            visible = isTouching && activeLetter != null,
            modifier = Modifier.align(Alignment.Center),
            enter = fadeIn() + scaleIn(initialScale = 0.6f),
            exit = fadeOut() + scaleOut(targetScale = 0.6f)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = activeLetter ?: "",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}
