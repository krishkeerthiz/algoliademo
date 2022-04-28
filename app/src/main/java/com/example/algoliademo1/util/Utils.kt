package com.example.algoliademo1.util

import java.text.SimpleDateFormat
import java.util.*

fun formatDate(date: Date): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.UK)
    return sdf.format(date).toString()
}

fun doorNumberCheck(value: String): Boolean {
    return !value.matches("^[a-zA-Z0-9. /,-]+$".toRegex()) || !value.contains("\\d".toRegex())
}

fun streetCheck(value: String): Boolean {
    return !value.matches("^[a-zA-Z0-9. /,]+$".toRegex())
}
