package com.mifos.mifosxdroid.online.grouploanaccount;

import android.util.Log;

import com.mifos.api.DataManager;
import com.mifos.mifosxdroid.base.BasePresenter;
import com.mifos.objects.accounts.loan.Loans;
import com.mifos.objects.organisation.LoanProducts;
import com.mifos.objects.templates.loans.AmortizationTypeOptions;
import com.mifos.objects.templates.loans.FundOptions;
import com.mifos.objects.templates.loans.GroupLoanTemplate;
import com.mifos.objects.templates.loans.InterestCalculationPeriodType;
import com.mifos.objects.templates.loans.InterestTypeOptions;
import com.mifos.objects.templates.loans.LoanOfficerOptions;
import com.mifos.objects.templates.loans.LoanPurposeOptions;
import com.mifos.objects.templates.loans.TermFrequencyTypeOptions;
import com.mifos.objects.templates.loans.TransactionProcessingStrategyOptions;
import com.mifos.services.data.GroupLoanPayload;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 08/06/16.
 */
public class GroupLoanAccountPresenter extends BasePresenter<GroupLoanAccountMvpView> {

    private final DataManager mDataManager;
    private Subscription mSubscription;

    @Inject
    public GroupLoanAccountPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }


    @Override
    public void attachView(GroupLoanAccountMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    public void loadAllLoans() {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getAllLoans()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<List<LoanProducts>>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError("Failed to fetch Loans");
                    }

                    @Override
                    public void onNext(List<LoanProducts> productLoans) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showAllLoans(productLoans);
                    }
                });
    }

    public void loadGroupLoansAccountTemplate(int groupId, int productId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.getGroupLoansAccountTemplate(groupId, productId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<GroupLoanTemplate>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError("Failed to load GroupLoan");
                    }

                    @Override
                    public void onNext(GroupLoanTemplate groupLoanTemplate) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showGroupLoanTemplate(
                                groupLoanTemplate);

                    }
                });
    }


    public void createGroupLoanAccount(GroupLoanPayload loansPayload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        if (mSubscription != null) mSubscription.unsubscribe();
        mSubscription = mDataManager.createGroupLoansAccount(loansPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Loans>() {
                    @Override
                    public void onCompleted() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError("Try Again");
                        Log.e("mayanklog", e.toString());
                    }

                    @Override
                    public void onNext(Loans loans) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showGroupLoansAccountCreatedSuccessfully(loans);
                    }
                });
    }

    public List<String> fillamortizationSpinnerOptions(final List<AmortizationTypeOptions>
                                                               amortizationTypeOptions) {
        final ArrayList<String> amortizationNameList = new ArrayList<>();
        Observable.from(amortizationTypeOptions)
                .subscribe(new Action1<AmortizationTypeOptions>() {
                    @Override
                    public void call(AmortizationTypeOptions amortizationTypeOptions) {
                        amortizationNameList.add(amortizationTypeOptions.getValue());
                    }
                });
        return amortizationNameList;
    }

    public List<String> fillinterestcalculationperiodSpinnerOptions(final
                                                                    List
                                                                    <InterestCalculationPeriodType>
                                                               interestCalculationPeriodType) {
        final ArrayList<String> interestCalculationPeriodNameList = new ArrayList<>();
        Observable.from(interestCalculationPeriodType)
                .subscribe(new Action1<InterestCalculationPeriodType>() {
                    @Override
                    public void call(InterestCalculationPeriodType interestCalculationPeriodType) {
                        interestCalculationPeriodNameList.add(
                                interestCalculationPeriodType.getValue());
                    }
                });
        return interestCalculationPeriodNameList;
    }

    public List<String> filltransactionProcessingStrategySpinnerOptions(final
             List<TransactionProcessingStrategyOptions>
              transactionProcessingStrategyOptions) {
        final ArrayList<String> transactionProcessingStrategyNameList = new ArrayList<>();
        Observable.from(transactionProcessingStrategyOptions)
                .subscribe(new Action1<TransactionProcessingStrategyOptions>() {
                    @Override
                    public void call(TransactionProcessingStrategyOptions
                                             transactionProcessingStrategyOptions) {
                        transactionProcessingStrategyNameList.add(
                                transactionProcessingStrategyOptions.getName());
                    }
                });
       // Log.d("mayanksdf"," "+transactionProcessingStrategyNameList.size());
        return transactionProcessingStrategyNameList;
    }


    public List<String> fillLoanProductSpinner(final List<LoanProducts> loanProducts) {
        final ArrayList<String> loanProductsNameList = new ArrayList<>();
        Observable.from(loanProducts)
                .subscribe(new Action1<LoanProducts>() {
                    @Override
                    public void call(LoanProducts loanProducts) {
                        loanProductsNameList.add(loanProducts.getName());
                    }
                });
        return loanProductsNameList;
    }

    public List<String> filltermFrequencyTypeSpinnerOptions(final List<TermFrequencyTypeOptions>
                                                                    termFrequencyTypeOptions) {
        final ArrayList<String> termFrequencyNameList = new ArrayList<>();
        Observable.from(termFrequencyTypeOptions)
                .subscribe(new Action1<TermFrequencyTypeOptions>() {
                    @Override
                    public void call(TermFrequencyTypeOptions termFrequencyTypeOptions) {
                        termFrequencyNameList.add(termFrequencyTypeOptions.getValue());
                    }
                });
        return termFrequencyNameList;
    }

    public List<String> fillloanPurposeTypeSpinnerOptions(final List<LoanPurposeOptions>
                                                                  loanPurposeOptions) {
        final ArrayList<String> loanPurposeNameList = new ArrayList<>();
        Observable.from(loanPurposeOptions)
                .subscribe(new Action1<LoanPurposeOptions>() {
                    @Override
                    public void call(LoanPurposeOptions loanPurposeOptions) {
                        loanPurposeNameList.add(loanPurposeOptions.getName());
                    }
                });
        return loanPurposeNameList;
    }

    public List<String> fillinterestTypeOptionsSpinnerOptions(final List<InterestTypeOptions>
                                                                      interestTypeOptions) {
        final ArrayList<String> interestTypeNameList = new ArrayList<>();
        Observable.from(interestTypeOptions)
                .subscribe(new Action1<InterestTypeOptions>() {
                    @Override
                    public void call(InterestTypeOptions interestTypeOptions) {
                        interestTypeNameList.add(interestTypeOptions.getValue());
                    }
                });
        return interestTypeNameList;
    }

    public List<String> fillloanOfficerSpinnerOptions(final List<LoanOfficerOptions>
                                                              loanOfficerOptions) {
        final ArrayList<String> loanOfficerNameList = new ArrayList<>();
        Observable.from(loanOfficerOptions)
                .subscribe(new Action1<LoanOfficerOptions>() {
                    @Override
                    public void call(LoanOfficerOptions loanOfficerOptions) {
                        loanOfficerNameList.add(loanOfficerOptions.getDisplayName());
                    }
                });
        return loanOfficerNameList;
    }

    public List<String> fillfundSpinnerOptions(final List<FundOptions> fundOptions) {
        final ArrayList<String> fundNameList = new ArrayList<>();
        Observable.from(fundOptions)
                .subscribe(new Action1<FundOptions>() {
                    @Override
                    public void call(FundOptions fundOptions) {
                        fundNameList.add(fundOptions.getName());
                    }
                });
        return fundNameList;
    }

}
