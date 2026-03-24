package com.example.composelearn.data.remote

import com.example.composelearn.data.model.Post
import com.example.composelearn.data.model.User
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit API 接口定义
 *
 * 【关键概念】Retrofit 注解
 * - @GET("path"): HTTP GET 请求
 * - @Path("param"): URL 路径参数替换
 * - suspend fun: 协程挂起函数，配合 ViewModel 中的 viewModelScope 使用
 *
 * 使用 https://jsonplaceholder.typicode.com 作为免费测试 API
 */
interface ApiService {

    /** 获取所有用户列表 */
    @GET("users")
    suspend fun getUsers(): List<User>

    /** 根据 ID 获取单个用户 */
    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Int): User

    /** 获取所有文章列表 */
    @GET("posts")
    suspend fun getPosts(): List<Post>

    /** 获取指定用户的文章 */
    @GET("users/{userId}/posts")
    suspend fun getPostsByUser(@Path("userId") userId: Int): List<Post>
}
