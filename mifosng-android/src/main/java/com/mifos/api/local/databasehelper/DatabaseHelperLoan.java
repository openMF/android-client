package com.mifos.api.local.databasehelper;

import com.mifos.objects.accounts.loan.ActualDisbursementDate;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.objects.accounts.loan.LoanWithAssociations_Table;
import com.mifos.objects.accounts.loan.Timeline;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.Arrays;

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
}
