/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.NavigationView;
import android.support.test.espresso.IdlingResource;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.activity.pathtracking.PathTrackingActivity;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.offline.offlinedashbarod.OfflineDashboardFragment;
import com.mifos.mifosxdroid.online.centerlist.CenterListFragment;
import com.mifos.mifosxdroid.online.clientlist.ClientListFragment;
import com.mifos.mifosxdroid.online.createnewcenter.CreateNewCenterFragment;
import com.mifos.mifosxdroid.online.createnewclient.CreateNewClientFragment;
import com.mifos.mifosxdroid.online.createnewgroup.CreateNewGroupFragment;
import com.mifos.mifosxdroid.online.groupslist.GroupsListFragment;
import com.mifos.mifosxdroid.online.search.SearchFragment;
import com.mifos.objects.user.User;
import com.mifos.utils.Constants;
import com.mifos.utils.EspressoIdlingResource;
import com.mifos.utils.PrefManager;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    private Menu menu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        ButterKnife.bind(this);

        replaceFragment(new SearchFragment(), false, R.id.container);

        // setup navigation drawer and Navigation Toggle click and Offline Mode SwitchButton
        setupNavigationBar();

        //addOnBackStackChangedListener
        //to change title after Back Stack Changed
        addOnBackStackChangedListener();
    }

    private void addOnBackStackChangedListener() {
        if (getSupportFragmentManager() == null) {
            return;
        }
        getSupportFragmentManager()
                .addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        Fragment fragment = fragmentManager.findFragmentById(R.id.container);
                        if (fragment instanceof CreateNewClientFragment) {
                            setActionBarTitle(R.string.create_client);
                            setMenuCreateClient(false);
                            setMenuCreateCentre(true);
                            setMenuCreateGroup(true);
                        } else if (fragment instanceof CreateNewGroupFragment) {
                            setActionBarTitle(R.string.create_group);
                            setMenuCreateClient(true);
                            setMenuCreateCentre(true);
                            setMenuCreateGroup(false);
                        } else if (fragment instanceof CreateNewCenterFragment) {
                            setActionBarTitle(R.string.create_center);
                            setMenuCreateClient(true);
                            setMenuCreateCentre(false);
                            setMenuCreateGroup(true);
                        }
                    }
                });

    }

    private void setMenuCreateGroup(boolean isEnabled) {
        if (menu != null) {
            //position of mItem_create_new_group is 2
            menu.getItem(2).setEnabled(isEnabled);
        }

    }

    private void setMenuCreateCentre(boolean isEnabled) {
        if (menu != null) {
            //position of mItem_create_new_centre is 1
            menu.getItem(1).setEnabled(isEnabled);
        }
    }

    private void setMenuCreateClient(boolean isEnabled) {
        if (menu != null) {
            //position of mItem_create_new_client is 0
            menu.getItem(0).setEnabled(isEnabled);
        }
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

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (slideOffset != 0)
                    hideKeyboard(mDrawerLayout);
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };
        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
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
        clearFragmentBackStack();
        final Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.item_dashboard:
                replaceFragment(new SearchFragment(), false, R.id.container);
                break;
            case R.id.item_clients:
                replaceFragment(ClientListFragment.newInstance(), false, R.id.container);
                break;
            case R.id.item_groups:
                replaceFragment(GroupsListFragment.newInstance(), false, R.id.container);
                break;
            case R.id.item_centers:
                replaceFragment(CenterListFragment.newInstance(), false, R.id.container);
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
     * downloads the logged in user's username
     * sets dummy profile picture as no profile picture attribute available
     */
    private void loadClientDetails() {

        // download logged in user
        final User loggedInUser = PrefManager.getUser();

        TextView textViewUsername = ButterKnife.findById(mNavigationHeader, R.id.tv_user_name);
        textViewUsername.setText(loggedInUser.getUsername());

        // no profile picture credential, using dummy profile picture
        ImageView imageViewUserPicture = ButterKnife
                .findById(mNavigationHeader, R.id.iv_user_picture);
        imageViewUserPicture.setImageResource(R.drawable.ic_dp_placeholder);
    }

    @Override
    public void onBackPressed() {
        // check if the nav mDrawer is open
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            setMenuCreateClient(true);
            setMenuCreateCentre(true);
            setMenuCreateGroup(true);
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        hideKeyboard(mDrawerLayout);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mItem_create_new_client:
                setActionBarTitle(R.string.create_client);
                openCreateClient();
                break;
            case R.id.mItem_create_new_center:
                setActionBarTitle(R.string.create_center);
                openCreateCenter();
                break;
            case R.id.mItem_create_new_group:
                openCreateGroup();
                setActionBarTitle(R.string.create_group);
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


