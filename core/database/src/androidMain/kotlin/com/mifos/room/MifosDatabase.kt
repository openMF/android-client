/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mifos.room.dao.CenterDao
import com.mifos.room.dao.ChargeDao
import com.mifos.room.dao.ClientDao
import com.mifos.room.dao.ColumnValueDao
import com.mifos.room.dao.GroupsDao
import com.mifos.room.dao.LoanDao
import com.mifos.room.dao.OfficeDao
import com.mifos.room.dao.SavingsDao
import com.mifos.room.dao.StaffDao
import com.mifos.room.dao.SurveyDao
import com.mifos.room.entities.PaymentTypeOptionEntity
import com.mifos.room.entities.accounts.loans.ActualDisbursementDateEntity
import com.mifos.room.entities.accounts.loans.LoanAccountEntity
import com.mifos.room.entities.accounts.loans.LoanRepaymentRequestEntity
import com.mifos.room.entities.accounts.loans.LoanRepaymentResponseEntity
import com.mifos.room.entities.accounts.loans.LoanStatusEntity
import com.mifos.room.entities.accounts.loans.LoanTimelineEntity
import com.mifos.room.entities.accounts.loans.LoanTypeEntity
import com.mifos.room.entities.accounts.loans.LoanWithAssociationsEntity
import com.mifos.room.entities.accounts.loans.LoansAccountSummaryEntity
import com.mifos.room.entities.accounts.savings.SavingAccountCurrencyEntity
import com.mifos.room.entities.accounts.savings.SavingAccountDepositTypeEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountStatusEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountSummaryEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountTransactionEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountTransactionRequestEntity
import com.mifos.room.entities.accounts.savings.SavingsAccountWithAssociationsEntity
import com.mifos.room.entities.accounts.savings.SavingsTransactionDateEntity
import com.mifos.room.entities.accounts.savings.SavingsTransactionTypeEntity
import com.mifos.room.entities.center.CenterPayloadEntity
import com.mifos.room.entities.client.ChargeCalculationTypeEntity
import com.mifos.room.entities.client.ChargeTimeTypeEntity
import com.mifos.room.entities.client.ChargesEntity
import com.mifos.room.entities.client.ClientChargeCurrencyEntity
import com.mifos.room.entities.client.ClientDateEntity
import com.mifos.room.entities.client.ClientEntity
import com.mifos.room.entities.client.ClientPayloadEntity
import com.mifos.room.entities.client.ClientStatusEntity
import com.mifos.room.entities.group.CenterDateEntity
import com.mifos.room.entities.group.CenterEntity
import com.mifos.room.entities.group.GroupDateEntity
import com.mifos.room.entities.group.GroupEntity
import com.mifos.room.entities.group.GroupPayloadEntity
import com.mifos.room.entities.noncore.ColumnHeader
import com.mifos.room.entities.noncore.ColumnValue
import com.mifos.room.entities.noncore.DataTableEntity
import com.mifos.room.entities.noncore.DataTablePayload
import com.mifos.room.entities.noncore.NoteEntity
import com.mifos.room.entities.organisation.OfficeEntity
import com.mifos.room.entities.organisation.OfficeOpeningDateEntity
import com.mifos.room.entities.organisation.StaffEntity
import com.mifos.room.entities.survey.ComponentDatasEntity
import com.mifos.room.entities.survey.QuestionDatasEntity
import com.mifos.room.entities.survey.ResponseDatasEntity
import com.mifos.room.entities.survey.SurveyEntity
import com.mifos.room.entities.templates.clients.ClientsTemplateEntity
import com.mifos.room.entities.templates.clients.InterestTypeEntity
import com.mifos.room.entities.templates.clients.OfficeOptionsEntity
import com.mifos.room.entities.templates.clients.OptionsEntity
import com.mifos.room.entities.templates.clients.SavingProductOptionsEntity
import com.mifos.room.entities.templates.clients.StaffOptionsEntity
import com.mifos.room.entities.templates.loans.LoanRepaymentTemplateEntity
import com.mifos.room.entities.templates.savings.SavingsAccountTransactionTemplateEntity
import com.mifos.room.typeconverters.CustomTypeConverters

@Database(
    entities = [
        // loans package
        ActualDisbursementDateEntity::class,
        LoanAccountEntity::class,
        LoanRepaymentRequestEntity::class,
        LoanRepaymentResponseEntity::class,
        LoanStatusEntity::class,
        LoanTypeEntity::class,
        LoanWithAssociationsEntity::class,
        LoansAccountSummaryEntity::class,
        LoanTimelineEntity::class,
        // savings package
        SavingAccountDepositTypeEntity::class,
        SavingsAccountEntity::class,
        SavingAccountCurrencyEntity::class,
        SavingsAccountTransactionEntity::class,
        SavingsAccountWithAssociationsEntity::class,
        SavingsAccountTransactionRequestEntity::class,
        SavingsAccountTransactionTemplateEntity::class,
        SavingsTransactionDateEntity::class,
        SavingsAccountStatusEntity::class,
        SavingsAccountSummaryEntity::class,
        SavingsTransactionTypeEntity::class,
        // center package
        CenterPayloadEntity::class,
        // client package
        ChargeCalculationTypeEntity::class,
        ChargesEntity::class,
        ChargeTimeTypeEntity::class,
        ClientEntity::class,
        ClientPayloadEntity::class,
        ClientChargeCurrencyEntity::class,
        ClientDateEntity::class,
        ClientStatusEntity::class,
        // group package
        CenterEntity::class,
        CenterDateEntity::class,
        GroupEntity::class,
        GroupDateEntity::class,
        GroupPayloadEntity::class,
        // non-core package
        ColumnHeader::class,
        ColumnValue::class,
        DataTableEntity::class,
        DataTablePayload::class,
        NoteEntity::class,
        // organisation package
        OfficeEntity::class,
        OfficeOpeningDateEntity::class,
        StaffEntity::class,
        // servey package
        ComponentDatasEntity::class,
        QuestionDatasEntity::class,
        ResponseDatasEntity::class,
        SurveyEntity::class,
        // templates package
        ClientsTemplateEntity::class,
        InterestTypeEntity::class,
        OfficeOptionsEntity::class,
        OptionsEntity::class,
        SavingProductOptionsEntity::class,
        StaffOptionsEntity::class,
        LoanRepaymentTemplateEntity::class,
        // zip models package
        PaymentTypeOptionEntity::class,
    ],
    version = MifosDatabase.VERSION,
    exportSchema = false,
    autoMigrations = [],
)
@TypeConverters(
    CustomTypeConverters::class,
)
actual abstract class MifosDatabase : RoomDatabase() {
    actual abstract val centerDao: CenterDao
    actual abstract val chargeDao: ChargeDao
    actual abstract val clientDao: ClientDao
    actual abstract val columnValueDao: ColumnValueDao
    actual abstract val groupsDao: GroupsDao
    actual abstract val loanDao: LoanDao
    actual abstract val officeDao: OfficeDao
    actual abstract val savingsDao: SavingsDao
    actual abstract val staffDao: StaffDao
    actual abstract val surveyDao: SurveyDao

    companion object {
        const val VERSION = 1
    }
}
