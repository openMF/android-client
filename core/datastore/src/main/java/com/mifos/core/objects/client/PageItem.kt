/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.client

import com.mifos.core.objects.Timeline

/**
 * Created by ishankhanna on 09/02/14.
 */
data class PageItem(
    var id: Int = 0,

    var accountNo: String? = null,

    var status: Status? = null,

    var isActive: Boolean = false,

    var activationDate: List<Int> = ArrayList(),

    var firstname: String? = null,

    var middlename: String? = null,

    var lastname: String? = null,

    var displayName: String? = null,

    var officeId: Int = 0,

    var officeName: String? = null,

    var staffId: Int = 0,

    var staffName: String? = null,

    var timeline: Timeline? = null,

    var fullname: String? = null,

    var imageId: Int = 0,

    var isImagePresent: Boolean = false,

    var externalId: String? = null
)