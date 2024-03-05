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
data class Identifier(
    var id: Int? = null,

    var clientId: Int? = null,

    var documentKey: String? = null,

    var documentType: DocumentType? = null,

    var description: String? = null,

    var status: String? = null
) : Parcelable