package com.mifos.core.common.utils

actual class Parcel {
    actual fun readByte(): Byte {
        TODO("Not yet implemented")
    }

    actual fun readInt(): Int {
        TODO("Not yet implemented")
    }

    actual fun readFloat(): Float {
        TODO("Not yet implemented")
    }

    actual fun readDouble(): Double {
        TODO("Not yet implemented")
    }

    actual fun readString(): String? {
        TODO("Not yet implemented")
    }

    actual fun writeByte(value: Byte) {
    }

    actual fun writeInt(value: Int) {
    }

    actual fun writeFloat(value: Float) {
    }

    actual fun writeDouble(value: Double) {
    }

    actual fun writeString(value: String?) {
    }

}

actual interface Parcelable
actual annotation class Parcelize actual constructor()
actual annotation class IgnoredOnParcel actual constructor()
actual interface Parceler<P> {
    actual fun create(parcel: Parcel): P
    actual fun P.write(parcel: Parcel, flags: Int)

}

actual annotation class TypeParceler<T, P : Parceler<in T>> actual constructor()