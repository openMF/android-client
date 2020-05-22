/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.core;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.mifos.mifosxdroid.R;
import com.mifos.utils.Network;

/**
 * @author fomenkoo
 */
public class MifosBaseFragment extends Fragment {

    private BaseActivityCallback callback;
    private Activity activity;
    private InputMethodManager inputManager;
    private MifosProgressBarHandler mMifosProgressBarHandler;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        try {
            callback = (BaseActivityCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement " +
                    "BaseActivityCallback");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inputManager = (InputMethodManager) getActivity().getSystemService(Context
                .INPUT_METHOD_SERVICE);
        mMifosProgressBarHandler = new MifosProgressBarHandler(getActivity());
    }

    public void showAlertDialog(String title, String message) {
        new MaterialDialog.Builder().init(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(getStringMessage(R.string.dialog_action_ok), new
                        DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .createMaterialDialog()
                .show();
    }

    public Toolbar getToolbar() {
        return ((MifosBaseActivity) getActivity()).getToolbar();
    }

    protected void showMifosProgressDialog() {
        showMifosProgressDialog("Working...");
    }

    protected void showMifosProgressDialog(String message) {
        if (callback != null)
            callback.showProgress(message);
    }

    protected void hideMifosProgressDialog() {
        if (callback != null)
            callback.hideProgress();
    }

    protected void logout() {
        callback.logout();
    }

    protected void setToolbarTitle(String title) {
        callback.setToolbarTitle(title);
    }

    public void hideKeyboard(View view) {
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager
                .RESULT_UNCHANGED_SHOWN);
    }

    protected void showMifosProgressBar() {
        mMifosProgressBarHandler.show();
    }

    protected void hideMifosProgressBar() {
        mMifosProgressBarHandler.hide();
    }

    protected String getStringMessage(int message) {
        return getResources().getString(message);
    }

    protected Boolean isOnline() {
        return Network.isOnline(getActivity());
    }
}
