package cmp.navigation.components

import cmp.navigation.authenticated.NavigationItem
import kotlinx.collections.immutable.ImmutableList

data class ScaffoldNavigationData(
    val onNavigationClick: (NavigationItem) -> Unit,
    val navigationItems: ImmutableList<NavigationItem>,
    val selectedNavigationItem: NavigationItem?,
    val shouldShowNavigation: Boolean,
)