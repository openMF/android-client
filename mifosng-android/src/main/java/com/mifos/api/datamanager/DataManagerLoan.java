package com.mifos.api.datamanager;

import com.mifos.api.BaseApiManager;
import com.mifos.api.local.databasehelper.DatabaseHelperLoan;
import com.mifos.objects.accounts.loan.LoanWithAssociations;
import com.mifos.objects.accounts.loan.Loans;
import com.mifos.objects.organisation.LoanProducts;
import com.mifos.objects.templates.loans.LoanTemplate;
import com.mifos.services.data.LoansPayload;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;

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

    public Observable<LoanWithAssociations> getLoanById(int loanAccountNumber) {
        return mBaseApiManager.getLoanApi().getLoanByIdWithAllAssociations(loanAccountNumber);
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
