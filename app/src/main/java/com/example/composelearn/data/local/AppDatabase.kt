package com.example.composelearn.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.composelearn.data.model.TodoItem

/**
 * Room 数据库定义
 *
 * 【关键概念】
 * - @Database: 标记此类为 Room 数据库
 *   entities: 包含的表（Entity 类列表）
 *   version: 数据库版本号（升级时递增，配合 Migration 使用）
 * - abstract class + abstract fun: Room 在编译时自动生成实现类
 * - 单例模式: 整个应用只需一个 Database 实例
 *
 * 【线程安全】
 * - @Volatile + synchronized 双重检查锁 保证线程安全的单例
 * - Room 的 DAO 方法是 suspend 函数，自动在 IO 线程执行
 */
@Database(
    entities = [TodoItem::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun todoDao(): TodoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "compose_learn_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
