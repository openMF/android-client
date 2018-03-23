package com.mifos.mifosxdroid.online.grouploanaccount;

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

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Rajan Maurya on 08/06/16.
 */
public class GroupLoanAccountPresenter extends BasePresenter<GroupLoanAccountMvpView> {

    private final DataManager mDataManager;
    private CompositeDisposable compositeDisposable;

    @Inject
    public GroupLoanAccountPresenter(DataManager dataManager) {
        mDataManager = dataManager;
        compositeDisposable = new CompositeDisposable();
    }


    @Override
    public void attachView(GroupLoanAccountMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        compositeDisposable.clear();
    }

    public void loadAllLoans() {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        compositeDisposable.add(mDataManager.getAllLoans()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<List<LoanProducts>>() {
                    @Override
                    public void onComplete() {
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
                })
        );
    }

    public void loadGroupLoansAccountTemplate(int groupId, int productId) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        compositeDisposable.add(mDataManager.getGroupLoansAccountTemplate(groupId, productId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<GroupLoanTemplate>() {
                    @Override
                    public void onComplete() {
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
                })
        );
    }


    public void createGroupLoanAccount(GroupLoanPayload loansPayload) {
        checkViewAttached();
        getMvpView().showProgressbar(true);
        compositeDisposable.add(mDataManager.createGroupLoansAccount(loansPayload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(new DisposableObserver<Loans>() {
                    @Override
                    public void onComplete() {
                        getMvpView().showProgressbar(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showFetchingError("Try Again");
                    }

                    @Override
                    public void onNext(Loans loans) {
                        getMvpView().showProgressbar(false);
                        getMvpView().showGroupLoansAccountCreatedSuccessfully(loans);
                    }
                })
        );
    }

    public List<String> filterAmortizations(final List<AmortizationTypeOptions>
                                                               amortizationTypeOptions) {
        final ArrayList<String> amortizationNameList = new ArrayList<>();
        Observable.fromIterable(amortizationTypeOptions)
                .subscribe(new Consumer<AmortizationTypeOptions>() {
                    @Override
                    public void accept(AmortizationTypeOptions amortizationTypeOptions) {
                        amortizationNameList.add(amortizationTypeOptions.getValue());
                    }
                });
        return amortizationNameList;
    }

    public List<String> filterInterestCalculationPeriods(final
                                                                    List
                                                                    <InterestCalculationPeriodType>
                                                               interestCalculationPeriodType) {
        final ArrayList<String> interestCalculationPeriodNameList = new ArrayList<>();
        Observable.fromIterable(interestCalculationPeriodType)
                .subscribe(new Consumer<InterestCalculationPeriodType>() {
                    @Override
                    public void accept(InterestCalculationPeriodType interestCalculationPeriodType) {
                        interestCalculationPeriodNameList.add(
                                interestCalculationPeriodType.getValue());
                    }
                });
        return interestCalculationPeriodNameList;
    }

    public List<String> filterTransactionProcessingStrategies(final
             List<TransactionProcessingStrategyOptions>
              transactionProcessingStrategyOptions) {
        final ArrayList<String> transactionProcessingStrategyNameList = new ArrayList<>();
        Observable.fromIterable(transactionProcessingStrategyOptions)
                .subscribe(new Consumer<TransactionProcessingStrategyOptions>() {
                    @Override
                    public void accept(TransactionProcessingStrategyOptions
                                             transactionProcessingStrategyOptions) {
                        transactionProcessingStrategyNameList.add(
                                transactionProcessingStrategyOptions.getName());
                    }
                });
        return transactionProcessingStrategyNameList;
    }


    public List<String> filterLoanProducts(final List<LoanProducts> loanProducts) {
        final ArrayList<String> loanProductsNameList = new ArrayList<>();
        Observable.fromIterable(loanProducts)
                .subscribe(new Consumer<LoanProducts>() {
                    @Override
                    public void accept(LoanProducts loanProducts) {
                        loanProductsNameList.add(loanProducts.getName());
                    }
                });
        return loanProductsNameList;
    }

    public List<String> filterTermFrequencyTypes(final List<TermFrequencyTypeOptions>
                                                                    termFrequencyTypeOptions) {
        final ArrayList<String> termFrequencyNameList = new ArrayList<>();
        Observable.fromIterable(termFrequencyTypeOptions)
                .subscribe(new Consumer<TermFrequencyTypeOptions>() {
                    @Override
                    public void accept(TermFrequencyTypeOptions termFrequencyTypeOptions) {
                        termFrequencyNameList.add(termFrequencyTypeOptions.getValue());
                    }
                });
        return termFrequencyNameList;
    }

    public List<String> filterLoanPurposeTypes(final List<LoanPurposeOptions>
                                                                  loanPurposeOptions) {
        final ArrayList<String> loanPurposeNameList = new ArrayList<>();
        Observable.fromIterable(loanPurposeOptions)
                .subscribe(new Consumer<LoanPurposeOptions>() {
                    @Override
                    public void accept(LoanPurposeOptions loanPurposeOptions) {
                        loanPurposeNameList.add(loanPurposeOptions.getName());
                    }
                });
        return loanPurposeNameList;
    }

    public List<String> filterInterestTypeOptions(final List<InterestTypeOptions>
                                                                      interestTypeOptions) {
        final ArrayList<String> interestTypeNameList = new ArrayList<>();
        Observable.fromIterable(interestTypeOptions)
                .subscribe(new Consumer<InterestTypeOptions>() {
                    @Override
                    public void accept(InterestTypeOptions interestTypeOptions) {
                        interestTypeNameList.add(interestTypeOptions.getValue());
                    }
                });
        return interestTypeNameList;
    }

    public List<String> filterLoanOfficers(final List<LoanOfficerOptions>
                                                              loanOfficerOptions) {
        final ArrayList<String> loanOfficerNameList = new ArrayList<>();
        Observable.fromIterable(loanOfficerOptions)
                .subscribe(new Consumer<LoanOfficerOptions>() {
                    @Override
                    public void accept(LoanOfficerOptions loanOfficerOptions) {
                        loanOfficerNameList.add(loanOfficerOptions.getDisplayName());
                    }
                });
        return loanOfficerNameList;
    }

    public List<String> filterFunds(final List<FundOptions> fundOptions) {
        final ArrayList<String> fundNameList = new ArrayList<>();
        Observable.fromIterable(fundOptions)
                .subscribe(new Consumer<FundOptions>() {
                    @Override
                    public void accept(FundOptions fundOptions) {
                        fundNameList.add(fundOptions.getName());
                    }
                });
        return fundNameList;
    }

}
