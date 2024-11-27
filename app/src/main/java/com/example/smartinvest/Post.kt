package com.example.smartinvest
data class Post(
    val postId: String = "", // Use a unique ID for each post
    val username: String = "",
    val content: String = "",
    var likes: Int = 0
){


        // No-argument constructor (required by Firebase)
        constructor() : this( "","", "", 0)
    }
