/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.core;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.mifos.App;
import com.mifos.api.ApiRequestInterceptor;
import com.mifos.mifosxdroid.ClientListActivity;
import com.mifos.mifosxdroid.GroupListActivity;
import com.mifos.mifosxdroid.OfflineCenterInputActivity;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.SplashScreenActivity;
import com.mifos.mifosxdroid.SurveyActivity;
import com.mifos.mifosxdroid.activity.PathTrackingActivity;
import com.mifos.mifosxdroid.online.CentersActivity;
import com.mifos.mifosxdroid.online.ClientListFragment;
import com.mifos.mifosxdroid.online.ClientSearchFragment;
import com.mifos.mifosxdroid.online.GroupsListFragment;
import com.mifos.objects.client.Client;
import com.mifos.utils.PrefManager;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * @author fomenkoo
 */
public class MifosBaseActivity extends AppCompatActivity implements BaseActivityCallback, NavigationView.OnNavigationItemSelectedListener {

    private ProgressDialog progress;
    protected Toolbar toolbar;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
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
    public void hideProgress() {
        if (progress != null && progress.isShowing())
            progress.dismiss();
    }

    @Override
    public void logout() {
        PrefManager.clearToken();
        startActivity(new Intent(this, SplashScreenActivity.class));
        finish();
    }

    public void replaceFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        invalidateOptionsMenu();
        String backStateName = fragment.getClass().getName();
        boolean fragmentPopped = getSupportFragmentManager().popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped && getSupportFragmentManager().findFragmentByTag(backStateName) == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(containerId, fragment, backStateName);
            if (addToBackStack) {
                transaction.addToBackStack(backStateName);
            }
            transaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        // check if the nav mDrawer is open
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START))
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        else
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // ignore the current selected item
        if (item.isChecked()) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            return false;
        }

        // select which activity to open
        final Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.item_dashboard:
                replaceFragment(new ClientSearchFragment(), false, R.id.container);
                break;
            case R.id.item_clients:
                replaceFragment(new ClientListFragment(), false, R.id.container);
                break;
            case R.id.item_groups:
                replaceFragment(new GroupsListFragment(), false, R.id.container);
                break;
            case R.id.item_centers:
                intent.setClass(getApplicationContext(), CentersActivity.class);
                startNavigationClickActivity(intent);
                break;
            case R.id.item_survey:
                intent.setClass(getApplicationContext(), SurveyActivity.class);
                startNavigationClickActivity(intent);
                break;
            case R.id.item_path_tracker:
                intent.setClass(getApplicationContext(), PathTrackingActivity.class);
                startNavigationClickActivity(intent);
                break;
            case R.id.item_offline:
                intent.setClass(getApplicationContext(), OfflineCenterInputActivity.class);
                startNavigationClickActivity(intent);
                break;

        }

        // close the drawer
        mDrawerLayout.closeDrawer(Gravity.LEFT);
        mNavigationView.setCheckedItem(R.id.item_dashboard);
        return true;
    }

    /**
     * sets up the navigation mDrawer in the activity
     */
    protected void setupNavigationBar() {

        // setup navigation view
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        // setup drawer layout and sync to toolbar
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // make an API call to fetch logged in client's details
        loadClientDetails();
    }

    /**
     * downloads the client name and picture(if exists)
     * sets the downloaded data to the nav drawer account header
     */
    private void loadClientDetails() {

        // download client details
        final int userId = PrefManager.getUserId();
        App.apiManager.getClient(userId, new Callback<Client>() {
                    @Override
                    public void success(Client client, final Response response) {

                        // add name to profile
                        String name = client.getDisplayName();
                        TextView textViewUsername = (TextView) findViewById(R.id.tv_user_name);
                        textViewUsername.setText(name);

                        // download image
                        if (client.isImagePresent()) {
                            String url = PrefManager.getInstanceUrl()
                                    + "/"
                                    + "clients/"
                                    + userId
                                    + "/images?maxHeight=120&maxWidth=120";
                            GlideUrl glideUrl = new GlideUrl(url, new LazyHeaders.Builder()
                                    .addHeader(ApiRequestInterceptor.HEADER_TENANT, PrefManager.getTenant())
                                    .addHeader(ApiRequestInterceptor.HEADER_AUTH, PrefManager.getToken())
                                    .addHeader("Accept", "application/octet-stream")
                                    .build());

                            ImageView imageViewUserPicture = (ImageView) findViewById(R.id.iv_user_picture);
                            Glide.with(getApplicationContext())
                                    .load(glideUrl)
                                    .asBitmap()
                                    .error(R.drawable.ic_account_circle)
                                    .placeholder(R.drawable.ic_account_circle)
                                    .into(new BitmapImageViewTarget(imageViewUserPicture) {
                                        @Override
                                        protected void setResource(Bitmap result) {
                                            // check a valid bitmap is downloaded
                                            if (result == null || result.getWidth() == 0)
                                                return;

                                            // set to image view
                                            ImageView imageViewUserPicture = (ImageView) findViewById(R.id.iv_user_picture);
                                            imageViewUserPicture.setImageBitmap(result);
                                        }
                                    });
                        }
                    }


                    @Override
                    public void failure(RetrofitError error) {

                    }
                }

        );
    }

    public void startNavigationClickActivity(final Intent intent){
        android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);

            }
        }, 500);
    }

}
