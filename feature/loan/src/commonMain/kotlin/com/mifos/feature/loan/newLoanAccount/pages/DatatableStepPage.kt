/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    kotlin.time.ExperimentalTime::class,
)

package com.mifos.feature.loan.newLoanAccount.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidclient.feature.loan.generated.resources.Res
import androidclient.feature.loan.generated.resources.back
import androidclient.feature.loan.generated.resources.feature_loan_charge_submit
import androidclient.feature.loan.generated.resources.next
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.ui.components.MifosTwoButtonRow
import com.mifos.feature.loan.newLoanAccount.NewLoanAccountAction
import com.mifos.room.entities.noncore.ColumnHeader
import com.mifos.room.entities.noncore.DataTableEntity
import org.jetbrains.compose.resources.stringResource
import template.core.base.designsystem.theme.KptTheme
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Inline per-datatable step inside the loan-creation stepper (GAP-DT-013).
 *
 * Renders a fully-dynamic form derived from [DataTableEntity.columnHeaderData].
 * Skips system columns:
 *   - `columnPrimaryKey == true` (auto-generated loan_id)
 *   - column name in `created_at` / `updated_at` (audit timestamps)
 *
 * Per-column widget choice keyed on `columnDisplayType` (matches Fineract
 * `ResultsetColumnHeaderData.ColumnDisplayType` enum, which the mapper writes as
 * the uppercase string). CODELOOKUP/CODEVALUE dropdowns are populated from
 * `columnValues` returned per column in the SAME response — no separate fetch.
 *
 * The picked-id (not display string) is persisted for CODELOOKUP columns —
 * Fineract expects code ids in the submit payload.
 *
 * @param table       the datatable schema for this step
 * @param values      current values keyed by column name (lifted to ViewModel)
 * @param onValueChange `(columnName, value) -> Unit` — value is String / Boolean / Int
 */
@Composable
fun DatatableStepPage(
    table: DataTableEntity,
    values: Map<String, Any>,
    onValueChange: (columnName: String, value: Any) -> Unit,
    onAction: (NewLoanAccountAction) -> Unit,
    isLastStep: Boolean,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(horizontal = KptTheme.spacing.md, vertical = KptTheme.spacing.md),
    ) {
        Text(
            text = table.registeredTableName ?: "",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )
        Spacer(modifier = Modifier.height(KptTheme.spacing.md))

        // Render each user-editable column. PK + audit + FK columns filtered out;
        // headers with null `dataTableColumnName` are also skipped (defensive —
        // @SerialName now ensures it's populated for normal Fineract responses).
        table.columnHeaderData
            .filter { header -> header.isUserEditable() }
            .forEach { header ->
                DatatableField(
                    header = header,
                    currentValue = values[header.dataTableColumnName],
                    onValueChange = { v ->
                        // Submit payload uses the RAW columnName (incl. _cd_ suffix);
                        // that's what Fineract expects on POST /loans.datatables[].data.
                        header.dataTableColumnName?.let { onValueChange(it, v) }
                    },
                )
            }

        Spacer(modifier = Modifier.height(KptTheme.spacing.md))

        // GAP-DT-013: each inline page provides its own Back / Next (or Submit on
        // the very last page if datatables happen to be the trailing steps —
        // currently impossible because Preview is always after, but we keep the
        // logic in case the ordering changes).
        MifosTwoButtonRow(
            firstBtnText = stringResource(Res.string.back),
            secondBtnText = stringResource(
                if (isLastStep) Res.string.feature_loan_charge_submit else Res.string.next,
            ),
            onFirstBtnClick = { onAction(NewLoanAccountAction.PreviousStep) },
            onSecondBtnClick = {
                if (isLastStep) onAction(NewLoanAccountAction.SubmitLoanApplication)
                else onAction(NewLoanAccountAction.NextStep)
            },
        )
    }
}

/**
 * Columns Fineract auto-fills on insert — never user input.
 *  - `created_at` / `updated_at` (+ camelCase): audit timestamps.
 *  - `loan_id` / `client_id` / etc: foreign key to the parent entity (m_loan,
 *    m_client, m_group, …). Visible in the API response because the schema is
 *    flat, but the backend populates the FK from the parent on POST.
 *
 * Discovered at runtime — product 1's INSTITUCION_FINANCIERA datatable surfaces a
 * `loan_id` column with `isColumnPrimaryKey: false`, so the PK filter alone is
 * insufficient. Web-app strips these by name; we do the same.
 */
private val SYSTEM_COLUMNS = setOf(
    "created_at", "updated_at", "createdAt", "updatedAt",
    "loan_id", "client_id", "group_id", "savings_id", "share_id", "office_id",
)

private fun ColumnHeader.isUserEditable(): Boolean {
    val name = dataTableColumnName ?: return false
    return columnPrimaryKey == false && name !in SYSTEM_COLUMNS
}

/**
 * Pick a human label for a column.
 *
 * Codelookup columns are stored as `{columnCode}_cd_{descriptiveName}`. Either
 * half can be the more descriptive one depending on how the Fineract admin set
 * the schema up — and the descriptive half is sometimes truncated by Fineract's
 * own column-length limit. Heuristic: pick whichever side is LONGER.
 *
 * Examples (verified against product 1 + 7 samples):
 *  - `STATE_cd_ESTADO`                                  code=5 suffix=6  → ESTADO
 *  - `YesNo_cd_YA_CUENTA_CON_UN_NEGOCIO`                code=5 suffix=24 → YA_CUENTA_CON_UN_NEGOCIO
 *  - `YesNo_cd_QUIERE_EMPRENDER_UN_NEGOCIO`             code=5 suffix=27 → QUIERE_EMPRENDER_UN_NEGOCIO
 *  - `CREDITO_DE_ALGUNA_INSTITUCION_FINANCIERA_cd_CREDITO_DE_ALGUNA_I`
 *                                                       code=40 suffix=19 → CREDITO_DE_ALGUNA_INSTITUCION_FINANCIERA
 *  - `NOMBRE_RAZON_SOCIAL` (no `_cd_`)                  → NOMBRE_RAZON_SOCIAL
 *
 * History:
 *  - v1 used substringBefore → STATE worked but `YesNo_cd_*` collapsed.
 *  - v2 used substringAfter → YesNo split fine but truncated `CREDITO_…_I`.
 *  - v3 (current) picks the longer of (code, suffix).
 */
private fun ColumnHeader.displayLabel(): String {
    val name = dataTableColumnName ?: return ""
    if ("_cd_" !in name) return name
    val suffix = name.substringAfter("_cd_")
    val code = columnCode.orEmpty()
    return if (code.length > suffix.length) code else suffix
}

@Composable
private fun DatatableField(
    header: ColumnHeader,
    currentValue: Any?,
    onValueChange: (Any) -> Unit,
) {
    val label = header.displayLabel().ifBlank { return }
    when (header.columnDisplayType) {
        "STRING" -> TextFieldRow(
            value = currentValue as? String ?: "",
            onValueChange = onValueChange,
            label = label,
            keyboardType = KeyboardType.Text,
            singleLine = true,
        )

        "TEXT" -> TextFieldRow(
            value = currentValue as? String ?: "",
            onValueChange = onValueChange,
            label = label,
            keyboardType = KeyboardType.Text,
            singleLine = false,
        )

        "INTEGER" -> TextFieldRow(
            value = currentValue as? String ?: "",
            onValueChange = onValueChange,
            label = label,
            keyboardType = KeyboardType.Number,
            singleLine = true,
        )

        "DECIMAL", "FLOAT" -> TextFieldRow(
            value = currentValue as? String ?: "",
            onValueChange = onValueChange,
            label = label,
            keyboardType = KeyboardType.Decimal,
            singleLine = true,
        )

        "DATE" -> DateFieldRow(
            label = label,
            currentValue = currentValue as? String,
            onDateSelected = onValueChange,
        )

        "BOOLEAN" -> BooleanFieldRow(
            label = label,
            currentValue = currentValue as? Boolean ?: false,
            onValueChange = onValueChange,
        )

        "CODELOOKUP", "CODEVALUE" -> DropdownFieldRow(
            label = label,
            header = header,
            // Stored value is the code id (Int); the rendered text is the display string
            // looked up from header.columnValues.
            currentId = currentValue as? Int,
            onIdSelected = onValueChange,
        )

        // DATETIME and unknown types: silently skip (DATETIME is typically the audit
        // created_at/updated_at which we already filter by name above; any unknown
        // type would just render an empty hole anyway).
        else -> Unit
    }
}

@Composable
private fun TextFieldRow(
    value: String,
    onValueChange: (Any) -> Unit,
    label: String,
    keyboardType: KeyboardType,
    singleLine: Boolean,
) {
    MifosOutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        label = label,
        keyboardType = keyboardType,
        singleLine = singleLine,
        maxLines = if (singleLine) 1 else 5,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = KptTheme.spacing.sm),
    )
}

@Composable
private fun DateFieldRow(
    label: String,
    currentValue: String?,
    onDateSelected: (Any) -> Unit,
) {
    var showDatePicker by rememberSaveable(label) { mutableStateOf(false) }
    var selectedMillis by rememberSaveable(label) {
        mutableLongStateOf(Clock.System.now().toEpochMilliseconds())
    }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedMillis)

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    datePickerState.selectedDateMillis?.let { millis ->
                        selectedMillis = millis
                        onDateSelected(DateHelper.getDateAsStringFromLong(millis))
                    }
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    MifosDatePickerTextField(
        value = currentValue ?: DateHelper.getDateAsStringFromLong(selectedMillis),
        label = label,
        openDatePicker = { showDatePicker = true },
    )
    Spacer(modifier = Modifier.height(KptTheme.spacing.sm))
}

@Composable
private fun BooleanFieldRow(
    label: String,
    currentValue: Boolean,
    onValueChange: (Any) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = KptTheme.spacing.sm),
    ) {
        Text(text = label, modifier = Modifier.weight(1f))
        Switch(
            checked = currentValue,
            onCheckedChange = { onValueChange(it) },
        )
    }
}

@Composable
private fun DropdownFieldRow(
    label: String,
    header: ColumnHeader,
    currentId: Int?,
    onIdSelected: (Any) -> Unit,
) {
    val options = header.columnValues.mapNotNull { it.value }
    val ids = header.columnValues.map { it.id }
    val currentDisplay = remember(currentId, options, ids) {
        currentId?.let { id ->
            val pos = ids.indexOf(id)
            if (pos >= 0) options.getOrNull(pos) else null
        }.orEmpty()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = KptTheme.spacing.sm),
    ) {
        MifosTextFieldDropdown(
            value = currentDisplay,
            onValueChanged = { /* typed input ignored — readOnly */ },
            label = label,
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            options = options,
            onOptionSelected = { index, _ ->
                ids.getOrNull(index)?.let { onIdSelected(it) }
            },
        )
    }
}
