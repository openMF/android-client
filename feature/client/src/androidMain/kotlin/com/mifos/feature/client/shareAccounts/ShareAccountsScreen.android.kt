package com.mifos.feature.client.shareAccounts

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.mifos.core.designsystem.component.MifosCircularProgress
import com.mifos.core.designsystem.component.MifosSweetError
import com.mifos.core.model.objects.account.share.ShareAccounts
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource

@Composable
internal actual fun ShareAccountsScreenContent(
    pagingFlow: Flow<PagingData<ShareAccounts>>,
    onAction: (ShareAccountsAction) -> Unit,
    modifier: Modifier
) {
    val sharePagingList = pagingFlow.collectAsLazyPagingItems()

    when(sharePagingList.loadState.refresh){
        is LoadState.Error -> {
            MifosSweetError(message = "Failed to fetch share accounts") {
                onAction.invoke(ShareAccountsAction.Refresh)
            }
        }

        LoadState.Loading -> MifosCircularProgress()

        is LoadState.NotLoading -> Unit
    }

    LazyColumn {
        items(
            count = sharePagingList.itemCount,
            key = {index -> sharePagingList[index]?.id ?: index}
        ) { index ->
            sharePagingList[index]?.let { account ->

            }
        }
    }
}