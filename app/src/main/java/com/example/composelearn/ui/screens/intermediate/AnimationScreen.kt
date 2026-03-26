package com.example.composelearn.ui.screens.intermediate

import com.example.composelearn.ui.components.SectionTitle
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 动画效果演示页面
 *
 * 【核心概念】Compose 动画体系
 *
 * 1. animateXxxAsState - 状态驱动的单值动画
 *    值变化时自动插值动画过渡，最简单的动画方式
 *
 * 2. AnimatedVisibility - 显示/隐藏动画
 *    控制组件出现/消失时的过渡效果（淡入淡出、展开收起等）
 *
 * 3. Crossfade - 内容切换动画
 *    在两个 Composable 之间做淡入淡出切换
 *
 * 4. InfiniteTransition - 无限循环动画
 *    持续运行的动画（如加载指示器、呼吸灯）
 *
 * 5. updateTransition - 多属性联动动画
 *    一个状态变化同时驱动多个属性的动画
 */
@Composable
fun AnimationScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ===== 1. animateXxxAsState =====
        SectionTitle("1. animateXxxAsState - 单值动画")
        Text("状态变化时，值会通过动画平滑过渡（而非瞬间跳变）")

        var expanded by remember { mutableStateOf(false) }
        // animateDpAsState: Dp 值的平滑动画
        val width by animateDpAsState(
            targetValue = if (expanded) 300.dp else 100.dp,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
            label = "width"
        )
        // animateColorAsState: 颜色的平滑动画
        val color by animateColorAsState(
            targetValue = if (expanded) MaterialTheme.colorScheme.primary
                         else MaterialTheme.colorScheme.tertiary,
            animationSpec = tween(durationMillis = 500),
            label = "color"
        )
        // animateFloatAsState: Float 值的平滑动画
        val cornerRadius by animateFloatAsState(
            targetValue = if (expanded) 32f else 8f,
            label = "corner"
        )

        Box(
            modifier = Modifier
                .width(width)
                .height(60.dp)
                .background(color, RoundedCornerShape(cornerRadius.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                if (expanded) "展开状态" else "收起",
                color = Color.White
            )
        }
        Button(onClick = { expanded = !expanded }) {
            Text(if (expanded) "收起" else "展开")
        }

        // ===== 2. AnimatedVisibility =====
        SectionTitle("2. AnimatedVisibility - 显隐动画")
        Text("控制组件的出现/消失动画效果")

        var visible by remember { mutableStateOf(true) }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { visible = true }) { Text("显示") }
            OutlinedButton(onClick = { visible = false }) { Text("隐藏") }
        }

        // enter: 出现动画  |  exit: 消失动画
        // 可以用 + 组合多个效果
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn() + slideInVertically() + expandVertically(),
            exit = fadeOut() + slideOutVertically() + shrinkVertically()
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("这个卡片可以带动画显示和隐藏")
                }
            }
        }

        // ===== 3. Crossfade =====
        SectionTitle("3. Crossfade - 内容切换动画")
        Text("在不同内容之间做淡入淡出的平滑切换")

        var currentPage by remember { mutableIntStateOf(0) }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            (0..2).forEach { page ->
                FilterChip(
                    selected = currentPage == page,
                    onClick = { currentPage = page },
                    label = { Text("页面 ${page + 1}") }
                )
            }
        }

        // Crossfade 根据 targetState 的值做淡入淡出切换
        Crossfade(
            targetState = currentPage,
            animationSpec = tween(500),
            label = "crossfade"
        ) { page ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (page) {
                        0 -> MaterialTheme.colorScheme.primaryContainer
                        1 -> MaterialTheme.colorScheme.secondaryContainer
                        else -> MaterialTheme.colorScheme.tertiaryContainer
                    }
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "页面 ${page + 1} 的内容",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }

        // ===== 4. InfiniteTransition =====
        SectionTitle("4. InfiniteTransition - 无限循环动画")
        Text("持续运行的动画，适合加载指示器、呼吸灯效果")

        val infiniteTransition = rememberInfiniteTransition(label = "infinite")
        // 颜色循环
        val animatedColor by infiniteTransition.animateColor(
            initialValue = Color(0xFFE57373),
            targetValue = Color(0xFF64B5F6),
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse    // 正反交替
            ),
            label = "color"
        )
        // 旋转循环
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart    // 重头开始
            ),
            label = "rotation"
        )
        // 缩放呼吸
        val scale by infiniteTransition.animateFloat(
            initialValue = 0.8f,
            targetValue = 1.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 颜色渐变
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(animatedColor, RoundedCornerShape(12.dp))
            )
            // 旋转图标
            Icon(
                Icons.Default.Settings,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .rotate(rotation),
                tint = MaterialTheme.colorScheme.primary
            )
            // 缩放呼吸
            Icon(
                Icons.Default.Favorite,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .scale(scale),
                tint = Color.Red
            )
        }

        // ===== 5. updateTransition - 多属性联动 =====
        SectionTitle("5. updateTransition - 多属性联动")
        Text("一个状态变化同时驱动多个动画属性")

        var isSelected by remember { mutableStateOf(false) }
        val transition = updateTransition(targetState = isSelected, label = "selection")

        val bgColor by transition.animateColor(label = "bg") { selected ->
            if (selected) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surfaceVariant
        }
        val size by transition.animateDp(label = "size") { selected ->
            if (selected) 120.dp else 80.dp
        }
        val corner by transition.animateDp(label = "corner") { selected ->
            if (selected) 60.dp else 12.dp
        }

        Box(
            modifier = Modifier
                .size(size)
                .background(bgColor, RoundedCornerShape(corner)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (isSelected) Icons.Default.Check else Icons.Default.Add,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
        Button(onClick = { isSelected = !isSelected }) {
            Text(if (isSelected) "取消选择" else "选择")
        }

        // ===== 6. AnimationSpec 对比 =====
        SectionTitle("6. AnimationSpec - 动画规格")
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                AnimSpecInfo("tween(500)", "固定时长 500ms 的线性插值")
                AnimSpecInfo("spring()", "物理弹簧效果，有回弹感")
                AnimSpecInfo("keyframes", "关键帧动画，精确控制每个时间点的值")
                AnimSpecInfo("snap()", "无动画，直接跳到目标值")
                AnimSpecInfo("infiniteRepeatable()", "无限循环动画")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun AnimSpecInfo(name: String, desc: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(name, style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.width(160.dp),
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
        Text(desc, style = MaterialTheme.typography.bodySmall)
    }
}
