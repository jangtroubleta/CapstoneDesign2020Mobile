package com.example.capstonedesign2020mobile

import java.io.Serializable

data class ResumeDetail (
    val id : String,
    val image : String,
    val name : String,
    val userid : String,
    val gender : String,
    val address : String,
    val call_number : String,
    val rank_name : String,
    val score : String,
    val message : String,
    val result : String,
    val audition_id : String
) : Serializable