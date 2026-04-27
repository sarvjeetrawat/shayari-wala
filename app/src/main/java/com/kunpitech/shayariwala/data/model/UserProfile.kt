package com.kunpitech.shayariwala.data.model

data class UserProfile(
    val uid         : String = "",
    val displayName : String = "",
    val bio         : String = "",
    val savedIds    : List<String> = emptyList(),
    val writtenCount: Int    = 0,
    val totalLikes  : Int    = 0,
)