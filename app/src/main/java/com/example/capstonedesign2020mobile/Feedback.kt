package com.example.capstonedesign2020mobile

import java.io.Serializable

data class Feedback(val id: String, val name: String, val title : String, val date : String, val answer : String?) : Serializable
