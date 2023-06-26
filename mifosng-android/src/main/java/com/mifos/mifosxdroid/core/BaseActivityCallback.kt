/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.core

import androidx.appcompat.widget.SwitchCompat

/**
 * @author fomenkoo
 */
interface BaseActivityCallback {
    fun showProgress(message: String)
    fun setToolbarTitle(title: String)
    fun setUserStatus(userStatus: SwitchCompat)
    fun hideProgress()
    fun logout()
}