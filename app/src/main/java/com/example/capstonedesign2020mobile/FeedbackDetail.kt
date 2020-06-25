package com.example.capstonedesign2020mobile

import java.io.Serializable

data class FeedbackDetail(
    val id: String,
    val title: String,
    val date: String,
    val image: String,
    val name: String,
    val video: String,
    val content: String,
    val answer : String?
) : Serializable
