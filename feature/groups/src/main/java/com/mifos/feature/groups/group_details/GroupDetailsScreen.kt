package com.mifos.feature.groups.group_details

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.HomeWork
import androidx.compose.material.icons.outlined.Numbers
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mifos.core.common.utils.Utils
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosMenuDropDownItem
import com.mifos.core.designsystem.component.MifosScaffold
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.designsystem.icon.MifosIcons
import com.mifos.core.designsystem.theme.Black
import com.mifos.core.designsystem.theme.BluePrimary
import com.mifos.core.designsystem.theme.BluePrimaryDark
import com.mifos.core.designsystem.theme.BlueSecondary
import com.mifos.core.designsystem.theme.DarkGray
import com.mifos.core.designsystem.theme.White
import com.mifos.core.objects.accounts.loan.LoanAccount
import com.mifos.core.objects.accounts.savings.DepositType
import com.mifos.core.objects.accounts.savings.SavingsAccount
import com.mifos.core.objects.client.Client
import com.mifos.core.objects.group.Group
import com.mifos.feature.groups.R

@Composable
fun GroupDetailsScreen(
    groupId: Int,
    onBackPressed: () -> Unit,
    addLoanAccount: (Int) -> Unit,
    addSavingsAccount: (Int) -> Unit,
    documents: (Int) -> Unit,
    groupClients: (List<Client>) -> Unit,
    moreGroupInfo: (Int) -> Unit,
    notes: (Int) -> Unit,
    loanAccountSelected: (Int) -> Unit,
    savingsAccountSelected: (Int, DepositType) -> Unit,
    activateGroup: (Int) -> Unit
) {

    val viewModel: GroupDetailsViewModel = hiltViewModel()
    val state by viewModel.groupDetailsUiState.collectAsStateWithLifecycle()
    val loanAccounts by viewModel.loanAccount.collectAsStateWithLifecycle()
    val savingsAccounts by viewModel.savingsAccounts.collectAsStateWithLifecycle()
    val groupAssociateClients by viewModel.groupAssociateClients.collectAsStateWithLifecycle()
    var groupClientEnable by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = groupAssociateClients) {
        if (groupAssociateClients.isNotEmpty() && groupClientEnable) {
            groupClients(groupAssociateClients)
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.getGroupDetails(groupId)
    }

    GroupDetailsScreen(
        groupId = groupId,
        state = state,
        loanAccounts = loanAccounts,
        savingsAccounts = savingsAccounts,
        loanAccountSelected = loanAccountSelected,
        savingsAccountSelected = savingsAccountSelected,
        onBackPressed = onBackPressed,
        onMenuClick = { menu ->
            when (menu) {
                MenuItems.ADD_LOAN_ACCOUNT -> addLoanAccount(groupId)
                MenuItems.ADD_SAVINGS_ACCOUNT -> addSavingsAccount(groupId)
                MenuItems.DOCUMENTS -> documents(groupId)
                MenuItems.GROUP_CLIENTS -> {
                    groupClientEnable = true
                    viewModel.getGroupAssociateClients(groupId)
                }

                MenuItems.MORE_GROUP_INFO -> moreGroupInfo(groupId)
                MenuItems.NOTES -> notes(groupId)
            }
        },
        activateGroup = { activateGroup(groupId) }
    )

}

@Composable
fun GroupDetailsScreen(
    groupId: Int,
    state: GroupDetailsUiState,
    loanAccounts: List<LoanAccount>,
    savingsAccounts: List<SavingsAccount>,
    loanAccountSelected: (Int) -> Unit,
    savingsAccountSelected: (Int, DepositType) -> Unit,
    onBackPressed: () -> Unit,
    onMenuClick: (MenuItems) -> Unit,
    activateGroup: () -> Unit
) {

    val snackbarHostState = remember { SnackbarHostState() }
    var showMenu by remember { mutableStateOf(false) }
    var groupActive by remember { mutableStateOf(true) }

    MifosScaffold(
        icon = MifosIcons.arrowBack,
        title = stringResource(id = R.string.feature_groups_group),
        onBackPressed = onBackPressed,
        actions = {
            IconButton(onClick = { showMenu = showMenu.not() }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
            }
            DropdownMenu(
                modifier = Modifier.background(White),
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                MifosMenuDropDownItem(option = stringResource(id = R.string.feature_groups_add_loan_account)) {
                    onMenuClick(MenuItems.ADD_LOAN_ACCOUNT)
                    showMenu = false
                }
                MifosMenuDropDownItem(option = stringResource(id = R.string.feature_groups_add_savings_account)) {
                    onMenuClick(MenuItems.ADD_SAVINGS_ACCOUNT)
                    showMenu = false
                }
                MifosMenuDropDownItem(option = stringResource(id = R.string.feature_groups_documents)) {
                    onMenuClick(MenuItems.DOCUMENTS)
                    showMenu = false
                }
                MifosMenuDropDownItem(option = stringResource(id = R.string.feature_groups_group_clients)) {
                    onMenuClick(MenuItems.GROUP_CLIENTS)
                    showMenu = false
                }
                MifosMenuDropDownItem(option = stringResource(id = R.string.feature_groups_more_group_info)) {
                    onMenuClick(MenuItems.MORE_GROUP_INFO)
                    showMenu = false
                }
                MifosMenuDropDownItem(option = stringResource(id = R.string.feature_groups_notes)) {
                    onMenuClick(MenuItems.NOTES)
                    showMenu = false
                }
            }
        },
        snackbarHostState = snackbarHostState,
        bottomBar = {
            if (!groupActive) {
                Button(
                    onClick = { activateGroup() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(44.dp)
                        .padding(start = 16.dp, end = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSystemInDarkTheme()) BluePrimaryDark else BluePrimary
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.feature_groups_activate_group),
                        fontSize = 16.sp
                    )
                }

            }
        }
    ) { paddingValue ->
        Column(modifier = Modifier.padding(paddingValue)) {
            when (state) {
                is GroupDetailsUiState.Error -> MifosSweetError(message = stringResource(id = state.message)) {

                }

                is GroupDetailsUiState.Loading -> MifosCircularProgress()

                is GroupDetailsUiState.ShowGroup -> {
                    GroupDetailsContent(
                        group = state.group,
                        loanAccounts = loanAccounts,
                        savingsAccounts = savingsAccounts,
                        loanAccountSelected = loanAccountSelected,
                        savingsAccountSelected = savingsAccountSelected,
                        activateGroup = {
                            groupActive = false
                        }
                    )
                }
            }
        }

    }

}

@Composable
fun GroupDetailsContent(
    group: Group,
    loanAccounts: List<LoanAccount>,
    savingsAccounts: List<SavingsAccount>,
    loanAccountSelected: (Int) -> Unit,
    savingsAccountSelected: (Int, DepositType) -> Unit,
    activateGroup: () -> Unit
) {
    if (group.active == false) {
        activateGroup()
    }
    Column(modifier = Modifier) {
        group.name?.let {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                text = it,
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = Black,
                textAlign = TextAlign.Center
            )
        }
        group.externalId?.let {
            MifosCenterDetailsText(
                icon = Icons.Outlined.Numbers,
                field = stringResource(id = R.string.feature_groups_external_id),
                value = it
            )
        }
        MifosCenterDetailsText(
            icon = Icons.Outlined.DateRange,
            field = stringResource(id = R.string.feature_groups_activation_date),
            value = Utils.getStringOfDate(group.activationDate)
        )
        group.officeName?.let {
            MifosCenterDetailsText(
                icon = Icons.Outlined.HomeWork,
                field = stringResource(id = R.string.feature_groups_office),
                value = it
            )
        }
        group.staffName?.let {
            MifosCenterDetailsText(
                icon = Icons.Outlined.PersonOutline,
                field = stringResource(id = R.string.feature_groups_staff),
                value = it
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (loanAccounts.isNotEmpty() || savingsAccounts.isNotEmpty()) {
            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = stringResource(id = R.string.feature_groups_accounts),
                style = TextStyle(
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Medium,
                    fontStyle = FontStyle.Normal
                ),
                color = Black,
                textAlign = TextAlign.Start
            )
            HorizontalDivider(modifier = Modifier.padding(start = 16.dp, end = 16.dp))
        }
        if (loanAccounts.isNotEmpty()) {
            MifosLoanAccountExpendableCard(
                stringResource(id = R.string.feature_groups_loan_account),
                loanAccounts,
                loanAccountSelected = loanAccountSelected
            )
        }
        if (savingsAccounts.isNotEmpty()) {
            MifosSavingsAccountExpendableCard(
                stringResource(id = R.string.feature_groups_savings_account),
                savingsAccounts,
                savingsAccountSelected = savingsAccountSelected
            )
        }
    }
}

@Composable
fun MifosCenterDetailsText(icon: ImageVector, field: String, value: String) {
    Row(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(18.dp),
            imageVector = icon,
            contentDescription = null,
            tint = DarkGray
        )
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            text = field,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Normal
            ),
            color = Black,
            textAlign = TextAlign.Start
        )
        Text(

            text = value,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                fontStyle = FontStyle.Normal
            ),
            color = DarkGray,
            textAlign = TextAlign.Start
        )
    }
}


@Composable
fun MifosLoanAccountExpendableCard(
    accountType: String,
    loanAccounts: List<LoanAccount>,
    loanAccountSelected: (Int) -> Unit
) {

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
                        fontStyle = FontStyle.Normal
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
                MifosLoanAccountsLazyColumn(loanAccounts, loanAccountSelected)
            }
        }
    }
}


@Composable
fun MifosLoanAccountsLazyColumn(
    loanAccounts: List<LoanAccount>,
    loanAccountSelected: (Int) -> Unit
) {

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
                    modifier = Modifier
                        .padding(5.dp)
                        .clickable(onClick = {
                            loanAccount.id?.let {
                                loanAccountSelected(
                                    it
                                )
                            }
                        }),
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
                                    fontStyle = FontStyle.Normal
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
                                fontStyle = FontStyle.Normal
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
                                fontStyle = FontStyle.Normal
                            ),
                            color = Black,
                            textAlign = TextAlign.Start
                        )
                    }
                }
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun MifosSavingsAccountExpendableCard(
    accountType: String,
    savingsAccount: List<SavingsAccount>,
    savingsAccountSelected: (Int, DepositType) -> Unit
) {

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
                        fontStyle = FontStyle.Normal
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
                MifosSavingsAccountsLazyColumn(savingsAccount, savingsAccountSelected)
            }
        }
    }
}

@Composable
fun MifosSavingsAccountsLazyColumn(
    savingsAccounts: List<SavingsAccount>,
    savingsAccountSelected: (Int, DepositType) -> Unit
) {

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
                    modifier = Modifier
                        .padding(5.dp)
                        .clickable(onClick = {
                            savingsAccount.id?.let {
                                savingsAccount.depositType?.let { it1 ->
                                    savingsAccountSelected(
                                        it, it1
                                    )
                                }
                            }
                        }),
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
                                    fontStyle = FontStyle.Normal
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
                                fontStyle = FontStyle.Normal
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
                                fontStyle = FontStyle.Normal
                            ),
                            color = Black,
                            textAlign = TextAlign.Start
                        )
                    }
                }
                HorizontalDivider()
            }
        }
    }
}

class GroupDetailsUiStateProvider : PreviewParameterProvider<GroupDetailsUiState> {

    override val values: Sequence<GroupDetailsUiState>
        get() = sequenceOf(
            GroupDetailsUiState.Loading,
            GroupDetailsUiState.Error(R.string.feature_groups_failed_to_fetch_group_and_account),
            GroupDetailsUiState.ShowGroup(group = Group(name = "Group", active = true)),
            GroupDetailsUiState.ShowGroup(group = Group(name = "Group", active = false))
        )
}


@Preview
@Composable
private fun GroupDetailsScreenPreview(
    @PreviewParameter(GroupDetailsUiStateProvider::class) state: GroupDetailsUiState
) {
    GroupDetailsScreen(
        groupId = 1,
        state = state,
        onBackPressed = {},
        onMenuClick = {},
        loanAccounts = sampleLoanAccountList,
        savingsAccounts = sampleSavingAccountList,
        loanAccountSelected = {},
        savingsAccountSelected = { _, _ ->
        },
        activateGroup = {}
    )
}

enum class MenuItems {
    ADD_LOAN_ACCOUNT,
    ADD_SAVINGS_ACCOUNT,
    DOCUMENTS,
    GROUP_CLIENTS,
    MORE_GROUP_INFO,
    NOTES
}

val sampleLoanAccountList = List(10) {
    LoanAccount(id = it, productName = "Product $it")
}

val sampleSavingAccountList = List(10) {
    SavingsAccount(id = it, productName = "Product $it")
}