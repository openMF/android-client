package com.mifos.feature.about

import androidx.compose.ui.graphics.Color

data class AboutItem(
    val icon: Int?,
    val title: Int,
    val subtitle: Int?,
    val color: Color,
    val id: AboutItems
)