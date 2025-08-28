package com.mifos.feature.client.clientGeneral

import androidclient.feature.client.generated.resources.Res
import androidclient.feature.client.generated.resources.client_performance_history_active_loans_count_label
import androidclient.feature.client.generated.resources.client_performance_history_active_savings_label
import androidclient.feature.client.generated.resources.client_performance_history_last_loan_amount_label
import androidclient.feature.client.generated.resources.client_performance_history_loan_cycle_count_label
import androidclient.feature.client.generated.resources.client_performance_history_total_savings_label
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.theme.AppColors
import com.mifos.core.designsystem.theme.DesignToken
import com.mifos.core.designsystem.theme.MifosTheme
import com.mifos.core.designsystem.theme.MifosTypography
import com.mifos.core.ui.components.MifosRowCard
import com.mifos.core.ui.util.TextUtil
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview


@Preview()
@Composable
fun ClientProfileGeneralScaffold(
    modifier: Modifier = Modifier
){
    MifosTheme {
        MifosScaffold(
            modifier.background(Color.White)
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(
                        vertical = DesignToken.padding.extraLarge,
                        horizontal = DesignToken.padding.large,
                    ),
            ) {
                Text(
                    "Performance History",
                    style = MaterialTheme.typography.labelLarge
                )

                Spacer(Modifier.height(DesignToken.spacing.medium))

                PerformanceHistory()

                Spacer(Modifier.height(DesignToken.spacing.largeIncreased))

                Text(
                    "Actions",
                    style = MaterialTheme.typography.labelLarge,
                )

                Spacer(Modifier.height(DesignToken.spacing.small))

                clientProfileGeneralActions.forEach {
                    MifosRowCard(
                        title = stringResource(it.title),
                        imageVector = it.icon,
                        leftValues = listOf(
                            TextUtil(
                                text = stringResource(it.subTitle),
                                style = MifosTypography.bodySmall,
                                color = MaterialTheme.colorScheme.secondary,
                            ),
                        ),
                        rightValues = listOf(
                            TextUtil(
                                text = "12",
                                style = MifosTypography.bodySmall,
                                color = AppColors.customEnable,
                            ),
                        ),
                        modifier = Modifier
                            .padding(vertical = DesignToken.padding.medium)
                            .clickable{

                            },
                    )
                }
            }

        }
    }

}

@Preview()
@Composable
fun PerformanceHistory() {
    Column(
        Modifier.background(color = Color.White)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
                .wrapContentHeight()
                .clip(
                    RoundedCornerShape(12)
                )
                .background(MaterialTheme.colorScheme.primary)
                .padding(DesignToken.padding.largeIncreasedExtra), //24
            contentAlignment = Alignment.Center,
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(
                    DesignToken.spacing.small   //8
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PerformanceHistoryRows(
                    stringResource(Res.string.client_performance_history_loan_cycle_count_label),
                    "24"
                )

                PerformanceHistoryRows(
                    stringResource(Res.string.client_performance_history_active_loans_count_label),
                    "12"
                )

                PerformanceHistoryRows(
                    stringResource(Res.string.client_performance_history_last_loan_amount_label),
                    "$ 24, 000"
                )

                PerformanceHistoryRows(
                    stringResource(Res.string.client_performance_history_active_savings_label),
                    "8"
                )

                PerformanceHistoryRows(
                    stringResource(Res.string.client_performance_history_total_savings_label),
                    "$ 16, 000"
                )
            }
        }
    }
}

@Composable
fun PerformanceHistoryRows(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(
        color = AppColors.customWhite,
        fontStyle = MaterialTheme.typography.labelMedium.fontStyle
    )
){
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(label, style = textStyle)
        Text(value , style = textStyle)
    }
}

