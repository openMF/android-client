package com.mifos.core.common.utils

import android.content.Context

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import androidx.core.content.ContextCompat
import com.mifos.core.common.R
import com.mifos.core.objects.PaymentTypeOption
import rx.Observable
import java.text.DateFormat
import java.util.Calendar
import java.util.TimeZone

object Utils {

    /**
     * This Method filtering the List<PaymentTypeOption> and if any PaymentTypeOption id is equal to
     * the paymentId. and return the match PaymentType Name.
     *
     * @param paymentId Payment Type Id
     * @param paymentTypeOptions PaymentTypeOptions
     * @return PaymentType Name
    </PaymentTypeOption> */
    fun getPaymentTypeName(
        paymentId: Int,
        paymentTypeOptions: List<PaymentTypeOption>?
    ): String? {
        return ""
//        return paymentTypeOptions
//            ?.firstOrNull { it.id == paymentId }
//            ?.name
    }

    fun getStringOfDate(dateObj: List<Int?>): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        dateObj.getOrNull(0)?.let { year ->
            calendar.set(Calendar.YEAR, year)
        }
        dateObj.getOrNull(1)?.let { month ->
            calendar.set(Calendar.MONTH, month - 1)
        }
        dateObj.getOrNull(2)?.let { day ->
            calendar.set(Calendar.DAY_OF_MONTH, day)
        }
        val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM)
        return dateFormat.format(calendar.time)
    }

}