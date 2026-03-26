package com.example.composelearn.ui.screens.intermediate

import com.example.composelearn.ui.components.SectionTitle
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

/**
 * 表单输入演示页面
 *
 * 【核心概念】
 * - TextField / OutlinedTextField: 文本输入框（类似 EditText）
 * - Checkbox: 多选框
 * - Switch: 开关
 * - RadioButton: 单选按钮
 * - Slider: 滑块
 * - DropdownMenu: 下拉菜单
 *
 * 【受控组件模式】Compose 中所有输入组件都是"受控"的：
 * - value 参数: 显示当前值（来自 State）
 * - onValueChange 回调: 用户输入时触发，更新 State
 * - State 变化 → recomposition → 输入框显示新值
 * 这与 React 的受控组件模式完全一致
 */
@Composable
fun FormScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ===== 1. TextField 文本输入 =====
        SectionTitle("1. TextField - 文本输入框")

        // 基础 TextField (Material Filled 样式)
        var text1 by remember { mutableStateOf("") }
        TextField(
            value = text1,
            onValueChange = { text1 = it },
            label = { Text("Filled 样式") },
            placeholder = { Text("请输入...") },
            modifier = Modifier.fillMaxWidth()
        )

        // OutlinedTextField (带边框)
        var text2 by remember { mutableStateOf("") }
        OutlinedTextField(
            value = text2,
            onValueChange = { text2 = it },
            label = { Text("Outlined 样式") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            trailingIcon = {
                if (text2.isNotEmpty()) {
                    IconButton(onClick = { text2 = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "清除")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // 密码输入框
        var password by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("密码") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "切换密码可见性"
                    )
                }
            },
            // VisualTransformation 控制显示方式（密码点/明文）
            visualTransformation = if (passwordVisible) VisualTransformation.None
                                    else PasswordVisualTransformation(),
            // KeyboardOptions 控制键盘类型
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth()
        )

        // 数字输入
        var number by remember { mutableStateOf("") }
        OutlinedTextField(
            value = number,
            onValueChange = { newValue ->
                // 只允许输入数字
                if (newValue.all { it.isDigit() }) number = newValue
            },
            label = { Text("只能输入数字") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // ===== 2. Checkbox 多选框 =====
        SectionTitle("2. Checkbox - 多选框")
        val checkItems = listOf("Kotlin", "Java", "Dart", "Swift")
        val checkedStates = remember { mutableStateMapOf<String, Boolean>() }

        checkItems.forEach { item ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = checkedStates[item] ?: false,
                    onCheckedChange = { checkedStates[item] = it }
                )
                Text(item, modifier = Modifier.weight(1f))
            }
        }
        val selected = checkedStates.filter { it.value }.keys
        if (selected.isNotEmpty()) {
            Text("已选择: ${selected.joinToString()}", color = MaterialTheme.colorScheme.primary)
        }

        // ===== 3. Switch 开关 =====
        SectionTitle("3. Switch - 开关")
        var wifiEnabled by remember { mutableStateOf(true) }
        var bluetoothEnabled by remember { mutableStateOf(false) }
        var darkModeEnabled by remember { mutableStateOf(false) }

        SwitchRow("Wi-Fi", wifiEnabled) { wifiEnabled = it }
        SwitchRow("蓝牙", bluetoothEnabled) { bluetoothEnabled = it }
        SwitchRow("深色模式", darkModeEnabled) { darkModeEnabled = it }

        // ===== 4. RadioButton 单选 =====
        SectionTitle("4. RadioButton - 单选按钮")
        val radioOptions = listOf("小杯", "中杯", "大杯", "超大杯")
        var selectedOption by remember { mutableStateOf(radioOptions[1]) }

        radioOptions.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                RadioButton(
                    selected = option == selectedOption,
                    onClick = { selectedOption = option }
                )
                Text(option)
            }
        }
        Text("已选择: $selectedOption", color = MaterialTheme.colorScheme.primary)

        // ===== 5. Slider 滑块 =====
        SectionTitle("5. Slider - 滑块")
        var sliderValue by remember { mutableFloatStateOf(50f) }
        Text("音量: ${sliderValue.toInt()}%")
        Slider(
            value = sliderValue,
            onValueChange = { sliderValue = it },
            valueRange = 0f..100f,   // 值范围
            steps = 9                 // 将范围分成 10 段（9 个刻度）
        )

        // RangeSlider: 双端滑块
        var rangeValues by remember { mutableStateOf(20f..80f) }
        Text("价格区间: ¥${rangeValues.start.toInt()} - ¥${rangeValues.endInclusive.toInt()}")
        RangeSlider(
            value = rangeValues,
            onValueChange = { rangeValues = it },
            valueRange = 0f..200f
        )

        // ===== 6. DropdownMenu 下拉菜单 =====
        SectionTitle("6. DropdownMenu - 下拉选择")
        var expanded by remember { mutableStateOf(false) }
        var selectedCity by remember { mutableStateOf("请选择城市") }
        val cities = listOf("北京", "上海", "广州", "深圳", "杭州", "成都")

        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(selectedCity)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                cities.forEach { city ->
                    DropdownMenuItem(
                        text = { Text(city) },
                        onClick = {
                            selectedCity = city
                            expanded = false
                        },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
