@file:OptIn(ExperimentalMaterial3Api::class)

package com.mifos.feature.client.clientDetails.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.HomeWork
import androidx.compose.material.icons.outlined.MobileFriendly
import androidx.compose.material.icons.outlined.Numbers
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mifos.core.designsystem.component.MifosAccountExpendableCard
import com.mifos.core.designsystem.component.MifosClientDetailsText
import com.mifos.core.designsystem.component.MifosMenuDropDownItem
import com.mifos.core.designsystem.theme.Black
import com.mifos.core.designsystem.theme.White
import com.mifos.feature.client.R

@Composable
fun ClientDetailsScreen() {

    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = White),
                navigationIcon = {
                    IconButton(
                        onClick = { },
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBackIosNew,
                            contentDescription = null,
                            tint = Black,
                        )
                    }
                },
                title = {
                    Text(
                        text = "Client",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Medium,
                            fontStyle = FontStyle.Normal,
                            fontFamily = FontFamily(Font(R.font.outfit_medium))
                        ),
                        color = Black,
                        textAlign = TextAlign.Start
                    )
                },
                actions = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
                    }
                    DropdownMenu(
                        modifier = Modifier.background(White),
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        MifosMenuDropDownItem(option = "Add Loan Account") {

                        }
                        MifosMenuDropDownItem(option = "Add Savings Account") {

                        }
                        MifosMenuDropDownItem(option = "Charges") {

                        }
                        MifosMenuDropDownItem(option = "Documents") {

                        }
                        MifosMenuDropDownItem(option = "Identifiers") {

                        }
                        MifosMenuDropDownItem(option = "More client info") {

                        }
                        MifosMenuDropDownItem(option = "Notes") {

                        }
                        MifosMenuDropDownItem(option = "Pinpoint location") {

                        }
                        MifosMenuDropDownItem(option = "Survey") {

                        }
                        MifosMenuDropDownItem(option = "Upload Signature") {

                        }
                    }
                }
            )
        },
        containerColor = White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                AsyncImage(
                    modifier = Modifier
                        .size(75.dp)
                        .clip(RoundedCornerShape(100)),
                    model = R.drawable.ic_launcher,
                    contentDescription = null,
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                modifier = Modifier.padding(16.dp),
                text = "Client-Name",
                style = TextStyle(
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    fontStyle = FontStyle.Normal,
                    fontFamily = FontFamily(Font(R.font.outfit_regular))
                ),
                color = Black,
                textAlign = TextAlign.Start
            )
            Spacer(modifier = Modifier.height(6.dp))
            MifosClientDetailsText(
                icon = Icons.Outlined.Numbers,
                field = "Account Number",
                value = "0000000002"
            )
            MifosClientDetailsText(
                icon = Icons.Outlined.Numbers,
                field = "External Id",
                value = "0000000002"
            )
            MifosClientDetailsText(
                icon = Icons.Outlined.DateRange,
                field = "Activation Date",
                value = "New Group"
            )
            MifosClientDetailsText(
                icon = Icons.Outlined.HomeWork,
                field = "Office",
                value = "New Group"
            )
            MifosClientDetailsText(
                icon = Icons.Outlined.MobileFriendly,
                field = "Mobile Number",
                value = "0000000002"
            )
            MifosClientDetailsText(
                icon = Icons.Outlined.Groups,
                field = "Group",
                value = "New Group"
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                modifier = Modifier.padding(start = 16.dp, bottom = 6.dp),
                text = "Accounts",
                style = TextStyle(
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Medium,
                    fontStyle = FontStyle.Normal,
                    fontFamily = FontFamily(Font(R.font.outfit_medium))
                ),
                color = Black,
                textAlign = TextAlign.Start
            )
            MifosAccountExpendableCard("Loan Account")
            MifosAccountExpendableCard("Savings Account")
        }
    }
}


@Preview
@Composable
private fun ClientDetailsScreenPreview() {
    ClientDetailsScreen()
}