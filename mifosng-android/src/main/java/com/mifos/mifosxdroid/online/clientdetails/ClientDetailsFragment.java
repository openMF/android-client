/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.clientdetails;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.widget.PopupMenu;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.iconify.fonts.MaterialIcons;
import com.joanzapata.iconify.widget.IconTextView;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.activity.pinpointclient.PinpointClientActivity;
import com.mifos.mifosxdroid.adapters.LoanAccountsListAdapter;
import com.mifos.mifosxdroid.adapters.SavingsAccountsListAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.online.activate.ActivateFragment;
import com.mifos.mifosxdroid.online.clientcharge.ClientChargeFragment;
import com.mifos.mifosxdroid.online.clientidentifiers.ClientIdentifiersFragment;
import com.mifos.mifosxdroid.online.datatable.DataTableFragment;
import com.mifos.mifosxdroid.online.documentlist.DocumentListFragment;
import com.mifos.mifosxdroid.online.loanaccount.LoanAccountFragment;
import com.mifos.mifosxdroid.online.note.NoteFragment;
import com.mifos.mifosxdroid.online.savingsaccount.SavingsAccountFragment;
import com.mifos.mifosxdroid.online.sign.SignatureFragment;
import com.mifos.mifosxdroid.online.surveylist.SurveyListFragment;
import com.mifos.mifosxdroid.views.CircularImageView;
import com.mifos.objects.accounts.ClientAccounts;
import com.mifos.objects.accounts.savings.DepositType;
import com.mifos.objects.client.Charges;
import com.mifos.objects.client.Client;
import com.mifos.utils.Constants;
import com.mifos.utils.FragmentConstants;
import com.mifos.utils.ImageLoaderUtils;
import com.mifos.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.OnClickListener;
import static android.view.View.OnTouchListener;
import static android.view.View.VISIBLE;


public class ClientDetailsFragment extends MifosBaseFragment implements ClientDetailsMvpView {

    // Intent response codes. Each response code must be a unique integer.
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 2;
    private static final int UPLOAD_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    private static final int CHECK_PERMISSIONS = 1010;
    public static final int MENU_ITEM_DATA_TABLES = 1000;
    public static final int MENU_ITEM_PIN_POINT = 1001;
    public static final int MENU_ITEM_CLIENT_CHARGES = 1003;
    public static final int MENU_ITEM_ADD_SAVINGS_ACCOUNT = 1004;
    public static final int MENU_ITEM_ADD_LOAN_ACCOUNT = 1005;
    public static final int MENU_ITEM_DOCUMENTS = 1006;
    public static final int MENU_ITEM_UPLOAD_SIGN = 1010;
    public static final int MENU_ITEM_IDENTIFIERS = 1007;
    public static final int MENU_ITEM_SURVEYS = 1008;
    public static final int MENU_ITEM_NOTE = 1009;

    String imgDecodableString;
    private final String TAG = ClientDetailsFragment.class.getSimpleName();

    public int clientId;
    List<Charges> chargesList = new ArrayList<>();

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

    @BindView(R.id.tv_mobile_no)
    TextView tvMobileNo;

    @BindView(R.id.tv_group)
    TextView tvGroup;

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

    @BindView(R.id.tableRow_mobile_no)
    TableRow rowMobileNo;

    @BindView(R.id.ll_bottom_panel)
    LinearLayout llBottomPanel;

    @BindView(R.id.rl_client)
    RelativeLayout rlClient;

    @Inject
    ClientDetailsPresenter mClientDetailsPresenter;

    private View rootView;
    private OnFragmentInteractionListener mListener;
    private File clientImageFile = new File(Environment.getExternalStorageDirectory().toString() +
            "/client_image.png");
    private AccountAccordion accountAccordion;
    private boolean isClientActive = false;


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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        if (getArguments() != null) {
            clientId = getArguments().getInt(Constants.CLIENT_ID);
        }
        setHasOptionsMenu(true);
        checkPermissions();
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

    @OnClick(R.id.btn_activate_client)
    void onClickActivateClient() {
        activateClient();
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
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == UPLOAD_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getActivity().getApplicationContext().getContentResolver().query(
                        selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                Bitmap pickedImage = BitmapFactory.decodeFile(imgDecodableString);
                saveBitmap(clientImageFile, pickedImage);
                uploadImage(clientImageFile);
            } else if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE
                    && resultCode == RESULT_OK) {
                uploadImage(clientImageFile);
            } else {
                Toaster.show(rootView, R.string.havent_picked_image,
                        Toast.LENGTH_LONG);
            }
        } catch (Exception e) {
            Toaster.show(rootView, e.toString(), Toast.LENGTH_LONG);
        }
    }

    public void saveBitmap(File file, Bitmap mBitmap) {
        try {
            file.createNewFile();
            FileOutputStream fOut = null;
            fOut = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception exception) {
            //Empty catch block to prevent crashing
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (isClientActive) {
            menu.add(Menu.NONE, MENU_ITEM_DATA_TABLES, Menu.NONE, getString(R.string.more_info));
            menu.add(Menu.NONE, MENU_ITEM_PIN_POINT, Menu.NONE, getString(R.string.pinpoint));
            menu.add(Menu.NONE, MENU_ITEM_CLIENT_CHARGES, Menu.NONE, getString(R.string.charges));
            menu.add(Menu.NONE, MENU_ITEM_ADD_SAVINGS_ACCOUNT, Menu.NONE, getString(R.string
                    .savings_account));
            menu.add(Menu.NONE, MENU_ITEM_ADD_LOAN_ACCOUNT, Menu.NONE,
                    getString(R.string.add_loan));
            menu.add(Menu.NONE, MENU_ITEM_DOCUMENTS, Menu.NONE, getString(R.string.documents));
            menu.add(Menu.NONE, MENU_ITEM_UPLOAD_SIGN, Menu.NONE, R.string.upload_sign);
            menu.add(Menu.NONE, MENU_ITEM_IDENTIFIERS, Menu.NONE, getString(R.string.identifiers));
            menu.add(Menu.NONE, MENU_ITEM_SURVEYS, Menu.NONE, getString(R.string.survey));
            menu.add(Menu.NONE, MENU_ITEM_NOTE, Menu.NONE, getString(R.string.note));
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ITEM_DATA_TABLES:
                loadClientDataTables();
                break;
            case MENU_ITEM_DOCUMENTS:
                loadDocuments();
                break;
            case MENU_ITEM_UPLOAD_SIGN:
                loadSignUpload();
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
            case MENU_ITEM_PIN_POINT:
                Intent i = new Intent(getActivity(), PinpointClientActivity.class);
                i.putExtra(Constants.CLIENT_ID, clientId);
                startActivity(i);
                break;
            case MENU_ITEM_SURVEYS:
                loadSurveys();
                break;
            case MENU_ITEM_NOTE:
                loadNotes();
        }
        return super.onOptionsItemSelected(item);
    }

    public void captureClientImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(clientImageFile));
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    CHECK_PERMISSIONS);
        }
    }

    public void uploadClientImage() {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start the Intent
        galleryIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(clientImageFile));
        startActivityForResult(galleryIntent, UPLOAD_IMAGE_ACTIVITY_REQUEST_CODE);
    }


    /**
     * A service to upload the image of the client.
     *
     * @param pngFile - PNG images supported at the moment
     */
    private void uploadImage(File pngFile) {
        mClientDetailsPresenter.uploadImage(clientId, pngFile);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mClientDetailsPresenter.detachView();
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

    public void loadNotes() {
        NoteFragment noteFragment = NoteFragment.newInstance(Constants
                .ENTITY_TYPE_CLIENTS, clientId);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_DETAILS);
        fragmentTransaction.replace(R.id.container, noteFragment);
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
                (clientId, false);
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

    public void activateClient() {
        ActivateFragment activateFragment =
                ActivateFragment.newInstance(clientId, Constants.ACTIVATE_CLIENT);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_DETAILS);
        fragmentTransaction.replace(R.id.container, activateFragment);
        fragmentTransaction.commit();
    }

    public void loadClientDataTables() {
        DataTableFragment loanAccountFragment = DataTableFragment.newInstance(Constants
                .DATA_TABLE_NAME_CLIENT, clientId);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_DETAILS);
        fragmentTransaction.replace(R.id.container, loanAccountFragment);
        fragmentTransaction.commit();
    }

    public void loadSignUpload() {
        SignatureFragment fragment = new SignatureFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.CLIENT_ID, clientId);
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_CLIENT_DETAILS);
        fragmentTransaction.replace(R.id.container, fragment).commit();

    }

    @Override
    public void showProgressbar(boolean show) {
        if (show) {
            rlClient.setVisibility(GONE);
            showMifosProgressBar();
        } else {
            rlClient.setVisibility(VISIBLE);
            hideMifosProgressBar();
        }
    }

    @Override
    public void showClientInformation(final Client client) {
        if (client != null) {
            setToolbarTitle(getString(R.string.client) + " - " + client.getDisplayName());
            isClientActive = client.isActive();
            getActivity().invalidateOptionsMenu();
            if (!client.isActive()) {
                llBottomPanel.setVisibility(VISIBLE);
            }
            tv_fullName.setText(client.getDisplayName());
            tv_accountNumber.setText(client.getAccountNo());
            tvGroup.setText(client.getGroupNames());
            tv_externalId.setText(client.getExternalId());
            tvMobileNo.setText(client.getMobileNo());
            if (TextUtils.isEmpty(client.getAccountNo()))
                rowAccount.setVisibility(GONE);

            if (TextUtils.isEmpty(client.getExternalId()))
                rowExternal.setVisibility(GONE);

            if (TextUtils.isEmpty(client.getMobileNo()))
                rowMobileNo.setVisibility(GONE);

            if (TextUtils.isEmpty(client.getGroupNames()))
                rowGroup.setVisibility(GONE);

            try {
                String dateString = Utils.getStringOfDate(
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
                loadClientProfileImage();
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
                    if (!client.isImagePresent()) {
                        menu.getMenu().findItem(R.id.client_image_remove).setVisible(false);
                    }
                    menu.setOnMenuItemClickListener(
                            new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem menuItem) {
                                    switch (menuItem.getItemId()) {
                                        case R.id.client_image_upload:
                                            uploadClientImage();
                                            break;
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
        loadClientProfileImage();
    }

    @Override
    public void showUploadImageProgressbar(boolean b) {
        if (b) {
            pb_imageProgressBar.setVisibility(VISIBLE);
        } else {
            pb_imageProgressBar.setVisibility(GONE);
        }
    }


    public void loadClientProfileImage() {
        pb_imageProgressBar.setVisibility(VISIBLE);
        ImageLoaderUtils.loadImage(getActivity(), clientId, iv_clientImage);
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
            private double mListViewCount;

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
                mListViewCount = Double.valueOf(getCountView(context)
                        .getText()
                        .toString());
                ListView listView = getListView(context);
                resizeListView(context, listView);
                listView.setVisibility(VISIBLE);
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

            private void resizeListView(Activity context, ListView listView) {
                if (mListViewCount < 4) {
                    //default listview height is 200dp,which displays 4 listview items.
                    // This calculates the required listview height
                    // if listview items are less than 4
                    double heightInDp = (mListViewCount / 4) * 200;
                    double heightInPx = heightInDp * context.getResources()
                            .getDisplayMetrics()
                            .density;
                    ViewGroup.LayoutParams params = listView.getLayoutParams();
                    params.height = (int) heightInPx;
                    listView.setLayoutParams(params);
                    listView.requestLayout();
                }
            }
        }
    }
}
