/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.clientdetails;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.joanzapata.iconify.widget.IconTextView;
import com.mifos.api.MifosInterceptor;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.activity.pinpointclient.PinpointClientActivity;
import com.mifos.mifosxdroid.adapters.LoanAccountsListAdapter;
import com.mifos.mifosxdroid.adapters.SavingsAccountsListAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.ProgressableFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.online.clientcharge.ClientChargeFragment;
import com.mifos.mifosxdroid.online.clientidentifiers.ClientIdentifiersFragment;
import com.mifos.mifosxdroid.online.datatabledata.DataTableDataFragment;
import com.mifos.mifosxdroid.online.documentlist.DocumentListFragment;
import com.mifos.mifosxdroid.online.loanaccount.LoanAccountFragment;
import com.mifos.mifosxdroid.online.savingsaccount.SavingsAccountFragment;
import com.mifos.mifosxdroid.online.surveylist.SurveyListFragment;
import com.mifos.mifosxdroid.views.CircularImageView;
import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.objects.accounts.savings.DepositType;
import com.mifos.objects.client.Charges;
import com.mifos.objects.client.Client;
import com.mifos.objects.noncore.DataTable;
import com.mifos.utils.Constants;
import com.mifos.utils.FragmentConstants;
import com.mifos.utils.PrefManager;
import com.mifos.utils.Utils;

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

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;

import static android.view.View.GONE;
import static android.view.View.OnClickListener;
import static android.view.View.OnTouchListener;
import static android.view.View.VISIBLE;


public class ClientDetailsFragment extends ProgressableFragment implements GoogleApiClient
        .ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,
        ClientDetailsMvpView {

    public static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    // Intent response codes. Each response code must be a unique integer.
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;

    public static final int MENU_ITEM_DATA_TABLES = 1000;
    public static final int MENU_PIN_PONIT = 1001;
    public static final int MENU_ITEM_CLIENT_CHARGES = 1003;
    public static final int MENU_ITEM_ADD_SAVINGS_ACCOUNT = 1004;
    public static final int MENU_ITEM_ADD_LOAN_ACCOUNT = 1005;
    public static final int MENU_ITEM_DOCUMENTS = 1006;
    public static final int MENU_ITEM_IDENTIFIERS = 1007;
    public static final int MENU_ITEM_SURVEYS = 1008;

    private final String TAG = ClientDetailsFragment.class.getSimpleName();

    public int clientId;
    public List<DataTable> clientDataTables = new ArrayList<>();
    List<Charges> chargesList = new ArrayList<Charges>();

    @BindView(R.id.tv_fullName)
    TextView tv_fullName;

    @BindView(R.id.tv_accountNumber)
    TextView tv_accountNumber;

    @BindView(R.id.tv_externalId)
    TextView tv_externalId;

    @BindView(R.id.tv_activationDate)
    TextView tv_activationDate;

    @BindView(R.id.tv_office)
    TextView tv_office;

    @BindView(R.id.iv_clientImage)
    CircularImageView iv_clientImage;

    @BindView(R.id.pb_imageProgressBar)
    ProgressBar pb_imageProgressBar;

    @BindView(R.id.row_account)
    TableRow rowAccount;

    @BindView(R.id.row_external)
    TableRow rowExternal;

    @BindView(R.id.row_activation)
    TableRow rowActivation;

    @BindView(R.id.row_office)
    TableRow rowOffice;

    @BindView(R.id.row_group)
    TableRow rowGroup;

    @BindView(R.id.row_staff)
    TableRow rowStaff;

    @BindView(R.id.row_loan)
    TableRow rowLoan;

    @Inject
    ClientDetailsPresenter mClientDetailsPresenter;

    private View rootView;
    private OnFragmentInteractionListener mListener;
    private File capturedClientImageFile;
    // Null if play services are not available.
    private GoogleApiClient mGoogleApiClient;
    // True if play services are available and location services are connected.
    private AtomicBoolean locationAvailable = new AtomicBoolean(false);

    private AccountAccordion accountAccordion;
    private ImageLoadingAsyncTask imageLoadingAsyncTask;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param clientId Client's Id
     */
    public static ClientDetailsFragment newInstance(int clientId) {
        ClientDetailsFragment fragment = new ClientDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.CLIENT_ID, clientId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDetach() {
        if (imageLoadingAsyncTask != null && !imageLoadingAsyncTask.getStatus().equals(AsyncTask
                .Status.FINISHED))
            imageLoadingAsyncTask.cancel(true);
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        if (getArguments() != null)
            clientId = getArguments().getInt(Constants.CLIENT_ID);
        setHasOptionsMenu(true);
        capturedClientImageFile = new File(getActivity().getExternalCacheDir(), "client_image.png");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_client_details, container, false);

        ButterKnife.bind(this, rootView);
        mClientDetailsPresenter.attachView(this);

        inflateClientInformation();

        return rootView;
    }

    public void inflateClientInformation() {
        mClientDetailsPresenter.loadClientDetailsAndClientAccounts(clientId);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().getClass().getSimpleName() + " must " +
                    "implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK)
            uploadImage(capturedClientImageFile);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        menu.clear();

        menu.addSubMenu(Menu.NONE, MENU_ITEM_DATA_TABLES, Menu.NONE, Constants
                .DATA_TABLE_CLIENTS_NAME);

        //menu.add(Menu.NONE, MENU_PIN_PONIT, Menu.NONE, getString(R.string.action_save_location));
        menu.add(Menu.NONE, MENU_PIN_PONIT, Menu.NONE, getString(R.string.pinpoint));
        menu.add(Menu.NONE, MENU_ITEM_CLIENT_CHARGES, Menu.NONE, getString(R.string.charges));
        menu.add(Menu.NONE, MENU_ITEM_ADD_SAVINGS_ACCOUNT, Menu.NONE, getString(R.string
                .savings_account));
        menu.add(Menu.NONE, MENU_ITEM_ADD_LOAN_ACCOUNT, Menu.NONE, getString(R.string.add_loan));
        menu.add(Menu.NONE, MENU_ITEM_DOCUMENTS, Menu.NONE, getString(R.string.documents));
        menu.add(Menu.NONE, MENU_ITEM_IDENTIFIERS, Menu.NONE, getString(R.string.identifiers));
        menu.add(Menu.NONE, MENU_ITEM_SURVEYS, Menu.NONE, getString(R.string.survey));


        SubMenu more_info_subSubMenu = menu.findItem(MENU_ITEM_DATA_TABLES).getSubMenu();

        int SUBMENU_ITEM_ID = 0;

        // Create a Sub Menu that holds a link to all data tables
        if (more_info_subSubMenu != null && clientDataTables != null && clientDataTables.size() >
                0) {
            Iterator<DataTable> dataTableIterator = clientDataTables.iterator();
            while (dataTableIterator.hasNext()) {
                more_info_subSubMenu.add(Menu.NONE, SUBMENU_ITEM_ID, Menu.NONE, dataTableIterator
                        .next()
                        .getRegisteredTableName());
                SUBMENU_ITEM_ID++;
            }
        }

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id >= 0 && id < clientDataTables.size()) {

            DataTableDataFragment dataTableDataFragment
                    = DataTableDataFragment.newInstance(clientDataTables.get(id), clientId);
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                    .beginTransaction();
            fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_DETAILS);
            fragmentTransaction.replace(R.id.container, dataTableDataFragment, FragmentConstants
                    .FRAG_DATA_TABLE);

            fragmentTransaction.commit();
        }

        switch (item.getItemId()) {
            case MENU_ITEM_DOCUMENTS:
                loadDocuments();
                break;
            case MENU_ITEM_CLIENT_CHARGES:
                loadClientCharges();
                break;
            case MENU_ITEM_ADD_SAVINGS_ACCOUNT:
                addsavingsaccount();
                break;
            case MENU_ITEM_ADD_LOAN_ACCOUNT:
                addloanaccount();
                break;
            case MENU_ITEM_IDENTIFIERS:
                loadIdentifiers();
                break;
            case MENU_PIN_PONIT:
                Intent i = new Intent(getActivity(), PinpointClientActivity.class);
                i.putExtra(PinpointClientActivity.EXTRA_CLIENT_ID, clientId);
                startActivity(i);
                break;
            case MENU_ITEM_SURVEYS:
                loadSurveys();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void captureClientImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(capturedClientImageFile));
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    /**
     * A service to upload the image of the client.
     *
     * @param pngFile - PNG images supported at the moment
     */
    private void uploadImage(File pngFile) {
        mClientDetailsPresenter.uploadImage(clientId, pngFile);
    }


    /**
     * Use this method to fetch all datatables for client and inflate them as
     * menu options
     */
    public void inflateDataTablesList() {
        mClientDetailsPresenter.loadClientDataTable();
    }

    /**
     * Called when the Fragment is visible to the user.  This is generally
     * tied to {@link android.app.Activity#onStart() Activity.onStart} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    /**
     * Called when the Fragment is no longer started.  This is generally
     * tied to {@link android.app.Activity#onStop() Activity.onStop} of the containing
     * Activity's lifecycle.
     */
    @Override
    public void onStop() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mClientDetailsPresenter.detachView();
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onConnected(Bundle bundle) {
        locationAvailable.set(true);
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation
                (mGoogleApiClient);
        Log.d(TAG, "Connected to location services");
        try {
            Log.d(TAG, "Current location: " + mLastLocation.toString());
        } catch (NullPointerException e) {
            //Location client is Null
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        locationAvailable.set(false);
        Log.d(TAG, "Disconnected from location services");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        locationAvailable.set(false);
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(),
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, "Connection to location services failed" + connectionResult
                        .getErrorCode(), e);
                Toaster.show(rootView, "Connection to location services failed.");
            }
        } else { // No resolution available.
            Log.e(TAG, "Connection to location services failed" + connectionResult.getErrorCode());
            Toaster.show(rootView, "Connection to location services failed.");
        }
    }

    public void loadDocuments() {
        DocumentListFragment documentListFragment = DocumentListFragment.newInstance(Constants
                .ENTITY_TYPE_CLIENTS, clientId);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_DETAILS);
        fragmentTransaction.replace(R.id.container, documentListFragment);
        fragmentTransaction.commit();
    }

    public void loadClientCharges() {
        ClientChargeFragment clientChargeFragment = ClientChargeFragment.newInstance(clientId,
                chargesList);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_DETAILS);
        fragmentTransaction.replace(R.id.container, clientChargeFragment);
        fragmentTransaction.commit();
    }

    public void loadIdentifiers() {
        ClientIdentifiersFragment clientIdentifiersFragment = ClientIdentifiersFragment
                .newInstance(clientId);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_DETAILS);
        fragmentTransaction.replace(R.id.container, clientIdentifiersFragment);
        fragmentTransaction.commit();
    }

    public void loadSurveys() {
        SurveyListFragment surveyListFragment = SurveyListFragment.newInstance(clientId);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_DETAILS);
        fragmentTransaction.replace(R.id.container, surveyListFragment);
        fragmentTransaction.commit();
    }

    public void addsavingsaccount() {
        SavingsAccountFragment savingsAccountFragment = SavingsAccountFragment.newInstance
                (clientId);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_DETAILS);
        fragmentTransaction.replace(R.id.container, savingsAccountFragment);
        fragmentTransaction.commit();
    }

    public void addloanaccount() {
        LoanAccountFragment loanAccountFragment = LoanAccountFragment.newInstance(clientId);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_DETAILS);
        fragmentTransaction.replace(R.id.container, loanAccountFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void showProgressbar(boolean b) {
        showProgress(false);
    }

    @Override
    public void showClientDataTable(List<DataTable> dataTables) {
        if (dataTables != null) {
            Iterator<DataTable> dataTableIterator = dataTables.iterator();
            clientDataTables.clear();
            while (dataTableIterator.hasNext()) {
                clientDataTables.add(dataTableIterator.next());
            }

        }
    }

    @Override
    public void showClientInformation(Client client) {
        if (client != null) {
            setToolbarTitle(getString(R.string.client) + " - " + client.getLastname());
            tv_fullName.setText(client.getDisplayName());
            tv_accountNumber.setText(client.getAccountNo());
            tv_externalId.setText(client.getExternalId());
            if (TextUtils.isEmpty(client.getAccountNo()))
                rowAccount.setVisibility(GONE);

            if (TextUtils.isEmpty(client.getExternalId()))
                rowExternal.setVisibility(GONE);

            try {
                String dateString = Utils.getStringOfDate(getActivity(),
                        client.getActivationDate());
                tv_activationDate.setText(dateString);

                if (TextUtils.isEmpty(dateString))
                    rowActivation.setVisibility(GONE);

            } catch (IndexOutOfBoundsException e) {
                Toast.makeText(getActivity(), getString(R.string.error_client_inactive),
                        Toast.LENGTH_SHORT).show();
                tv_activationDate.setText("");
            }
            tv_office.setText(client.getOfficeName());

            if (TextUtils.isEmpty(client.getOfficeName()))
                rowOffice.setVisibility(GONE);

            if (client.isImagePresent()) {
                loadImageGlide(client.getId());
            } else {
                iv_clientImage.setImageDrawable(
                        ResourcesCompat.getDrawable(getResources(), R.drawable
                                .ic_launcher, null));

                pb_imageProgressBar.setVisibility(GONE);
            }

            iv_clientImage.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu menu = new PopupMenu(getActivity(), view);
                    menu.getMenuInflater().inflate(R.menu.client_image_popup, menu
                            .getMenu());
                    menu.setOnMenuItemClickListener(
                            new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem menuItem) {
                                    switch (menuItem.getItemId()) {
                                        case R.id.client_image_capture:
                                            captureClientImage();
                                            break;
                                        case R.id.client_image_remove:
                                            mClientDetailsPresenter.deleteClientImage(clientId);
                                            break;
                                        default:
                                            Log.e("ClientDetailsFragment", "Unrecognized " +
                                                    "client " +
                                                    "image menu item");
                                    }
                                    return true;
                                }
                            });
                    menu.show();
                }
            });
            //inflateClientsAccounts();
        }
    }

    @Override
    public void showUploadImageSuccessfully(ResponseBody response, String imagePath) {
        Toaster.show(rootView, R.string.client_image_updated);
        iv_clientImage.setImageBitmap(BitmapFactory.decodeFile(imagePath));
    }

    @Override
    public void showUploadImageFailed(String s) {
        Toaster.show(rootView, s);
        loadImageGlide(clientId);
    }

    @Override
    public void showUploadImageProgressbar(boolean b) {
        if (b) {
            pb_imageProgressBar.setVisibility(VISIBLE);
        } else {
            pb_imageProgressBar.setVisibility(GONE);
        }
    }


    public void loadImageGlide(int clientId){
        pb_imageProgressBar.setVisibility(VISIBLE);
        String url = PrefManager.getInstanceUrl()
                + "clients/"
                + clientId
                + "/images?maxHeight=120&maxWidth=120";

        GlideUrl glideUrl = new GlideUrl(url, new LazyHeaders.Builder()
                .addHeader(MifosInterceptor.HEADER_TENANT, "default")
                .addHeader(MifosInterceptor.HEADER_AUTH, PrefManager.getToken())
                .addHeader("Accept", "application/octet-stream")
                .build());
        Log.d("ashu1",glideUrl.toString());
        Glide.with(getActivity())
                .load(glideUrl)
                .error(R.drawable.ic_launcher)
                .into(iv_clientImage);
        pb_imageProgressBar.setVisibility(GONE);
    }

    @Override
    public void showClientImageDeletedSuccessfully() {
        Toaster.show(rootView, "Image deleted");
        iv_clientImage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable
                .ic_launcher));
    }

    @Override
    public void showClientAccount(ClientAccounts clientAccounts) {
        // Proceed only when the fragment is added to the activity.
        if (!isAdded()) {
            return;
        }
        accountAccordion = new AccountAccordion(getActivity());
        if (clientAccounts.getLoanAccounts().size() > 0) {
            AccountAccordion.Section section = AccountAccordion.Section.LOANS;
            final LoanAccountsListAdapter adapter =
                    new LoanAccountsListAdapter(getActivity().getApplicationContext(),
                            clientAccounts.getLoanAccounts());
            section.connect(getActivity(), adapter, new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i,
                                        long l) {
                    mListener.loadLoanAccountSummary(adapter.getItem(i).getId());
                }
            });
        }

        if (clientAccounts.getNonRecurringSavingsAccounts().size() > 0) {
            AccountAccordion.Section section = AccountAccordion.Section.SAVINGS;
            final SavingsAccountsListAdapter adapter =
                    new SavingsAccountsListAdapter(getActivity().getApplicationContext(),
                            clientAccounts.getNonRecurringSavingsAccounts());
            section.connect(getActivity(), adapter, new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i,
                                        long l) {
                    mListener.loadSavingsAccountSummary(adapter.getItem(i).getId(),
                            adapter.getItem(i).getDepositType());
                }
            });
        }

        if (clientAccounts.getRecurringSavingsAccounts().size() > 0) {
            AccountAccordion.Section section = AccountAccordion.Section.RECURRING;
            final SavingsAccountsListAdapter adapter =
                    new SavingsAccountsListAdapter(getActivity().getApplicationContext(),
                            clientAccounts.getRecurringSavingsAccounts());
            section.connect(getActivity(), adapter, new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i,
                                        long l) {
                    mListener.loadSavingsAccountSummary(adapter.getItem(i).getId(),
                            adapter.getItem(i).getDepositType());
                }
            });
        }
        inflateDataTablesList();
    }

    @Override
    public void showFetchingError(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    public interface OnFragmentInteractionListener {
        void loadLoanAccountSummary(int loanAccountNumber);

        void loadSavingsAccountSummary(int savingsAccountNumber, DepositType accountType);
    }


    private static class AccountAccordion {
        private final Activity context;
        private Section currentSection;

        private AccountAccordion(Activity context) {
            this.context = context;
            Section.configure(context, this);
        }

        public void setCurrentSection(Section currentSection) {
            // close previous section
            if (this.currentSection != null) {
                this.currentSection.close(context);
            }

            this.currentSection = currentSection;

            // open new section
            if (this.currentSection != null) {
                this.currentSection.open(context);
            }
        }

        private enum Section {
            LOANS(R.id.account_accordion_section_loans, R.string.loanAccounts),
            SAVINGS(R.id.account_accordion_section_savings, R.string.savingAccounts),
            RECURRING(R.id.account_accordion_section_recurring, R.string.recurringAccount);

            private static final MaterialIcons LIST_OPEN_ICON = MaterialIcons.md_add_circle_outline;
            private static final MaterialIcons LIST_CLOSED_ICON = MaterialIcons
                    .md_remove_circle_outline;

            private final int sectionId;
            private final int textViewStringId;

            Section(int sectionId, int textViewStringId) {
                this.sectionId = sectionId;
                this.textViewStringId = textViewStringId;
            }

            public static void configure(Activity context, final AccountAccordion accordion) {
                for (Section section : Section.values()) {
                    section.configureSection(context, accordion);
                }
            }

            public TextView getTextView(Activity context) {
                return (TextView) getSectionView(context).findViewById(R.id.tv_toggle_accounts);
            }

            public IconTextView getIconView(Activity context) {
                return (IconTextView) getSectionView(context).findViewById(R.id
                        .tv_toggle_accounts_icon);
            }

            public ListView getListView(Activity context) {
                return (ListView) getSectionView(context).findViewById(R.id.lv_accounts);
            }

            public TextView getCountView(Activity context) {
                return (TextView) getSectionView(context).findViewById(R.id.tv_count_accounts);
            }

            public View getSectionView(Activity context) {
                return context.findViewById(this.sectionId);
            }

            public void connect(Activity context, ListAdapter adapter, AdapterView
                    .OnItemClickListener onItemClickListener) {
                getCountView(context).setText(String.valueOf(adapter.getCount()));
                ListView listView = getListView(context);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(onItemClickListener);
            }

            public void open(Activity context) {
                IconTextView iconView = getIconView(context);
                iconView.setText("{" + LIST_CLOSED_ICON.key() + "}");
                getListView(context).setVisibility(VISIBLE);
            }

            public void close(Activity context) {
                IconTextView iconView = getIconView(context);
                iconView.setText("{" + LIST_OPEN_ICON.key() + "}");
                getListView(context).setVisibility(GONE);
            }

            private void configureSection(Activity context, final AccountAccordion accordion) {
                final ListView listView = getListView(context);
                final TextView textView = getTextView(context);
                final IconTextView iconView = getIconView(context);

                OnClickListener onClickListener = new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Section.this.equals(accordion.currentSection)) {
                            accordion.setCurrentSection(null);
                        } else if (listView != null && listView.getCount() > 0) {
                            accordion.setCurrentSection(Section.this);
                        }
                    }
                };

                if (textView != null) {
                    textView.setOnClickListener(onClickListener);
                    textView.setText(context.getString(textViewStringId));
                }

                if (iconView != null) {
                    iconView.setOnClickListener(onClickListener);
                }

                if (listView != null) {
                    //This is used to handle touch events on the list view and consume them without
                    //passing onto scroll view
                    listView.setOnTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent motionEvent) {
                            view.getParent().requestDisallowInterceptTouchEvent(true);
                            return false;
                        }
                    });
                }
                // initialize section in closed state
                close(context);
            }
        }
    }

    public class ImageLoadingAsyncTask extends AsyncTask<Integer, Void, Void> {
        Bitmap bmp;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pb_imageProgressBar.setVisibility(VISIBLE);
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            String url = PrefManager.getInstanceUrl()
                    + "clients/"
                    + integers[0]
                    + "/images?maxHeight=120&maxWidth=120";

            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) (new URL(url))
                        .openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty(MifosInterceptor.HEADER_TENANT,
                        "default");
                httpURLConnection.setRequestProperty(MifosInterceptor.HEADER_AUTH,
                        PrefManager.getToken());
                httpURLConnection.setRequestProperty("Accept", "application/octet-stream");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();
                InputStream inputStream = httpURLConnection.getInputStream();
                bmp = BitmapFactory.decodeStream(inputStream);
                httpURLConnection.disconnect();
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
                iv_clientImage.setImageDrawable(
                        ContextCompat.getDrawable(getActivity(), R.drawable.ic_launcher));
                pb_imageProgressBar.setVisibility(GONE);
            }

        }
    }

}
