package com.example.algoliademo1.util

import java.text.SimpleDateFormat
import java.util.*

fun formatDate(date: Date): String{
    val sdf = SimpleDateFormat("dd/MM/yyyy")
    return sdf.format(date).toString()
}