/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.NavigationView;
import android.support.test.espresso.IdlingResource;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SwitchCompat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.mifos.api.BaseApiManager;
import com.mifos.api.DataManager;
import com.mifos.api.MifosInterceptor;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.SurveyActivity;
import com.mifos.mifosxdroid.activity.PathTrackingActivity;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.offline.offlinedashbarod.OfflineDashboardFragment;
import com.mifos.mifosxdroid.online.clientlist.ClientListFragment;
import com.mifos.mifosxdroid.online.clientsearch.ClientSearchFragment;
import com.mifos.mifosxdroid.online.createnewcenter.CreateNewCenterFragment;
import com.mifos.mifosxdroid.online.createnewclient.CreateNewClientFragment;
import com.mifos.mifosxdroid.online.createnewgroup.CreateNewGroupFragment;
import com.mifos.mifosxdroid.online.groupslist.GroupsListFragment;
import com.mifos.objects.client.Client;
import com.mifos.utils.Constants;
import com.mifos.utils.EspressoIdlingResource;
import com.mifos.utils.PrefManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ishankhanna on 09/02/14.
 */
public class DashboardActivity extends MifosBaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = DashboardActivity.class.getSimpleName();

    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;

    @BindView(R.id.drawer)
    DrawerLayout mDrawerLayout;

    View mNavigationHeader;
    SwitchCompat userStatusToggle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ButterKnife.bind(this);

        replaceFragment(new ClientSearchFragment(), false, R.id.container);

        // setup navigation drawer and Navigation Toggle click and Offline Mode SwitchButton
        setupNavigationBar();
    }


    /**
     * sets up the navigation mDrawer in the activity
     */
    protected void setupNavigationBar() {

        mNavigationHeader = mNavigationView.getHeaderView(0);
        setupUserStatusToggle();
        mNavigationView.setNavigationItemSelectedListener(this);

        // setup drawer layout and sync to toolbar
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                setUserStatus(userStatusToggle);
            }
        };
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // make an API call to fetch logged in client's details
        loadClientDetails();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // ignore the current selected item
        /*if (item.isChecked()) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
            return false;
        }*/

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
                replaceFragment(OfflineDashboardFragment.newInstance(), false, R.id.container);
                break;

        }

        // close the drawer
        mDrawerLayout.closeDrawer(Gravity.LEFT);
        mNavigationView.setCheckedItem(R.id.item_dashboard);
        return true;
    }


    /**
     * This SwitchCompat Toggle Handling the User Status.
     * Setting the User Status to Offline or Online
     */
    public void setupUserStatusToggle() {
        userStatusToggle
                = (SwitchCompat) mNavigationHeader.findViewById(R.id.user_status_toggle);
        if (PrefManager.getUserStatus() == Constants.USER_OFFLINE) {
            userStatusToggle.setChecked(true);
        }

        userStatusToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PrefManager.getUserStatus() == Constants.USER_OFFLINE) {
                    PrefManager.setUserStatus(Constants.USER_ONLINE);
                    userStatusToggle.setChecked(false);
                } else {
                    PrefManager.setUserStatus(Constants.USER_OFFLINE);
                    userStatusToggle.setChecked(true);
                }
            }
        });
    }

    public void startNavigationClickActivity(final Intent intent) {
        android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);

            }
        }, 500);
    }


    /**
     * downloads the client name and picture(if exists)
     * sets the downloaded data to the nav drawer account header
     */
    //TODO Setup In MVP
    private void loadClientDetails() {

        // download client details
        final int userId = PrefManager.getUserId();
        BaseApiManager baseApiManager = new BaseApiManager();
        DataManager mDataManager = new DataManager(baseApiManager);
        Observable<Client> call = mDataManager.getClient(userId);
        Subscription subscription = call.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Client>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Client client) {
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
                                    .addHeader(MifosInterceptor.HEADER_TENANT, PrefManager
                                            .getTenant())
                                    .addHeader(MifosInterceptor.HEADER_AUTH, PrefManager
                                            .getToken())
                                    .addHeader("Accept", "application/octet-stream")
                                    .build());

                            ImageView imageViewUserPicture = (ImageView) findViewById(R.id
                                    .iv_user_picture);
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
                                            ImageView imageViewUserPicture = (ImageView)
                                                    findViewById(R.id.iv_user_picture);
                                            imageViewUserPicture.setImageBitmap(result);
                                        }
                                    });
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        // check if the nav mDrawer is open
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.client_search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mItem_create_new_client:
                openCreateClient();
                break;
            case R.id.mItem_create_new_center:
                openCreateCenter();
                break;
            case R.id.mItem_create_new_group:
                openCreateGroup();
                break;
            case R.id.logout:
                logout();
                break;
            default: //DO NOTHING
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openCreateClient() {
        replaceFragment(CreateNewClientFragment.newInstance(), true, R.id.container);
    }

    public void openCreateCenter() {
        replaceFragment(CreateNewCenterFragment.newInstance(), true, R.id.container);

    }

    public void openCreateGroup() {
        replaceFragment(CreateNewGroupFragment.newInstance(), true, R.id.container);
    }

    @VisibleForTesting
    public IdlingResource getCountingIdlingResource() {
        return EspressoIdlingResource.getIdlingResource();
    }
}


