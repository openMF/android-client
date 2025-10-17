package com.mifos.room.entities.accounts.recurring

import kotlinx.serialization.Serializable

@Serializable
data class Incentive(
    val amount: Double? = null,
    val attributeName: AttributeName? = null,
    val attributeValue: String? = null,
    val attributeValueDesc: String? = null,
    val conditionType: ConditionType? = null,
    val entityType: EntityType? = null,
    val id: Int? = null,
    val incentiveType: IncentiveType? = null,
)