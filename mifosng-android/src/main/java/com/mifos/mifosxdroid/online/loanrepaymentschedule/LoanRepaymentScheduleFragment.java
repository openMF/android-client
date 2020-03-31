/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

package com.mifos.mifosxdroid.online.loanrepaymentschedule;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.mifos.mifosxdroid.R;
import com.mifos.mifosxdroid.adapters.LoanRepaymentScheduleAdapter;
import com.mifos.mifosxdroid.core.MifosBaseActivity;
import com.mifos.mifosxdroid.core.ProgressableFragment;
import com.mifos.mifosxdroid.core.util.Toaster;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.objects.accounts.loan.Period;
import com.mifos.objects.accounts.loan.RepaymentSchedule;
import com.mifos.utils.Constants;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LoanRepaymentScheduleFragment extends ProgressableFragment
        implements LoanRepaymentScheduleMvpView {


    private final String LOG_TAG = getClass().getSimpleName();

    @BindView(R.id.lv_repayment_schedule)
    ListView lv_repaymentSchedule;

    @BindView(R.id.tv_total_paid)
    TextView tv_totalPaid;

    @BindView(R.id.tv_total_upcoming)
    TextView tv_totalUpcoming;

    @BindView(R.id.tv_total_overdue)
    TextView tv_totalOverdue;

    @Inject
    LoanRepaymentSchedulePresenter mLoanRepaymentSchedulePresenter;

    private int loanAccountNumber;
    private View rootView;

    public static LoanRepaymentScheduleFragment newInstance(int loanAccountNumber) {
        LoanRepaymentScheduleFragment fragment = new LoanRepaymentScheduleFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.LOAN_ACCOUNT_NUMBER, loanAccountNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MifosBaseActivity) getActivity()).getActivityComponent().inject(this);
        if (getArguments() != null)
            loanAccountNumber = getArguments().getInt(Constants.LOAN_ACCOUNT_NUMBER);
        setHasOptionsMenu(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_loan_repayment_schedule, container, false);
        setToolbarTitle(getResources().getString(R.string.loan_repayment_schedule));

        ButterKnife.bind(this, rootView);
        mLoanRepaymentSchedulePresenter.attachView(this);

        inflateRepaymentSchedule();

        return rootView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        super.onPrepareOptionsMenu(menu);
    }

    public void inflateRepaymentSchedule() {
        mLoanRepaymentSchedulePresenter.loadLoanRepaySchedule(loanAccountNumber);
    }

    @Override
    public void showProgressbar(boolean b) {
        showProgress(b);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mLoanRepaymentSchedulePresenter.detachView();
    }

    @Override
    public void showLoanRepaySchedule(LoanWithAssociations loanWithAssociations) {
        /* Activity is null - Fragment has been detached; no need to do anything. */
        if (getActivity() == null) return;

        List<Period> listOfActualPeriods = loanWithAssociations
                .getRepaymentSchedule()
                .getlistOfActualPeriods();

        LoanRepaymentScheduleAdapter loanRepaymentScheduleAdapter = new
                LoanRepaymentScheduleAdapter(getActivity(), listOfActualPeriods);
        lv_repaymentSchedule.setAdapter(loanRepaymentScheduleAdapter);

        String totalRepaymentsCompleted = getResources().getString(R.string
                .complete) + "" +
                " : ";
        String totalRepaymentsOverdue = getResources().getString(R.string
                .overdue) + " : ";
        String totalRepaymentsPending = getResources().getString(R.string
                .pending) + " : ";
        //Implementing the Footer here
        tv_totalPaid.setText(totalRepaymentsCompleted + RepaymentSchedule
                .getNumberOfRepaymentsComplete(listOfActualPeriods));
        tv_totalOverdue.setText(totalRepaymentsOverdue + RepaymentSchedule
                .getNumberOfRepaymentsOverDue(listOfActualPeriods));
        tv_totalUpcoming.setText(totalRepaymentsPending + RepaymentSchedule
                .getNumberOfRepaymentsPending(listOfActualPeriods));
    }

    @Override
    public void showFetchingError(String s) {
        Toaster.show(rootView, s);
    }
}
