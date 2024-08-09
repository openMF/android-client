package com.mifos.mifosxdroid.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.mifos.mifosxdroid.Screens

@Composable
fun MifosNavigationBar(
    route: String,
    onRouteSelected: (targetRoute: String) -> Unit
) {
    val tabs = rememberSaveable {
        listOf(
            Screens.SearchScreen,
            Screens.ClientListScreen,
            Screens.CenterListScreen,
            Screens.GroupListScreen
        )
    }

    NavigationBar {
        tabs.forEach { item ->
            val targetRoute = item.route
            val selected = route.contains(targetRoute)
            NavigationBarItem(
                icon = {
                    item.icon?.let {
                        Icon(
                            imageVector = it,
                            contentDescription = item.title,
                            tint = if (selected) Color.Black else Color.Black.copy(0.7f)
                        )
                    }
                },
                label = {
                    Text(
                        text = item.title,
                        maxLines = 1,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        color = if (selected) Color.Black else Color.Black.copy(0.7f)
                    )
                },
                selected = selected,
                onClick = { onRouteSelected(targetRoute) }
            )
        }
    }
}