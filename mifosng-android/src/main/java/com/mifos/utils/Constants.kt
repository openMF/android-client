/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.utils

/**
 * Created by satya on 13/04/14.
 */
object Constants {
    //Search Entities
    const val SEARCH_ENTITY_CLIENT = "CLIENT"
    const val SEARCH_ENTITY_GROUP = "GROUP"
    const val SEARCH_ENTITY_LOAN = "LOAN"
    const val SEARCH_ENTITY_SAVING = "SAVING"
    const val SEARCH_ENTITY_CENTER = "CENTER"
    const val CLIENT_NAME = "clientName"
    const val CLIENT_ID = "clientId"
    const val ID = "id"
    const val CLIENT = "Client"
    const val CENTER_ID = "centerId"
    const val GROUP_ID = "groupId"
    const val GROUP_NAME = "groupName"
    const val GROUPS = "groups"
    const val GROUP_ACCOUNT = "isGroupAccount"
    const val CENTER = "center"
    const val STOP_TRACKING = "stop_tracking"
    const val SERVICE_STATUS = "service_status"

    //This needs to be 8 bits because validateRequestPermissionsRequestCode
    // in FragmentActivity requires requestCode to be of 8 bits, meaning the range is from 0 to 255.
    const val REQUEST_PERMISSION_SETTING = 254

    /**
     * User Logged In Status
     * 0 for Online and 1 for Offline
     */
    const val USER_ONLINE = false
    const val USER_OFFLINE = true

    /**
     * Constant to identify whether Simple Collection Sheet fragment has to be opened
     * or the Individual Collection Sheet.
     */
    const val COLLECTION_TYPE = "collection_type"
    const val EXTRA_COLLECTION_INDIVIDUAL = "individual"
    const val EXTRA_COLLECTION_COLLECTION = "collection"

    /**
     * Constants related to RunReports
     */

    const val HAS_SETTING_CHANGED = "hasSettingsChanged"
}