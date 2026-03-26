package com.example.composelearn.ui.screens.basic

import com.example.composelearn.ui.components.SectionTitle
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 基础布局演示页面
 *
 * 【核心概念】Compose 三大基础布局容器
 * - Column: 垂直排列子元素（类似 LinearLayout vertical）
 * - Row: 水平排列子元素（类似 LinearLayout horizontal）
 * - Box: 子元素层叠（类似 FrameLayout）
 *
 * 【Modifier 修饰符链】
 * - Modifier 是 Compose 最核心的概念之一
 * - 通过链式调用修改组件的尺寸、边距、背景、点击等属性
 * - 顺序很重要: padding 在 background 之前/之后效果不同
 */
@Composable
fun BasicLayoutScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())  // 使 Column 可滚动
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ===== 1. Column 垂直布局 =====
        SectionTitle("1. Column - 垂直布局")
        Text("子元素从上到下排列，类似传统的 LinearLayout(vertical)")
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outline)
                .padding(12.dp),
            // verticalArrangement 控制子元素间距分布
            verticalArrangement = Arrangement.spacedBy(8.dp),
            // horizontalAlignment 控制子元素水平对齐方式
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ColorBox("第一行", Color(0xFFE57373))
            ColorBox("第二行", Color(0xFF81C784))
            ColorBox("第三行", Color(0xFF64B5F6))
        }

        // ===== 2. Row 水平布局 =====
        SectionTitle("2. Row - 水平布局")
        Text("子元素从左到右排列，类似 LinearLayout(horizontal)")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outline)
                .padding(12.dp),
            // horizontalArrangement 控制子元素水平分布
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ColorBox("左", Color(0xFFE57373), width = 60)
            ColorBox("中", Color(0xFF81C784), width = 60)
            ColorBox("右", Color(0xFF64B5F6), width = 60)
        }

        // ===== 3. Box 层叠布局 =====
        SectionTitle("3. Box - 层叠布局")
        Text("子元素层叠放置，后添加的在上层（类似 FrameLayout）")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .border(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            // 第一层 - 背景色块
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.Center)        // Box 内用 align 定位
                    .background(Color(0xFFE57373))
            )
            // 第二层 - 覆盖在上方
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.Center)
                    .background(Color(0xFF81C784))
            )
            // 第三层 - 最上方
            Text(
                text = "层叠顶部",
                modifier = Modifier.align(Alignment.Center),
                color = Color.White
            )
        }

        // ===== 4. Modifier 顺序对比 =====
        SectionTitle("4. Modifier 顺序的影响")
        Text("padding 在 background 之前 vs 之后，效果完全不同")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // 先 padding 再 background: padding 区域没有背景色
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("先padding\n再background", style = MaterialTheme.typography.labelSmall)
                Box(
                    modifier = Modifier
                        .border(1.dp, MaterialTheme.colorScheme.outline)
                        .padding(16.dp)                         // ① 先留出外边距
                        .background(Color(0xFFE57373))          // ② 再填充背景
                ) {
                    Text("内容", color = Color.White)
                }
            }
            // 先 padding 再 background: padding 区域没有背景色
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("先padding\n再background\n再padding", style = MaterialTheme.typography.labelSmall)
                Box(
                    modifier = Modifier
                        .border(1.dp, MaterialTheme.colorScheme.outline)
                        .padding(16.dp)                         // ① 先留出外边距
                        .background(Color(0xFFE57373))          // ② 再填充背景
                        .padding(16.dp)                         // ③ 内边距
                ) {
                    Text("内容", color = Color.White)
                }
            }
            // 先 background 再 padding: 背景色覆盖 padding 区域
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("先background\n再padding", style = MaterialTheme.typography.labelSmall)
                Box(
                    modifier = Modifier
                        .border(1.dp, MaterialTheme.colorScheme.outline)
                        .background(Color(0xFF64B5F6))          // ① 先填充背景
                        .padding(16.dp)                         // ② 再留出内边距（背景色已占满）
                ) {
                    Text("内容", color = Color.White)
                }
            }
        }

        // ===== 5. Arrangement 对齐方式 =====
        SectionTitle("5. Arrangement 排列策略")
        val arrangements = listOf(
            "SpaceEvenly" to Arrangement.SpaceEvenly,
            "SpaceBetween" to Arrangement.SpaceBetween,
            "SpaceAround" to Arrangement.SpaceAround,
        )
        arrangements.forEach { (name, arrangement) ->
            Text(name, style = MaterialTheme.typography.labelMedium)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline)
                    .padding(8.dp),
                horizontalArrangement = arrangement
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF64B5F6))
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        // ===== 6. weight 权重分配 =====
        SectionTitle("6. weight - 权重分配")
        Text("类似 LinearLayout 的 layout_weight，按比例分配空间")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)                    // 占 1/4
                    .fillMaxHeight()
                    .background(Color(0xFFE57373)),
                contentAlignment = Alignment.Center
            ) { Text("1", color = Color.White) }

            Box(
                modifier = Modifier
                    .weight(2f)                    // 占 2/4
                    .fillMaxHeight()
                    .background(Color(0xFF81C784)),
                contentAlignment = Alignment.Center
            ) { Text("2", color = Color.White) }

            Box(
                modifier = Modifier
                    .weight(1f)                    // 占 1/4
                    .fillMaxHeight()
                    .background(Color(0xFF64B5F6)),
                contentAlignment = Alignment.Center
            ) { Text("1", color = Color.White) }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

/** 带颜色的方块，用于布局演示 */
@Composable
private fun ColorBox(label: String, color: Color, width: Int = 100) {
    Box(
        modifier = Modifier
            .width(width.dp)
            .height(40.dp)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, color = Color.White)
    }
}

