package cmp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import cmp.navigation.navigation.RootNavGraph
import com.mifos.core.data.util.NetworkMonitor
import com.mifos.core.designsystem.theme.MifosTheme
import org.koin.compose.koinInject

@Composable
fun ComposeApp(
    modifier: Modifier = Modifier,
    networkMonitor: NetworkMonitor = koinInject(),
) {
    MifosTheme {
        RootNavGraph(
            networkMonitor = networkMonitor,
            navHostController = rememberNavController(),
            modifier = modifier,
        )
    }
}