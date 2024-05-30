package com.mifos.core.objects.runreports

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Tarun on 03-08-17.
 */
@Parcelize
data class ColumnHeader(
    var columnDisplayType: String,
    var columnName: String,
    var columnType: String,
    var columnValues: List<String>,
    var isColumnNullable: Boolean,
    var isColumnPrimaryKey: Boolean
) : Parcelable