package com.example.composelearn.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 数据模型定义
 *
 * 【关键概念】data class
 * - Kotlin 的 data class 自动生成 equals / hashCode / toString / copy
 * - 非常适合做数据载体（DTO / Entity）
 */

/** 网络请求的用户数据模型 - 对应 JSONPlaceholder API */
data class User(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val phone: String,
    val website: String
)

/** 网络请求的文章数据模型 */
data class Post(
    val userId: Int,
    val id: Int,
    val title: String,
    val body: String
)

/**
 * Room 数据库实体 - Todo 待办事项
 *
 * @Entity 注解标记此类映射到数据库中的一张表
 * @PrimaryKey(autoGenerate = true) 自增主键
 */
@Entity(tableName = "todos")
data class TodoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
