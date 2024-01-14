package com.harsh.askgemini.data

import androidx.compose.ui.graphics.Color

data class Screen(
    val routeId: String,
    val titleResId: Int,
    val descriptionResId: Int,
    val backgroundColor: Color
)
