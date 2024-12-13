package com.example.smartinvest.community
data class Post(
    val postId: String = "", // מזהה ייחודי לפוסט
    val uid: String = "", // מזהה המשתמש
    val username: String = "", // שם המשתמש
    val content: String = "", // תוכן הפוסט
    var likes: Int = 0, // מספר הלייקים
    var likedBy: Map<String, Boolean> = emptyMap() // רשימת משתמשים שעשו לייק
) {
    constructor() : this("", "", "", "", 0, emptyMap())
}