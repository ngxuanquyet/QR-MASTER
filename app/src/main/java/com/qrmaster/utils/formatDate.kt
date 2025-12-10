package com.qrmaster.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Date.format(pattern: String = "dd/MM/yyyy HH:mm"): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(this)
}