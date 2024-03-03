package com.mifos.mifosxdroid.dialogfragments.synccenterdialog

import com.mifos.objects.accounts.CenterAccounts
import com.mifos.objects.accounts.ClientAccounts
import com.mifos.objects.accounts.GroupAccounts
import com.mifos.objects.accounts.loan.LoanWithAssociations
import com.mifos.objects.accounts.savings.SavingsAccountWithAssociations
import com.mifos.objects.client.Client
import com.mifos.objects.group.Center
import com.mifos.objects.group.CenterWithAssociations
import com.mifos.objects.group.Group
import com.mifos.objects.group.GroupWithAssociations
import com.mifos.objects.templates.loans.LoanRepaymentTemplate
import com.mifos.objects.templates.savings.SavingsAccountTransactionTemplate
import rx.Observable

/**
 * Created by Aditya Gupta on 16/08/23.
 */
interface SyncCentersDialogRepository {

    fun syncCenterAccounts(centerId: Int): Observable<CenterAccounts>

    fun syncLoanById(loanId: Int): Observable<LoanWithAssociations>

    fun syncLoanRepaymentTemplate(loanId: Int): Observable<LoanRepaymentTemplate>

    fun getCenterWithAssociations(centerId: Int): Observable<CenterWithAssociations>

    fun getGroupWithAssociations(groupId: Int): Observable<GroupWithAssociations>

    fun syncGroupAccounts(groupId: Int): Observable<GroupAccounts>

    fun syncClientAccounts(clientId: Int): Observable<ClientAccounts>

    fun syncGroupInDatabase(group: Group): Observable<Group>

    fun syncClientInDatabase(client: Client): Observable<Client>

    fun syncCenterInDatabase(center: Center): Observable<Center>

    fun syncSavingsAccount(
        type: String?, savingsAccountId: Int, association: String?
    ): Observable<SavingsAccountWithAssociations>

    fun syncSavingsAccountTransactionTemplate(
        savingsAccountType: String?, savingsAccountId: Int, transactionType: String?
    ): Observable<SavingsAccountTransactionTemplate>

}