package com.mifos.core.objects.runreports

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Tarun on 03-08-17.
 */
@Parcelize
class FullParameterListResponse(
    var columnHeaders: List<ColumnHeader>,
    var data: List<DataRow>
) : Parcelable