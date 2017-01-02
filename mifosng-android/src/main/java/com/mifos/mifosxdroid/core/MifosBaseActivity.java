/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.core;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.mifos.App;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.SplashScreenActivity;
import com.mifos.mifosxdroid.injection.component.ActivityComponent;
import com.mifos.mifosxdroid.injection.component.DaggerActivityComponent;
import com.mifos.mifosxdroid.injection.module.ActivityModule;
import com.mifos.mifosxdroid.online.DashboardActivity;
import com.mifos.utils.Constants;
import com.mifos.utils.PrefManager;

/**
 * @author fomenkoo
 */
public class MifosBaseActivity extends AppCompatActivity implements BaseActivityCallback {

    protected Toolbar toolbar;
    private ActivityComponent mActivityComponent;
    private ProgressDialog progress;


    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    public ActivityComponent getActivityComponent() {
        if (mActivityComponent == null) {
            mActivityComponent = DaggerActivityComponent.builder()
                    .activityModule(new ActivityModule(this))
                    .applicationComponent(App.get(this).getComponent())
                    .build();
        }
        return mActivityComponent;
    }

    public void setActionBarTitle(String title) {
        if (getSupportActionBar() != null && getTitle() != null) {
            setTitle(title);
        }
    }

    protected void showBackButton() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    protected void setActionBarTitle(int title) {
        setActionBarTitle(getResources().getString(title));
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showProgress(String message) {
        if (progress == null) {
            progress = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
            progress.setCancelable(false);
        }
        progress.setMessage(message);
        progress.show();

    }

    @Override
    public void setToolbarTitle(String title) {
        setActionBarTitle(title);
    }

    @Override
    public void setUserStatus(SwitchCompat userStatus) {
        if (PrefManager.getUserStatus() == Constants.USER_ONLINE) {
            userStatus.setChecked(false);
        } else if (PrefManager.getUserStatus() == Constants.USER_OFFLINE) {
            userStatus.setChecked(true);
        }
    }

    @Override
    public void hideProgress() {
        if (progress != null && progress.isShowing())
            progress.dismiss();
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputManager = (InputMethodManager) this
                 .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager
                 .RESULT_UNCHANGED_SHOWN);
    }

    @Override
    public void logout() {
        PrefManager.clearPrefs();
        startActivity(new Intent(this, SplashScreenActivity.class));
        finish();
    }

    public void replaceFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        invalidateOptionsMenu();
        String backStateName = fragment.getClass().getName();
        boolean fragmentPopped = getSupportFragmentManager().popBackStackImmediate(backStateName,
                0);

        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            DashboardActivity.fragmentBackStackEmpty = true;
        } else {
            DashboardActivity.fragmentBackStackEmpty = false;
        }

        if (!fragmentPopped && getSupportFragmentManager().findFragmentByTag(backStateName) ==
                null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(containerId, fragment, backStateName);
            if (addToBackStack) {
                transaction.addToBackStack(backStateName);
                DashboardActivity.fragmentBackStackEmpty = false;
            } else if (!DashboardActivity.fragmentBackStackEmpty) {
                DashboardActivity.lastNonBackStackFragment = backStateName;
            }
            transaction.commit();
        }
    }
}
