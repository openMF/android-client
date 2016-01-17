package com.mifos.mifosxdroid.core;

/**
 * Created by nunspecified on 16-01-15.
 */
public interface BaseActivityCallback {
    void showProgress(String message);

    void hideProgress();

    void logout();

    int getActionbarHeight();


}
