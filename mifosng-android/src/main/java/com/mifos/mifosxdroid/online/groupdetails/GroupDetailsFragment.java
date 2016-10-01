package com.mifos.mifosxdroid.online.groupdetails;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.joanzapata.iconify.fonts.MaterialIcons;
import com.joanzapata.iconify.widget.IconTextView;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.LoanAccountsListAdapter;
import com.mifos.mifosxdroid.adapters.SavingsAccountsListAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.ProgressableFragment;
import com.mifos.mifosxdroid.online.documentlist.DocumentListFragment;
import com.mifos.mifosxdroid.online.grouploanaccount.GroupLoanAccountFragment;
import com.mifos.objects.accounts.GroupAccounts;
import com.mifos.objects.accounts.savings.DepositType;
import com.mifos.objects.client.Client;
import com.mifos.objects.group.Group;
import com.mifos.objects.noncore.DataTable;
import com.mifos.utils.Constants;
import com.mifos.utils.FragmentConstants;
import com.mifos.utils.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by nellyk on 2/27/2016.
 */
public class GroupDetailsFragment extends ProgressableFragment implements GroupDetailsMvpView {

    public static final String LOG_TAG = GroupDetailsFragment.class.getSimpleName();

    @BindView(R.id.tv_groupsName)
    TextView tv_fullName;

    @BindView(R.id.tv_groupexternalId)
    TextView tv_externalId;

    @BindView(R.id.tv_groupactivationDate)
    TextView tv_activationDate;

    @BindView(R.id.tv_groupoffice)
    TextView tv_office;

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
    GroupDetailsPresenter mGroupDetailsPresenter;

    private View rootView;
    private int groupId;
    private AccountAccordion accountAccordion;
    private OnFragmentInteractionListener mListener;
    public List<DataTable> clientDataTables = new ArrayList<>();

    public static GroupDetailsFragment newInstance(int groupId) {
        GroupDetailsFragment fragment = new GroupDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.GROUP_ID, groupId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        if (getArguments() != null) {
            groupId = getArguments().getInt(Constants.GROUP_ID);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_group_details, container, false);

        ButterKnife.bind(this, rootView);
        mGroupDetailsPresenter.attachView(this);

        mGroupDetailsPresenter.loadGroupDetailsAndAccounts(groupId);
        mGroupDetailsPresenter.loadClientDataTable();

        return rootView;
    }

    public void loadDocuments() {
        DocumentListFragment documentListFragment = DocumentListFragment.newInstance(Constants
                .ENTITY_TYPE_GROUPS, groupId);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_GROUP_DETAILS);
        fragmentTransaction.replace(R.id.container, documentListFragment);
        fragmentTransaction.commit();
    }

    public void addGroupLoanAccount() {
        GroupLoanAccountFragment grouploanAccountFragment = GroupLoanAccountFragment.newInstance
                (groupId);
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.addToBackStack(FragmentConstants.FRAG_GROUP_DETAILS);
        fragmentTransaction.replace(R.id.container, grouploanAccountFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void showProgressbar(boolean b) {
        showProgress(b);
    }

    @Override
    public void showGroup(Group group) {
        if (group != null) {
            setToolbarTitle(getString(R.string.group) + " - " + group.getName());
            tv_fullName.setText(group.getName());
            tv_externalId.setText(group.getExternalId());

            try {
                String dateString = Utils.getStringOfDate(getActivity(), group.getActivationDate());
                tv_activationDate.setText(dateString);

                if (TextUtils.isEmpty(dateString))
                    rowActivation.setVisibility(GONE);

            } catch (IndexOutOfBoundsException e) {
                Toast.makeText(getActivity(), getString(R.string.error_group_inactive),
                        Toast.LENGTH_SHORT).show();
                tv_activationDate.setText("");
            }
            tv_office.setText(group.getOfficeName());

            if (TextUtils.isEmpty(group.getOfficeName()))
                rowOffice.setVisibility(GONE);
        }
    }

    @Override
    public void showGroupClients(List<Client> clients) {
        mListener.loadGroupClients(clients);
    }

    @Override
    public void showGroupAccounts(GroupAccounts groupAccounts) {
        // Proceed only when the fragment is added to the activity.
        if (!isAdded()) {
            return;
        }
        accountAccordion = new AccountAccordion(getActivity());
        if (groupAccounts.getLoanAccounts().size() > 0) {
            AccountAccordion.Section section = AccountAccordion.Section.LOANS;
            final LoanAccountsListAdapter adapter =
                    new LoanAccountsListAdapter(getActivity().getApplicationContext(),
                            groupAccounts.getLoanAccounts());
            section.connect(getActivity(), adapter, new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i,
                                        long l) {
                    mListener.loadLoanAccountSummary(adapter.getItem(i).getId());
                }
            });
        }

        if (groupAccounts.getNonRecurringSavingsAccounts().size() > 0) {
            AccountAccordion.Section section = AccountAccordion.Section.SAVINGS;
            final SavingsAccountsListAdapter adapter =
                    new SavingsAccountsListAdapter(getActivity().getApplicationContext(),
                            groupAccounts.getNonRecurringSavingsAccounts());
            section.connect(getActivity(), adapter, new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i,
                                        long l) {
                    mListener.loadSavingsAccountSummary(adapter.getItem(i).getId(),
                            adapter.getItem(i).getDepositType());
                }
            });
        }

        if (groupAccounts.getRecurringSavingsAccounts().size() > 0) {
            AccountAccordion.Section section = AccountAccordion.Section.RECURRING;
            final SavingsAccountsListAdapter adapter =
                    new SavingsAccountsListAdapter(getActivity().getApplicationContext(),
                            groupAccounts.getRecurringSavingsAccounts());
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
    public void showGroupDataTable(List<DataTable> dataTables) {
        if (dataTables != null) {
            Iterator<DataTable> dataTableIterator = dataTables.iterator();
            clientDataTables.clear();
            while (dataTableIterator.hasNext()) {
                clientDataTables.add(dataTableIterator.next());
            }
        }
    }

    @Override
    public void showFetchingError(int errorMessage) {
        Toast.makeText(getActivity(), getStringMessage(errorMessage), Toast.LENGTH_SHORT).show();
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.group, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.documents:
                loadDocuments();
                break;
            case R.id.add_group_loan:
                addGroupLoanAccount();
                break;
            case R.id.group_clients:
                mGroupDetailsPresenter.loadGroupAssociateClients(groupId);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mGroupDetailsPresenter.detachView();
    }


    public interface OnFragmentInteractionListener {
        void loadLoanAccountSummary(int loanAccountNumber);

        void loadSavingsAccountSummary(int savingsAccountNumber, DepositType accountType);

        void loadGroupClients(List<Client> clients);
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

                View.OnClickListener onClickListener = new View.OnClickListener() {
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
                    listView.setOnTouchListener(new View.OnTouchListener() {
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

}
