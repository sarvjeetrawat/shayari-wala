package com.kunpitech.shayariwala.data.model

data class Poet(
    val id          : String = "",
    val name        : String = "",
    val urduName    : String = "",
    val bio         : String = "",
    val shayariCount: Int    = 0,
    val category    : String = "",   // primary category they're known for
)