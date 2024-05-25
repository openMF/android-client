/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.noncore

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by ishankhanna on 03/07/14.
 */
@Parcelize
data class DocumentType(
    var id: Int? = null,

    var name: String? = null,

    var active: Boolean? = null,

    var mandatory: Boolean? = null,

    var description: String? = null,

    var position: Int? = null
) : Parcelable