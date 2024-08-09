package com.mifos.feature.savings.account

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.Network
import com.mifos.core.data.SavingsPayload
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosDatePickerTextField
import com.mifos.core.designsystem.component.MifosOutlinedTextField
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.component.MifosTextFieldDropdown
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.designsystem.theme.BluePrimaryDark
import com.mifos.core.objects.accounts.savings.FieldOfficerOptions
import com.mifos.core.objects.client.Savings
import com.mifos.core.objects.organisation.ProductSavings
import com.mifos.core.objects.templates.savings.SavingProductsTemplate
import com.mifos.core.objects.zipmodels.SavingProductsAndTemplate
import com.mifos.feature.savings.R
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Created by Pronay Sarker on 14/07/2024 (12:04 AM)
 */

@Composable
fun SavingsAccountScreen(
    navigateBack: () -> Unit
) {
    val viewModel: SavingAccountViewModel = hiltViewModel()
    val uiState by viewModel.savingAccountUiState.collectAsStateWithLifecycle()
    val savingProductsTemplate by viewModel.savingProductsTemplate.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.loadSavingsAccountsAndTemplate()
    }

    SavingsAccountScreen(
        uiState = uiState,
        savingProductsTemplate = savingProductsTemplate,
        navigateBack = navigateBack,
        onRetry = {
            viewModel.loadSavingsAccountsAndTemplate()
        },
        onSavingsProductSelected = {
            viewModel.loadLoanTemplateByProduct(it)
        },
        fetchTemplate = {
            viewModel.loadLoanTemplateByProduct(it)
        },
        clientId = viewModel.clientId,
        groupId = viewModel.groupId,
        isGroupAccount = viewModel.isGroupAccount,
        createSavingsAccount = { savingsPayload ->
            viewModel.createSavingsAccount(savingsPayload)
        }
    )
}

@Composable
fun SavingsAccountScreen(
    uiState: SavingAccountUiState,
    savingProductsTemplate: SavingProductsTemplate,
    onSavingsProductSelected: (Int) -> Unit,
    navigateBack: () -> Unit,
    onRetry: () -> Unit,
    fetchTemplate: (Int) -> Unit,
    isGroupAccount: Boolean,
    clientId: Int,
    groupId: Int,
    createSavingsAccount: (savingsPayload: SavingsPayload) -> Unit,
) {
    val snackBarHostState = remember {
        SnackbarHostState()
    }
    val context = LocalContext.current

    MifosScaffold(
        snackbarHostState = snackBarHostState,
        title = stringResource(id = R.string.feature_savings_create_savings_account),
        icon = MifosIcons.arrowBack,
        onBackPressed = navigateBack
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            when (uiState) {
                is SavingAccountUiState.ShowProgress -> {
                    MifosCircularProgress()
                }

                is SavingAccountUiState.ShowFetchingError -> {
                    MifosSweetError(message = stringResource(id = uiState.message)) {
                        onRetry()
                    }
                }

                is SavingAccountUiState.ShowFetchingErrorString -> {
                    MifosSweetError(
                        message = uiState.message,
                        buttonText = stringResource(id = R.string.feature_savings_go_back),
                        onclick = { onRetry() }
                    )
                }

                is SavingAccountUiState.LoadAllSavings -> {
                    val productSavingsList = uiState.savingsTemplate.getmProductSavings()

                    SavingsAccountContent(
                        clientId = clientId,
                        groupId = groupId,
                        isGroupAccount = isGroupAccount,
                        fieldOfficerOptions = savingProductsTemplate.fieldOfficerOptions
                            ?: emptyList(),
                        savingProductsTemplate = uiState.savingsTemplate,
                        productSavings = productSavingsList,
                        onSavingsProductSelected = onSavingsProductSelected,
                        createSavingsAccount = createSavingsAccount
                    )

                    productSavingsList[0].id?.let { it2 -> fetchTemplate.invoke(it2) }
                }

                is SavingAccountUiState.ShowSavingsAccountCreatedSuccessfully -> {
                    Toast.makeText(
                        context,
                        context.resources.getString(R.string.feature_savings_savings_account_submitted_for_approval),
                        Toast.LENGTH_LONG
                    ).show()

                    navigateBack()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingsAccountContent(
    clientId: Int,
    groupId: Int,
    isGroupAccount: Boolean,
    fieldOfficerOptions: List<FieldOfficerOptions>,
    productSavings: List<ProductSavings>,
    savingProductsTemplate: SavingProductsAndTemplate,
    onSavingsProductSelected: (Int) -> Unit,
    createSavingsAccount: (savingsPayload: SavingsPayload) -> Unit
) {
    var selectedSavingsProduct by rememberSaveable {
        mutableStateOf("")
    }
    var selectedFieldOfficer by rememberSaveable {
        mutableStateOf("")
    }
    var overDraftAllowed by rememberSaveable {
        mutableStateOf(false)
    }
    var enforceMinimumBalance by rememberSaveable {
        mutableStateOf(false)
    }
    var externalId by rememberSaveable {
        mutableStateOf("")
    }
    var maximumOverdraftAmount by rememberSaveable {
        mutableStateOf("")
    }
    var nominalAnnualInterest by rememberSaveable {
        mutableStateOf("")
    }
    var minimumOverdraftAmount by rememberSaveable {
        mutableStateOf("")
    }
    var minimumRequiredBalance by rememberSaveable {
        mutableStateOf("")
    }
    var interestCalculatedUsing by rememberSaveable {
        mutableStateOf("")
    }
    var interestPostingPeriod by rememberSaveable {
        mutableStateOf("")
    }
    var fieldOfficerId by rememberSaveable {
        mutableStateOf(0)
    }
    var selectedSavingsProductID by rememberSaveable {
        mutableStateOf(0)
    }
    var nominalAnnualInterestOverdraft by rememberSaveable {
        mutableStateOf("")
    }

    val context = LocalContext.current
    val density = LocalDensity.current
    val scrollState = rememberScrollState()

    var submittedOnDate by rememberSaveable { mutableLongStateOf(System.currentTimeMillis()) }
    var pickSubmitDate by rememberSaveable { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = submittedOnDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= System.currentTimeMillis()
            }
        }
    )

    if (pickSubmitDate) {
        DatePickerDialog(
            onDismissRequest = {
                pickSubmitDate = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            submittedOnDate = it
                        }
                        pickSubmitDate = false
                    }
                ) { Text(stringResource(id = R.string.feature_savings_select_date)) }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        pickSubmitDate = false
                    }
                ) { Text(stringResource(id = R.string.feature_savings_cancel)) }
            }
        )
        {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        MifosTextFieldDropdown(
            value = selectedSavingsProduct,
            onValueChanged = {
                selectedSavingsProduct = it
            },
            onOptionSelected = { index, value ->
                selectedSavingsProduct = value
                productSavings[index].id?.let {
                    selectedSavingsProductID = it
                    onSavingsProductSelected.invoke(it)
                }

            },
            label = R.string.feature_savings_product,
            options = productSavings.map { it.name.toString() },
            readOnly = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosTextFieldDropdown(
            value = selectedFieldOfficer,
            onValueChanged = {
                selectedFieldOfficer = it
            },
            onOptionSelected = { index, value ->
                selectedFieldOfficer = value
                fieldOfficerOptions[index].id?.let {
                    fieldOfficerId = it
                }
            },
            label = R.string.feature_savings_field_officer,
            options = fieldOfficerOptions.map { it.displayName.toString() },
            readOnly = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = externalId,
            onValueChange = { externalId = it },
            label = stringResource(id = R.string.feature_savings_external_id),
            error = null
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosDatePickerTextField(
            value = SimpleDateFormat(
                "dd MMMM yyyy", Locale.getDefault()
            ).format(submittedOnDate),
            label = R.string.feature_savings_submitted_on
        ) {
            pickSubmitDate = true
        }

        Spacer(modifier = Modifier.height(16.dp))


        MifosOutlinedTextField(
            value = nominalAnnualInterest,
            onValueChange = { nominalAnnualInterest = it },
            label = stringResource(id = R.string.feature_savings_nominal),
            error = null,
            keyboardType = KeyboardType.Number
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = savingProductsTemplate.mSavingProductsTemplate.interestCalculationType?.value.toString(),
            onValueChange = { interestCalculatedUsing = it },
            label = stringResource(id = R.string.feature_savings_interest_calc),
            error = null,
            readOnly = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = savingProductsTemplate.mSavingProductsTemplate.interestCompoundingPeriodType?.value.toString(),
            onValueChange = { },
            label = stringResource(id = R.string.feature_savings_interest_comp),
            error = null,
            readOnly = true,
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = savingProductsTemplate.mSavingProductsTemplate.interestPostingPeriodType?.value.toString(),
            onValueChange = { interestPostingPeriod = it },
            label = stringResource(id = R.string.feature_savings_interest_p_period),
            error = null,
            readOnly = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        MifosOutlinedTextField(
            value = savingProductsTemplate.mSavingProductsTemplate.interestCalculationDaysInYearType?.value.toString(),
            onValueChange = { },
            label = stringResource(id = R.string.feature_savings_days_in_year),
            error = null,
            readOnly = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = enforceMinimumBalance,
                onCheckedChange = { enforceMinimumBalance = !enforceMinimumBalance },
                colors = CheckboxDefaults.colors(
                    checkedColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary
                )
            )

            Text(text = stringResource(id = R.string.feature_savings_min_required_balance))
        }

        AnimatedVisibility(
            visible = enforceMinimumBalance,
            enter = slideInVertically {
                with(density) { -40.dp.roundToPx() }
            } + expandVertically(
                expandFrom = Alignment.Top
            ) + fadeIn(
                initialAlpha = 0.3f
            ),
            exit = slideOutVertically() + shrinkVertically() + fadeOut()
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            MifosOutlinedTextField(
                value = minimumRequiredBalance,
                onValueChange = { minimumRequiredBalance = it },
                label = stringResource(id = R.string.feature_savings_min_required_balance),
                error = null,
                keyboardType = KeyboardType.Number
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = overDraftAllowed,
                onCheckedChange = { overDraftAllowed = !overDraftAllowed },
                colors = CheckboxDefaults.colors(
                    checkedColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary
                )
            )

            Text(text = stringResource(id = R.string.feature_savings_overdraft_allowed))
        }

        AnimatedVisibility(
            visible = overDraftAllowed,
            enter = slideInVertically {
                with(density) { -40.dp.roundToPx() }
            } + expandVertically(
                expandFrom = Alignment.Top
            ) + fadeIn(
                initialAlpha = 0.3f
            ),
            exit = slideOutVertically() + shrinkVertically() + fadeOut()
        ) {
            Column {
                Spacer(modifier = Modifier.height(16.dp))

                MifosOutlinedTextField(
                    value = maximumOverdraftAmount,
                    onValueChange = { maximumOverdraftAmount = it },
                    label = stringResource(id = R.string.feature_savings_maxoverdraft),
                    error = null,
                    keyboardType = KeyboardType.Number
                )

                Spacer(modifier = Modifier.height(16.dp))

                MifosOutlinedTextField(
                    value = nominalAnnualInterestOverdraft,
                    onValueChange = { nominalAnnualInterestOverdraft = it },
                    label = stringResource(id = R.string.feature_savings_nominal_overdraft),
                    error = null,
                    keyboardType = KeyboardType.Number
                )

                Spacer(modifier = Modifier.height(16.dp))

                MifosOutlinedTextField(
                    value = minimumOverdraftAmount,
                    onValueChange = { minimumOverdraftAmount = it },
                    label = stringResource(id = R.string.feature_savings_min_overdraft),
                    error = null,
                    keyboardType = KeyboardType.Number
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(44.dp)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary
            ),
            onClick = {
                if (Network.isOnline(context)) {
                    val savingsPayload = SavingsPayload()

                    savingsPayload.externalId = externalId
                    savingsPayload.locale = "en"
                    savingsPayload.submittedOnDate = SimpleDateFormat(
                        "dd MMMM yyyy", Locale.getDefault()
                    ).format(submittedOnDate)
                    savingsPayload.dateFormat = "dd MMMM yyyy"
                    if (isGroupAccount) {
                        savingsPayload.groupId = groupId
                    } else {
                        savingsPayload.clientId = clientId
                    }
                    savingsPayload.productId = selectedSavingsProductID
                    savingsPayload.fieldOfficerId = fieldOfficerId
                    savingsPayload.nominalAnnualInterestRate = nominalAnnualInterest
                    savingsPayload.allowOverdraft = overDraftAllowed
                    savingsPayload.nominalAnnualInterestRateOverdraft = nominalAnnualInterestOverdraft
                    savingsPayload.overdraftLimit = maximumOverdraftAmount
                    savingsPayload.minOverdraftForInterestCalculation = minimumOverdraftAmount
                    savingsPayload.enforceMinRequiredBalance = enforceMinimumBalance
                    savingsPayload.minRequiredOpeningBalance = minimumRequiredBalance

                    createSavingsAccount.invoke(savingsPayload)
                } else {
                    Toast.makeText(
                        context,
                        context.resources.getString(R.string.feature_savings_error_not_connected_internet),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        ) {
            Text(text = stringResource(id = R.string.feature_savings_submit))
        }
    }
}


class SavingsAccountScreenPreviewProvider : PreviewParameterProvider<SavingAccountUiState> {
    override val values: Sequence<SavingAccountUiState>
        get() = sequenceOf(
            SavingAccountUiState.ShowProgress,
            SavingAccountUiState.LoadAllSavings(SavingProductsAndTemplate(
                mProductSavings = listOf(
                    ProductSavings(
                        id = 0,
                        name = "Product"
                    )
                ),
                mSavingProductsTemplate = SavingProductsTemplate()
            )),
            SavingAccountUiState.ShowFetchingErrorString("Failed to fetch"),
            SavingAccountUiState.ShowSavingsAccountCreatedSuccessfully(Savings()),
            SavingAccountUiState.ShowFetchingError(R.string.feature_savings_failed_to_fetch_savings_template)
        )
}

@Composable
@Preview(showSystemUi = true)
fun PreviewSavingsAccountScreen(
    @PreviewParameter(SavingsAccountScreenPreviewProvider::class) savingAccountUiState: SavingAccountUiState
) {
    SavingsAccountScreen(
        uiState = savingAccountUiState,
        savingProductsTemplate = SavingProductsTemplate(),
        onSavingsProductSelected = { } ,
        navigateBack = { },
        onRetry = { },
        fetchTemplate = { },
        isGroupAccount = true,
        clientId = 0,
        groupId = 0
    ) {

    }
}