/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.common.utils

expect interface Parcelable
expect annotation class IgnoredOnParcel
expect annotation class Parcelize
expect interface Parceler<P> {
    fun create(parcel: Parcel): P
    fun P.write(parcel: Parcel, flags: Int)
}

expect annotation class TypeParceler<T, P : Parceler<in T>>

expect class Parcel {
    fun readString(): String?
    fun readByte(): Byte
    fun readInt(): Int
    fun readFloat(): Float
    fun readDouble(): Double
    fun writeByte(value: Byte)
    fun writeInt(value: Int)
    fun writeFloat(value: Float)
    fun writeDouble(value: Double)
    fun writeString(value: String?)
}
