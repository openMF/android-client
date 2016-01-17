package com.mifos.mifosxdroid.core;

/**
 * Created by Nasim Banu on 15 Jan 2016.
 */
public interface BaseActivityCallback {
    void showProgress(String message);

    void hideProgress();

    void logout();

    int getActionbarHeight();


}
