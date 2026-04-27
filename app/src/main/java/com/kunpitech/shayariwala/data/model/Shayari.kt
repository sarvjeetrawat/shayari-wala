package com.kunpitech.shayariwala.data.model

data class Shayari(
    val id          : String = "",
    val hindiText   : String = "",
    val urduText    : String = "",
    val poet        : String = "",
    val category    : String = "",   // "ishq" | "dard" | "zindagi" | "khushi" | "judai" | "wafa"
    val likes       : Int    = 0,
    val comments    : Int    = 0,
    val isTrending  : Boolean = false,
    val createdAt   : Long   = 0L,
)