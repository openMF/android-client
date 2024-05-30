/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.collectionsheet

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by ishankhanna on 16/07/14.
 */
@Parcelize
data class EntityType(
    var id: Int? = null,

    var code: String? = null,

    var value: String? = null
) : Parcelable