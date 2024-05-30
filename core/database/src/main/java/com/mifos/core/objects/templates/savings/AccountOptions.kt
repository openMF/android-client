package com.mifos.core.objects.templates.savings

import android.os.Parcelable
import com.mifos.core.objects.accounts.loan.InterestType
import kotlinx.parcelize.Parcelize

/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
/**
 * Created by rajan on 13/3/16.
 */
@Parcelize
data class AccountOptions(
    var id: Int? = null,

    var name: String? = null,

    var glCode: Int? = null,

    var disabled: Boolean? = null,

    var manualEntriesAllowed: Boolean? = null,

    var type: InterestType? = null,

    var usage: InterestType? = null,

    var nameDecorated: String? = null,

    var tagId: TagId? = null
) : Parcelable