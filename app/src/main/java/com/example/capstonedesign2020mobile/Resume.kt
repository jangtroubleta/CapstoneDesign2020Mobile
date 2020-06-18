package com.example.capstonedesign2020mobile

import java.io.Serializable

data class Resume(
    val id: String,
    val image : String,
    val userid : String,
    val name : String,
    val result : String
) : Serializable