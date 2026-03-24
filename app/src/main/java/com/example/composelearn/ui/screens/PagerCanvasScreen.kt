package com.example.composelearn.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Pager 翻页 + Canvas 自定义绘制演示页面
 *
 * 【核心概念 1】HorizontalPager / VerticalPager
 * - Compose Foundation 提供的翻页组件（替代传统 ViewPager/ViewPager2）
 * - PagerState 管理当前页码、滑动偏移量
 * - 可与 TabRow 联动，实现 Tab + 滑动切换
 *
 * 【核心概念 2】Canvas 自定义绘制
 * - Canvas composable: Compose 中的自定义绘制入口
 * - DrawScope: 提供 drawCircle / drawLine / drawArc / drawPath 等绘制 API
 * - 类似传统 Android 的 View.onDraw(Canvas) + Paint
 * - 区别: Compose Canvas 是声明式的，状态变化自动重绘
 */
@Composable
fun PagerCanvasScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ===== 1. HorizontalPager + TabRow 联动 =====
        SectionTitle("1. HorizontalPager + TabRow")
        Text(
            "左右滑动切换页面，Tab 自动跟随；点击 Tab 也会滑动到对应页面。\n" +
            "这是传统 ViewPager + TabLayout 的 Compose 等价物。"
        )

        PagerWithTabDemo()

        // ===== 2. Pager 指示器 =====
        SectionTitle("2. Pager 自定义指示器")
        Text("用 Row + 圆点实现自定义翻页指示器")

        PagerWithIndicatorDemo()

        // ===== 3. Canvas 基础图形 =====
        SectionTitle("3. Canvas - 基础图形绘制")
        Text("Canvas composable 提供 DrawScope，可绘制圆形、矩形、线条、弧形、路径等")

        BasicCanvasDemo()

        // ===== 4. Canvas 交互式图表 =====
        SectionTitle("4. Canvas - 交互式柱状图")
        Text("结合 State 和 Canvas 实现响应式图表")

        BarChartDemo()

        // ===== 5. Canvas 时钟 =====
        SectionTitle("5. Canvas - 动态时钟")
        Text("结合 LaunchedEffect + Canvas 实现实时更新的模拟时钟")

        ClockDemo()

        Spacer(modifier = Modifier.height(32.dp))
    }
}

/**
 * HorizontalPager + TabRow 联动演示
 *
 * 实现原理：TabRow 和 HorizontalPager 共享同一个 PagerState。
 * - 手势滑动时：PagerState.currentPage 自动更新 → TabRow 高亮跟随
 * - 点击 Tab 时：通过协程调用 pagerState.animateScrollToPage(index) 驱动翻页
 *
 * rememberPagerState(pageCount = { tabs.size }) 中 pageCount 使用 lambda，
 * 这样当页面数量动态变化时 PagerState 可以自动适应。
 */
@Composable
private fun PagerWithTabDemo() {
    val tabs = listOf("照片", "视频", "音乐")
    val icons = listOf(Icons.Default.Photo, Icons.Default.Videocam, Icons.Default.MusicNote)
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    // animateScrollToPage 是挂起函数，需要 CoroutineScope
    val scope = rememberCoroutineScope()

    Card(modifier = Modifier.fillMaxWidth()) {
        Column {
            TabRow(selectedTabIndex = pagerState.currentPage) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            // 点击 Tab 时平滑滚动到对应页面
                            scope.launch { pagerState.animateScrollToPage(index) }
                        },
                        text = { Text(title) },
                        icon = { Icon(icons[index], null) }
                    )
                }
            }

            // HorizontalPager: 左右滑动翻页
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) { page ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            when (page) {
                                0 -> MaterialTheme.colorScheme.primaryContainer
                                1 -> MaterialTheme.colorScheme.secondaryContainer
                                else -> MaterialTheme.colorScheme.tertiaryContainer
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            icons[page], null,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "${tabs[page]} 页面内容",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text("← 左右滑动切换 →", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

/**
 * Pager + 自定义圆点指示器
 *
 * Compose Foundation 没有内置 PageIndicator 组件（不像传统 ViewPager2 有 TabLayoutMediator）。
 * 本示例使用 Row + Box(CircleShape) 手动实现。实际项目中可封装为通用组件，
 * 或使用 accompanist-pager-indicators 库。
 */
@Composable
private fun PagerWithIndicatorDemo() {
    val pageCount = 5
    val pagerState = rememberPagerState(pageCount = { pageCount })
    val colors = listOf(
        Color(0xFFE57373), Color(0xFF81C784), Color(0xFF64B5F6),
        Color(0xFFFFD54F), Color(0xFFBA68C8)
    )

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) { page ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colors[page]),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "第 ${page + 1} 页",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                }
            }

            // 圆点指示器
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(pageCount) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (pagerState.currentPage == index) 12.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (pagerState.currentPage == index)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.outlineVariant
                            )
                    )
                }
            }
        }
    }
}

/**
 * Canvas 基础图形绘制演示
 *
 * Canvas { } 的 lambda receiver 是 DrawScope，提供以下绘制坐标系：
 * - 原点 (0, 0) 在 Canvas 左上角
 * - X 轴向右增大，Y 轴向下增大
 * - 所有尺寸单位为 px（像素），非 dp
 * - size.width / size.height 为 Canvas 的实际像素尺寸
 *
 * 注意：MaterialTheme.colorScheme 的颜色必须在 Composable 上下文中读取，
 * 不能在 DrawScope 内直接调用（DrawScope 不是 @Composable）。
 * 因此需要提前提取到局部变量中。
 */
@Composable
private fun BasicCanvasDemo() {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val errorColor = MaterialTheme.colorScheme.error

    Card(modifier = Modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp)
        ) {
            val w = size.width
            val h = size.height

            drawCircle(
                color = primaryColor,
                radius = 30f,
                center = Offset(50f, 50f)
            )

            drawRect(
                color = secondaryColor,
                topLeft = Offset(120f, 20f),
                size = Size(80f, 60f)
            )

            drawRoundRect(
                color = tertiaryColor,
                topLeft = Offset(230f, 20f),
                size = Size(80f, 60f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f)
            )

            // StrokeCap.Round 使线段端点为圆形，避免生硬截断
            drawLine(
                color = errorColor,
                start = Offset(0f, h * 0.5f),
                end = Offset(w, h * 0.5f),
                strokeWidth = 3f,
                cap = StrokeCap.Round
            )

            // useCenter=false: 弧形不连接到圆心（环形进度条效果）
            // useCenter=true: 弧形连接到圆心（扇形/饼图效果）
            drawArc(
                color = primaryColor,
                startAngle = 0f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = Offset(40f, h * 0.55f),
                size = Size(80f, 80f),
                style = Stroke(width = 6f, cap = StrokeCap.Round)
            )

            // Path: 可绘制任意多边形/贝塞尔曲线，moveTo→lineTo→close 构成闭合路径
            val path = Path().apply {
                moveTo(200f, h * 0.55f)
                lineTo(250f, h * 0.95f)
                lineTo(150f, h * 0.95f)
                close()
            }
            drawPath(path, color = tertiaryColor)

            // dashPathEffect(intervals): intervals 数组交替定义 [线段长, 间隙长]
            drawCircle(
                color = secondaryColor,
                radius = 40f,
                center = Offset(w - 60f, h * 0.75f),
                style = Stroke(
                    width = 3f,
                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                        floatArrayOf(10f, 10f)
                    )
                )
            )
        }
    }
}

/**
 * 交互式柱状图
 *
 * rememberTextMeasurer() 是 Canvas 中绘制文字的前置条件：
 * DrawScope.drawText 需要预先测量文本尺寸，因为 Canvas 是底层绘制 API，
 * 不像 Text Composable 自带布局测量能力。
 *
 * barColor.copy(alpha = ...) 使柱子颜色深浅随数值变化，形成数据可视化效果。
 */
@Composable
private fun BarChartDemo() {
    var data by remember {
        mutableStateOf(listOf(65f, 45f, 80f, 55f, 90f, 70f, 40f))
    }
    val labels = listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")
    val barColor = MaterialTheme.colorScheme.primary
    val textMeasurer = rememberTextMeasurer()

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                val barCount = data.size
                val barWidth = size.width / (barCount * 2f)
                val maxValue = 100f

                data.forEachIndexed { index, value ->
                    val barHeight = (value / maxValue) * (size.height - 30f)
                    val x = barWidth * 0.5f + index * (size.width / barCount)

                    drawRoundRect(
                        color = barColor.copy(alpha = 0.3f + (value / maxValue) * 0.7f),
                        topLeft = Offset(x, size.height - 20f - barHeight),
                        size = Size(barWidth, barHeight),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f)
                    )

                    // 数值标签
                    val textLayout = textMeasurer.measure("${value.toInt()}")
                    drawText(
                        textLayout,
                        topLeft = Offset(
                            x + barWidth / 2 - textLayout.size.width / 2,
                            size.height - 25f - barHeight - textLayout.size.height
                        )
                    )
                }
            }

            // 标签行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                labels.forEach { label ->
                    Text(label, style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    data = data.map { (20..100).random().toFloat() }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Refresh, null)
                Spacer(Modifier.width(4.dp))
                Text("随机生成数据")
            }
        }
    }
}

/**
 * 动态模拟时钟
 *
 * 实现思路：
 * 1. LaunchedEffect 中每秒更新 currentTime（State），触发 recomposition
 * 2. Canvas 根据最新时间计算时/分/秒针角度并绘制
 *
 * 角度计算：
 * - 时钟 12 点方向对应 -90°（Canvas 默认 0° 指向右方/3 点钟方向）
 * - 时针: (hours + minutes/60) × 30° - 90°
 * - 分针: (minutes + seconds/60) × 6° - 90°
 * - 秒针: seconds × 6° - 90°
 *
 * DrawScope.rotate(degrees, pivot) 以 pivot 为旋转中心旋转绘制内容，
 * 先旋转坐标系再画线，比手动计算三角函数更简洁。
 */
@Composable
private fun ClockDemo() {
    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = System.currentTimeMillis()
            kotlinx.coroutines.delay(1000L)
        }
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val outlineColor = MaterialTheme.colorScheme.outline
    val errorColor = MaterialTheme.colorScheme.error

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 提前创建 Calendar 实例，避免在 Canvas DrawScope 内和外各创建一次
            val calendar = remember { java.util.Calendar.getInstance() }
                .apply { timeInMillis = currentTime }
            val hours = calendar.get(java.util.Calendar.HOUR)
            val minutes = calendar.get(java.util.Calendar.MINUTE)
            val seconds = calendar.get(java.util.Calendar.SECOND)

            Canvas(
                modifier = Modifier.size(200.dp)
            ) {
                val radius = size.minDimension / 2 - 10f
                val center = Offset(size.width / 2, size.height / 2)

                drawCircle(
                    color = outlineColor,
                    radius = radius,
                    center = center,
                    style = Stroke(width = 3f)
                )

                // 12 个刻度：i*30° 对应每小时间隔，-90° 将 0° 从 3 点方向移到 12 点方向
                for (i in 0 until 12) {
                    val angle = (i * 30.0 - 90) * PI / 180
                    val startR = radius - if (i % 3 == 0) 18f else 10f
                    val endR = radius - 4f
                    drawLine(
                        color = onSurfaceColor,
                        start = Offset(
                            center.x + (startR * cos(angle)).toFloat(),
                            center.y + (startR * sin(angle)).toFloat()
                        ),
                        end = Offset(
                            center.x + (endR * cos(angle)).toFloat(),
                            center.y + (endR * sin(angle)).toFloat()
                        ),
                        strokeWidth = if (i % 3 == 0) 3f else 1.5f
                    )
                }

                // 时针：rotate 旋转后画一条从中心到右侧的水平线（0°=3点方向，-90°修正到12点）
                val hourAngle = ((hours + minutes / 60f) * 30f - 90f)
                rotate(hourAngle, pivot = center) {
                    drawLine(
                        color = onSurfaceColor,
                        start = center,
                        end = Offset(center.x + radius * 0.5f, center.y),
                        strokeWidth = 5f,
                        cap = StrokeCap.Round
                    )
                }

                // 分针
                val minuteAngle = ((minutes + seconds / 60f) * 6f - 90f)
                rotate(minuteAngle, pivot = center) {
                    drawLine(
                        color = primaryColor,
                        start = center,
                        end = Offset(center.x + radius * 0.7f, center.y),
                        strokeWidth = 3f,
                        cap = StrokeCap.Round
                    )
                }

                // 秒针：起点反向延伸 15% 半径，形成"尾巴"效果（常见于真实时钟）
                val secondAngle = (seconds * 6f - 90f)
                rotate(secondAngle, pivot = center) {
                    drawLine(
                        color = errorColor,
                        start = Offset(center.x - radius * 0.15f, center.y),
                        end = Offset(center.x + radius * 0.8f, center.y),
                        strokeWidth = 1.5f,
                        cap = StrokeCap.Round
                    )
                }

                // 中心圆点
                drawCircle(color = errorColor, radius = 4f, center = center)
            }

            Text(
                "%02d:%02d:%02d".format(
                    calendar.get(java.util.Calendar.HOUR_OF_DAY),
                    minutes,
                    seconds
                ),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
