package com.mifos.mifosxdroid.online.collectionsheetindividualdetails;

import android.os.Bundle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.mifos.api.model.BulkRepaymentTransactions;
import com.mifos.api.model.IndividualCollectionSheetPayload;
import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.IndividualCollectionSheetDetailsAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.MifosBaseFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.mifosxdroid.online.GenerateCollectionSheetActivity;
import com.mifos.objects.accounts.loan.PaymentTypeOptions;
import com.mifos.objects.collectionsheet.IndividualCollectionSheet;
import com.mifos.objects.collectionsheet.LoanAndClientName;
import com.mifos.objects.collectionsheet.LoanCollectionSheet;
import com.mifos.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by aksh on 20/6/18.
 */

public class IndividualCollectionSheetDetailsFragment
        extends MifosBaseFragment implements IndividualCollectionSheetDetailsMvpView,
        OnRetrieveSheetItemData,
        IndividualCollectionSheetDetailsAdapter.ListAdapterListener {


    @BindView(R.id.recycler_collections)
    RecyclerView recyclerSheets;

    @Inject
    IndividualCollectionSheetDetailsPresenter presenter;

    IndividualCollectionSheetDetailsAdapter sheetsAdapter;

    private IndividualCollectionSheet sheet;
    private List<String> paymentTypeList;
    private List<LoanAndClientName> loansAndClientNames;
    private IndividualCollectionSheetPayload payload;
    private View rootView;
    private int requestCode = 1;

    private String actualDisbursementDate;
    private String transactionDate;

    public IndividualCollectionSheetDetailsFragment() {
    }

    public static IndividualCollectionSheetDetailsFragment
            newInstance(IndividualCollectionSheet sheet,
                String actualDisbursementDate, String transactionDate) {
        Bundle args = new Bundle();
        args.putParcelable(Constants.INDIVIDUAL_SHEET, sheet);
        args.putString(Constants.DISBURSEMENT_DATE, actualDisbursementDate);
        args.putString(Constants.TRANSACTION_DATE, transactionDate);
        IndividualCollectionSheetDetailsFragment fragment = new
                IndividualCollectionSheetDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        if (savedInstanceState != null) {
            sheet = (IndividualCollectionSheet) savedInstanceState.get(
                    Constants.EXTRA_COLLECTION_INDIVIDUAL);
            showCollectionSheetViews(sheet);
        }
        sheet = getArguments().getParcelable(Constants.INDIVIDUAL_SHEET);

        actualDisbursementDate = getArguments().getString(Constants.DISBURSEMENT_DATE);
        transactionDate = getArguments().getString(Constants.TRANSACTION_DATE);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.individual_collections_sheet_details,
                container, false);
        ButterKnife.bind(this, rootView);
        setToolbarTitle(getStringMessage(R.string.individual_collection_sheet));
        sheetsAdapter = new IndividualCollectionSheetDetailsAdapter(getContext(), this);
        presenter.attachView(this);
        this.payload = ((GenerateCollectionSheetActivity) getActivity()).getPayload();
        showCollectionSheetViews(sheet);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_individual_collectionsheet, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_submit_sheet:
                submitSheet();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showCollectionSheetViews(IndividualCollectionSheet sheet) {
        paymentTypeList = presenter.filterPaymentTypeOptions(sheet.getPaymentTypeOptions());
        loansAndClientNames = presenter.filterLoanAndClientNames(sheet.getClients());

        //Initialize payload's BulkRepaymentTransactions array with default values.
        //The changes made (if any) will be updated by the interface 'OnRetrieveSheetItemData'

        //methods.
        if (payload == null) {
            payload = new IndividualCollectionSheetPayload();
            for (LoanAndClientName loanAndClientName :
                    presenter.filterLoanAndClientNames(sheet.getClients())) {
                LoanCollectionSheet loanCollectionSheet = loanAndClientName.getLoan();
                payload.getBulkRepaymentTransactions().add(new BulkRepaymentTransactions(
                        loanCollectionSheet.getLoanId(),
                        loanCollectionSheet.getChargesDue() == null ?
                                loanCollectionSheet.getTotalDue() :
                                loanCollectionSheet.getTotalDue() +
                                        loanCollectionSheet.getChargesDue()));
            }
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerSheets.getContext(), layoutManager.getOrientation());
        recyclerSheets.setLayoutManager(layoutManager);
        recyclerSheets.addItemDecoration(dividerItemDecoration);
        recyclerSheets.setAdapter(sheetsAdapter);

        sheetsAdapter.setSheetItemClickListener(this);
        sheetsAdapter.setLoans(loansAndClientNames);
        sheetsAdapter.setPaymentTypeList(paymentTypeList);
        sheetsAdapter.setPaymentTypeOptionsList(sheet.getPaymentTypeOptions());
        sheetsAdapter.notifyDataSetChanged();
    }

    @Override
    public void showSuccess() {
        Toaster.show(rootView, getStringMessage(R.string.collectionsheet_submit_success));
    }

    @Override
    public void showError(String error) {
        Toaster.show(rootView, error);
    }

    @Override
    public void showProgressbar(boolean b) {
        if (b) {
            showMifosProgressDialog();
        } else {
            hideMifosProgressDialog();
        }
    }

    public IndividualCollectionSheetPayload getPayload() {
        return payload;
    }

    @Override
    public void onShowSheetMandatoryItem(BulkRepaymentTransactions transaction, int position) {

    }

    @Override
    public void onSaveAdditionalItem(BulkRepaymentTransactions transaction, int position) {
        payload.getBulkRepaymentTransactions().set(position, transaction);
    }


    @Override
    public void listItemPosition(int position) {
        ArrayList<String> paymentTypeOptionList = new ArrayList<>(paymentTypeList);
        ArrayList<PaymentTypeOptions> paymentTypeOptions = new ArrayList<>(sheet.
                getPaymentTypeOptions());
        LoanAndClientName current = loansAndClientNames.get(position);
        int clientId = current.getId();
        PaymentDetailsFragment fragment = new PaymentDetailsFragment().newInstance(position, payload
                , paymentTypeOptionList, current, paymentTypeOptions, clientId);
        fragment.setTargetFragment(this, requestCode);
        ((MifosBaseActivity) getContext()).replaceFragment(fragment, true, R.id.container);
    }

    private void submitSheet() {
        if (payload == null) {
            Toaster.show(rootView, getStringMessage(R.string.error_generate_sheet_first));
        } else {
            payload.setActualDisbursementDate(actualDisbursementDate);
            payload.setTransactionDate(transactionDate);
            presenter.submitIndividualCollectionSheet(payload);
        }
    }
}
