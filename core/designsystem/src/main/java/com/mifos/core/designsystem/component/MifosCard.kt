@file:OptIn(ExperimentalMaterial3Api::class)

package com.mifos.core.designsystem.component

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mifos.core.designsystem.R
import com.mifos.core.designsystem.theme.Black
import com.mifos.core.designsystem.theme.BlueSecondary
import com.mifos.core.designsystem.theme.DarkGray
import com.mifos.core.designsystem.theme.White
import com.mifos.core.objects.accounts.loan.LoanAccount
import com.mifos.core.objects.accounts.savings.DepositType
import com.mifos.core.objects.accounts.savings.SavingsAccount

@Composable
fun MifosLoanAccountExpendableCard(accountType: String, loanAccounts: List<LoanAccount>,loanAccountSelected : (Int)->Unit) {

    var expendableState by remember { mutableStateOf(false) }
    val rotateState by animateFloatAsState(
        targetValue = if (expendableState) 180f else 0f,
        label = ""
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            ),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(BlueSecondary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    text = accountType,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Normal,
                        fontFamily = FontFamily(Font(R.font.outfit_regular))
                    ),
                    color = Black,
                    textAlign = TextAlign.Start
                )
                IconButton(
                    modifier = Modifier
                        .size(24.dp),
                    onClick = { expendableState = !expendableState }) {
                    Icon(
                        modifier = Modifier.rotate(rotateState),
                        imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null
                    )
                }
            }

            if (expendableState) {

                Spacer(modifier = Modifier.height(10.dp))
                MifosLoanAccountsLazyColumn(loanAccounts,loanAccountSelected)
            }
        }
    }
}


@Composable
fun MifosLoanAccountsLazyColumn(loanAccounts: List<LoanAccount>,loanAccountSelected : (Int)->Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(White)
    ) {
        LazyColumn(
            modifier = Modifier
                .height((loanAccounts.size * 52).dp)
                .padding(6.dp)
        ) {
            items(loanAccounts) { loanAccount ->
                Row(
                    modifier = Modifier.padding(5.dp).clickable(onClick = { loanAccount.id?.let {
                        loanAccountSelected(
                            it
                        )
                    } }),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Canvas(modifier = Modifier
                        .size(20.dp)
                        .padding(4.dp), onDraw = {
                        drawCircle(
                            color = when {
                                loanAccount.status?.active == true -> {
                                    Color.Green
                                }

                                loanAccount.status?.waitingForDisbursal == true -> {
                                    Color.Blue
                                }

                                loanAccount.status?.pendingApproval == true -> {
                                    Color.Yellow
                                }

                                loanAccount.status?.active == true && loanAccount.inArrears == true -> {
                                    Color.Red
                                }

                                else -> {
                                    Color.DarkGray
                                }
                            }
                        )
                    })
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp)
                    ) {
                        loanAccount.productName?.let {
                            Text(
                                text = it,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontStyle = FontStyle.Normal,
                                    fontFamily = FontFamily(Font(R.font.outfit_regular))
                                ),
                                color = Black,
                                textAlign = TextAlign.Start
                            )
                        }
                        Text(
                            text = loanAccount.accountNo.toString(),
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                fontStyle = FontStyle.Normal,
                                fontFamily = FontFamily(Font(R.font.outfit_light))
                            ),
                            color = DarkGray,
                            textAlign = TextAlign.Start
                        )
                    }
                    loanAccount.productId?.let {
                        Text(
                            text = it.toString(),
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                fontStyle = FontStyle.Normal,
                                fontFamily = FontFamily(Font(R.font.outfit_regular))
                            ),
                            color = Black,
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun MifosSavingsAccountExpendableCard(accountType: String, savingsAccount: List<SavingsAccount>,savingsAccountSelected : (Int,DepositType)->Unit) {

    var expendableState by remember { mutableStateOf(false) }
    val rotateState by animateFloatAsState(
        targetValue = if (expendableState) 180f else 0f,
        label = ""
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            ),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(BlueSecondary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    text = accountType,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal,
                        fontStyle = FontStyle.Normal,
                        fontFamily = FontFamily(Font(R.font.outfit_regular))
                    ),
                    color = Black,
                    textAlign = TextAlign.Start
                )
                IconButton(
                    modifier = Modifier
                        .size(24.dp),
                    onClick = { expendableState = !expendableState }) {
                    Icon(
                        modifier = Modifier.rotate(rotateState),
                        imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null
                    )
                }
            }

            if (expendableState) {

                Spacer(modifier = Modifier.height(10.dp))
                MifosSavingsAccountsLazyColumn(savingsAccount,savingsAccountSelected)
            }
        }
    }
}


@Composable
fun MifosSavingsAccountsLazyColumn(savingsAccounts: List<SavingsAccount>,savingsAccountSelected : (Int,DepositType)->Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(White)
    ) {
        LazyColumn(
            modifier = Modifier
                .height((savingsAccounts.size * 50).dp)
                .padding(6.dp)
        ) {
            items(savingsAccounts) { savingsAccount ->
                Row(
                    modifier = Modifier.padding(5.dp).clickable(onClick = { savingsAccount.id?.let {
                        savingsAccount.depositType?.let { it1 ->
                            savingsAccountSelected(
                                it, it1
                            )
                        }
                    } }),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Canvas(modifier = Modifier
                        .size(20.dp)
                        .padding(4.dp), onDraw = {
                        drawCircle(
                            color = when {
                                savingsAccount.status?.active == true -> {
                                    Color.Green
                                }

                                savingsAccount.status?.approved == true -> {
                                    Color.Blue
                                }

                                savingsAccount.status?.submittedAndPendingApproval == true -> {
                                    Color.Yellow
                                }

                                else -> {
                                    Color.DarkGray
                                }
                            }
                        )
                    })
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 4.dp)
                    ) {
                        savingsAccount.productName?.let {
                            Text(
                                text = it,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontStyle = FontStyle.Normal,
                                    fontFamily = FontFamily(Font(R.font.outfit_regular))
                                ),
                                color = Black,
                                textAlign = TextAlign.Start
                            )
                        }
                        Text(
                            text = savingsAccount.accountNo.toString(),
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                fontStyle = FontStyle.Normal,
                                fontFamily = FontFamily(Font(R.font.outfit_light))
                            ),
                            color = DarkGray,
                            textAlign = TextAlign.Start
                        )
                    }
                    savingsAccount.productId?.let {
                        Text(
                            text = it.toString(),
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                fontStyle = FontStyle.Normal,
                                fontFamily = FontFamily(Font(R.font.outfit_regular))
                            ),
                            color = Black,
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }
        }
    }
}