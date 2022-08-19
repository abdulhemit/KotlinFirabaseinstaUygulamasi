package com.example.kotlininstagram

data class User(
    val id : String?,
    val name : String?,
    val email : String?,
    val password: String?,
    val post: post?
)
data class post(
    val downloadUrl: String?,
    val postText: String?,
    val date : String?
)