package com.mifos.core.objects.checkerinboxandtasks

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date

@Parcelize
data class CheckerTask(
    var id: Int,
    var madeOnDate: Long,
    var processingResult: String,
    var maker: String,
    var actionName: String,
    var entityName: String,
    var resourceId: String
) : Parcelable {

    var selectedFlag = false

    fun getDate(): String {
        val date = Date(madeOnDate)
        val dateFormat = SimpleDateFormat("dd MMM yyyy")
        return dateFormat.format(date)
    }

    fun getTimeStamp(): Timestamp {
        return Timestamp(madeOnDate)
    }
}