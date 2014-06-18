package com.mifos.mifosxdroid.online;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.LoanAccountsListAdapter;
import com.mifos.mifosxdroid.adapters.SavingsAccountsListAdapter;
import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.objects.client.Client;
import com.mifos.objects.noncore.DataTable;
import com.mifos.services.API;
import com.mifos.utils.Constants;
import com.mifos.utils.SafeUIBlockingUtility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;


public class ClientDetailsFragment extends Fragment {
    // Intent response codes. Each response code must be a unique integer.
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;

    private OnFragmentInteractionListener mListener;
    
    public static int clientId;

    @InjectView(R.id.tv_fullName) TextView tv_fullName;
    @InjectView(R.id.tv_accountNumber) TextView tv_accountNumber;
    @InjectView(R.id.tv_externalId) TextView tv_externalId;
    @InjectView(R.id.tv_activationDate) TextView tv_activationDate;
    @InjectView(R.id.tv_office) TextView tv_office;
    @InjectView(R.id.tv_group) TextView tv_group;
    @InjectView(R.id.tv_loanOfficer) TextView tv_loanOfficer;
    @InjectView(R.id.tv_loanCycle) TextView tv_loanCycle;
    @InjectView(R.id.tv_toggle_loan_accounts) TextView tv_toggle_loan_accounts;
    @InjectView(R.id.tv_toggle_savings_accounts) TextView tv_toggle_savings_accounts;
    @InjectView(R.id.tv_count_loan_accounts) TextView tv_count_loan_accounts;
    @InjectView(R.id.tv_count_savings_accounts) TextView tv_count_savings_accounts;
    @InjectView(R.id.lv_accounts_loans) ListView lv_accounts_loans;
    @InjectView(R.id.lv_accounts_savings) ListView lv_accounts_savings;
    @InjectView(R.id.iv_clientImage) ImageView iv_clientImage;


    View rootView;

    SafeUIBlockingUtility safeUIBlockingUtility;

    ActionBarActivity activity;

    SharedPreferences sharedPreferences;

    ActionBar actionBar;

    boolean isLoanAccountsListOpen = false;
    boolean isSavingsAccountsListOpen = false;
    private File capturedClientImageFile;


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
    public ClientDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            clientId = getArguments().getInt(Constants.CLIENT_ID);
            System.out.print(clientId);
        }
        capturedClientImageFile = new File(getActivity().getExternalCacheDir(), "client_image.png");
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


    private static byte[] inputStreamToByteArray(InputStream input) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        return output.toByteArray();
    }

    public void inflateClientInformation() {

        getClientDetails();

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
                    }

                    @Override
                    public void failure(RetrofitError retrofitError) {
                        Toast.makeText(activity, "Failed to update image", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    /**
     * Use this method to fetch and inflate client details
     * in the fragment
     *
     */
    public void getClientDetails() {

        safeUIBlockingUtility.safelyBlockUI();

        API.clientService.getClient(clientId, new Callback<Client>() {
            @Override
            public void success(final Client client, Response response) {

                if (client != null) {
                    actionBar.setTitle("Mifos Client - " + client.getLastname());
                    tv_fullName.setText(client.getDisplayName());
                    tv_accountNumber.setText(client.getAccountNo());
                    tv_externalId.setText(client.getExternalId());
                    tv_activationDate.setText(client.getFormattedActivationDateAsString());
                    tv_office.setText(client.getOfficeName());

                    // TODO: For some reason Retrofit always calls the failure() method even after
                    // receiving a 200 response with image bytes. Perhaps we need to change the
                    // argument type from TypedFile to something else?
                    if (client.isImagePresent()) {
                        API.clientService.getClientImage(client.getId(), new Callback<TypedFile>() {

                            @Override
                            public void success(final TypedFile file, Response response) {
                                try {
                                    // TODO: Parse bytes and render image in the UI.
                                    byte[] bytes = inputStreamToByteArray(file.in());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void failure(RetrofitError retrofitError) {
                                Log.d("ClientDetailsFragment", "No image found for clientId " + client.getId());
                            }

                        });
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
                final String loanListOpen = "- " + loanAccountsStringResource;
                final String loanListClosed = "+ " + loanAccountsStringResource;
                final String savingsListOpen = "- " + savingsAccountsStringResource;
                final String savingsListClosed = "+ " + savingsAccountsStringResource;

                if (clientAccounts.getLoanAccounts().size() > 0) {
                    LoanAccountsListAdapter loanAccountsListAdapter =
                            new LoanAccountsListAdapter(getActivity().getApplicationContext(), clientAccounts.getLoanAccounts());
                    tv_toggle_loan_accounts.setText(loanListClosed);
                    tv_count_loan_accounts.setText(String.valueOf(clientAccounts.getLoanAccounts().size()));
                    tv_toggle_loan_accounts.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!isLoanAccountsListOpen) {
                                isLoanAccountsListOpen = true;
                                tv_toggle_loan_accounts.setText(loanListOpen);
                                //TODO SIZE AND ANIMATION TO BE ADDED
                                //Drop Down and Fold Up
                                //Calculate Size of 1 cell and show a couple of them
                                isSavingsAccountsListOpen = false;
                                tv_toggle_savings_accounts.setText(savingsListClosed);
                                lv_accounts_savings.setVisibility(View.GONE);
                                lv_accounts_loans.setVisibility(View.VISIBLE);
                            } else {
                                isLoanAccountsListOpen = false;
                                tv_toggle_loan_accounts.setText(loanListClosed);
                                //TODO SIZE AND ANIMATION TO BE ADDED
                                //Drop Down and Fold Up
                                //Calculate Size of 1 cell and show a couple of them
                                lv_accounts_loans.setVisibility(View.GONE);
                            }
                        }
                    });
                    lv_accounts_loans.setAdapter(loanAccountsListAdapter);
                    lv_accounts_loans.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            mListener.loadLoanAccountSummary(clientAccounts.getLoanAccounts().get(i).getId());

                        }
                    });
                }

                if (clientAccounts.getSavingsAccounts().size() > 0) {
                    SavingsAccountsListAdapter savingsAccountsListAdapter =
                            new SavingsAccountsListAdapter(getActivity().getApplicationContext(), clientAccounts.getSavingsAccounts());
                    tv_toggle_savings_accounts.setText(savingsListClosed);
                    tv_count_savings_accounts.setText(String.valueOf(clientAccounts.getSavingsAccounts().size()));
                    tv_toggle_savings_accounts.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!isSavingsAccountsListOpen) {
                                isSavingsAccountsListOpen = true;
                                tv_toggle_savings_accounts.setText(savingsListOpen);
                                //TODO SIZE AND ANIMATION TO BE ADDED
                                //Drop Down and Fold Up
                                //Calculate Size of 1 cell and show a couple of them
                                isLoanAccountsListOpen = false;
                                tv_toggle_loan_accounts.setText(loanListClosed);
                                lv_accounts_loans.setVisibility(View.GONE);
                                lv_accounts_savings.setVisibility(View.VISIBLE);
                            } else {
                                isSavingsAccountsListOpen = false;
                                tv_toggle_savings_accounts.setText(savingsListClosed);
                                //TODO SIZE AND ANIMATION TO BE ADDED
                                //Drop Down and Fold Up
                                //Calculate Size of 1 cell and show a couple of them
                                lv_accounts_savings.setVisibility(View.GONE);
                            }
                        }
                    });
                    lv_accounts_savings.setAdapter(savingsAccountsListAdapter);
                    lv_accounts_savings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            mListener.loadSavingsAccountSummary(clientAccounts.getSavingsAccounts().get(i).getId());
                        }
                    });

                }

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


    public static List<DataTable> clientDataTables = new ArrayList<DataTable>();

    /**
     * Use this method to fetch all datatables for client and inflate them as
     * menu options
     */
    public void inflateDataTablesList(){

        safeUIBlockingUtility.safelyBlockUI();
        API.changeRestAdapterLogLevel(RestAdapter.LogLevel.NONE);
        API.dataTableService.getDatatablesOfClient(new Callback<List<DataTable>>() {
            @Override
            public void success(List<DataTable> dataTables, Response response) {

                if(dataTables != null)
                {
                    Log.i("DATATABLE", "FOUND");
                    ClientActivity.idOfDataTableToBeShownInMenu = Constants.DATA_TABLE_CLIENTS;
                    ClientActivity.shouldAddDataTables = Boolean.TRUE;
                    ClientActivity.didMenuDataChange = Boolean.TRUE;
                    Iterator<DataTable> dataTableIterator = dataTables.iterator();
                    ClientActivity.dataTableMenuItems.clear();
                    while(dataTableIterator.hasNext())
                    {
                        DataTable dataTable = dataTableIterator.next();
                        clientDataTables.add(dataTable);
                        ClientActivity.dataTableMenuItems.add(dataTable.getRegisteredTableName());
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

    public interface OnFragmentInteractionListener {

        public void loadLoanAccountSummary(int loanAccountNumber);
        public void loadSavingsAccountSummary(int savingsAccountNumber);

    }


}
