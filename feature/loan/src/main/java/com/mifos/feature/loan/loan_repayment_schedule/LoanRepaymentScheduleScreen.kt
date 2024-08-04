package com.mifos.feature.loan.loan_repayment_schedule

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.DateHelper
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.objects.accounts.loan.LoanWithAssociations
import com.mifos.core.objects.accounts.loan.Period
import com.mifos.core.objects.accounts.loan.RepaymentSchedule
import com.mifos.feature.loan.R

/**
 * Created by Pronay Sarker on 03/07/2024 (9:18 AM)
 */

@Composable
fun LoanRepaymentScheduleScreen(
    loanId: Int,
    viewModel: LoanRepaymentScheduleViewModel = hiltViewModel(),
    navigateBack: () -> Unit
) {
    val uiState by viewModel.loanRepaymentScheduleUiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.loanId = loanId
        viewModel.loadLoanRepaySchedule()
    }

    LoanRepaymentScheduleScreen(
        uiState = uiState,
        navigateBack = navigateBack,
        onRetry = { viewModel.loadLoanRepaySchedule() }
    )
}

@Composable
fun LoanRepaymentScheduleScreen(
    uiState: LoanRepaymentScheduleUiState,
    navigateBack: () -> Unit,
    onRetry: () -> Unit
) {
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    MifosScaffold(
        title = stringResource(R.string.feature_loan_loan_repayment_schedule),
        snackbarHostState = snackbarHostState,
        icon = MifosIcons.arrowBack,
        onBackPressed = navigateBack
    ) {
        Box(modifier = Modifier.padding(it)) {
            when (uiState) {
                is LoanRepaymentScheduleUiState.ShowFetchingError -> {
                    MifosSweetError(
                        message = uiState.message,
                        onclick = onRetry
                    )
                }

                is LoanRepaymentScheduleUiState.ShowLoanRepaySchedule -> {
                    LoanRepaymentScheduleContent(
                        uiState.loanWithAssociations.repaymentSchedule.getlistOfActualPeriods()
                    )
                }

                LoanRepaymentScheduleUiState.ShowProgressbar -> {
                    MifosCircularProgress()
                }
            }
        }
    }
}

@Composable
fun LoanRepaymentScheduleContent(
    periods: List<Period>
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        HeaderLoanRepaymentSchedule()

        Box {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(periods) { period ->
                    LoanRepaymentRowItem(
                        color = when {
                            period.complete != null && period.complete!! -> {
                                Color.Green
                            }

                            period.totalOverdue != null && period.totalOverdue!! > 0 -> {
                                Color.Red
                            }

                            else -> Color.Blue.copy(alpha = .7f)

                        },
                        date = period.dueDate?.let { DateHelper.getDateAsString(it) },
                        amountDue = period.totalDueForPeriod.toString(),
                        amountPaid = period.totalPaidForPeriod.toString()
                    )
                }
            }

            BottomBarLoanRepaymentSchedule(
                totalPaid = RepaymentSchedule.getNumberOfRepaymentsComplete(periods).toString(),
                totalOverdue = RepaymentSchedule.getNumberOfRepaymentsOverDue(periods).toString(),
                tvTotalUpcoming = RepaymentSchedule.getNumberOfRepaymentsPending(periods)
                    .toString(),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .background(color = Color.LightGray)
            )
        }
    }
}


@Composable
fun LoanRepaymentRowItem(
    color: Color,
    date: String?,
    amountDue: String,
    amountPaid: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Canvas(modifier = Modifier
                .size(20.dp)
                .padding(2.dp), onDraw = {
                drawRect(
                    color = color
                )
            })

            Text(
                modifier = Modifier.weight(3f),
                text = date ?: "",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                textAlign = TextAlign.End
            )

            Text(
                modifier = Modifier.weight(3f),
                text = amountDue,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                textAlign = TextAlign.End
            )

            Text(
                modifier = Modifier.weight(3f),
                text = amountPaid,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                textAlign = TextAlign.End
            )
        }

        HorizontalDivider()
    }
}

@Composable
fun HeaderLoanRepaymentSchedule() {
    Box(
        modifier = Modifier
            .background(Color.Red.copy(alpha = .5f))
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.weight(2f),
                text = stringResource(id = R.string.feature_loan_status),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                textAlign = TextAlign.Start
            )

            Text(
                modifier = Modifier.weight(2f),
                text = stringResource(id = R.string.feature_loan_date),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Text(
                modifier = Modifier.weight(3f),
                text = stringResource(id = R.string.feature_loan_loan_amount_due),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Text(
                modifier = Modifier.weight(3f),
                text = stringResource(id = R.string.feature_loan_amount_paid),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
fun BottomBarLoanRepaymentSchedule(
    totalPaid: String,
    totalOverdue: String,
    tvTotalUpcoming: String,
    modifier: Modifier
) {
    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.weight(3.4f),
                text = stringResource(id = R.string.feature_loan_complete) + " : " + totalPaid,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Start
            )

            Text(
                modifier = Modifier.weight(3.3f),
                text = stringResource(id = R.string.feature_loan_pending) + " : " + tvTotalUpcoming,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Text(
                modifier = Modifier.weight(3.3f),
                text = stringResource(id = R.string.feature_loan_overdue) + " : " + totalOverdue,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.End
            )
        }
    }
}

class LoanRepaymentSchedulePreviewProvider :
    PreviewParameterProvider<LoanRepaymentScheduleUiState> {

    val loanWithAssociations = LoanWithAssociations(
        repaymentSchedule = RepaymentSchedule(
            periods = listOf(
                Period(
                    complete = true,
                    totalDueForPeriod = 123.232,
                    totalPaidForPeriod = 34343.3434,
                    dueDate = listOf(2024, 6, 1)
                ),
                Period(
                    complete = true,
                    totalDueForPeriod = 123.232,
                    totalPaidForPeriod = 34343.3434,
                    dueDate = listOf(2024, 6, 1)
                ),
                Period(
                    complete = true,
                    totalDueForPeriod = 123.232,
                    totalPaidForPeriod = 34343.3434,
                    dueDate = listOf(2024, 6, 1)
                ),
                Period(
                    complete = true,
                    totalDueForPeriod = 123.232,
                    totalPaidForPeriod = 34343.3434,
                    dueDate = listOf(2024, 6, 1)
                ),
                Period(
                    complete = true,
                    totalDueForPeriod = 123.232,
                    totalPaidForPeriod = 34343.3434,
                    dueDate = listOf(2024, 6, 1)
                )
            )
        )
    )

    override val values: Sequence<LoanRepaymentScheduleUiState>
        get() = sequenceOf(
            LoanRepaymentScheduleUiState.ShowFetchingError("Error fetching loan repayment schedule"),
            LoanRepaymentScheduleUiState.ShowProgressbar,
            LoanRepaymentScheduleUiState.ShowLoanRepaySchedule(loanWithAssociations)
        )
}

@Composable
@Preview(showSystemUi = true)
fun PreviewLoanRepaymentSchedule(
    @PreviewParameter(LoanRepaymentSchedulePreviewProvider::class) loanRepaymentScheduleUiState: LoanRepaymentScheduleUiState
) {
    LoanRepaymentScheduleScreen(
        uiState = loanRepaymentScheduleUiState,
        navigateBack = { },
        onRetry = {}
    )
}