package com.mifos.api.datamanager;

import com.mifos.api.BaseApiManager;
import com.mifos.api.local.databasehelper.DatabaseHelperLoan;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.objects.accounts.loan.Loans;
import com.mifos.objects.organisation.LoanProducts;
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
                 * Return Clients from DatabaseHelperClient only one time.
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

}
