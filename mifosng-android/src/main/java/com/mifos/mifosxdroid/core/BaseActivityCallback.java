package com.mifos.mifosxdroid.core;

/**
 * @author fomenkoo
 */
public interface BaseActivityCallback {
    void showProgress(String message);

    void hideProgress();

    void logout();

    int getActionbarHeight();
}
