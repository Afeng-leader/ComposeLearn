package com.example.composelearn.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 文本样式演示页面
 *
 * 【核心概念】
 * - Text: Compose 中显示文字的基础组件（类似 TextView）
 * - TextStyle: 定义字体大小、颜色、粗细、行间距等
 * - AnnotatedString: 富文本，可在同一段文字中混合不同样式
 * - Material Typography: 预定义的文字层级体系
 */
@Composable
fun TextScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ===== 1. 基础文本 =====
        SectionTitle("1. 基础 Text")
        Text("这是一段普通文本")
        Text(
            text = "这段文本有最大行数限制，超过两行会显示省略号。" +
                    "Compose的Text组件通过maxLines和overflow参数控制文本溢出行为。" +
                    "这和传统Android的TextView.setEllipsize()类似但更简洁。",
            maxLines = 2,
            overflow = TextOverflow.Ellipsis  // 溢出时显示 "..."
        )

        // ===== 2. 字体大小与样式 =====
        SectionTitle("2. 字体大小与样式")
        Text("12sp 小字体", fontSize = 12.sp)
        Text("20sp 中字体", fontSize = 20.sp)
        Text("28sp 大字体", fontSize = 28.sp)
        Text(
            text = "粗体 + 斜体",
            fontWeight = FontWeight.Bold,   // 粗体
            fontStyle = FontStyle.Italic    // 斜体
        )
        Text(
            text = "等宽字体 Monospace",
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = "带下划线的文字",
            textDecoration = TextDecoration.Underline
        )
        Text(
            text = "删除线文字",
            textDecoration = TextDecoration.LineThrough
        )

        // ===== 3. 文字颜色 =====
        SectionTitle("3. 文字颜色")
        Text("主题 Primary 色", color = MaterialTheme.colorScheme.primary)
        Text("主题 Secondary 色", color = MaterialTheme.colorScheme.secondary)
        Text("主题 Error 色", color = MaterialTheme.colorScheme.error)
        Text("自定义 #FF5722", color = Color(0xFFFF5722))

        // ===== 4. Material Typography 预设层级 =====
        SectionTitle("4. Material Typography 预设样式")
        Text("你可以直接使用 MaterialTheme.typography 中的预设层级，保持应用风格统一")
        Text("Display Large", style = MaterialTheme.typography.displayLarge)
        Text("Headline Medium", style = MaterialTheme.typography.headlineMedium)
        Text("Title Large", style = MaterialTheme.typography.titleLarge)
        Text("Body Large", style = MaterialTheme.typography.bodyLarge)
        Text("Label Small", style = MaterialTheme.typography.labelSmall)

        // ===== 5. AnnotatedString 富文本 =====
        SectionTitle("5. AnnotatedString - 富文本")
        Text("在同一段文字中混合不同样式，类似 SpannableString")

        // buildAnnotatedString 构建富文本
        val annotatedText = buildAnnotatedString {
            append("这段话中 ")
            // withStyle 为指定范围添加样式
            withStyle(style = SpanStyle(color = Color.Red, fontWeight = FontWeight.Bold)) {
                append("红色粗体")
            }
            append(" 和 ")
            withStyle(style = SpanStyle(
                color = Color.Blue,
                fontSize = 20.sp,
                textDecoration = TextDecoration.Underline
            )) {
                append("蓝色大号下划线")
            }
            append(" 可以共存。")
        }
        Text(text = annotatedText)

        // 段落级别的样式
        val paragraphText = buildAnnotatedString {
            withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)) {
                append("这段文字居中对齐\n")
            }
            withStyle(style = ParagraphStyle(textAlign = TextAlign.End)) {
                append("这段文字右对齐\n")
            }
            withStyle(style = ParagraphStyle(lineHeight = 30.sp)) {
                append("这段文字设置了 30sp 的行高，让多行文字之间有更多的间距，阅读更舒适。")
            }
        }
        Text(text = paragraphText, modifier = Modifier.fillMaxWidth())

        // ===== 6. 文字对齐方式 =====
        SectionTitle("6. 文字对齐方式")
        Text("左对齐 (Start)", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)
        Text("居中对齐 (Center)", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        Text("右对齐 (End)", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.End)
        Text(
            text = "两端对齐 (Justify): 这段较长的文字用于演示两端对齐效果，" +
                    "Compose会自动调整单词间距以填满每一行。",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Justify
        )

        // ===== 7. letterSpacing 字间距 =====
        SectionTitle("7. 字间距 letterSpacing")
        Text("默认字间距")
        Text("letterSpacing = 4sp", letterSpacing = 4.sp)
        Text("letterSpacing = 8sp", letterSpacing = 8.sp)

        Spacer(modifier = Modifier.height(32.dp))
    }
}
