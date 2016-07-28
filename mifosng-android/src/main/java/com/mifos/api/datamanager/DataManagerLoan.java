package com.mifos.api.datamanager;

import com.mifos.api.BaseApiManager;
import com.mifos.api.local.databasehelper.DatabaseHelperLoan;
import com.mifos.objects.accounts.loan.LoanRepaymentRequest;
import com.mifos.objects.accounts.loan.LoanRepaymentResponse;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.objects.accounts.loan.Loans;
import com.mifos.objects.organisation.LoanProducts;
import com.mifos.objects.templates.loans.LoanRepaymentTemplate;
import com.mifos.objects.templates.loans.LoanTemplate;
import com.mifos.services.data.LoansPayload;
import com.mifos.utils.PrefManager;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Rajan Maurya on 15/07/16.
 */
@Singleton
public class DataManagerLoan {

    public final BaseApiManager mBaseApiManager;
    public final DatabaseHelperLoan mDatabaseHelperLoan;

    @Inject
    public DataManagerLoan(BaseApiManager baseApiManager,
                           DatabaseHelperLoan databaseHelperLoan) {
        mBaseApiManager = baseApiManager;
        mDatabaseHelperLoan = databaseHelperLoan;
    }


    /**
     * This Method sending the Request to REST API if UserStatus is 0 and
     * get the LoanWithAssociation. The response is pass to the DatabaseHelperLoan
     * that save the response in Database with Observable.defer and next pass the response to
     * DataManager to pass to Presenter to show in the view.
     * <p/>
     * If UserStatus is 1 means User is in the Offline mode, SO it send request to
     * DatabaseHelperLon to fetch Data from Database and give back to DataManager and DataManager
     * gives to Presenter to show on the view.
     *
     * @param loanId Loan Id of the Loan
     * @return LoanWithAssociation
     */
    public Observable<LoanWithAssociations> getLoanById(int loanId) {
        switch (PrefManager.getUserStatus()) {
            case 0:
                return mBaseApiManager.getLoanApi()
                        .getLoanByIdWithAllAssociations(loanId)
                        .concatMap(new Func1<LoanWithAssociations,
                                Observable<? extends LoanWithAssociations>>() {

                            @Override
                            public Observable<? extends LoanWithAssociations> call
                                    (LoanWithAssociations loanWithAssociations) {
                                return mDatabaseHelperLoan.saveLoanById(loanWithAssociations);
                            }
                        });
            case 1:
                /**
                 * Return LoanWithAssociation from DatabaseHelperLoan.
                 */
                return mDatabaseHelperLoan.getLoanById(loanId);

            default:
                return Observable.just(new LoanWithAssociations());
        }

    }

    public Observable<List<LoanProducts>> getAllLoans() {
        return mBaseApiManager.getLoanApi().getAllLoans();
    }

    public Observable<LoanTemplate> getLoansAccountTemplate(int clientId, int productId) {
        return mBaseApiManager.getLoanApi().getLoansAccountTemplate(clientId, productId);
    }

    public Observable<Loans> createLoansAccount(LoansPayload loansPayload) {
        return mBaseApiManager.getLoanApi().createLoansAccount(loansPayload);
    }

    public Observable<LoanRepaymentTemplate> getLoanRepayTemplate(int loanId) {
        return mBaseApiManager.getLoanApi().getLoanRepaymentTemplate(loanId);
    }


    /**
     * This Method For submitting the Loan Payment. This Method have two mode, One if Online when
     * PrefManager.getUserStatus() is 0, Whenever User Online the Post request going to Server
     * Directly, here is the End Point
     * {https://demo.openmf.org/fineract-provider/api/v1/loans/{loanId}/transactions?command
     * =repayment}
     * and get the LoanRepaymentResponse in response of Successful Transaction.
     * And Whenever User in Offline Mode the Request goes to DatabaseHelperLoan and DatabaseHelper
     * Save the Transaction on Database and in Response give the Empty LoanRepaymentResponse.
     *
     * @param loanId Loan id of The Loan
     * @param request Request Body of POST Request
     * @return LoanRepaymentResponse
     */
    public Observable<LoanRepaymentResponse> submitPayment(final int loanId,
                                                           final LoanRepaymentRequest request) {
        switch (PrefManager.getUserStatus()) {
            case 0:
                return mBaseApiManager.getLoanApi().submitPayment(loanId, request)
                        .concatMap(new Func1<LoanRepaymentResponse, Observable<? extends
                                LoanRepaymentResponse>>() {
                            @Override
                            public Observable<? extends LoanRepaymentResponse> call
                                    (LoanRepaymentResponse loanRepaymentResponse) {
                                return Observable.just(loanRepaymentResponse);
                            }
                        });
            case 1:
                /**
                 * Return LoanRepaymentResponse from DatabaseHelperLoan.
                 */
                return mDatabaseHelperLoan.saveLoanRepaymentTransaction(loanId, request);

            default:
                return Observable.just(new LoanRepaymentResponse());
        }
    }
}
