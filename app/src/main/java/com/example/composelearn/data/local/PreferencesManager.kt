package com.example.composelearn.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * DataStore 偏好设置管理器
 *
 * 【关键概念】DataStore vs SharedPreferences
 * - SharedPreferences: 同步 API，可能阻塞 UI 线程，不支持类型安全
 * - DataStore: 异步 API（基于 Flow），类型安全，支持事务
 * - Google 官方推荐用 DataStore 替代 SharedPreferences
 *
 * 【使用方式】
 * - preferencesDataStore: 扩展属性，在 Context 上创建 DataStore 实例
 * - Preferences.Key<T>: 类型安全的键，编译时检查类型匹配
 * - dataStore.data: 返回 Flow<Preferences>，数据变化时自动发射新值
 * - dataStore.edit {}: 事务性写入，保证原子性
 */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {

    companion object {
        val USERNAME_KEY = stringPreferencesKey("username")
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        val FONT_SIZE_KEY = intPreferencesKey("font_size")
        val NOTIFICATION_KEY = booleanPreferencesKey("notifications_enabled")
    }

    /** 读取用户名 - 返回 Flow，数据变化时自动通知 */
    val username: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[USERNAME_KEY] ?: "未设置"
    }

    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[DARK_MODE_KEY] ?: false
    }

    val fontSize: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[FONT_SIZE_KEY] ?: 16
    }

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[NOTIFICATION_KEY] ?: true
    }

    /** 保存用户名 */
    suspend fun saveUsername(name: String) {
        context.dataStore.edit { prefs ->
            prefs[USERNAME_KEY] = name
        }
    }

    suspend fun saveDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[DARK_MODE_KEY] = enabled
        }
    }

    suspend fun saveFontSize(size: Int) {
        context.dataStore.edit { prefs ->
            prefs[FONT_SIZE_KEY] = size
        }
    }

    suspend fun saveNotifications(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[NOTIFICATION_KEY] = enabled
        }
    }
}
