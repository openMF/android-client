/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.objects.client

/**
 * Created by ishankhanna on 09/02/14.
 */
data class Permission (
    var grouping: String? = null,

    var code: String? = null,

    var entityName: String? = null,

    var actionName: String? = null,

    var isSelected: Boolean = false
)