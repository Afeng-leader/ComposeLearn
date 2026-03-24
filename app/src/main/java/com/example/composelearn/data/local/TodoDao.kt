package com.example.composelearn.data.local

import androidx.room.*
import com.example.composelearn.data.model.TodoItem
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO (Data Access Object) - 数据库操作接口
 *
 * 【关键概念】
 * - @Dao: 标记此接口为 Room 的数据访问对象
 * - @Query: 自定义 SQL 查询
 * - @Insert: 插入操作，onConflict 指定冲突策略
 * - @Update / @Delete: 更新/删除操作
 * - Flow<List<T>>: 返回可观察的数据流，数据变化时自动通知
 *   配合 Compose 的 collectAsState()，实现数据库 → UI 的实时同步
 *
 * 【与传统 Android 的对比】
 * - 传统: SQLiteOpenHelper + ContentProvider + Cursor
 * - Room: 注解驱动，编译时检查 SQL，自动生成实现类
 */
@Dao
interface TodoDao {

    /** 查询所有待办，按创建时间倒序 */
    @Query("SELECT * FROM todos ORDER BY createdAt DESC")
    fun getAllTodos(): Flow<List<TodoItem>>

    /** 查询未完成的待办 */
    @Query("SELECT * FROM todos WHERE isCompleted = 0 ORDER BY createdAt DESC")
    fun getActiveTodos(): Flow<List<TodoItem>>

    /** 查询已完成的待办 */
    @Query("SELECT * FROM todos WHERE isCompleted = 1 ORDER BY createdAt DESC")
    fun getCompletedTodos(): Flow<List<TodoItem>>

    /** 插入一条待办 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: TodoItem)

    /** 更新一条待办（如标记完成） */
    @Update
    suspend fun updateTodo(todo: TodoItem)

    /** 删除一条待办 */
    @Delete
    suspend fun deleteTodo(todo: TodoItem)

    /** 删除所有已完成的待办 */
    @Query("DELETE FROM todos WHERE isCompleted = 1")
    suspend fun deleteCompleted()
}
