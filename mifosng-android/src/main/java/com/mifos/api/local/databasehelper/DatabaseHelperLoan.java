package com.mifos.api.local.databasehelper;

import com.mifos.objects.accounts.loan.ActualDisbursementDate;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.objects.accounts.loan.Timeline;

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

}
