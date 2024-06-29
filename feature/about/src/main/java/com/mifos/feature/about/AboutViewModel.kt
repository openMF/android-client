package com.mifos.feature.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mifos.core.designsystem.theme.BlueSecondary
import com.mifos.core.designsystem.theme.White
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor() : ViewModel() {

    private val _aboutUiState = MutableStateFlow<AboutUiState>(AboutUiState.Loading)
    val aboutUiState = _aboutUiState.asStateFlow()

    fun getAboutOptions() = viewModelScope.launch {
        try {
            val options = listOf(
                AboutItem(
                    icon = null,
                    title = R.string.feature_about_app_version,
                    subtitle = null,
                    color = BlueSecondary,
                    id = AboutItems.APP_VERSION
                ),
                AboutItem(
                    icon = R.drawable.feature_about_ic_website,
                    title = R.string.feature_about_website,
                    subtitle = null,
                    color = White,
                    id = AboutItems.OFFICIAL_WEBSITE
                ),
                AboutItem(
                    icon = R.drawable.feature_about_icon_twitter,
                    title = R.string.feature_about_support_twitter,
                    subtitle = null,
                    color = White,
                    id = AboutItems.TWITTER
                ),
                AboutItem(
                    icon = R.drawable.feature_about_ic_source_code,
                    title = R.string.feature_about_support_github,
                    subtitle = null,
                    color = White,
                    id = AboutItems.SOURCE_CODE
                ),
                AboutItem(
                    icon = null,
                    title = R.string.feature_about_license,
                    subtitle = R.string.feature_about_license_sub,
                    color = BlueSecondary,
                    id = AboutItems.LICENSE
                )
            )
            _aboutUiState.value = AboutUiState.AboutOptions(options)
        } catch (exception: Exception) {
            _aboutUiState.value = AboutUiState.Error(R.string.feature_about_failed_to_load)
        }
    }
}

enum class AboutItems {
    CONTRIBUTIONS, APP_VERSION, OFFICIAL_WEBSITE, TWITTER, SOURCE_CODE, LICENSE
}