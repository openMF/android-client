/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mobile-wallet/blob/master/LICENSE.md
 */
package org.mifos.core.datastore

expect annotation class Parcelize()

//expect annotation class IgnoredOnParcel()

expect interface Parceler<P> {
    fun create(parcel: Parcel): P

    fun P.write(parcel: Parcel, flags: Int)
}

expect annotation class TypeParceler<T, P : Parceler<in T>>()

expect class Parcel {
   fun readByte(): Byte
    fun readInt(): Int

    fun readFloat(): Float
    fun readDouble(): Double
    fun readString(): String?

    fun writeByte(value: Byte)
    fun writeInt(value: Int)

    fun writeFloat(value: Float)

    fun writeDouble(value: Double)
    fun writeString(value: String?)
}
