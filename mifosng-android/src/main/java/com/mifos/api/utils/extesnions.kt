package com.mifos.api.utils

import java.util.*

fun Date.toArray(): List<Int> {
    return listOf(this.year, this.month, this.date)
}

fun Date.toIntArray(): IntArray {
    return IntArray(3).let {
        it.plus(this.year)
        it.plus(this.month)
        it.plus(this.date)
    }
}

fun IntArray.toDate(): Date {
    return Date(this[0], this[1], this[2])
}