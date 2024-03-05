package com.mifos.core.objects.accounts.savings

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by nkiboi on 12/15/2015.
 */
@Parcelize
class FieldOfficerOptions(
    var id: Int? = null,

    var firstname: String? = null,

    var lastname: String? = null,

    var displayName: String? = null
) : Parcelable