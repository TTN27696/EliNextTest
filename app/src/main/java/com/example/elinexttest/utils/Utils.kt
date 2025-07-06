package com.example.elinexttest.utils

import android.content.Context
import android.util.TypedValue
import java.util.UUID

fun Int.dpToPx(context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        context.resources.displayMetrics
    ).toInt()
}

fun getImageURL() = "https://picsum.photos/200/200?random=${UUID.randomUUID()}"