package com.mifos.objects.runreports

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator

/**
 * Created by Tarun on 03-08-17.
 */
class FullParameterListResponse : Parcelable {
    var columnHeaders: List<ColumnHeader>? = null
    var data: List<DataRow>? = null
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeTypedList(columnHeaders)
        dest.writeTypedList(data)
    }

    constructor() {}
    protected constructor(`in`: Parcel) {
        columnHeaders = `in`.createTypedArrayList(ColumnHeader.CREATOR)
        data = `in`.createTypedArrayList(DataRow.CREATOR)
    }

    companion object {
        val CREATOR: Creator<FullParameterListResponse?> =
            object : Creator<FullParameterListResponse?> {
                override fun createFromParcel(source: Parcel): FullParameterListResponse? {
                    return FullParameterListResponse(source)
                }

                override fun newArray(size: Int): Array<FullParameterListResponse?> {
                    return arrayOfNulls(size)
                }
            }
    }
}