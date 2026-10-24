/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package kpt.core.store.mapping

import kpt.core.database.center.entity.CenterAccounts as CenterAccountsRow
import kpt.core.database.center.entity.CenterDateEntity as CenterDateRow
import kpt.core.database.center.entity.CenterEntity as CenterRow
import kpt.core.database.center.entity.CenterPayloadEntity as CenterPayloadRow
import kpt.core.database.center.entity.CenterWithAssociations as CenterWithAssociationsRow
import kpt.core.database.charge.entity.ChargeCalculationTypeEntity as ChargeCalculationTypeRow
import kpt.core.database.charge.entity.ChargeTimeTypeEntity as ChargeTimeTypeRow
import kpt.core.database.charge.entity.ChargesEntity as ChargesRow
import kpt.core.database.charge.entity.ClientChargeCurrencyEntity as ClientChargeCurrencyRow
import kpt.core.database.client.entity.AddressConfiguration as AddressConfigurationRow
import kpt.core.database.client.entity.AddressTemplate as AddressTemplateRow
import kpt.core.database.client.entity.ClientAccounts as ClientAccountsRow
import kpt.core.database.client.entity.ClientClassificationEntity as ClientClassificationRow
import kpt.core.database.client.entity.ClientDateEntity as ClientDateRow
import kpt.core.database.client.entity.ClientEntity as ClientRow
import kpt.core.database.client.entity.ClientGenderEntity as ClientGenderRow
import kpt.core.database.client.entity.ClientPayloadEntity as ClientPayloadRow
import kpt.core.database.client.entity.ClientStatusEntity as ClientStatusRow
import kpt.core.database.client.entity.ClientTypeEntity as ClientTypeRow
import kpt.core.database.client.entity.ClientsTemplateEntity as ClientsTemplateRow
import kpt.core.database.client.entity.InterestTypeEntity as InterestTypeOptionRow
import kpt.core.database.client.entity.OfficeOptionsEntity as OfficeOptionsRow
import kpt.core.database.client.entity.OptionsEntity as OptionsRow
import kpt.core.database.client.entity.SavingProductOptionsEntity as SavingProductOptionsRow
import kpt.core.database.client.entity.StaffOptionsEntity as StaffOptionsRow
import kpt.core.database.collectionsheet.entity.CenterDetail as CenterDetailRow
import kpt.core.database.collectionsheet.entity.ClientCollectionSheet as ClientCollectionSheetRow
import kpt.core.database.collectionsheet.entity.CollectionSheetResponse as CollectionSheetResponseRow
import kpt.core.database.collectionsheet.entity.GroupCollectionSheet as GroupCollectionSheetRow
import kpt.core.database.collectionsheet.entity.IndividualCollectionSheet as IndividualCollectionSheetRow
import kpt.core.database.collectionsheet.entity.MeetingFallCalendar as MeetingFallCalendarRow
import kpt.core.database.collectionsheet.entity.SavingsCollectionSheet as SavingsCollectionSheetRow
import kpt.core.database.datatable.entity.ColumnHeader as ColumnHeaderRow
import kpt.core.database.datatable.entity.ColumnValue as ColumnValueRow
import kpt.core.database.datatable.entity.DataTableEntity as DataTableRow
import kpt.core.database.datatable.entity.DataTablePayload as DataTablePayloadRow
import kpt.core.database.group.entity.GroupAccounts as GroupAccountsRow
import kpt.core.database.group.entity.GroupDateEntity as GroupDateRow
import kpt.core.database.group.entity.GroupEntity as GroupRow
import kpt.core.database.group.entity.GroupPayloadEntity as GroupPayloadRow
import kpt.core.database.group.entity.GroupWithAssociations as GroupWithAssociationsRow
import kpt.core.database.loan.entity.ActualDisbursementDateEntity as ActualDisbursementDateRow
import kpt.core.database.loan.entity.Loan as LoanRow
import kpt.core.database.loan.entity.LoanAccountEntity as LoanAccountRow
import kpt.core.database.loan.entity.LoanAccountSummaryEntity as LoanAccountSummaryRow
import kpt.core.database.loan.entity.LoanRepaymentRequestEntity as LoanRepaymentRequestRow
import kpt.core.database.loan.entity.LoanRepaymentResponseEntity as LoanRepaymentResponseRow
import kpt.core.database.loan.entity.LoanRepaymentTemplateEntity as LoanRepaymentTemplateRow
import kpt.core.database.loan.entity.LoanStatusEntity as LoanStatusRow
import kpt.core.database.loan.entity.LoanTemplate as LoanTemplateRow
import kpt.core.database.loan.entity.LoanTimelineEntity as LoanTimelineRow
import kpt.core.database.loan.entity.LoanTransactionTemplate as LoanTransactionTemplateRow
import kpt.core.database.loan.entity.LoanTypeEntity as LoanAccountTypeRow
import kpt.core.database.office.entity.OfficeEntity as OfficeRow
import kpt.core.database.office.entity.OfficeOpeningDateEntity as OfficeOpeningDateRow
import kpt.core.database.payment.entity.PaymentTypeOptionEntity as PaymentTypeOptionRow
import kpt.core.database.savings.entity.Charge as ChargeRow
import kpt.core.database.savings.entity.SavingAccountCurrencyEntity as SavingAccountCurrencyRow
import kpt.core.database.savings.entity.SavingAccountDepositTypeEntity as SavingAccountDepositTypeRow
import kpt.core.database.savings.entity.SavingProductsTemplate as SavingProductsTemplateRow
import kpt.core.database.savings.entity.SavingsAccountEntity as SavingsAccountRow
import kpt.core.database.savings.entity.SavingsAccountStatusEntity as SavingsAccountStatusRow
import kpt.core.database.savings.entity.SavingsAccountSummaryEntity as SavingsAccountSummaryRow
import kpt.core.database.savings.entity.SavingsAccountTransactionEntity as SavingsAccountTransactionRow
import kpt.core.database.savings.entity.SavingsAccountTransactionRequestEntity as SavingsAccountTransactionRequestRow
import kpt.core.database.savings.entity.SavingsAccountTransactionTemplateEntity as SavingsAccountTransactionTemplateRow
import kpt.core.database.savings.entity.SavingsAccountWithAssociationsEntity as SavingsAccountWithAssociationsRow
import kpt.core.database.savings.entity.SavingsTransactionDateEntity as SavingsTransactionDateRow
import kpt.core.database.savings.entity.SavingsTransactionTypeEntity as SavingsTransactionTypeRow
import kpt.core.database.staff.entity.StaffEntity as StaffRow
import kpt.core.database.survey.entity.ComponentDatasEntity as ComponentDatasRow
import kpt.core.database.survey.entity.QuestionDatasEntity as QuestionDatasRow
import kpt.core.database.survey.entity.ResponseDatasEntity as ResponseDatasRow
import kpt.core.database.survey.entity.SurveyEntity as SurveyRow
import kpt.core.model.center.Center
import kpt.core.model.center.CenterAccounts
import kpt.core.model.center.CenterDate
import kpt.core.model.center.CenterPayload
import kpt.core.model.center.CenterWithAssociations
import kpt.core.model.charge.ChargeCalculationType
import kpt.core.model.charge.ChargeTimeType
import kpt.core.model.charge.Charges
import kpt.core.model.charge.ClientChargeCurrency
import kpt.core.model.client.AddressConfiguration
import kpt.core.model.client.AddressTemplate
import kpt.core.model.client.Client
import kpt.core.model.client.ClientAccounts
import kpt.core.model.client.ClientClassification
import kpt.core.model.client.ClientDate
import kpt.core.model.client.ClientGender
import kpt.core.model.client.ClientPayload
import kpt.core.model.client.ClientStatus
import kpt.core.model.client.ClientType
import kpt.core.model.client.ClientsTemplate
import kpt.core.model.client.InterestTypeOption
import kpt.core.model.client.OfficeOptions
import kpt.core.model.client.Options
import kpt.core.model.client.SavingProductOptions
import kpt.core.model.client.StaffOptions
import kpt.core.model.collectionsheet.CenterDetail
import kpt.core.model.collectionsheet.ClientCollectionSheet
import kpt.core.model.collectionsheet.CollectionSheetResponse
import kpt.core.model.collectionsheet.GroupCollectionSheet
import kpt.core.model.collectionsheet.IndividualCollectionSheet
import kpt.core.model.collectionsheet.MeetingFallCalendar
import kpt.core.model.collectionsheet.SavingsCollectionSheet
import kpt.core.model.datatable.ColumnHeader
import kpt.core.model.datatable.ColumnValue
import kpt.core.model.datatable.DataTable
import kpt.core.model.datatable.DataTablePayload
import kpt.core.model.group.Group
import kpt.core.model.group.GroupAccounts
import kpt.core.model.group.GroupDate
import kpt.core.model.group.GroupPayload
import kpt.core.model.group.GroupWithAssociations
import kpt.core.model.loan.ActualDisbursementDate
import kpt.core.model.loan.Loan
import kpt.core.model.loan.LoanAccount
import kpt.core.model.loan.LoanAccountSummary
import kpt.core.model.loan.LoanAccountType
import kpt.core.model.loan.LoanRepaymentRequest
import kpt.core.model.loan.LoanRepaymentResponse
import kpt.core.model.loan.LoanRepaymentTemplate
import kpt.core.model.loan.LoanStatus
import kpt.core.model.loan.LoanTemplate
import kpt.core.model.loan.LoanTimeline
import kpt.core.model.loan.LoanTransactionTemplate
import kpt.core.model.office.Office
import kpt.core.model.office.OfficeOpeningDate
import kpt.core.model.payment.PaymentTypeOption
import kpt.core.model.savings.Charge
import kpt.core.model.savings.SavingAccountCurrency
import kpt.core.model.savings.SavingAccountDepositType
import kpt.core.model.savings.SavingProductsTemplate
import kpt.core.model.savings.SavingsAccount
import kpt.core.model.savings.SavingsAccountStatus
import kpt.core.model.savings.SavingsAccountSummary
import kpt.core.model.savings.SavingsAccountTransaction
import kpt.core.model.savings.SavingsAccountTransactionRequest
import kpt.core.model.savings.SavingsAccountTransactionTemplate
import kpt.core.model.savings.SavingsAccountWithAssociations
import kpt.core.model.savings.SavingsTransactionDate
import kpt.core.model.savings.SavingsTransactionType
import kpt.core.model.staff.Staff
import kpt.core.model.survey.ComponentDatas
import kpt.core.model.survey.QuestionDatas
import kpt.core.model.survey.ResponseDatas
import kpt.core.model.survey.Survey

/*
 * The core/database ↔ core/model boundary. GENERATED from the entity constructors (one pair per
 * type); field names are identical on both sides, so a field added to a row and not to its model
 * fails to compile here rather than being dropped silently.
 *
 * Entities never leave core/store: stores read rows and emit these models, and an offline store's
 * writer converts a model back to its row. Features see only core/model (Store5 gate
 * S5-FEATURE-DEP-DIRECTION).
 */

fun ActualDisbursementDateRow.toModel(): ActualDisbursementDate = ActualDisbursementDate(
    loanId = loanId,
    year = year,
    month = month,
    date = date,
)

fun ActualDisbursementDate.toEntity(): ActualDisbursementDateRow = ActualDisbursementDateRow(
    loanId = loanId,
    year = year,
    month = month,
    date = date,
)

fun AddressTemplateRow.toModel(): AddressTemplate = AddressTemplate(
    addressTypeIdOptions = addressTypeIdOptions.map { it.toModel() },
    countryIdOptions = countryIdOptions.map { it.toModel() },
    stateProvinceIdOptions = stateProvinceIdOptions.map { it.toModel() },
)

fun AddressTemplate.toEntity(): AddressTemplateRow = AddressTemplateRow(
    addressTypeIdOptions = addressTypeIdOptions.map { it.toEntity() },
    countryIdOptions = countryIdOptions.map { it.toEntity() },
    stateProvinceIdOptions = stateProvinceIdOptions.map { it.toEntity() },
)

fun CenterDateRow.toModel(): CenterDate = CenterDate(
    centerId = centerId,
    chargeId = chargeId,
    day = day,
    month = month,
    year = year,
)

fun CenterDate.toEntity(): CenterDateRow = CenterDateRow(
    centerId = centerId,
    chargeId = chargeId,
    day = day,
    month = month,
    year = year,
)

fun CenterRow.toModel(): Center = Center(
    id = id,
    sync = sync,
    accountNo = accountNo,
    name = name,
    officeId = officeId,
    officeName = officeName,
    staffId = staffId,
    staffName = staffName,
    hierarchy = hierarchy,
    status = status?.toModel(),
    active = active,
    centerDate = centerDate?.toModel(),
    activationDate = activationDate,
    timeline = timeline?.toModel(),
    externalId = externalId,
)

fun Center.toEntity(): CenterRow = CenterRow(
    id = id,
    sync = sync,
    accountNo = accountNo,
    name = name,
    officeId = officeId,
    officeName = officeName,
    staffId = staffId,
    staffName = staffName,
    hierarchy = hierarchy,
    status = status?.toEntity(),
    active = active,
    centerDate = centerDate?.toEntity(),
    activationDate = activationDate,
    timeline = timeline?.toEntity(),
    externalId = externalId,
)

fun CenterPayloadRow.toModel(): CenterPayload = CenterPayload(
    id = id,
    errorMessage = errorMessage,
    dateFormat = dateFormat,
    locale = locale,
    name = name,
    officeId = officeId,
    active = active,
    activationDate = activationDate,
)

fun CenterPayload.toEntity(): CenterPayloadRow = CenterPayloadRow(
    id = id,
    errorMessage = errorMessage,
    dateFormat = dateFormat,
    locale = locale,
    name = name,
    officeId = officeId,
    active = active,
    activationDate = activationDate,
)

fun ChargeRow.toModel(): Charge = Charge(
    id = id,
    chargeId = chargeId,
    accountId = accountId,
    name = name,
    chargeTimeType = chargeTimeType?.toModel(),
    chargeCalculationType = chargeCalculationType?.toModel(),
    percentage = percentage,
    amountPercentageAppliedTo = amountPercentageAppliedTo,
    currency = currency?.toModel(),
    amount = amount,
    amountPaid = amountPaid,
    amountWaived = amountWaived,
    amountWrittenOff = amountWrittenOff,
    amountOutstanding = amountOutstanding,
    amountOrPercentage = amountOrPercentage,
    penalty = penalty,
    additionalProperties = additionalProperties,
)

fun Charge.toEntity(): ChargeRow = ChargeRow(
    id = id,
    chargeId = chargeId,
    accountId = accountId,
    name = name,
    chargeTimeType = chargeTimeType?.toEntity(),
    chargeCalculationType = chargeCalculationType?.toEntity(),
    percentage = percentage,
    amountPercentageAppliedTo = amountPercentageAppliedTo,
    currency = currency?.toEntity(),
    amount = amount,
    amountPaid = amountPaid,
    amountWaived = amountWaived,
    amountWrittenOff = amountWrittenOff,
    amountOutstanding = amountOutstanding,
    amountOrPercentage = amountOrPercentage,
    penalty = penalty,
    additionalProperties = additionalProperties,
)

fun ChargeCalculationTypeRow.toModel(): ChargeCalculationType = ChargeCalculationType(
    id = id,
    code = code,
    value = value,
)

fun ChargeCalculationType.toEntity(): ChargeCalculationTypeRow = ChargeCalculationTypeRow(
    id = id,
    code = code,
    value = value,
)

fun ChargeTimeTypeRow.toModel(): ChargeTimeType = ChargeTimeType(
    id = id,
    code = code,
    value = value,
)

fun ChargeTimeType.toEntity(): ChargeTimeTypeRow = ChargeTimeTypeRow(
    id = id,
    code = code,
    value = value,
)

fun ChargesRow.toModel(): Charges = Charges(
    resourceType = resourceType,
    resourceId = resourceId,
    id = id,
    clientId = clientId,
    loanId = loanId,
    chargeId = chargeId,
    name = name,
    chargeTimeType = chargeTimeType?.toModel(),
    chargeDueDate = chargeDueDate?.toModel(),
    dueDate = dueDate,
    chargeCalculationType = chargeCalculationType?.toModel(),
    currency = currency?.toModel(),
    amount = amount,
    amountPaid = amountPaid,
    amountWaived = amountWaived,
    amountWrittenOff = amountWrittenOff,
    amountOutstanding = amountOutstanding,
    penalty = penalty,
    active = active,
    paid = paid,
    waived = waived,
)

fun Charges.toEntity(): ChargesRow = ChargesRow(
    resourceType = resourceType,
    resourceId = resourceId,
    id = id,
    clientId = clientId,
    loanId = loanId,
    chargeId = chargeId,
    name = name,
    chargeTimeType = chargeTimeType?.toEntity(),
    chargeDueDate = chargeDueDate?.toEntity(),
    dueDate = dueDate,
    chargeCalculationType = chargeCalculationType?.toEntity(),
    currency = currency?.toEntity(),
    amount = amount,
    amountPaid = amountPaid,
    amountWaived = amountWaived,
    amountWrittenOff = amountWrittenOff,
    amountOutstanding = amountOutstanding,
    penalty = penalty,
    active = active,
    paid = paid,
    waived = waived,
)

fun ClientAccountsRow.toModel(): ClientAccounts = ClientAccounts(
    loanAccounts = loanAccounts.map { it.toModel() },
    savingsAccounts = savingsAccounts.map { it.toModel() },
    shareAccounts = shareAccounts,
)

fun ClientAccounts.toEntity(): ClientAccountsRow = ClientAccountsRow(
    loanAccounts = loanAccounts.map { it.toEntity() },
    savingsAccounts = savingsAccounts.map { it.toEntity() },
    shareAccounts = shareAccounts,
)

fun ClientChargeCurrencyRow.toModel(): ClientChargeCurrency = ClientChargeCurrency(
    id = id,
    code = code,
    name = name,
    decimalPlaces = decimalPlaces,
    inMultiplesOf = inMultiplesOf,
    displaySymbol = displaySymbol,
    nameCode = nameCode,
    displayLabel = displayLabel,
)

fun ClientChargeCurrency.toEntity(): ClientChargeCurrencyRow = ClientChargeCurrencyRow(
    id = id,
    code = code,
    name = name,
    decimalPlaces = decimalPlaces,
    inMultiplesOf = inMultiplesOf,
    displaySymbol = displaySymbol,
    nameCode = nameCode,
    displayLabel = displayLabel,
)

fun ClientClassificationRow.toModel(): ClientClassification = ClientClassification(
    id = id,
    name = name,
)

fun ClientClassification.toEntity(): ClientClassificationRow = ClientClassificationRow(
    id = id,
    name = name,
)

fun ClientDateRow.toModel(): ClientDate = ClientDate(
    clientId = clientId,
    chargeId = chargeId,
    day = day,
    month = month,
    year = year,
)

fun ClientDate.toEntity(): ClientDateRow = ClientDateRow(
    clientId = clientId,
    chargeId = chargeId,
    day = day,
    month = month,
    year = year,
)

fun ClientRow.toModel(): Client = Client(
    id = id,
    groupId = groupId,
    groupName = groupName,
    accountNo = accountNo,
    clientId = clientId,
    status = status?.toModel(),
    sync = sync,
    active = active,
    clientDate = clientDate?.toModel(),
    activationDate = activationDate,
    dateOfBirth = dateOfBirth,
    groups = groups?.map { it.toModel() },
    mobileNo = mobileNo,
    firstname = firstname,
    middlename = middlename,
    lastname = lastname,
    displayName = displayName,
    officeId = officeId,
    officeName = officeName,
    staffId = staffId,
    staffName = staffName,
    timeline = timeline,
    fullname = fullname,
    savingsAccountId = savingsAccountId,
    imageId = imageId,
    imagePresent = imagePresent,
    externalId = externalId,
    emailAddress = emailAddress,
    legalForm = legalForm?.toModel(),
    gender = gender?.toModel(),
    clientType = clientType?.toModel(),
    clientClassification = clientClassification?.toModel(),
)

fun Client.toEntity(): ClientRow = ClientRow(
    id = id,
    groupId = groupId,
    groupName = groupName,
    accountNo = accountNo,
    clientId = clientId,
    status = status?.toEntity(),
    sync = sync,
    active = active,
    clientDate = clientDate?.toEntity(),
    activationDate = activationDate,
    dateOfBirth = dateOfBirth,
    groups = groups?.map { it.toEntity() },
    mobileNo = mobileNo,
    firstname = firstname,
    middlename = middlename,
    lastname = lastname,
    displayName = displayName,
    officeId = officeId,
    officeName = officeName,
    staffId = staffId,
    staffName = staffName,
    timeline = timeline,
    fullname = fullname,
    savingsAccountId = savingsAccountId,
    imageId = imageId,
    imagePresent = imagePresent,
    externalId = externalId,
    emailAddress = emailAddress,
    legalForm = legalForm?.toEntity(),
    gender = gender?.toEntity(),
    clientType = clientType?.toEntity(),
    clientClassification = clientClassification?.toEntity(),
)

fun ClientGenderRow.toModel(): ClientGender = ClientGender(
    id = id,
    name = name,
)

fun ClientGender.toEntity(): ClientGenderRow = ClientGenderRow(
    id = id,
    name = name,
)

fun ClientPayloadRow.toModel(): ClientPayload = ClientPayload(
    id = id,
    clientCreationTime = clientCreationTime,
    errorMessage = errorMessage,
    firstname = firstname,
    lastname = lastname,
    middlename = middlename,
    emailAddress = emailAddress,
    officeId = officeId,
    staffId = staffId,
    genderId = genderId,
    active = active,
    activationDate = activationDate,
    submittedOnDate = submittedOnDate,
    dateOfBirth = dateOfBirth,
    mobileNo = mobileNo,
    externalId = externalId,
    clientTypeId = clientTypeId,
    clientClassificationId = clientClassificationId,
    address = address,
    dateFormat = dateFormat,
    locale = locale,
    datatables = datatables?.map { it.toModel() },
    legalFormId = legalFormId,
)

fun ClientPayload.toEntity(): ClientPayloadRow = ClientPayloadRow(
    id = id,
    clientCreationTime = clientCreationTime,
    errorMessage = errorMessage,
    firstname = firstname,
    lastname = lastname,
    middlename = middlename,
    emailAddress = emailAddress,
    officeId = officeId,
    staffId = staffId,
    genderId = genderId,
    active = active,
    activationDate = activationDate,
    submittedOnDate = submittedOnDate,
    dateOfBirth = dateOfBirth,
    mobileNo = mobileNo,
    externalId = externalId,
    clientTypeId = clientTypeId,
    clientClassificationId = clientClassificationId,
    address = address,
    dateFormat = dateFormat,
    locale = locale,
    datatables = datatables?.map { it.toEntity() },
    legalFormId = legalFormId,
)

fun ClientStatusRow.toModel(): ClientStatus = ClientStatus(
    id = id,
    code = code,
    value = value,
)

fun ClientStatus.toEntity(): ClientStatusRow = ClientStatusRow(
    id = id,
    code = code,
    value = value,
)

fun ClientTypeRow.toModel(): ClientType = ClientType(
    id = id,
    name = name,
)

fun ClientType.toEntity(): ClientTypeRow = ClientTypeRow(
    id = id,
    name = name,
)

fun ClientsTemplateRow.toModel(): ClientsTemplate = ClientsTemplate(
    activationDate = activationDate,
    officeId = officeId,
    officeOptions = officeOptions?.map { it.toModel() },
    staffOptions = staffOptions?.map { it.toModel() },
    savingProductOptions = savingProductOptions?.map { it.toModel() },
    genderOptions = genderOptions?.map { it.toModel() },
    clientTypeOptions = clientTypeOptions?.map { it.toModel() },
    clientClassificationOptions = clientClassificationOptions?.map { it.toModel() },
    clientLegalFormOptions = clientLegalFormOptions?.map { it.toModel() },
    dataTables = dataTables?.map { it.toModel() },
)

fun ClientsTemplate.toEntity(): ClientsTemplateRow = ClientsTemplateRow(
    activationDate = activationDate,
    officeId = officeId,
    officeOptions = officeOptions?.map { it.toEntity() },
    staffOptions = staffOptions?.map { it.toEntity() },
    savingProductOptions = savingProductOptions?.map { it.toEntity() },
    genderOptions = genderOptions?.map { it.toEntity() },
    clientTypeOptions = clientTypeOptions?.map { it.toEntity() },
    clientClassificationOptions = clientClassificationOptions?.map { it.toEntity() },
    clientLegalFormOptions = clientLegalFormOptions?.map { it.toEntity() },
    dataTables = dataTables?.map { it.toEntity() },
)

fun ColumnHeaderRow.toModel(): ColumnHeader = ColumnHeader(
    id = id,
    columnCode = columnCode,
    columnDisplayType = columnDisplayType,
    columnLength = columnLength,
    dataTableColumnName = dataTableColumnName,
    columnType = columnType,
    columnNullable = columnNullable,
    columnPrimaryKey = columnPrimaryKey,
    registeredTableName = registeredTableName,
    columnValues = columnValues.map { it.toModel() },
)

fun ColumnHeader.toEntity(): ColumnHeaderRow = ColumnHeaderRow(
    id = id,
    columnCode = columnCode,
    columnDisplayType = columnDisplayType,
    columnLength = columnLength,
    dataTableColumnName = dataTableColumnName,
    columnType = columnType,
    columnNullable = columnNullable,
    columnPrimaryKey = columnPrimaryKey,
    registeredTableName = registeredTableName,
    columnValues = columnValues.map { it.toEntity() },
)

fun ColumnValueRow.toModel(): ColumnValue = ColumnValue(
    id = id,
    value = value,
    score = score,
    registeredTableName = registeredTableName,
)

fun ColumnValue.toEntity(): ColumnValueRow = ColumnValueRow(
    id = id,
    value = value,
    score = score,
    registeredTableName = registeredTableName,
)

fun DataTableRow.toModel(): DataTable = DataTable(
    id = id,
    applicationTableName = applicationTableName,
    columnHeaderData = columnHeaderData.map { it.toModel() },
    registeredTableName = registeredTableName,
)

fun DataTable.toEntity(): DataTableRow = DataTableRow(
    id = id,
    applicationTableName = applicationTableName,
    columnHeaderData = columnHeaderData.map { it.toEntity() },
    registeredTableName = registeredTableName,
)

fun DataTablePayloadRow.toModel(): DataTablePayload = DataTablePayload(
    id = id,
    clientCreationTime = clientCreationTime,
    dataTableString = dataTableString,
    registeredTableName = registeredTableName,
    data = data,
)

fun DataTablePayload.toEntity(): DataTablePayloadRow = DataTablePayloadRow(
    id = id,
    clientCreationTime = clientCreationTime,
    dataTableString = dataTableString,
    registeredTableName = registeredTableName,
    data = data,
)

fun GroupDateRow.toModel(): GroupDate = GroupDate(
    groupId = groupId,
    chargeId = chargeId,
    day = day,
    month = month,
    year = year,
)

fun GroupDate.toEntity(): GroupDateRow = GroupDateRow(
    groupId = groupId,
    chargeId = chargeId,
    day = day,
    month = month,
    year = year,
)

fun GroupRow.toModel(): Group = Group(
    id = id,
    accountNo = accountNo,
    sync = sync,
    name = name,
    status = status?.toModel(),
    active = active,
    groupDate = groupDate?.toModel(),
    activationDate = activationDate,
    officeId = officeId,
    officeName = officeName,
    centerId = centerId,
    centerName = centerName,
    staffId = staffId,
    staffName = staffName,
    hierarchy = hierarchy,
    groupLevel = groupLevel,
    timeline = timeline,
    externalId = externalId,
)

fun Group.toEntity(): GroupRow = GroupRow(
    id = id,
    accountNo = accountNo,
    sync = sync,
    name = name,
    status = status?.toEntity(),
    active = active,
    groupDate = groupDate?.toEntity(),
    activationDate = activationDate,
    officeId = officeId,
    officeName = officeName,
    centerId = centerId,
    centerName = centerName,
    staffId = staffId,
    staffName = staffName,
    hierarchy = hierarchy,
    groupLevel = groupLevel,
    timeline = timeline,
    externalId = externalId,
)

fun GroupPayloadRow.toModel(): GroupPayload = GroupPayload(
    id = id,
    errorMessage = errorMessage,
    officeId = officeId,
    active = active,
    activationDate = activationDate,
    submittedOnDate = submittedOnDate,
    externalId = externalId,
    name = name,
    locale = locale,
    dateFormat = dateFormat,
)

fun GroupPayload.toEntity(): GroupPayloadRow = GroupPayloadRow(
    id = id,
    errorMessage = errorMessage,
    officeId = officeId,
    active = active,
    activationDate = activationDate,
    submittedOnDate = submittedOnDate,
    externalId = externalId,
    name = name,
    locale = locale,
    dateFormat = dateFormat,
)

fun InterestTypeOptionRow.toModel(): InterestTypeOption = InterestTypeOption(
    id = id,
    code = code,
    value = value,
)

fun InterestTypeOption.toEntity(): InterestTypeOptionRow = InterestTypeOptionRow(
    id = id,
    code = code,
    value = value,
)

fun LoanAccountRow.toModel(): LoanAccount = LoanAccount(
    id = id,
    clientId = clientId,
    groupId = groupId,
    centerId = centerId,
    accountNo = accountNo,
    externalId = externalId,
    productId = productId,
    productName = productName,
    currency = currency,
    status = status?.toModel(),
    loanType = loanType?.toModel(),
    loanCycle = loanCycle,
    inArrears = inArrears,
    originalLoan = originalLoan,
    loanBalance = loanBalance,
    amountPaid = amountPaid,
)

fun LoanAccount.toEntity(): LoanAccountRow = LoanAccountRow(
    id = id,
    clientId = clientId,
    groupId = groupId,
    centerId = centerId,
    accountNo = accountNo,
    externalId = externalId,
    productId = productId,
    productName = productName,
    currency = currency,
    status = status?.toEntity(),
    loanType = loanType?.toEntity(),
    loanCycle = loanCycle,
    inArrears = inArrears,
    originalLoan = originalLoan,
    loanBalance = loanBalance,
    amountPaid = amountPaid,
)

fun LoanRepaymentRequestRow.toModel(): LoanRepaymentRequest = LoanRepaymentRequest(
    timeStamp = timeStamp,
    loanId = loanId,
    errorMessage = errorMessage,
    dateFormat = dateFormat,
    locale = locale,
    transactionDate = transactionDate,
    transactionAmount = transactionAmount,
    paymentTypeId = paymentTypeId,
    note = note,
    accountNumber = accountNumber,
    checkNumber = checkNumber,
    routingCode = routingCode,
    receiptNumber = receiptNumber,
    bankNumber = bankNumber,
)

fun LoanRepaymentRequest.toEntity(): LoanRepaymentRequestRow = LoanRepaymentRequestRow(
    timeStamp = timeStamp,
    loanId = loanId,
    errorMessage = errorMessage,
    dateFormat = dateFormat,
    locale = locale,
    transactionDate = transactionDate,
    transactionAmount = transactionAmount,
    paymentTypeId = paymentTypeId,
    note = note,
    accountNumber = accountNumber,
    checkNumber = checkNumber,
    routingCode = routingCode,
    receiptNumber = receiptNumber,
    bankNumber = bankNumber,
)

fun LoanRepaymentTemplateRow.toModel(): LoanRepaymentTemplate = LoanRepaymentTemplate(
    loanId = loanId,
    type = type,
    date = date,
    currency = currency?.toModel(),
    amount = amount,
    principalPortion = principalPortion,
    interestPortion = interestPortion,
    feeChargesPortion = feeChargesPortion,
    penaltyChargesPortion = penaltyChargesPortion,
    paymentTypeOptions = paymentTypeOptions?.map { it.toModel() },
)

fun LoanRepaymentTemplate.toEntity(): LoanRepaymentTemplateRow = LoanRepaymentTemplateRow(
    loanId = loanId,
    type = type,
    date = date,
    currency = currency?.toEntity(),
    amount = amount,
    principalPortion = principalPortion,
    interestPortion = interestPortion,
    feeChargesPortion = feeChargesPortion,
    penaltyChargesPortion = penaltyChargesPortion,
    paymentTypeOptions = paymentTypeOptions?.map { it.toEntity() },
)

fun LoanStatusRow.toModel(): LoanStatus = LoanStatus(
    id = id,
    code = code,
    value = value,
    pendingApproval = pendingApproval,
    waitingForDisbursal = waitingForDisbursal,
    active = active,
    closedObligationsMet = closedObligationsMet,
    closedWrittenOff = closedWrittenOff,
    closedRescheduled = closedRescheduled,
    closed = closed,
    overpaid = overpaid,
)

fun LoanStatus.toEntity(): LoanStatusRow = LoanStatusRow(
    id = id,
    code = code,
    value = value,
    pendingApproval = pendingApproval,
    waitingForDisbursal = waitingForDisbursal,
    active = active,
    closedObligationsMet = closedObligationsMet,
    closedWrittenOff = closedWrittenOff,
    closedRescheduled = closedRescheduled,
    closed = closed,
    overpaid = overpaid,
)

fun LoanTemplateRow.toModel(): LoanTemplate = LoanTemplate(
    clientId = clientId,
    clientAccountNo = clientAccountNo,
    clientName = clientName,
    clientOfficeId = clientOfficeId,
    loanProductId = loanProductId,
    loanProductName = loanProductName,
    isLoanProductLinkedToFloatingRate = isLoanProductLinkedToFloatingRate,
    fundId = fundId,
    fundName = fundName,
    currency = currency,
    principal = principal,
    approvedPrincipal = approvedPrincipal,
    proposedPrincipal = proposedPrincipal,
    termFrequency = termFrequency,
    termPeriodFrequencyType = termPeriodFrequencyType,
    numberOfRepayments = numberOfRepayments,
    repaymentEvery = repaymentEvery,
    repaymentFrequencyType = repaymentFrequencyType,
    interestRatePerPeriod = interestRatePerPeriod,
    interestRateFrequencyType = interestRateFrequencyType,
    annualInterestRate = annualInterestRate,
    isFloatingInterestRate = isFloatingInterestRate,
    amortizationType = amortizationType,
    interestType = interestType,
    interestCalculationPeriodType = interestCalculationPeriodType,
    allowPartialPeriodInterestCalculation = allowPartialPeriodInterestCalculation,
    transactionProcessingStrategyId = transactionProcessingStrategyId,
    graceOnArrearsAgeing = graceOnArrearsAgeing,
    timeline = timeline,
    productOptions = productOptions,
    dataTables = dataTables.mapTo(ArrayList()) { it.toModel() },
    loanOfficerOptions = loanOfficerOptions,
    loanPurposeOptions = loanPurposeOptions,
    fundOptions = fundOptions,
    termFrequencyTypeOptions = termFrequencyTypeOptions,
    repaymentFrequencyTypeOptions = repaymentFrequencyTypeOptions,
    repaymentFrequencyNthDayTypeOptions = repaymentFrequencyNthDayTypeOptions,
    repaymentFrequencyDaysOfWeekTypeOptions = repaymentFrequencyDaysOfWeekTypeOptions,
    interestRateFrequencyTypeOptions = interestRateFrequencyTypeOptions,
    amortizationTypeOptions = amortizationTypeOptions,
    interestTypeOptions = interestTypeOptions,
    interestCalculationPeriodTypeOptions = interestCalculationPeriodTypeOptions,
    transactionProcessingStrategyOptions = transactionProcessingStrategyOptions,
    chargeOptions = chargeOptions,
    loanCollateralOptions = loanCollateralOptions,
    multiDisburseLoan = multiDisburseLoan,
    transactionProcessingStrategyCode = transactionProcessingStrategyCode,
    canDefineInstallmentAmount = canDefineInstallmentAmount,
    canDisburse = canDisburse,
    product = product,
    daysInMonthType = daysInMonthType,
    daysInYearType = daysInYearType,
    isInterestRecalculationEnabled = isInterestRecalculationEnabled,
    isvaliableInstallmentsAllowed = isvaliableInstallmentsAllowed,
    minimumGap = minimumGap,
    maximumGap = maximumGap,
    accountLinkingOptions = accountLinkingOptions,
    loanScheduleType = loanScheduleType,
    overdueCharges = overdueCharges.map { it.toModel() },
)

fun LoanTemplate.toEntity(): LoanTemplateRow = LoanTemplateRow(
    clientId = clientId,
    clientAccountNo = clientAccountNo,
    clientName = clientName,
    clientOfficeId = clientOfficeId,
    loanProductId = loanProductId,
    loanProductName = loanProductName,
    isLoanProductLinkedToFloatingRate = isLoanProductLinkedToFloatingRate,
    fundId = fundId,
    fundName = fundName,
    currency = currency,
    principal = principal,
    approvedPrincipal = approvedPrincipal,
    proposedPrincipal = proposedPrincipal,
    termFrequency = termFrequency,
    termPeriodFrequencyType = termPeriodFrequencyType,
    numberOfRepayments = numberOfRepayments,
    repaymentEvery = repaymentEvery,
    repaymentFrequencyType = repaymentFrequencyType,
    interestRatePerPeriod = interestRatePerPeriod,
    interestRateFrequencyType = interestRateFrequencyType,
    annualInterestRate = annualInterestRate,
    isFloatingInterestRate = isFloatingInterestRate,
    amortizationType = amortizationType,
    interestType = interestType,
    interestCalculationPeriodType = interestCalculationPeriodType,
    allowPartialPeriodInterestCalculation = allowPartialPeriodInterestCalculation,
    transactionProcessingStrategyId = transactionProcessingStrategyId,
    graceOnArrearsAgeing = graceOnArrearsAgeing,
    timeline = timeline,
    productOptions = productOptions,
    dataTables = dataTables.mapTo(ArrayList()) { it.toEntity() },
    loanOfficerOptions = loanOfficerOptions,
    loanPurposeOptions = loanPurposeOptions,
    fundOptions = fundOptions,
    termFrequencyTypeOptions = termFrequencyTypeOptions,
    repaymentFrequencyTypeOptions = repaymentFrequencyTypeOptions,
    repaymentFrequencyNthDayTypeOptions = repaymentFrequencyNthDayTypeOptions,
    repaymentFrequencyDaysOfWeekTypeOptions = repaymentFrequencyDaysOfWeekTypeOptions,
    interestRateFrequencyTypeOptions = interestRateFrequencyTypeOptions,
    amortizationTypeOptions = amortizationTypeOptions,
    interestTypeOptions = interestTypeOptions,
    interestCalculationPeriodTypeOptions = interestCalculationPeriodTypeOptions,
    transactionProcessingStrategyOptions = transactionProcessingStrategyOptions,
    chargeOptions = chargeOptions,
    loanCollateralOptions = loanCollateralOptions,
    multiDisburseLoan = multiDisburseLoan,
    transactionProcessingStrategyCode = transactionProcessingStrategyCode,
    canDefineInstallmentAmount = canDefineInstallmentAmount,
    canDisburse = canDisburse,
    product = product,
    daysInMonthType = daysInMonthType,
    daysInYearType = daysInYearType,
    isInterestRecalculationEnabled = isInterestRecalculationEnabled,
    isvaliableInstallmentsAllowed = isvaliableInstallmentsAllowed,
    minimumGap = minimumGap,
    maximumGap = maximumGap,
    accountLinkingOptions = accountLinkingOptions,
    loanScheduleType = loanScheduleType,
    overdueCharges = overdueCharges.map { it.toEntity() },
)

fun LoanTimelineRow.toModel(): LoanTimeline = LoanTimeline(
    loanId = loanId,
    submittedOnDate = submittedOnDate,
    submittedByUsername = submittedByUsername,
    submittedByFirstname = submittedByFirstname,
    submittedByLastname = submittedByLastname,
    approvedOnDate = approvedOnDate,
    approvedByUsername = approvedByUsername,
    approvedByFirstname = approvedByFirstname,
    approvedByLastname = approvedByLastname,
    expectedDisbursementDate = expectedDisbursementDate,
    actualDisburseDate = actualDisburseDate?.toModel(),
    actualDisbursementDate = actualDisbursementDate,
    disbursedByUsername = disbursedByUsername,
    disbursedByFirstname = disbursedByFirstname,
    disbursedByLastname = disbursedByLastname,
    closedOnDate = closedOnDate,
    expectedMaturityDate = expectedMaturityDate,
    withdrawnOnDate = withdrawnOnDate,
)

fun LoanTimeline.toEntity(): LoanTimelineRow = LoanTimelineRow(
    loanId = loanId,
    submittedOnDate = submittedOnDate,
    submittedByUsername = submittedByUsername,
    submittedByFirstname = submittedByFirstname,
    submittedByLastname = submittedByLastname,
    approvedOnDate = approvedOnDate,
    approvedByUsername = approvedByUsername,
    approvedByFirstname = approvedByFirstname,
    approvedByLastname = approvedByLastname,
    expectedDisbursementDate = expectedDisbursementDate,
    actualDisburseDate = actualDisburseDate?.toEntity(),
    actualDisbursementDate = actualDisbursementDate,
    disbursedByUsername = disbursedByUsername,
    disbursedByFirstname = disbursedByFirstname,
    disbursedByLastname = disbursedByLastname,
    closedOnDate = closedOnDate,
    expectedMaturityDate = expectedMaturityDate,
    withdrawnOnDate = withdrawnOnDate,
)

fun LoanAccountTypeRow.toModel(): LoanAccountType = LoanAccountType(
    id = id,
    code = code,
    value = value,
)

fun LoanAccountType.toEntity(): LoanAccountTypeRow = LoanAccountTypeRow(
    id = id,
    code = code,
    value = value,
)

fun OfficeRow.toModel(): Office = Office(
    id = id,
    externalId = externalId,
    name = name,
    nameDecorated = nameDecorated,
    officeOpeningDate = officeOpeningDate?.toModel(),
    openingDate = openingDate,
)

fun Office.toEntity(): OfficeRow = OfficeRow(
    id = id,
    externalId = externalId,
    name = name,
    nameDecorated = nameDecorated,
    officeOpeningDate = officeOpeningDate?.toEntity(),
    openingDate = openingDate,
)

fun OfficeOpeningDateRow.toModel(): OfficeOpeningDate = OfficeOpeningDate(
    officeId = officeId,
    year = year,
    month = month,
    day = day,
)

fun OfficeOpeningDate.toEntity(): OfficeOpeningDateRow = OfficeOpeningDateRow(
    officeId = officeId,
    year = year,
    month = month,
    day = day,
)

fun OfficeOptionsRow.toModel(): OfficeOptions = OfficeOptions(
    id = id,
    name = name,
    nameDecorated = nameDecorated,
)

fun OfficeOptions.toEntity(): OfficeOptionsRow = OfficeOptionsRow(
    id = id,
    name = name,
    nameDecorated = nameDecorated,
)

fun OptionsRow.toModel(): Options = Options(
    optionType = optionType,
    id = id,
    name = name,
    position = position,
    description = description,
    activeStatus = activeStatus,
)

fun Options.toEntity(): OptionsRow = OptionsRow(
    optionType = optionType,
    id = id,
    name = name,
    position = position,
    description = description,
    activeStatus = activeStatus,
)

fun PaymentTypeOptionRow.toModel(): PaymentTypeOption = PaymentTypeOption(
    id = id,
    name = name,
    description = description,
    isCashPayment = isCashPayment,
    position = position,
)

fun PaymentTypeOption.toEntity(): PaymentTypeOptionRow = PaymentTypeOptionRow(
    id = id,
    name = name,
    description = description,
    isCashPayment = isCashPayment,
    position = position,
)

fun SavingAccountCurrencyRow.toModel(): SavingAccountCurrency = SavingAccountCurrency(
    id = id,
    code = code,
    name = name,
    decimalPlaces = decimalPlaces,
    inMultiplesOf = inMultiplesOf,
    displaySymbol = displaySymbol,
    nameCode = nameCode,
    displayLabel = displayLabel,
)

fun SavingAccountCurrency.toEntity(): SavingAccountCurrencyRow = SavingAccountCurrencyRow(
    id = id,
    code = code,
    name = name,
    decimalPlaces = decimalPlaces,
    inMultiplesOf = inMultiplesOf,
    displaySymbol = displaySymbol,
    nameCode = nameCode,
    displayLabel = displayLabel,
)

fun SavingAccountDepositTypeRow.toModel(): SavingAccountDepositType = SavingAccountDepositType(
    id = id,
    code = code,
    value = value,
)

fun SavingAccountDepositType.toEntity(): SavingAccountDepositTypeRow = SavingAccountDepositTypeRow(
    id = id,
    code = code,
    value = value,
)

fun SavingProductOptionsRow.toModel(): SavingProductOptions = SavingProductOptions(
    id = id,
    name = name,
    withdrawalFeeForTransfers = withdrawalFeeForTransfers,
    allowOverdraft = allowOverdraft,
)

fun SavingProductOptions.toEntity(): SavingProductOptionsRow = SavingProductOptionsRow(
    id = id,
    name = name,
    withdrawalFeeForTransfers = withdrawalFeeForTransfers,
    allowOverdraft = allowOverdraft,
)

fun SavingProductsTemplateRow.toModel(): SavingProductsTemplate = SavingProductsTemplate(
    currency = currency,
    interestCompoundingPeriodType = interestCompoundingPeriodType,
    interestPostingPeriodType = interestPostingPeriodType,
    interestCalculationType = interestCalculationType,
    interestCalculationDaysInYearType = interestCalculationDaysInYearType,
    accountingRule = accountingRule,
    currencyOptions = currencyOptions,
    interestCompoundingPeriodTypeOptions = interestCompoundingPeriodTypeOptions,
    interestPostingPeriodTypeOptions = interestPostingPeriodTypeOptions,
    interestCalculationTypeOptions = interestCalculationTypeOptions,
    interestCalculationDaysInYearTypeOptions = interestCalculationDaysInYearTypeOptions,
    lockinPeriodFrequencyTypeOptions = lockinPeriodFrequencyTypeOptions,
    withdrawalFeeTypeOptions = withdrawalFeeTypeOptions,
    paymentTypeOptions = paymentTypeOptions?.map { it.toModel() },
    accountingRuleOptions = accountingRuleOptions,
    liabilityAccountOptions = liabilityAccountOptions,
    assetAccountOptions = assetAccountOptions,
    expenseAccountOptions = expenseAccountOptions,
    incomeAccountOptions = incomeAccountOptions,
    fieldOfficerOptions = fieldOfficerOptions,
    chargeOptions = chargeOptions,
)

fun SavingProductsTemplate.toEntity(): SavingProductsTemplateRow = SavingProductsTemplateRow(
    currency = currency,
    interestCompoundingPeriodType = interestCompoundingPeriodType,
    interestPostingPeriodType = interestPostingPeriodType,
    interestCalculationType = interestCalculationType,
    interestCalculationDaysInYearType = interestCalculationDaysInYearType,
    accountingRule = accountingRule,
    currencyOptions = currencyOptions,
    interestCompoundingPeriodTypeOptions = interestCompoundingPeriodTypeOptions,
    interestPostingPeriodTypeOptions = interestPostingPeriodTypeOptions,
    interestCalculationTypeOptions = interestCalculationTypeOptions,
    interestCalculationDaysInYearTypeOptions = interestCalculationDaysInYearTypeOptions,
    lockinPeriodFrequencyTypeOptions = lockinPeriodFrequencyTypeOptions,
    withdrawalFeeTypeOptions = withdrawalFeeTypeOptions,
    paymentTypeOptions = paymentTypeOptions?.map { it.toEntity() },
    accountingRuleOptions = accountingRuleOptions,
    liabilityAccountOptions = liabilityAccountOptions,
    assetAccountOptions = assetAccountOptions,
    expenseAccountOptions = expenseAccountOptions,
    incomeAccountOptions = incomeAccountOptions,
    fieldOfficerOptions = fieldOfficerOptions,
    chargeOptions = chargeOptions,
)

fun SavingsAccountRow.toModel(): SavingsAccount = SavingsAccount(
    id = id,
    clientId = clientId,
    groupId = groupId,
    centerId = centerId,
    accountNo = accountNo,
    productId = productId,
    shortProductName = shortProductName,
    productName = productName,
    status = status?.toModel(),
    currency = currency?.toModel(),
    accountBalance = accountBalance,
    depositType = depositType?.toModel(),
    lastActiveTransactionDate = lastActiveTransactionDate,
)

fun SavingsAccount.toEntity(): SavingsAccountRow = SavingsAccountRow(
    id = id,
    clientId = clientId,
    groupId = groupId,
    centerId = centerId,
    accountNo = accountNo,
    productId = productId,
    shortProductName = shortProductName,
    productName = productName,
    status = status?.toEntity(),
    currency = currency?.toEntity(),
    accountBalance = accountBalance,
    depositType = depositType?.toEntity(),
    lastActiveTransactionDate = lastActiveTransactionDate,
)

fun SavingsAccountStatusRow.toModel(): SavingsAccountStatus = SavingsAccountStatus(
    id = id,
    code = code,
    value = value,
    submittedAndPendingApproval = submittedAndPendingApproval,
    approved = approved,
    rejected = rejected,
    withdrawnByApplicant = withdrawnByApplicant,
    active = active,
    closed = closed,
)

fun SavingsAccountStatus.toEntity(): SavingsAccountStatusRow = SavingsAccountStatusRow(
    id = id,
    code = code,
    value = value,
    submittedAndPendingApproval = submittedAndPendingApproval,
    approved = approved,
    rejected = rejected,
    withdrawnByApplicant = withdrawnByApplicant,
    active = active,
    closed = closed,
)

fun SavingsAccountSummaryRow.toModel(): SavingsAccountSummary = SavingsAccountSummary(
    savingsId = savingsId,
    currency = currency?.toModel(),
    totalDeposits = totalDeposits,
    accountBalance = accountBalance,
    totalWithdrawals = totalWithdrawals,
    totalInterestEarned = totalInterestEarned,
)

fun SavingsAccountSummary.toEntity(): SavingsAccountSummaryRow = SavingsAccountSummaryRow(
    savingsId = savingsId,
    currency = currency?.toEntity(),
    totalDeposits = totalDeposits,
    accountBalance = accountBalance,
    totalWithdrawals = totalWithdrawals,
    totalInterestEarned = totalInterestEarned,
)

fun SavingsAccountTransactionRow.toModel(): SavingsAccountTransaction = SavingsAccountTransaction(
    id = id,
    savingsAccountId = savingsAccountId,
    transactionType = transactionType?.toModel(),
    accountId = accountId,
    accountNo = accountNo,
    savingsTransactionDate = savingsTransactionDate?.toModel(),
    date = date,
    currency = currency?.toModel(),
    amount = amount,
    runningBalance = runningBalance,
    reversed = reversed,
)

fun SavingsAccountTransaction.toEntity(): SavingsAccountTransactionRow = SavingsAccountTransactionRow(
    id = id,
    savingsAccountId = savingsAccountId,
    transactionType = transactionType?.toEntity(),
    accountId = accountId,
    accountNo = accountNo,
    savingsTransactionDate = savingsTransactionDate?.toEntity(),
    date = date,
    currency = currency?.toEntity(),
    amount = amount,
    runningBalance = runningBalance,
    reversed = reversed,
)

fun SavingsAccountTransactionTemplateRow.toModel(): SavingsAccountTransactionTemplate = SavingsAccountTransactionTemplate(
    accountId = accountId,
    accountNo = accountNo,
    date = date,
    reversed = reversed,
    paymentTypeOptions = paymentTypeOptions.map { it.toModel() },
)

fun SavingsAccountTransactionTemplate.toEntity(): SavingsAccountTransactionTemplateRow = SavingsAccountTransactionTemplateRow(
    accountId = accountId,
    accountNo = accountNo,
    date = date,
    reversed = reversed,
    paymentTypeOptions = paymentTypeOptions.map { it.toEntity() },
)

fun SavingsAccountWithAssociationsRow.toModel(): SavingsAccountWithAssociations = SavingsAccountWithAssociations(
    id = id,
    accountNo = accountNo,
    clientId = clientId,
    clientName = clientName,
    savingsProductId = savingsProductId,
    savingsProductName = savingsProductName,
    fieldOfficerId = fieldOfficerId,
    status = status?.toModel(),
    timeline = timeline?.toModel(),
    currency = currency?.toModel(),
    nominalAnnualInterestRate = nominalAnnualInterestRate,
    interestCompoundingPeriodType = interestCompoundingPeriodType,
    interestPostingPeriodType = interestPostingPeriodType,
    interestCalculationType = interestCalculationType,
    interestCalculationDaysInYearType = interestCalculationDaysInYearType,
    minRequiredOpeningBalance = minRequiredOpeningBalance,
    lockinPeriodFrequency = lockinPeriodFrequency,
    lockinPeriodFrequencyType = lockinPeriodFrequencyType,
    withdrawalFeeForTransfers = withdrawalFeeForTransfers,
    allowOverdraft = allowOverdraft,
    enforceMinRequiredBalance = enforceMinRequiredBalance,
    withHoldTax = withHoldTax,
    lastActiveTransactionDate = lastActiveTransactionDate,
    dormancyTrackingActive = dormancyTrackingActive,
    overdraftLimit = overdraftLimit,
    summary = summary?.toModel(),
    transactions = transactions.map { it.toModel() },
    charges = charges.map { it?.toModel() },
)

fun SavingsAccountWithAssociations.toEntity(): SavingsAccountWithAssociationsRow = SavingsAccountWithAssociationsRow(
    id = id,
    accountNo = accountNo,
    clientId = clientId,
    clientName = clientName,
    savingsProductId = savingsProductId,
    savingsProductName = savingsProductName,
    fieldOfficerId = fieldOfficerId,
    status = status?.toEntity(),
    timeline = timeline?.toEntity(),
    currency = currency?.toEntity(),
    nominalAnnualInterestRate = nominalAnnualInterestRate,
    interestCompoundingPeriodType = interestCompoundingPeriodType,
    interestPostingPeriodType = interestPostingPeriodType,
    interestCalculationType = interestCalculationType,
    interestCalculationDaysInYearType = interestCalculationDaysInYearType,
    minRequiredOpeningBalance = minRequiredOpeningBalance,
    lockinPeriodFrequency = lockinPeriodFrequency,
    lockinPeriodFrequencyType = lockinPeriodFrequencyType,
    withdrawalFeeForTransfers = withdrawalFeeForTransfers,
    allowOverdraft = allowOverdraft,
    enforceMinRequiredBalance = enforceMinRequiredBalance,
    withHoldTax = withHoldTax,
    lastActiveTransactionDate = lastActiveTransactionDate,
    dormancyTrackingActive = dormancyTrackingActive,
    overdraftLimit = overdraftLimit,
    summary = summary?.toEntity(),
    transactions = transactions.map { it.toEntity() },
    charges = charges.map { it?.toEntity() },
)

fun SavingsTransactionDateRow.toModel(): SavingsTransactionDate = SavingsTransactionDate(
    transactionId = transactionId,
    year = year,
    month = month,
    day = day,
)

fun SavingsTransactionDate.toEntity(): SavingsTransactionDateRow = SavingsTransactionDateRow(
    transactionId = transactionId,
    year = year,
    month = month,
    day = day,
)

fun SavingsTransactionTypeRow.toModel(): SavingsTransactionType = SavingsTransactionType(
    id = id,
    code = code,
    value = value,
    deposit = deposit,
    withdrawal = withdrawal,
    interestPosting = interestPosting,
    feeDeduction = feeDeduction,
    initiateTransfer = initiateTransfer,
    approveTransfer = approveTransfer,
    withdrawTransfer = withdrawTransfer,
    rejectTransfer = rejectTransfer,
    overdraftInterest = overdraftInterest,
    writtenoff = writtenoff,
    overdraftFee = overdraftFee,
)

fun SavingsTransactionType.toEntity(): SavingsTransactionTypeRow = SavingsTransactionTypeRow(
    id = id,
    code = code,
    value = value,
    deposit = deposit,
    withdrawal = withdrawal,
    interestPosting = interestPosting,
    feeDeduction = feeDeduction,
    initiateTransfer = initiateTransfer,
    approveTransfer = approveTransfer,
    withdrawTransfer = withdrawTransfer,
    rejectTransfer = rejectTransfer,
    overdraftInterest = overdraftInterest,
    writtenoff = writtenoff,
    overdraftFee = overdraftFee,
)

fun StaffRow.toModel(): Staff = Staff(
    id = id,
    firstname = firstname,
    lastname = lastname,
    mobileNo = mobileNo,
    displayName = displayName,
    officeId = officeId,
    officeName = officeName,
    isLoanOfficer = isLoanOfficer,
    isActive = isActive,
)

fun Staff.toEntity(): StaffRow = StaffRow(
    id = id,
    firstname = firstname,
    lastname = lastname,
    mobileNo = mobileNo,
    displayName = displayName,
    officeId = officeId,
    officeName = officeName,
    isLoanOfficer = isLoanOfficer,
    isActive = isActive,
)

fun StaffOptionsRow.toModel(): StaffOptions = StaffOptions(
    id = id,
    firstname = firstname,
    lastname = lastname,
    displayName = displayName,
    officeId = officeId,
    officeName = officeName,
    isLoanOfficer = isLoanOfficer,
    isActive = isActive,
)

fun StaffOptions.toEntity(): StaffOptionsRow = StaffOptionsRow(
    id = id,
    firstname = firstname,
    lastname = lastname,
    displayName = displayName,
    officeId = officeId,
    officeName = officeName,
    isLoanOfficer = isLoanOfficer,
    isActive = isActive,
)

fun AddressConfigurationRow.toModel(): AddressConfiguration = AddressConfiguration(
    enabled = enabled,
)

fun AddressConfiguration.toEntity(): AddressConfigurationRow = AddressConfigurationRow(
    enabled = enabled,
)

fun CenterDetailRow.toModel(): CenterDetail = CenterDetail(
    staffId = staffId,
    staffName = staffName,
    meetingFallCenters = meetingFallCenters?.map { it.toModel() },
)

fun CenterDetail.toEntity(): CenterDetailRow = CenterDetailRow(
    staffId = staffId,
    staffName = staffName,
    meetingFallCenters = meetingFallCenters?.map { it.toEntity() },
)

fun CollectionSheetResponseRow.toModel(): CollectionSheetResponse = CollectionSheetResponse(
    attendanceTypeOptions = attendanceTypeOptions,
    dueDate = dueDate,
    groups = groups.map { it.toModel() },
    loanProducts = loanProducts,
    paymentTypeOptions = paymentTypeOptions.map { it.toModel() },
    savingsProducts = savingsProducts,
)

fun CollectionSheetResponse.toEntity(): CollectionSheetResponseRow = CollectionSheetResponseRow(
    attendanceTypeOptions = attendanceTypeOptions,
    dueDate = dueDate,
    groups = groups.map { it.toEntity() },
    loanProducts = loanProducts,
    paymentTypeOptions = paymentTypeOptions.map { it.toEntity() },
    savingsProducts = savingsProducts,
)

fun IndividualCollectionSheetRow.toModel(): IndividualCollectionSheet = IndividualCollectionSheet(
    dueDate = dueDate,
    clients = clients?.mapTo(ArrayList()) { it.toModel() },
    paymentTypeOptions = paymentTypeOptions,
)

fun IndividualCollectionSheet.toEntity(): IndividualCollectionSheetRow = IndividualCollectionSheetRow(
    dueDate = dueDate,
    clients = clients?.mapTo(ArrayList()) { it.toEntity() },
    paymentTypeOptions = paymentTypeOptions,
)

fun LoanRow.toModel(): Loan = Loan(
    id = id,
    accountNo = accountNo,
    status = status?.toModel(),
    clientId = clientId,
    clientName = clientName,
    clientOfficeId = clientOfficeId,
    loanProductId = loanProductId,
    loanProductName = loanProductName,
    loanProductDescription = loanProductDescription,
    fundId = fundId,
    fundName = fundName,
    loanOfficerId = loanOfficerId,
    loanOfficerName = loanOfficerName,
    loanType = loanType?.toModel(),
    currency = currency,
    principal = principal,
    approvedPrincipal = approvedPrincipal,
    termFrequency = termFrequency,
    termPeriodFrequencyType = termPeriodFrequencyType,
    numberOfRepayments = numberOfRepayments,
    repaymentEvery = repaymentEvery,
    repaymentFrequencyType = repaymentFrequencyType,
    interestRatePerPeriod = interestRatePerPeriod,
    interestRateFrequencyType = interestRateFrequencyType,
    annualInterestRate = annualInterestRate,
    amortizationType = amortizationType,
    interestType = interestType,
    interestCalculationPeriodType = interestCalculationPeriodType,
    transactionProcessingStrategyId = transactionProcessingStrategyId,
    transactionProcessingStrategyName = transactionProcessingStrategyName,
    syncDisbursementWithMeeting = syncDisbursementWithMeeting,
    timeline = timeline?.toModel(),
    summary = summary?.toModel(),
    feeChargesAtDisbursementCharged = feeChargesAtDisbursementCharged,
    loanCounter = loanCounter,
    loanProductCounter = loanProductCounter,
    multiDisburseLoan = multiDisburseLoan,
    canDisburse = canDisburse,
    inArrears = inArrears,
    isNPA = isNPA,
)

fun Loan.toEntity(): LoanRow = LoanRow(
    id = id,
    accountNo = accountNo,
    status = status?.toEntity(),
    clientId = clientId,
    clientName = clientName,
    clientOfficeId = clientOfficeId,
    loanProductId = loanProductId,
    loanProductName = loanProductName,
    loanProductDescription = loanProductDescription,
    fundId = fundId,
    fundName = fundName,
    loanOfficerId = loanOfficerId,
    loanOfficerName = loanOfficerName,
    loanType = loanType?.toEntity(),
    currency = currency,
    principal = principal,
    approvedPrincipal = approvedPrincipal,
    termFrequency = termFrequency,
    termPeriodFrequencyType = termPeriodFrequencyType,
    numberOfRepayments = numberOfRepayments,
    repaymentEvery = repaymentEvery,
    repaymentFrequencyType = repaymentFrequencyType,
    interestRatePerPeriod = interestRatePerPeriod,
    interestRateFrequencyType = interestRateFrequencyType,
    annualInterestRate = annualInterestRate,
    amortizationType = amortizationType,
    interestType = interestType,
    interestCalculationPeriodType = interestCalculationPeriodType,
    transactionProcessingStrategyId = transactionProcessingStrategyId,
    transactionProcessingStrategyName = transactionProcessingStrategyName,
    syncDisbursementWithMeeting = syncDisbursementWithMeeting,
    timeline = timeline?.toEntity(),
    summary = summary?.toEntity(),
    feeChargesAtDisbursementCharged = feeChargesAtDisbursementCharged,
    loanCounter = loanCounter,
    loanProductCounter = loanProductCounter,
    multiDisburseLoan = multiDisburseLoan,
    canDisburse = canDisburse,
    inArrears = inArrears,
    isNPA = isNPA,
)

fun LoanRepaymentResponseRow.toModel(): LoanRepaymentResponse = LoanRepaymentResponse(
    id = id,
    officeId = officeId,
    clientId = clientId,
    loanId = loanId,
    resourceId = resourceId,
    changes = changes,
)

fun LoanRepaymentResponse.toEntity(): LoanRepaymentResponseRow = LoanRepaymentResponseRow(
    id = id,
    officeId = officeId,
    clientId = clientId,
    loanId = loanId,
    resourceId = resourceId,
    changes = changes,
)

fun SavingsAccountTransactionRequestRow.toModel(): SavingsAccountTransactionRequest = SavingsAccountTransactionRequest(
    id = id,
    savingAccountId = savingAccountId,
    savingsAccountType = savingsAccountType,
    transactionType = transactionType,
    dateFormat = dateFormat,
    locale = locale,
    transactionDate = transactionDate,
    transactionAmount = transactionAmount,
    paymentTypeId = paymentTypeId,
    note = note,
    accountNumber = accountNumber,
    checkNumber = checkNumber,
    routingCode = routingCode,
    receiptNumber = receiptNumber,
    bankNumber = bankNumber,
    errorMessage = errorMessage,
)

fun SavingsAccountTransactionRequest.toEntity(): SavingsAccountTransactionRequestRow = SavingsAccountTransactionRequestRow(
    id = id,
    savingAccountId = savingAccountId,
    savingsAccountType = savingsAccountType,
    transactionType = transactionType,
    dateFormat = dateFormat,
    locale = locale,
    transactionDate = transactionDate,
    transactionAmount = transactionAmount,
    paymentTypeId = paymentTypeId,
    note = note,
    accountNumber = accountNumber,
    checkNumber = checkNumber,
    routingCode = routingCode,
    receiptNumber = receiptNumber,
    bankNumber = bankNumber,
    errorMessage = errorMessage,
)

fun SurveyRow.toModel(): Survey = Survey(
    id = id,
    key = key,
    name = name,
    description = description,
    isSync = isSync,
    countryCode = countryCode,
    questionDatas = questionDatas.map { it.toModel() },
    componentDatas = componentDatas.map { it.toModel() },
)

fun Survey.toEntity(): SurveyRow = SurveyRow(
    id = id,
    key = key,
    name = name,
    description = description,
    isSync = isSync,
    countryCode = countryCode,
    questionDatas = questionDatas.map { it.toEntity() },
    componentDatas = componentDatas.map { it.toEntity() },
)

fun CenterAccountsRow.toModel(): CenterAccounts = CenterAccounts(
    loanAccounts = loanAccounts.map { it.toModel() },
    savingsAccounts = savingsAccounts.map { it.toModel() },
    memberLoanAccounts = memberLoanAccounts.map { it.toModel() },
)

fun CenterAccounts.toEntity(): CenterAccountsRow = CenterAccountsRow(
    loanAccounts = loanAccounts.map { it.toEntity() },
    savingsAccounts = savingsAccounts.map { it.toEntity() },
    memberLoanAccounts = memberLoanAccounts.map { it.toEntity() },
)

fun CenterWithAssociationsRow.toModel(): CenterWithAssociations = CenterWithAssociations(
    id = id,
    accountNo = accountNo,
    name = name,
    externalId = externalId,
    officeId = officeId,
    officeName = officeName,
    staffId = staffId,
    staffName = staffName,
    hierarchy = hierarchy,
    status = status?.toModel(),
    active = active,
    activationDate = activationDate,
    timeline = timeline,
    groupMembers = groupMembers.map { it.toModel() },
    collectionMeetingCalendar = collectionMeetingCalendar,
)

fun CenterWithAssociations.toEntity(): CenterWithAssociationsRow = CenterWithAssociationsRow(
    id = id,
    accountNo = accountNo,
    name = name,
    externalId = externalId,
    officeId = officeId,
    officeName = officeName,
    staffId = staffId,
    staffName = staffName,
    hierarchy = hierarchy,
    status = status?.toEntity(),
    active = active,
    activationDate = activationDate,
    timeline = timeline,
    groupMembers = groupMembers.map { it.toEntity() },
    collectionMeetingCalendar = collectionMeetingCalendar,
)

fun GroupAccountsRow.toModel(): GroupAccounts = GroupAccounts(
    loanAccounts = loanAccounts.map { it.toModel() },
    savingsAccounts = savingsAccounts.map { it.toModel() },
)

fun GroupAccounts.toEntity(): GroupAccountsRow = GroupAccountsRow(
    loanAccounts = loanAccounts.map { it.toEntity() },
    savingsAccounts = savingsAccounts.map { it.toEntity() },
)

fun GroupWithAssociationsRow.toModel(): GroupWithAssociations = GroupWithAssociations(
    id = id,
    accountNo = accountNo,
    name = name,
    status = status?.toModel(),
    active = active,
    activationDate = activationDate,
    officeId = officeId,
    officeName = officeName,
    staffId = staffId,
    staffName = staffName,
    hierarchy = hierarchy,
    groupLevel = groupLevel,
    clientMembers = clientMembers.map { it.toModel() },
    timeline = timeline,
)

fun GroupWithAssociations.toEntity(): GroupWithAssociationsRow = GroupWithAssociationsRow(
    id = id,
    accountNo = accountNo,
    name = name,
    status = status?.toEntity(),
    active = active,
    activationDate = activationDate,
    officeId = officeId,
    officeName = officeName,
    staffId = staffId,
    staffName = staffName,
    hierarchy = hierarchy,
    groupLevel = groupLevel,
    clientMembers = clientMembers.map { it.toEntity() },
    timeline = timeline,
)

fun LoanTransactionTemplateRow.toModel(): LoanTransactionTemplate = LoanTransactionTemplate(
    type = type,
    date = date,
    amount = amount,
    manuallyReversed = manuallyReversed,
    possibleNextRepaymentDate = possibleNextRepaymentDate,
    paymentTypeOptions = paymentTypeOptions.map { it.toModel() },
)

fun LoanTransactionTemplate.toEntity(): LoanTransactionTemplateRow = LoanTransactionTemplateRow(
    type = type,
    date = date,
    amount = amount,
    manuallyReversed = manuallyReversed,
    possibleNextRepaymentDate = possibleNextRepaymentDate,
    paymentTypeOptions = paymentTypeOptions.map { it.toEntity() },
)

fun ComponentDatasRow.toModel(): ComponentDatas = ComponentDatas(
    id = id,
    key = key,
    text = text,
    description = description,
    sequenceNo = sequenceNo,
)

fun ComponentDatas.toEntity(): ComponentDatasRow = ComponentDatasRow(
    id = id,
    key = key,
    text = text,
    description = description,
    sequenceNo = sequenceNo,
)

fun QuestionDatasRow.toModel(): QuestionDatas = QuestionDatas(
    id = id,
    surveyId = surveyId,
    componentKey = componentKey,
    key = key,
    text = text,
    description = description,
    sequenceNo = sequenceNo,
    responseDatas = responseDatas.map { it.toModel() },
)

fun QuestionDatas.toEntity(): QuestionDatasRow = QuestionDatasRow(
    id = id,
    surveyId = surveyId,
    componentKey = componentKey,
    key = key,
    text = text,
    description = description,
    sequenceNo = sequenceNo,
    responseDatas = responseDatas.map { it.toEntity() },
)

fun ResponseDatasRow.toModel(): ResponseDatas = ResponseDatas(
    responseId = responseId,
    id = id,
    text = text,
    sequenceNo = sequenceNo,
    value = value,
)

fun ResponseDatas.toEntity(): ResponseDatasRow = ResponseDatasRow(
    responseId = responseId,
    id = id,
    text = text,
    sequenceNo = sequenceNo,
    value = value,
)

fun GroupCollectionSheetRow.toModel(): GroupCollectionSheet = GroupCollectionSheet(
    clients = clients.mapTo(ArrayList()) { it.toModel() },
    groupId = groupId,
    groupName = groupName,
    levelId = levelId,
    levelName = levelName,
    staffId = staffId,
    staffName = staffName,
)

fun GroupCollectionSheet.toEntity(): GroupCollectionSheetRow = GroupCollectionSheetRow(
    clients = clients.mapTo(ArrayList()) { it.toEntity() },
    groupId = groupId,
    groupName = groupName,
    levelId = levelId,
    levelName = levelName,
    staffId = staffId,
    staffName = staffName,
)

fun ClientCollectionSheetRow.toModel(): ClientCollectionSheet = ClientCollectionSheet(
    clientId = clientId,
    clientName = clientName,
    loans = loans,
    attendanceType = attendanceType,
    savings = savings.mapTo(ArrayList()) { it.toModel() },
)

fun ClientCollectionSheet.toEntity(): ClientCollectionSheetRow = ClientCollectionSheetRow(
    clientId = clientId,
    clientName = clientName,
    loans = loans,
    attendanceType = attendanceType,
    savings = savings.mapTo(ArrayList()) { it.toEntity() },
)

fun SavingsCollectionSheetRow.toModel(): SavingsCollectionSheet = SavingsCollectionSheet(
    accountId = accountId,
    accountStatusId = accountStatusId,
    currency = currency?.toModel(),
    depositAccountType = depositAccountType,
    dueAmount = dueAmount,
    productId = productId,
    productName = productName,
    savingsId = savingsId,
)

fun SavingsCollectionSheet.toEntity(): SavingsCollectionSheetRow = SavingsCollectionSheetRow(
    accountId = accountId,
    accountStatusId = accountStatusId,
    currency = currency?.toEntity(),
    depositAccountType = depositAccountType,
    dueAmount = dueAmount,
    productId = productId,
    productName = productName,
    savingsId = savingsId,
)

fun MeetingFallCalendarRow.toModel(): MeetingFallCalendar = MeetingFallCalendar(
    accountNo = accountNo,
    activationDate = activationDate,
    isActive = isActive,
    collectionMeetingCalendar = collectionMeetingCalendar,
    hierarchy = hierarchy,
    id = id,
    installmentDue = installmentDue,
    name = name,
    officeId = officeId,
    staffId = staffId,
    staffName = staffName,
    status = status?.toModel(),
    totalCollected = totalCollected,
    totalOverdue = totalOverdue,
    totaldue = totaldue,
)

fun MeetingFallCalendar.toEntity(): MeetingFallCalendarRow = MeetingFallCalendarRow(
    accountNo = accountNo,
    activationDate = activationDate,
    isActive = isActive,
    collectionMeetingCalendar = collectionMeetingCalendar,
    hierarchy = hierarchy,
    id = id,
    installmentDue = installmentDue,
    name = name,
    officeId = officeId,
    staffId = staffId,
    staffName = staffName,
    status = status?.toEntity(),
    totalCollected = totalCollected,
    totalOverdue = totalOverdue,
    totaldue = totaldue,
)

fun LoanAccountSummaryRow.toModel(): LoanAccountSummary = LoanAccountSummary(
    loanId = loanId,
    currency = currency?.toModel(),
    principalDisbursed = principalDisbursed,
    principalPaid = principalPaid,
    principalWaived = principalWaived,
    principalWrittenOff = principalWrittenOff,
    principalOutstanding = principalOutstanding,
    principalOverdue = principalOverdue,
    interestCharged = interestCharged,
    interestPaid = interestPaid,
    interestWaived = interestWaived,
    interestWrittenOff = interestWrittenOff,
    interestOutstanding = interestOutstanding,
    interestOverdue = interestOverdue,
    feeChargesCharged = feeChargesCharged,
    feeChargesDueAtDisbursementCharged = feeChargesDueAtDisbursementCharged,
    feeChargesPaid = feeChargesPaid,
    feeChargesWaived = feeChargesWaived,
    feeChargesWrittenOff = feeChargesWrittenOff,
    feeChargesOutstanding = feeChargesOutstanding,
    feeChargesOverdue = feeChargesOverdue,
    penaltyChargesCharged = penaltyChargesCharged,
    penaltyChargesPaid = penaltyChargesPaid,
    penaltyChargesWaived = penaltyChargesWaived,
    penaltyChargesWrittenOff = penaltyChargesWrittenOff,
    penaltyChargesOutstanding = penaltyChargesOutstanding,
    penaltyChargesOverdue = penaltyChargesOverdue,
    totalExpectedRepayment = totalExpectedRepayment,
    totalRepayment = totalRepayment,
    totalExpectedCostOfLoan = totalExpectedCostOfLoan,
    totalCostOfLoan = totalCostOfLoan,
    totalWaived = totalWaived,
    totalWrittenOff = totalWrittenOff,
    totalOutstanding = totalOutstanding,
    totalOverdue = totalOverdue,
    overdueSinceDate = overdueSinceDate,
)

fun LoanAccountSummary.toEntity(): LoanAccountSummaryRow = LoanAccountSummaryRow(
    loanId = loanId,
    currency = currency?.toEntity(),
    principalDisbursed = principalDisbursed,
    principalPaid = principalPaid,
    principalWaived = principalWaived,
    principalWrittenOff = principalWrittenOff,
    principalOutstanding = principalOutstanding,
    principalOverdue = principalOverdue,
    interestCharged = interestCharged,
    interestPaid = interestPaid,
    interestWaived = interestWaived,
    interestWrittenOff = interestWrittenOff,
    interestOutstanding = interestOutstanding,
    interestOverdue = interestOverdue,
    feeChargesCharged = feeChargesCharged,
    feeChargesDueAtDisbursementCharged = feeChargesDueAtDisbursementCharged,
    feeChargesPaid = feeChargesPaid,
    feeChargesWaived = feeChargesWaived,
    feeChargesWrittenOff = feeChargesWrittenOff,
    feeChargesOutstanding = feeChargesOutstanding,
    feeChargesOverdue = feeChargesOverdue,
    penaltyChargesCharged = penaltyChargesCharged,
    penaltyChargesPaid = penaltyChargesPaid,
    penaltyChargesWaived = penaltyChargesWaived,
    penaltyChargesWrittenOff = penaltyChargesWrittenOff,
    penaltyChargesOutstanding = penaltyChargesOutstanding,
    penaltyChargesOverdue = penaltyChargesOverdue,
    totalExpectedRepayment = totalExpectedRepayment,
    totalRepayment = totalRepayment,
    totalExpectedCostOfLoan = totalExpectedCostOfLoan,
    totalCostOfLoan = totalCostOfLoan,
    totalWaived = totalWaived,
    totalWrittenOff = totalWrittenOff,
    totalOutstanding = totalOutstanding,
    totalOverdue = totalOverdue,
    overdueSinceDate = overdueSinceDate,
)
