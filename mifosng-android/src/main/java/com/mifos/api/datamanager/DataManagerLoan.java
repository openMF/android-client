package com.mifos.api.datamanager;

import com.mifos.api.BaseApiManager;
import com.mifos.api.local.databasehelper.DatabaseHelperLoan;
import com.mifos.objects.PaymentTypeOption;
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
                return mBaseApiManager.getLoanApi().getLoanByIdWithAllAssociations(loanId);

            case 1:
                /**
                 * Return LoanWithAssociation from DatabaseHelperLoan.
                 */
                return mDatabaseHelperLoan.getLoanById(loanId);

            default:
                return Observable.just(new LoanWithAssociations());
        }

    }


    /**
     * This Method sending the Request to REST API and
     * get the LoanWithAssociation. The response is pass to the DatabaseHelperLoan
     * that save the response in Database with Observable.defer and next pass the response to
     * DataManager to pass to Presenter to show in the view.
     *
     * @param loanId Loan Id
     * @return LoanWithAssociations
     */
    public Observable<LoanWithAssociations> syncLoanById(int loanId) {
        return mBaseApiManager.getLoanApi()
                .getLoanByIdWithAllAssociations(loanId)
                .concatMap(new Func1<LoanWithAssociations, Observable<? extends
                        LoanWithAssociations>>() {
                    @Override
                    public Observable<? extends LoanWithAssociations> call
                            (LoanWithAssociations loanWithAssociations) {
                        return mDatabaseHelperLoan.saveLoanById(loanWithAssociations);
                    }
                });
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

    /**
     * This Method to request the LoanRepaymentTemplate according to Loan Id and get
     * LoanRepaymentTemplate in Response. This method work in both mode Online and Offline.
     * if PrefManager.getUserStatus() is 0, means user is in Online Mode the Request goes to the
     * Server End Point directly. Here is End Point :
     * {https://demo.openmf.org/fineract-provider/api/v1/loans/{loanId}/transactions/template
     * ?command=repayment}
     * and get LoanRepaymentTemplate in response and then call the
     * mDatabaseHelperLoan.saveLoanRepaymentTemplate(loanId,loanRepaymentTemplate); to save the
     * Template into Database for accessing in the Offline.
     * <p/>
     * if PrefManager.getUserStatus() is 1, It means user is Offline Mode, Request goes to the
     * mDatabaseHelperLoan to load the LoanRepaymentTemplate according loanId and gives the
     * LoanRepaymentTemplate in Response.
     *
     * @param loanId Loan Id of the LoanRepaymentTemplate
     * @return LoanRepaymentTemplate
     */
    public Observable<LoanRepaymentTemplate> getLoanRepayTemplate(final int loanId) {
        switch (PrefManager.getUserStatus()) {
            case 0:
                return mBaseApiManager.getLoanApi().getLoanRepaymentTemplate(loanId);

            case 1:
                /**
                 * Return LoanRepaymentTemplate from DatabaseHelperLoan.
                 */
                return mDatabaseHelperLoan.getLoanRepayTemplate(loanId);

            default:
                return Observable.just(new LoanRepaymentTemplate());
        }
    }

    /**
     * This Method to request the LoanRepaymentTemplate according to Loan Id and get
     * LoanRepaymentTemplate in Response. Request goes to the Server End Point directly.
     * Here is End Point :
     * {https://demo.openmf.org/fineract-provider/api/v1/loans/{loanId}/transactions/template
     * ?command=repayment}
     * and get LoanRepaymentTemplate in response and then call the
     * mDatabaseHelperLoan.saveLoanRepaymentTemplate(loanId,loanRepaymentTemplate); to save the
     * Template into Database for accessing in the Offline.
     *
     * @param loanId Loan Id
     * @return LoanRepaymentTemplate
     */
    public Observable<LoanRepaymentTemplate> syncLoanRepaymentTemplate(final int loanId) {
        return mBaseApiManager.getLoanApi().getLoanRepaymentTemplate(loanId)
                .concatMap(new Func1<LoanRepaymentTemplate, Observable<? extends
                        LoanRepaymentTemplate>>() {
                    @Override
                    public Observable<? extends LoanRepaymentTemplate> call
                            (LoanRepaymentTemplate loanRepaymentTemplate) {
                        return mDatabaseHelperLoan.saveLoanRepaymentTemplate(loanId,
                                loanRepaymentTemplate);
                    }
                });
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
     * @param loanId  Loan id of The Loan
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


    /**
     * This Method send Query to DatabaseLoan and get the List<LoanRepayment> saved LoanRepayments.
     * These LoanRepayment are those LoanRepayment that are saved during the Offline LoanRepayment.
     *
     * @return List<LoanRepaymentRequest>
     */
    public Observable<List<LoanRepaymentRequest>> getDatabaseLoanRepayments() {
        return mDatabaseHelperLoan.readAllLoanRepaymentTransaction();
    }


    /**
     * This method request a Observable to DatabaseHelperLoan and DatabaseHelper check in
     * LoanRepayment Table that with this loan Id, any entry is available or not.
     * <p/>
     * If yes, It means with this loan id already a LoanRepayment had been done. and return the
     * LoanRepayment, Now User cannot make new LoanRepayment till that He/She will not sync the
     * previous LoanRepayment.
     * <p/>
     * If NO, this will return null that represent that there is no previous LoanRepayment In
     * Database with this Loan Id, user is able to make new one.
     *
     * @param loanId
     * @return LoanRepayment with this Loan Id reference.
     */
    public Observable<LoanRepaymentRequest> getDatabaseLoanRepaymentByLoanId(int loanId) {
        return mDatabaseHelperLoan.getDatabaseLoanRepaymentByLoanId(loanId);
    }


    /**
     * This Method Load the PaymentTypeOption of any Loan, Saving, Reoccurring from Database table
     * PaymentTypeOption_Table.
     *
     * @return List<PaymentTypeOption>
     */
    public Observable<List<PaymentTypeOption>> getPaymentTypeOption() {
        return mDatabaseHelperLoan.getPaymentTypeOption();
    }

    /**
     * This method sending request DatabaseHelper and Deleting the LoanRepayment with loanId
     * from LoanRepayment_Table and again loading list of LoanRepayment from Database.
     *
     * @param loanId Loan Id of the Loan
     * @return List<LoanRepaymentRequest>
     */
    public Observable<List<LoanRepaymentRequest>> deleteAndUpdateLoanRepayments(int loanId) {
        return mDatabaseHelperLoan.deleteAndUpdateLoanRepayments(loanId);
    }


    /**
     * This Method updating LoanRepayment in to Database and return the same LoanRepayment
     *
     * @param loanRepaymentRequest Updating LoanRepaymentRequest in to Database
     * @return LoanRepaymentRequest
     */
    public Observable<LoanRepaymentRequest> updateLoanRepaymentTransaction(
            LoanRepaymentRequest loanRepaymentRequest) {
        return mDatabaseHelperLoan.updateLoanRepaymentTransaction(loanRepaymentRequest);
    }
}
