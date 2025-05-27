package com.mifos.core.common.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun formatDate(millis: Long): String {
    val dateTime = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.currentSystemDefault())
    return "${dateTime.dayOfMonth} ${dateTime.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${dateTime.year}"
}