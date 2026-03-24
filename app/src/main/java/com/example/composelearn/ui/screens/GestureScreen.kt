package com.example.composelearn.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * 手势交互演示页面
 *
 * 【核心概念】Compose 手势系统
 *
 * 1. Modifier.pointerInput: 最底层的手势检测入口
 *    - detectTapGestures: 单击/双击/长按
 *    - detectDragGestures: 拖拽（自由/水平/垂直）
 *    - detectTransformGestures: 缩放/旋转/平移（多点触控）
 *
 * 2. 高层手势 Modifier:
 *    - Modifier.clickable: 点击（已在其他页面使用）
 *    - Modifier.draggable: 单轴拖拽
 *    - Modifier.scrollable: 可滚动
 *    - Modifier.transformable: 缩放/旋转/平移
 *
 * 【与传统 Android 的对比】
 * - 传统: OnTouchListener + MotionEvent + GestureDetector
 * - Compose: pointerInput + detect*Gestures（声明式，更简洁）
 */
@Composable
fun GestureScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ===== 1. 单击 / 双击 / 长按 =====
        SectionTitle("1. detectTapGestures - 点击手势")
        Text("通过 pointerInput + detectTapGestures 检测不同类型的点击")

        TapGestureDemo()

        // ===== 2. 自由拖拽 =====
        SectionTitle("2. detectDragGestures - 拖拽手势")
        Text("拖动彩色圆球在区域内自由移动")

        DragGestureDemo()

        // ===== 3. 单轴拖拽 (draggable) =====
        SectionTitle("3. Modifier.draggable - 单轴拖拽")
        Text("高层 API，限定为水平方向拖拽")

        DraggableDemo()

        // ===== 4. 缩放与旋转 =====
        SectionTitle("4. transformable - 缩放/旋转/平移")
        Text("支持双指缩放、旋转和平移（模拟器中可用 Ctrl+鼠标模拟双指）")

        TransformGestureDemo()

        // ===== 5. 手势 API 对比 =====
        SectionTitle("5. 手势 API 层级")
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                GestureApiInfo(
                    "pointerInput + detect*",
                    "最灵活的底层 API",
                    "完全自定义手势逻辑"
                )
                GestureApiInfo(
                    "Modifier.draggable",
                    "单轴拖拽高层 API",
                    "水平/垂直滑块、侧滑"
                )
                GestureApiInfo(
                    "Modifier.scrollable",
                    "可滚动高层 API",
                    "自定义滚动容器"
                )
                GestureApiInfo(
                    "Modifier.transformable",
                    "多点触控高层 API",
                    "图片查看器（缩放/旋转）"
                )
                GestureApiInfo(
                    "Modifier.clickable",
                    "点击高层 API",
                    "按钮/卡片点击（含涟漪效果）"
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

/**
 * 点击手势演示
 *
 * pointerInput(Unit) 中的 Unit 是 key 参数：
 * key = Unit 表示"永不重建"，手势检测器在整个 Composable 生命周期内只初始化一次。
 * 如果需要根据某个 State 重建手势处理逻辑，可以传入该 State 作为 key。
 *
 * detectTapGestures 的回调优先级：onPress → (等待判定) → onTap / onDoubleTap / onLongPress
 * 双击判定会导致单击有短暂延迟（需等待确认不是双击的第一次点击）
 */
@Composable
private fun TapGestureDemo() {
    var lastGesture by remember { mutableStateOf("尚未检测到手势") }
    var tapCount by remember { mutableIntStateOf(0) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                lastGesture = "单击 (onTap) 位置: (${it.x.toInt()}, ${it.y.toInt()})"
                                tapCount++
                            },
                            onDoubleTap = {
                                lastGesture = "双击 (onDoubleTap)"
                                tapCount += 2
                            },
                            onLongPress = {
                                lastGesture = "长按 (onLongPress)"
                            },
                            onPress = {
                                // onPress 最先触发，可做按压缩放/高亮反馈
                                // 调用 tryAwaitRelease() 可以等待手指抬起
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.TouchApp, null, modifier = Modifier.size(32.dp))
                    Text("在此区域 单击 / 双击 / 长按")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("检测到: $lastGesture", color = MaterialTheme.colorScheme.primary)
            Text("累计点击: $tapCount 次")
        }
    }
}

/**
 * 自由拖拽演示
 *
 * 使用 Modifier.offset { } (lambda 版本) 而非 Modifier.offset(x, y)，
 * 因为 lambda 版本在布局阶段直接读取偏移量，跳过 recomposition，性能更优。
 * 对于频繁变化的位移，应始终使用 lambda 版本。
 */
@Composable
private fun DragGestureDemo() {
    var offset by remember { mutableStateOf(Offset(100f, 60f)) }
    // 记录容器实际尺寸（px），用于动态约束拖拽边界
    var containerSize by remember { mutableStateOf(androidx.compose.ui.unit.IntSize.Zero) }
    val ballSizePx = with(LocalDensity.current) { 48.dp.toPx() }

    Card(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .onSizeChanged { containerSize = it }
        ) {
            Box(
                modifier = Modifier
                    .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            // consume() 标记此触摸事件已被处理，防止父级 ScrollState 同时响应
                            change.consume()
                            // 使用容器实际尺寸减去球的直径作为边界，确保球不会超出可视区域
                            val maxX = (containerSize.width - ballSizePx).coerceAtLeast(0f)
                            val maxY = (containerSize.height - ballSizePx).coerceAtLeast(0f)
                            offset = Offset(
                                x = (offset.x + dragAmount.x).coerceIn(0f, maxX),
                                y = (offset.y + dragAmount.y).coerceIn(0f, maxY)
                            )
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.OpenWith,
                    null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                "位置: (${offset.x.toInt()}, ${offset.y.toInt()})",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

/**
 * 单轴拖拽演示
 *
 * Modifier.draggable 是 pointerInput + detectDragGestures 的高层封装：
 * - 只需指定 orientation（Horizontal/Vertical）和 state
 * - 不需要手动处理 change.consume()
 * - 适合简单的单轴拖拽场景，如自定义滑块、侧滑操作
 */
@Composable
private fun DraggableDemo() {
    val density = LocalDensity.current
    var offsetX by remember { mutableFloatStateOf(0f) }
    // 在 Compose 中 dp → px 转换需要通过 LocalDensity
    val maxWidthPx = with(density) { 250.dp.toPx() }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("水平拖拽滑块:")
            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                // 轨道
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .align(Alignment.Center)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
                // 滑块
                Box(
                    modifier = Modifier
                        .offset { IntOffset(offsetX.roundToInt(), 0) }
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .draggable(
                            orientation = Orientation.Horizontal,
                            state = rememberDraggableState { delta ->
                                offsetX = (offsetX + delta).coerceIn(0f, maxWidthPx)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.DragHandle,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Text(
                "偏移量: ${offsetX.toInt()}px (${(offsetX / maxWidthPx * 100).toInt()}%)",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

/**
 * 缩放/旋转/平移演示
 *
 * graphicsLayer 比 Modifier.offset + Modifier.scale 更高效：
 * - graphicsLayer 在绘制阶段应用变换，不触发重新布局
 * - offset/scale 等 Modifier 变化会触发重新布局阶段
 * 对于实时动画和手势变换，应优先使用 graphicsLayer
 *
 * detectTransformGestures 的参数含义：
 * - centroid: 多指操作的中心点
 * - pan: 平移偏移量（Offset）
 * - zoom: 缩放因子（1.0 = 无缩放）
 * - rotation: 旋转角度增量（度数）
 */
@Composable
private fun TransformGestureDemo() {
    var scale by remember { mutableFloatStateOf(1f) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            rotationZ = rotation,
                            translationX = offset.x,
                            translationY = offset.y
                        )
                        .pointerInput(Unit) {
                            detectTransformGestures { _, pan, zoom, rotationDelta ->
                                scale = (scale * zoom).coerceIn(0.5f, 3f)
                                rotation += rotationDelta
                                offset = Offset(
                                    offset.x + pan.x,
                                    offset.y + pan.y
                                )
                            }
                        }
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.tertiary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Image,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("缩放: ${"%.1f".format(scale)}x | 旋转: ${rotation.toInt()}°")

            OutlinedButton(onClick = {
                scale = 1f; rotation = 0f; offset = Offset.Zero
            }) {
                Icon(Icons.Default.Refresh, null)
                Spacer(Modifier.width(4.dp))
                Text("重置变换")
            }
        }
    }
}

@Composable
private fun GestureApiInfo(name: String, level: String, useCase: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            name,
            modifier = Modifier.width(160.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
        )
        Column {
            Text(level, style = MaterialTheme.typography.bodySmall)
            Text("场景: $useCase", style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
