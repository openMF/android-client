package com.mifos.core.network.model

import kotlinx.serialization.Serializable

@Serializable
data class CollateralItem(
    val quality: String,
    val basePrice: Double,
    val unitType: String,
    val pctToBase: Double,
    val currency: String,
    val name: String,
    val id: Int,
)