package com.mifos.mifosxdroid.core;

/**
 * @author fomenkoo
 */
public interface BaseActivityCallback {
    void showProgress(String message);

    void setToolbarTitle(String title);

    void hideProgress();

    void logout();
}
