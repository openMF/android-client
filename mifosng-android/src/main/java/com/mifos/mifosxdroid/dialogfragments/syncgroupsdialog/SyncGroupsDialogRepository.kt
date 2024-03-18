package com.mifos.mifosxdroid.dialogfragments.syncgroupsdialog

import com.mifos.core.objects.accounts.ClientAccounts
import com.mifos.core.objects.accounts.GroupAccounts
import com.mifos.core.objects.accounts.loan.LoanWithAssociations
import com.mifos.core.objects.accounts.savings.SavingsAccountWithAssociations
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.group.Group
import com.mifos.core.objects.group.GroupWithAssociations
import com.mifos.core.objects.templates.loans.LoanRepaymentTemplate
import com.mifos.core.objects.templates.savings.SavingsAccountTransactionTemplate
import rx.Observable

/**
 * Created by Aditya Gupta on 16/08/23.
 */
interface SyncGroupsDialogRepository {

    fun syncGroupAccounts(groupId: Int): Observable<GroupAccounts>


    fun syncLoanById(loanId: Int): Observable<LoanWithAssociations>

    fun syncLoanRepaymentTemplate(loanId: Int): Observable<LoanRepaymentTemplate>

    fun syncSavingsAccount(
        type: String?, savingsAccountId: Int, association: String?
    ): Observable<SavingsAccountWithAssociations>

    fun syncSavingsAccountTransactionTemplate(
        savingsAccountType: String?, savingsAccountId: Int, transactionType: String?
    ): Observable<SavingsAccountTransactionTemplate>

    fun getGroupWithAssociations(groupId: Int): Observable<GroupWithAssociations>

    fun syncClientInDatabase(client: Client): Observable<Client>

    fun syncClientAccounts(clientId: Int): Observable<ClientAccounts>

    fun syncGroupInDatabase(group: Group): Observable<Group>

}