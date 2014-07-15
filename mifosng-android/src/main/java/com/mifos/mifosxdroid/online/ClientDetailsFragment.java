package com.mifos.mifosxdroid.online;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.joanzapata.android.iconify.Iconify;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.LoanAccountsListAdapter;
import com.mifos.mifosxdroid.adapters.SavingsAccountsListAdapter;
import com.mifos.objects.User;
import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.objects.client.Client;
import com.mifos.objects.noncore.DataTable;
import com.mifos.services.API;
import com.mifos.services.data.GpsCoordinatesRequest;
import com.mifos.services.data.GpsCoordinatesResponse;
import com.mifos.utils.Constants;
import com.mifos.utils.DateHelper;
import com.mifos.utils.FragmentConstants;
import com.mifos.utils.SafeUIBlockingUtility;

import org.apache.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;


public class ClientDetailsFragment extends Fragment implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {
    /*
    * Define a request code to send to Google Play services
    * This code is returned in Activity.onActivityResult
    */
    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    /**
     * IDs for Inflation of Menus and their Items
     */
    public static final int MENU_ITEM_SAVE_LOCATION = 1000;
    public static final int MENU_ITEM_DATA_TABLES = 1001;
    public static final int MENU_ITEM_DOCUMENTS = 1003;
    public static final int MENU_ITEM_IDENTIFIERS = 1004;

    // Intent response codes. Each response code must be a unique integer.
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    public static int clientId;
    public static List<DataTable> clientDataTables = new ArrayList<DataTable>();
    @InjectView(R.id.tv_fullName)
    TextView tv_fullName;
    @InjectView(R.id.tv_accountNumber)
    TextView tv_accountNumber;
    @InjectView(R.id.tv_externalId)
    TextView tv_externalId;
    @InjectView(R.id.tv_activationDate)
    TextView tv_activationDate;
    @InjectView(R.id.tv_office)
    TextView tv_office;
    @InjectView(R.id.tv_group)
    TextView tv_group;
    @InjectView(R.id.tv_loanOfficer)
    TextView tv_loanOfficer;
    @InjectView(R.id.tv_loanCycle)
    TextView tv_loanCycle;
    @InjectView(R.id.tv_toggle_loan_accounts_icon)
    TextView tv_toggle_loan_accounts_icon;
    @InjectView(R.id.tv_toggle_savings_accounts_icon)
    TextView tv_toggle_savings_accounts_icon;
    @InjectView(R.id.tv_toggle_loan_accounts)
    TextView tv_toggle_loan_accounts;
    @InjectView(R.id.tv_toggle_savings_accounts)
    TextView tv_toggle_savings_accounts;
    @InjectView(R.id.tv_count_loan_accounts)
    TextView tv_count_loan_accounts;
    @InjectView(R.id.tv_count_savings_accounts)
    TextView tv_count_savings_accounts;
    @InjectView(R.id.lv_accounts_loans)
    ListView lv_accounts_loans;
    @InjectView(R.id.lv_accounts_savings)
    ListView lv_accounts_savings;
    @InjectView(R.id.iv_clientImage)
    ImageView iv_clientImage;

    View rootView;

    SafeUIBlockingUtility safeUIBlockingUtility;

    ActionBarActivity activity;

    SharedPreferences sharedPreferences;

    ActionBar actionBar;

    boolean isLoanAccountsListOpen = false;
    boolean isSavingsAccountsListOpen = false;
    private OnFragmentInteractionListener mListener;
    private File capturedClientImageFile;
    // Null if play services are not available.
    private LocationClient mLocationClient;
    // True if play services are available and location services are connected.
    private AtomicBoolean locationAvailable = new AtomicBoolean(false);

    /**Image Loading Task for this instance
      Creating an instance object because if the fragment detaches itself from the activity
      The task might throw IllegalStateException
      So it is important to kill the task before the fragment detaches itself from the activity
    */
    private ImageLoadingAsyncTask imageLoadingAsyncTask;

    public ClientDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param clientId Client's Id
     * @return A new instance of fragment ClientDetailsFragment.
     */
    public static ClientDetailsFragment newInstance(int clientId) {
        ClientDetailsFragment fragment = new ClientDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.CLIENT_ID, clientId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            clientId = getArguments().getInt(Constants.CLIENT_ID);
        }

        //Necessary Call to add and update the Menu in a Fragment
        setHasOptionsMenu(true);

        capturedClientImageFile = new File(getActivity().getExternalCacheDir(), "client_image.png");

        // Initialize location client only if play services are available.
        if (servicesConnected()) {
            mLocationClient = new LocationClient(getActivity(), this, this);
        } else {
            mLocationClient = null;
            locationAvailable.set(false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_client_details, container, false);
        activity = (ActionBarActivity) getActivity();
        safeUIBlockingUtility = new SafeUIBlockingUtility(ClientDetailsFragment.this.getActivity());
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        actionBar = activity.getSupportActionBar();
        ButterKnife.inject(this, rootView);
        inflateClientInformation();

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {

        if(imageLoadingAsyncTask != null) {

            if (!imageLoadingAsyncTask.getStatus().equals(AsyncTask.Status.FINISHED)) {
                imageLoadingAsyncTask.cancel(true);
            }
        }

        super.onDetach();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                onClientImageCapture(resultCode, data);
                break;
        }
    }

    /**
     * Initialize the contents of the Activity's standard options menu.  You
     * should place your menu items in to <var>menu</var>.  For this method
     * to be called, you must have first called {@link #setHasOptionsMenu}.  See
     * {@link android.app.Activity#onCreateOptionsMenu(android.view.Menu) Activity.onCreateOptionsMenu}
     * for more information.
     *
     * @param menu     The options menu in which you place your items.
     * @param inflater
     * @see #setHasOptionsMenu
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Prepare the Screen's standard options menu to be displayed.  This is
     * called right before the menu is shown, every time it is shown.  You can
     * use this method to efficiently enable/disable items or otherwise
     * dynamically modify the contents.  See
     * {@link android.app.Activity#onPrepareOptionsMenu(android.view.Menu) Activity.onPrepareOptionsMenu}
     * for more information.
     *
     * @param menu The options menu as last shown or first initialized by
     *             onCreateOptionsMenu().
     * @see #setHasOptionsMenu
     * @see #onCreateOptionsMenu
     */
    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        menu.clear();
        //TODO : Localisation of Menu Item Strings
        menu.addSubMenu(Menu.NONE, MENU_ITEM_DATA_TABLES, Menu.NONE, Constants.DATA_TABLE_CLIENTS_NAME);
        //TODO Enable Save Location
        //menu.add(Menu.NONE, MENU_ITEM_SAVE_LOCATION, Menu.NONE, getString(R.string.action_save_location));
        menu.add(Menu.NONE, MENU_ITEM_DOCUMENTS, Menu.NONE, getString(R.string.documents));
        menu.add(Menu.NONE, MENU_ITEM_IDENTIFIERS, Menu.NONE, getString(R.string.identifiers));
        // This is the ID of Each data table which will be used in onOptionsItemSelected Method
        int SUBMENU_ITEM_ID = 0;

        // Create a Sub Menu that holds a link to all data tables
        SubMenu dataTableSubMenu = menu.getItem(0).getSubMenu();
        if (dataTableSubMenu != null && clientDataTables != null && clientDataTables.size() > 0) {
            Iterator<DataTable> dataTableIterator = clientDataTables.iterator();
            while (dataTableIterator.hasNext()) {
                dataTableSubMenu.add(Menu.NONE, SUBMENU_ITEM_ID, Menu.NONE, dataTableIterator.next().getRegisteredTableName());
                SUBMENU_ITEM_ID++;
            }
        }
        super.onPrepareOptionsMenu(menu);
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     * <p/>
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == MENU_ITEM_SAVE_LOCATION) {

            saveLocation();
        } else if (id >= 0 && id < clientDataTables.size()) {

            DataTableDataFragment dataTableDataFragment
                    = DataTableDataFragment.newInstance(clientDataTables.get(id), clientId);
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_DETAILS);
            fragmentTransaction.replace(R.id.global_container, dataTableDataFragment);
            fragmentTransaction.commit();
        } else if (id == MENU_ITEM_DOCUMENTS) {

            loadDocuments();

        } else if (id == MENU_ITEM_IDENTIFIERS) {

            loadIdentifiers();
        }


        return super.onOptionsItemSelected(item);
    }

    public void inflateClientInformation() {

        getClientDetails();

    }

    public void captureClientImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(capturedClientImageFile));
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    public void deleteClientImage() {
        API.clientService.deleteClientImage(clientId, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Toast.makeText(activity, "Image deleted", Toast.LENGTH_SHORT).show();
                iv_clientImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher));
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Toast.makeText(activity, "Failed to delete image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onClientImageCapture(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            uploadImage(capturedClientImageFile);
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // User cancelled the image capture.
        } else {
            Toast.makeText(activity, "Failed to capture image", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage(File pngFile) {
        API.clientService.uploadClientImage(clientId,
                new TypedFile("image/png", pngFile),
                new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response2) {
                        Toast.makeText(activity, "Client image updated", Toast.LENGTH_SHORT).show();
                        imageLoadingAsyncTask = new ImageLoadingAsyncTask();
                        imageLoadingAsyncTask.execute(clientId);

                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        Toast.makeText(activity, "Failed to update image", Toast.LENGTH_SHORT).show();
                        imageLoadingAsyncTask = new ImageLoadingAsyncTask();
                        imageLoadingAsyncTask.execute(clientId);

                    }
                }
        );
    }

    /**
     * Use this method to fetch and inflate client details
     * in the fragment
     */
    public void getClientDetails() {

        safeUIBlockingUtility.safelyBlockUI();

        API.clientService.getClient(clientId, new Callback<Client>() {
            @Override
            public void success(final Client client, Response response) {

                if (client != null) {
                    actionBar.setTitle(getString(R.string.client) + " - " + client.getLastname());
                    tv_fullName.setText(client.getDisplayName());
                    tv_accountNumber.setText(client.getAccountNo());
                    tv_externalId.setText(client.getExternalId());

                    try {
                        tv_activationDate.setText(DateHelper.getDateAsString(client.getActivationDate()));
                    }catch (IndexOutOfBoundsException e) {
                        Toast.makeText(getActivity(), getString(R.string.error_client_inactive), Toast.LENGTH_SHORT).show();
                    } finally {
                        tv_activationDate.setText("");
                    }
                    tv_office.setText(client.getOfficeName());

                    // TODO: For some reason Retrofit always calls the failure() method even after
                    // receiving a 200 response with image bytes. Perhaps we need to change the
                    // argument type from TypedFile to something else?
                    if (client.isImagePresent()) {

                        imageLoadingAsyncTask = new ImageLoadingAsyncTask();
                        imageLoadingAsyncTask.execute(client.getId());

                    }

                    iv_clientImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            PopupMenu menu = new PopupMenu(getActivity(), view);
                            menu.getMenuInflater().inflate(R.menu.client_image_popup, menu.getMenu());
                            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem menuItem) {
                                    switch (menuItem.getItemId()) {
                                        case R.id.client_image_capture:
                                            captureClientImage();
                                            break;
                                        case R.id.client_image_remove:
                                            deleteClientImage();
                                            break;
                                        default:
                                            Log.e("ClientDetailsFragment", "Unrecognized client image menu item");
                                    }
                                    return true;
                                }
                            });
                            menu.show();
                        }
                    });

                    safeUIBlockingUtility.safelyUnBlockUI();

                    inflateClientsAccounts();


                }

            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Toast.makeText(activity, "Client not found.", Toast.LENGTH_SHORT).show();
                safeUIBlockingUtility.safelyUnBlockUI();

            }
        });


    }

    /**
     * Use this method to fetch and inflate all loan and savings accounts
     * of the client and inflate them in the fragment
     */
    public void inflateClientsAccounts() {

        safeUIBlockingUtility.safelyBlockUI();

        API.clientAccountsService.getAllAccountsOfClient(clientId, new Callback<ClientAccounts>() {
            @Override
            public void success(final ClientAccounts clientAccounts, Response response) {

                // Proceed only when the fragment is added to the activity.
                if (!isAdded()) {
                    return;
                }

                final String loanAccountsStringResource = getResources().getString(R.string.loanAccounts);
                final String savingsAccountsStringResource = getResources().getString(R.string.savingAccounts);

                final Iconify.IconValue listOpenIcon = Iconify.IconValue.fa_minus_circle;
                final Iconify.IconValue listClosedIcon = Iconify.IconValue.fa_plus_circle;
                tv_toggle_loan_accounts_icon.setText(listClosedIcon.formattedName());
                tv_toggle_savings_accounts_icon.setText(listClosedIcon.formattedName());

                Iconify.addIcons(tv_toggle_loan_accounts_icon, tv_toggle_savings_accounts_icon);


                if (clientAccounts.getLoanAccounts().size() > 0) {
                    LoanAccountsListAdapter loanAccountsListAdapter =
                            new LoanAccountsListAdapter(getActivity().getApplicationContext(), clientAccounts.getLoanAccounts());
                    tv_toggle_loan_accounts_icon.setText(listClosedIcon.formattedName());
                    tv_toggle_loan_accounts.setText(loanAccountsStringResource);
                    tv_count_loan_accounts.setText(String.valueOf(clientAccounts.getLoanAccounts().size()));
                    tv_toggle_loan_accounts.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!isLoanAccountsListOpen) {
                                isLoanAccountsListOpen = true;

                                tv_toggle_loan_accounts_icon.setText(listOpenIcon.formattedName());
                                //TODO SIZE AND ANIMATION TO BE ADDED
                                //Drop Down and Fold Up
                                //Calculate Size of 1 cell and show a couple of them
                                isSavingsAccountsListOpen = false;
                                tv_toggle_savings_accounts_icon.setText(listClosedIcon.formattedName());
                                lv_accounts_savings.setVisibility(View.GONE);
                                lv_accounts_loans.setVisibility(View.VISIBLE);
                            } else {
                                isLoanAccountsListOpen = false;
                                tv_toggle_loan_accounts_icon.setText(listClosedIcon.formattedName());
                                //TODO SIZE AND ANIMATION TO BE ADDED
                                //Drop Down and Fold Up
                                //Calculate Size of 1 cell and show a couple of them
                                lv_accounts_loans.setVisibility(View.GONE);
                            }

                            //Adding Icons to the TextView by this call
                            Iconify.addIcons(tv_toggle_loan_accounts_icon, tv_toggle_savings_accounts_icon);

                        }
                    });
                    lv_accounts_loans.setAdapter(loanAccountsListAdapter);
                    lv_accounts_loans.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            mListener.loadLoanAccountSummary(clientAccounts.getLoanAccounts().get(i).getId());

                        }
                    });

                    //This is used to handle touch events on the list view and consume them without
                    //passing onto scroll view
                    lv_accounts_loans.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {

                            view.getParent().requestDisallowInterceptTouchEvent(true);

                            return false;
                        }
                    });
                }

                if (clientAccounts.getSavingsAccounts().size() > 0) {
                    SavingsAccountsListAdapter savingsAccountsListAdapter =
                            new SavingsAccountsListAdapter(getActivity().getApplicationContext(), clientAccounts.getSavingsAccounts());
                    tv_toggle_savings_accounts_icon.setText(listClosedIcon.formattedName());
                    tv_toggle_savings_accounts.setText(savingsAccountsStringResource);
                    tv_count_savings_accounts.setText(String.valueOf(clientAccounts.getSavingsAccounts().size()));
                    tv_toggle_savings_accounts.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!isSavingsAccountsListOpen) {
                                isSavingsAccountsListOpen = true;
                                tv_toggle_savings_accounts_icon.setText(listOpenIcon.formattedName());
                                //TODO SIZE AND ANIMATION TO BE ADDED
                                //Drop Down and Fold Up
                                //Calculate Size of 1 cell and show a couple of them
                                isLoanAccountsListOpen = false;
                                tv_toggle_loan_accounts_icon.setText(listClosedIcon.formattedName());
                                lv_accounts_loans.setVisibility(View.GONE);
                                lv_accounts_savings.setVisibility(View.VISIBLE);
                            } else {
                                isSavingsAccountsListOpen = false;
                                tv_toggle_savings_accounts_icon.setText(listClosedIcon.formattedName());
                                //TODO SIZE AND ANIMATION TO BE ADDED
                                //Drop Down and Fold Up
                                //Calculate Size of 1 cell and show a couple of them
                                lv_accounts_savings.setVisibility(View.GONE);
                            }
                            //Adding Icons to the TextView by this call
                            Iconify.addIcons(tv_toggle_loan_accounts_icon, tv_toggle_savings_accounts_icon);

                        }
                    });

                    lv_accounts_savings.setAdapter(savingsAccountsListAdapter);
                    lv_accounts_savings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            mListener.loadSavingsAccountSummary(clientAccounts.getSavingsAccounts().get(i).getId());
                        }
                    });

                    //This is used to handle touch events on the list view and consume them without
                    //passing onto scroll view
                    lv_accounts_savings.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {

                            view.getParent().requestDisallowInterceptTouchEvent(true);

                            return false;
                        }
                    });

                }


                Iconify.addIcons(tv_toggle_loan_accounts_icon, tv_toggle_savings_accounts_icon);

                safeUIBlockingUtility.safelyUnBlockUI();

                inflateDataTablesList();


            }

            @Override
            public void failure(RetrofitError retrofitError) {

                Toast.makeText(activity, "Accounts not found.", Toast.LENGTH_SHORT).show();

                safeUIBlockingUtility.safelyUnBlockUI();

            }
        });

    }

    /**
     * Use this method to fetch all datatables for client and inflate them as
     * menu options
     */
    public void inflateDataTablesList() {

        safeUIBlockingUtility.safelyBlockUI();
        API.dataTableService.getDatatablesOfClient(new Callback<List<DataTable>>() {
            @Override
            public void success(List<DataTable> dataTables, Response response) {

                if (dataTables != null) {
                    Iterator<DataTable> dataTableIterator = dataTables.iterator();
                    clientDataTables.clear();
                    while (dataTableIterator.hasNext()) {
                        DataTable dataTable = dataTableIterator.next();
                        clientDataTables.add(dataTable);
                    }
                }

                safeUIBlockingUtility.safelyUnBlockUI();

            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.i("DATATABLE", retrofitError.getLocalizedMessage());
                safeUIBlockingUtility.safelyUnBlockUI();

            }
        });

    }


    /**
     * Returns true if Google Play services is available, otherwise false.
     */
    boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            Log.d(getActivity().getLocalClassName(), "Google Play Services connected");
            return true;
            // Google Play services was not available for some reason
        } else {
            Log.w(getActivity().getLocalClassName(), "Google Play Services not available");
            Toast.makeText(getActivity(), "Location not available", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * Called when the Fragment is visible to the user.  This is generally
     * tied to {@link android.app.Activity#onStart() Activity.onStart} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onStart() {
        super.onStart();

        // Connect the client.
        if (mLocationClient != null) {
            mLocationClient.connect();
        }
    }

    /**
     * Called when the Fragment is no longer started.  This is generally
     * tied to {@link android.app.Activity#onStop() Activity.onStop} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onStop() {
        // Disconnecting the client invalidates it.
        if (mLocationClient != null) {
            mLocationClient.disconnect();
        }

        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        locationAvailable.set(true);
        Log.d(getActivity().getLocalClassName(), "Connected to location services");
        try {
            Log.d(getActivity().getLocalClassName(), "Current location: "
                    + mLocationClient.getLastLocation().toString());
        } catch (NullPointerException e) {
            //Location client is Null
        }
    }

    @Override
    public void onDisconnected() {
        locationAvailable.set(false);
        Log.d(getActivity().getLocalClassName(), "Disconnected from location services");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        locationAvailable.set(false);
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);

                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
                Log.e(getActivity().getLocalClassName(),
                        "Connection to location services failed" + connectionResult.getErrorCode());
                Toast.makeText(getActivity(), "Connection to location services failed.",
                        Toast.LENGTH_SHORT).show();
            }
        } else { // No resolution available.
            Log.e(getActivity().getLocalClassName(),
                    "Connection to location services failed" + connectionResult.getErrorCode());
            Toast.makeText(getActivity(), "Connection to location services failed.",
                    Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * This method just passes on a users click from Icon to
     * Savings Account Text View
     * <p/>
     * Clicking on this icon will trigger an action that gets
     * triggered when the adjacent view is clicked i.e. SavingsAccounts Text View
     */

    @OnClick(R.id.tv_toggle_savings_accounts_icon)
    public void onSavingsAccountToggleIconClicked() {

        tv_toggle_savings_accounts.performClick();

    }

    /**
     * This method just passes on a users click from Icon to
     * Loan Account Text View.
     * <p/>
     * Clicking on this icon will trigger an action that gets
     * triggered when the adjacent view is clicked i.e. LoanAccounts Text View
     */

    @OnClick(R.id.tv_toggle_loan_accounts_icon)
    public void onLoanAccountsToggleIconClicked() {

        tv_toggle_loan_accounts.performClick();

    }

    public void saveLocation() {

        try {

            if (locationAvailable.get()) {
                final Location location = mLocationClient.getLastLocation();


                API.gpsCoordinatesService.setGpsCoordinates(clientId,
                        new GpsCoordinatesRequest(location.getLatitude(), location.getLongitude()),
                        new Callback<GpsCoordinatesResponse>() {
                            @Override
                            public void success(GpsCoordinatesResponse gpsCoordinatesResponse, Response response) {
                                Toast.makeText(getActivity(), "Current location saved successfully: "
                                        + location.toString(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void failure(RetrofitError retrofitError) {

                                /*
                                  *  TODO:
                                  *  1. Ask Vishwas about how to parse the error json response?
                                  *     Does it follow a pattern that can be mapped here
                                  *  2. Implement a proper mechanism to read the error messages and perform actions based on them
                                  *
                                 */
                                if (retrofitError.getResponse().getStatus() == HttpStatus.SC_FORBIDDEN && retrofitError.getResponse().getBody().toString().contains("already exists")) {

                                    API.gpsCoordinatesService.updateGpsCoordinates(clientId,
                                            new GpsCoordinatesRequest(location.getLatitude(), location.getLongitude()),
                                            new Callback<GpsCoordinatesResponse>() {
                                                @Override
                                                public void success(GpsCoordinatesResponse gpsCoordinatesResponse, Response response) {

                                                    Toast.makeText(getActivity(), "Current location updated successfully: "
                                                            + location.toString(), Toast.LENGTH_SHORT).show();

                                                }

                                                @Override
                                                public void failure(RetrofitError retrofitError) {

                                                    Toast.makeText(getActivity(), "Current location could not be updated: "
                                                            + location.toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                    );

                                } else {

                                    Toast.makeText(getActivity(), "Current location could not be saved: "
                                            + location.toString(), Toast.LENGTH_SHORT).show();

                                }

                            }
                        }
                );

            } else {
                // Display the connection status
                Toast.makeText(getActivity(), "Location not available",
                        Toast.LENGTH_SHORT).show();
                Log.w(getActivity().getLocalClassName(), "Location not available");
            }
        } catch (NullPointerException e) {
            Toast.makeText(getActivity(), activity.getString(R.string.error_save_location_not_available),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void loadDocuments() {

        DocumentListFragment documentListFragment = DocumentListFragment.newInstance(Constants.ENTITY_TYPE_CLIENTS, clientId);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_DETAILS);
        fragmentTransaction.replace(R.id.global_container, documentListFragment);
        fragmentTransaction.commit();

    }

    public void loadIdentifiers() {

        ClientIdentifiersFragment clientIdentifiersFragment = ClientIdentifiersFragment.newInstance(clientId);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_DETAILS);
        fragmentTransaction.replace(R.id.global_container, clientIdentifiersFragment);
        fragmentTransaction.commit();

    }

    public interface OnFragmentInteractionListener {

        public void loadLoanAccountSummary(int loanAccountNumber);

        public void loadSavingsAccountSummary(int savingsAccountNumber);

    }

    public class ImageLoadingAsyncTask extends AsyncTask<Integer, Void, Void> {

        Bitmap bmp;

        @Override
        protected Void doInBackground(Integer... integers) {

            SharedPreferences pref = PreferenceManager
                    .getDefaultSharedPreferences(Constants.applicationContext);
            String authToken = pref.getString(User.AUTHENTICATION_KEY, "NA");
            String mInstanceUrl = pref.getString(Constants.INSTANCE_URL_KEY,
                    getString(R.string.default_instance_url));

            String url = Constants.PROTOCOL_HTTPS
                    + mInstanceUrl
                    + Constants.API_PATH + "/"
                    + "clients/"
                    + integers[0]
                    + "/images?maxHeight=120&maxWidth=120";

            try {

                HttpURLConnection httpURLConnection = (HttpURLConnection) (new URL(url)).openConnection();

                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("X-Mifos-Platform-TenantId", "default");
                httpURLConnection.setRequestProperty(API.HEADER_AUTHORIZATION, authToken);
                httpURLConnection.setRequestProperty("Accept", "application/octet-stream");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();
                Log.i("Connected", "True");
                InputStream inputStream = httpURLConnection.getInputStream();

                bmp = BitmapFactory.decodeStream(inputStream);

                httpURLConnection.disconnect();
                Log.i("Connected", "False");

            } catch (MalformedURLException e) {

            } catch (IOException ioe) {

            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            if (bmp != null) {
                iv_clientImage.setImageBitmap(bmp);
            } else {
                iv_clientImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher));
            }

        }
    }

}
