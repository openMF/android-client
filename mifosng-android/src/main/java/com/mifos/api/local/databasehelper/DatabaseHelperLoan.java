package com.mifos.api.local.databasehelper;

import com.mifos.objects.PaymentTypeOption;
import com.mifos.objects.accounts.loan.ActualDisbursementDate;
import com.mifos.objects.accounts.loan.LoanRepaymentRequest;
import com.mifos.objects.accounts.loan.LoanRepaymentRequest_Table;
import com.mifos.objects.accounts.loan.LoanRepaymentResponse;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.objects.accounts.loan.LoanWithAssociations_Table;
import com.mifos.objects.accounts.loan.Timeline;
import com.mifos.objects.templates.loans.LoanRepaymentTemplate;
import com.mifos.objects.templates.loans.LoanRepaymentTemplate_Table;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Func0;

/**
 * Created by Rajan Maurya on 15/07/16.
 */
@Singleton
public class DatabaseHelperLoan {


    @Inject
    public DatabaseHelperLoan() {

    }


    /**
     * This Method Saving the Loan In Database table according to Loan Id
     *
     * @param loanWithAssociations
     * @return LoanWithAssociation
     */
    public Observable<LoanWithAssociations> saveLoanById(final LoanWithAssociations
                                                                 loanWithAssociations) {
        return Observable.defer(new Func0<Observable<LoanWithAssociations>>() {
            @Override
            public Observable<LoanWithAssociations> call() {

                //Setting Loan Id in Summary Table
                loanWithAssociations.getSummary().setLoanId(loanWithAssociations.getId());

                // Setting Timeline
                Timeline timeline = loanWithAssociations.getTimeline();
                timeline.setLoanId(loanWithAssociations.getId());

                //Setting ActualDisbursement in Table
                ActualDisbursementDate actualDisbursementDate =
                        new ActualDisbursementDate(loanWithAssociations.getId(),
                                loanWithAssociations.getTimeline().getActualDisbursementDate()
                                        .get(0),
                                loanWithAssociations.getTimeline().getActualDisbursementDate()
                                        .get(1),
                                loanWithAssociations.getTimeline().getActualDisbursementDate()
                                        .get(2));
                timeline.setActualDisburseDate(actualDisbursementDate);

                loanWithAssociations.setTimeline(timeline);

                // save LoanWithAssociation
                loanWithAssociations.save();

                return Observable.just(loanWithAssociations);
            }
        });
    }


    /**
     * Retrieving LoanWithAssociation according to Loan Id from Database Table
     *
     * @param loanId
     * @return LoanWithAssociation
     */
    public Observable<LoanWithAssociations> getLoanById(final int loanId) {
        return Observable.defer(new Func0<Observable<LoanWithAssociations>>() {
            @Override
            public Observable<LoanWithAssociations> call() {

                LoanWithAssociations loanWithAssociations = SQLite.select()
                        .from(LoanWithAssociations.class)
                        .where(LoanWithAssociations_Table.id.eq(loanId))
                        .querySingle();

                // Setting the actualDisbursementDate
                if (loanWithAssociations != null) {
                    loanWithAssociations.getTimeline()
                            .setActualDisbursementDate(Arrays.asList(
                                    loanWithAssociations.getTimeline().getActualDisburseDate()
                                            .getYear(), loanWithAssociations.getTimeline()
                                            .getActualDisburseDate().getMonth(),
                                    loanWithAssociations
                                            .getTimeline().getActualDisburseDate().getDate()));

                }

                return Observable.just(loanWithAssociations);
            }
        });
    }


    /**
     * This Method Saving the Loan Transaction Offline in Database Table
     *
     * @param loanId               Loan Id
     * @param loanRepaymentRequest Request Body of Loan Transaction
     * @return LoanRepaymentResponse
     */
    public Observable<LoanRepaymentResponse> saveLoanRepaymentTransaction(
            final int loanId, final LoanRepaymentRequest loanRepaymentRequest) {

        return Observable.defer(new Func0<Observable<LoanRepaymentResponse>>() {
            @Override
            public Observable<LoanRepaymentResponse> call() {

                //Setting Loan Id and Time Stamp
                loanRepaymentRequest.setLoanId(loanId);
                loanRepaymentRequest.setTimeStamp((System.currentTimeMillis() / 1000));

                //Saving Transaction In Database Table
                loanRepaymentRequest.save();

                return Observable.just(new LoanRepaymentResponse());
            }
        });
    }


    /**
     * Read All LoanRepaymentRequest from Database ascending Order of TimeStamp
     *
     * @return List<LoanRepaymentRequest>
     */
    public Observable<List<LoanRepaymentRequest>> readAllLoanRepaymentTransaction() {
        return Observable.defer(new Func0<Observable<List<LoanRepaymentRequest>>>() {
            @Override
            public Observable<List<LoanRepaymentRequest>> call() {

                List<LoanRepaymentRequest> loanRepaymentRequests = SQLite.select()
                        .from(LoanRepaymentRequest.class)
                        .orderBy(LoanRepaymentRequest_Table.timeStamp, true)
                        .queryList();

                return Observable.just(loanRepaymentRequests);
            }
        });
    }


    /**
     * This Method send a query to Sqlite Database and get the LoanRepaymentRequest Where
     * Loan Id is Loan Id,
     *
     * This method used to check that LoanRepayment in offline mode,
     * Is already done with this loanId or not, If Yes then new Transaction can be made if
     * old one will be sync to server.
     *
     * @param loanId Loan Id
     * @return LoanRepaymentRequest by Loan Id
     */
    public Observable<LoanRepaymentRequest> getDatabaseLoanRepaymentByLoanId(final int loanId) {
        return Observable.defer(new Func0<Observable<LoanRepaymentRequest>>() {
            @Override
            public Observable<LoanRepaymentRequest> call() {

                LoanRepaymentRequest loanRepaymentRequest = SQLite.select()
                        .from(LoanRepaymentRequest.class)
                        .where(LoanRepaymentRequest_Table.loanId.eq(loanId))
                        .querySingle();

                return Observable.just(loanRepaymentRequest);
            }
        });
    }

    /**
     * This method saves the LoanRepaymentTemplate in Database for making Transaction In offline
     * and As the Template is saved in the Database, its return the same LoanRepaymentTemplate.
     *
     * @param loanId                Loan Id of the LoanTemplate
     * @param loanRepaymentTemplate LoanRepaymentTemplate for saving in Database
     * @return LoanRepaymentTemplate
     */
    public Observable<LoanRepaymentTemplate> saveLoanRepaymentTemplate(
            final int loanId, final LoanRepaymentTemplate loanRepaymentTemplate) {

        return Observable.defer(new Func0<Observable<LoanRepaymentTemplate>>() {
            @Override
            public Observable<LoanRepaymentTemplate> call() {

                loanRepaymentTemplate.setLoanId(loanId);

                for (PaymentTypeOption paymentTypeOption : loanRepaymentTemplate
                        .getPaymentTypeOptions()) {
                    paymentTypeOption.save();
                }

                loanRepaymentTemplate.save();

                return Observable.just(loanRepaymentTemplate);
            }
        });
    }


    /**
     * This Method retrieve the LoanRepaymentTemplate from Database LoanRepaymentTemplate_Table
     * according to Loan Id and retrieve the PaymentTypeOptions according to templateType
     * LoanRepaymentTemplate
     *
     * @param loanId Loan Id of the LoanRepaymentTemplate.
     * @return LoanRepaymentTemplate from Database Query.
     */
    public Observable<LoanRepaymentTemplate> getLoanRepayTemplate(final int loanId) {
        return Observable.defer(new Func0<Observable<LoanRepaymentTemplate>>() {
            @Override
            public Observable<LoanRepaymentTemplate> call() {

                LoanRepaymentTemplate loanRepaymentTemplate = SQLite.select()
                        .from(LoanRepaymentTemplate.class)
                        .where(LoanRepaymentTemplate_Table.loanId.eq(loanId))
                        .querySingle();

                List<PaymentTypeOption> paymentTypeOptions = SQLite.select()
                        .from(PaymentTypeOption.class)
                        .queryList();

                if (loanRepaymentTemplate != null) {
                    loanRepaymentTemplate.setPaymentTypeOptions(paymentTypeOptions);
                }

                return Observable.just(loanRepaymentTemplate);
            }
        });
    }


    /**
     * This Method request a query to Database in PaymentTypeOption_Table with argument paymentType
     * and return the list of PaymentTypeOption
     *
     * @return List<PaymentTypeOption>
     */
    public Observable<List<PaymentTypeOption>> getPaymentTypeOption() {
        return Observable.defer(new Func0<Observable<List<PaymentTypeOption>>>() {
            @Override
            public Observable<List<PaymentTypeOption>> call() {

                List<PaymentTypeOption> paymentTypeOptions = SQLite.select()
                        .from(PaymentTypeOption.class)
                        .queryList();

                return Observable.just(paymentTypeOptions);
            }
        });
    }

    /**
     * This Method Deleting the LoanRepayment with the loanId and loading the
     * List<LoanRepaymentRequest> from Database and return the SyncLoanRepaymentTransactionPresenter
     * that synced LoanRepayment is deleted from Database and updated Database Table entries is this
     *
     * @param loanId loan Id of the LoanRepayment
     * @return List<LoanRepaymentRequest>
     */
    public Observable<List<LoanRepaymentRequest>> deleteAndUpdateLoanRepayments(final int loanId) {
        return Observable.defer(new Func0<Observable<List<LoanRepaymentRequest>>>() {
            @Override
            public Observable<List<LoanRepaymentRequest>> call() {

                Delete.table(LoanRepaymentRequest.class,
                        LoanRepaymentRequest_Table.loanId.eq(loanId));

                List<LoanRepaymentRequest> loanRepaymentRequests = SQLite.select()
                        .from(LoanRepaymentRequest.class)
                        .orderBy(LoanRepaymentRequest_Table.timeStamp, true)
                        .queryList();

                return Observable.just(loanRepaymentRequests);
            }
        });
    }

    /**
     * This Method updating the LoanRepayment to Database Table. this method will be called
     * whenever error will come during sync the LoanRepayment. This method saving the Error
     * message to the Table entry.
     *
     * @param loanRepaymentRequest LoanRepayment for update
     * @return LoanRepaymentRequest
     */
    public Observable<LoanRepaymentRequest> updateLoanRepaymentTransaction(
            final LoanRepaymentRequest loanRepaymentRequest) {
        return Observable.defer(new Func0<Observable<LoanRepaymentRequest>>() {
            @Override
            public Observable<LoanRepaymentRequest> call() {
                loanRepaymentRequest.update();
                return Observable.just(loanRepaymentRequest);
            }
        });
    }

}
