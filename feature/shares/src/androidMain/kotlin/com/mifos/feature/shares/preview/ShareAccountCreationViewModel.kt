package com.mifos.androidclient.features.shares.preview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.androidclient.features.shares.data.models.Charge
import com.mifos.androidclient.features.shares.data.models.ShareAccountData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShareAccountCreationViewModel : ViewModel() {

    private val _shareAccountData = MutableStateFlow<ShareAccountData?>(null)
    val shareAccountData: StateFlow<ShareAccountData?> = _shareAccountData.asStateFlow()

    fun updateShareAccountData(data: ShareAccountData) {
        _shareAccountData.value = data
    }
    
    fun populateWithMockData() {
        val mockData = ShareAccountData(
            productName = "Wallet",
            externalId = "33333",
            submittedDate = "09 June 2025",
            currency = "USD",
            currentPrice = "$10,000",
            totalNumberOfShares = "10",
            defaultSavingsAccount = "Nobi",
            applicationDate = "26-09-2025",
            allowDividends = true,
            minimumActivePeriod = "2 Months",
            lockInPeriod = "6 Weeks",
            charges = listOf(
                Charge("Savings Administration", "Flat", "10-06-2025", "$2"),
                Charge("Account Opening Fee", "Flat", "26-09-2025", "$5")
            )
        )
        _shareAccountData.value = mockData 
    }

    fun submitNewShareAccount() {
        viewModelScope.launch {
        }
    }
}
