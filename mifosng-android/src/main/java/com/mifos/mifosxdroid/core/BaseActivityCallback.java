/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.core;

import androidx.appcompat.widget.SwitchCompat;

/**
 * @author fomenkoo
 */
public interface BaseActivityCallback {
    void showProgress(String message);

    void setToolbarTitle(String title);

    void setUserStatus(SwitchCompat userStatus);

    void hideProgress();

    void logout();
}
