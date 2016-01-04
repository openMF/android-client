package com.mifos.mifosxdroid.core;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * @author fomenkoo
 */
public class BaseFragment extends Fragment {

    private BaseActivityCallback callback;
    private Activity activity;
    private InputMethodManager inputManager;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        try {
            callback = (BaseActivityCallback) activity;
        } catch (ClassCastException e) {
            // Stub
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public Toolbar getToolbar() {
        return ((BaseActivity) getActivity()).getToolbar();
    }

    protected void showProgress(String message) {
        if (callback != null)
            callback.showProgress(message);
    }

    protected void hideProgress() {
        if (callback != null)
            callback.hideProgress();
    }

    protected void logout() {
        callback.logout();
    }

    public void hideKeyboard(View view) {
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

}
