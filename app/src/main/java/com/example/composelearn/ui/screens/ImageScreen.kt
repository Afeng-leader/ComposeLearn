package com.example.composelearn.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

/**
 * 图片展示演示页面
 *
 * 【核心概念】
 * - Image: 显示图片的基础组件（类似 ImageView）
 * - Icon: 显示 Material Icon 的组件
 * - ContentScale: 控制图片缩放方式（类似 ImageView.ScaleType）
 * - clip: 裁剪形状（圆形、圆角等）
 * - ColorFilter: 图片颜色滤镜
 *
 * 【注意】
 * 本演示使用 Material Icons 和 Compose 内置图形代替真实图片
 * 实际项目中可使用 Coil 库的 AsyncImage 加载网络图片
 */
@Composable
fun ImageScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ===== 1. Material Icons =====
        SectionTitle("1. Material Icons")
        Text("Compose 内置大量 Material Design 图标，通过 Icons.Default.Xxx 使用")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconWithLabel(Icons.Default.Home, "Home")
            IconWithLabel(Icons.Default.Favorite, "Favorite")
            IconWithLabel(Icons.Default.Settings, "Settings")
            IconWithLabel(Icons.Default.Person, "Person")
            IconWithLabel(Icons.Default.Search, "Search")
        }

        // ===== 2. Icon 尺寸与颜色 =====
        SectionTitle("2. Icon 尺寸与颜色")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Star, contentDescription = null,
                modifier = Modifier.size(24.dp), tint = Color.Gray)
            Icon(Icons.Default.Star, contentDescription = null,
                modifier = Modifier.size(36.dp), tint = Color(0xFFFFD700))
            Icon(Icons.Default.Star, contentDescription = null,
                modifier = Modifier.size(48.dp), tint = Color(0xFFFF5722))
            Icon(Icons.Default.Star, contentDescription = null,
                modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
        }

        // ===== 3. Shape 裁剪 =====
        SectionTitle("3. clip - 形状裁剪")
        Text("使用 Modifier.clip() 将内容裁剪为不同形状")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // 圆形裁剪 - 常用于头像
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Text("CircleShape", style = MaterialTheme.typography.labelSmall)
            }

            // 圆角矩形
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Image,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                Text("RoundedCorner", style = MaterialTheme.typography.labelSmall)
            }

            // 无裁剪
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(MaterialTheme.colorScheme.tertiaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Landscape,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
                Text("无裁剪", style = MaterialTheme.typography.labelSmall)
            }
        }

        // ===== 4. Border 边框 =====
        SectionTitle("4. border - 添加边框")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // 圆形 + 边框 = 头像样式
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(48.dp))
            }
            // 渐变边框
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .border(
                        width = 3.dp,
                        brush = Brush.linearGradient(listOf(Color.Red, Color.Blue)),
                        shape = CircleShape
                    )
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(48.dp))
            }
        }

        // ===== 5. ContentScale 缩放模式 =====
        SectionTitle("5. ContentScale - 缩放模式对比")
        Text("ContentScale 决定图片如何适应容器（类似 ImageView.ScaleType）")
        val scales = listOf(
            "Crop" to ContentScale.Crop,
            "Fit" to ContentScale.Fit,
            "FillBounds" to ContentScale.FillBounds,
            "Inside" to ContentScale.Inside,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            scales.forEach { (name, scale) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .border(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Image(
                            imageVector = Icons.Default.Landscape,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = scale,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                        )
                    }
                    Text(name, style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        // ===== 6. ColorFilter 颜色滤镜 =====
        SectionTitle("6. ColorFilter - 颜色滤镜")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(Color.Red, Color.Green, Color.Blue, Color.Magenta).forEach { color ->
                Image(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    colorFilter = ColorFilter.tint(color)
                )
            }
        }

        // ===== 7. 网络图片提示 =====
        SectionTitle("7. AsyncImage - 网络图片加载")
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("实际项目中加载网络图片推荐使用 Coil 库:", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                AsyncImage(
                    model = "https://img2.baidu.com/it/u=2376489989,3127732063&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=657",
                    contentDescription = "图片描述",
                    modifier = Modifier.size(200.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun IconWithLabel(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = label, modifier = Modifier.size(32.dp))
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}
