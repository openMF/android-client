package com.mifos.api.local.databasehelper;

import com.mifos.objects.accounts.loan.ActualDisbursementDate;
import com.mifos.objects.accounts.loan.LoanRepaymentRequest;
import com.mifos.objects.accounts.loan.LoanRepaymentRequest_Table;
import com.mifos.objects.accounts.loan.LoanRepaymentResponse;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.objects.accounts.loan.LoanWithAssociations_Table;
import com.mifos.objects.accounts.loan.Timeline;
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
     *
     * This Method Saving the Loan Transaction Offline in Database Table
     *
     * @param loanId Loan Id
     * @param loanRepaymentRequest Request Body of Loan Transaction
     * @return LoanRepaymentResponse
     */
    public Observable<LoanRepaymentResponse> saveLoanRepaymentTransaction(
            final int  loanId, final LoanRepaymentRequest loanRepaymentRequest) {

        return Observable.defer(new Func0<Observable<LoanRepaymentResponse>>() {
            @Override
            public Observable<LoanRepaymentResponse> call() {

                //Setting Loan Id and Time Stamp
                loanRepaymentRequest.setLoanId(loanId);
                loanRepaymentRequest.setTimeStamp((System.currentTimeMillis()/1000));

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

}
