package com.mifos.objects

import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

data class CheckerTask(@SerializedName("id") var id: Int,
                       @SerializedName("madeOnDate") var madeOnDate: Long,
                       @SerializedName("processingResult") var status: String,
                       @SerializedName("maker") var maker: String,
                       @SerializedName("actionName") var action: String,
                       @SerializedName("entityName") var entity: String,
                       @SerializedName("resourceId") var resourceId: String) {

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