package com.mifos.core.objects.templates.clients

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by mayankjindal on 13/12/16.
 */
@Parcelize
data class ChargeTemplate(
    val penalty: Boolean,
    val chargeOptions: List<ChargeOptions>
) : Parcelable