/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.about

import androidclient.feature.about.generated.resources.Res
import androidclient.feature.about.generated.resources.feature_about_app_version
import androidclient.feature.about.generated.resources.feature_about_ic_source_code
import androidclient.feature.about.generated.resources.feature_about_ic_website
import androidclient.feature.about.generated.resources.feature_about_icon_twitter
import androidclient.feature.about.generated.resources.feature_about_license
import androidclient.feature.about.generated.resources.feature_about_license_sub
import androidclient.feature.about.generated.resources.feature_about_support_github
import androidclient.feature.about.generated.resources.feature_about_support_twitter
import androidclient.feature.about.generated.resources.feature_about_website

val aboutItems = listOf(
    AboutItem(
        icon = null,
        title = Res.string.feature_about_app_version,
        id = AboutItems.APP_VERSION,
    ),
    AboutItem(
        icon = Res.drawable.feature_about_ic_website,
        title = Res.string.feature_about_website,
        id = AboutItems.OFFICIAL_WEBSITE,
    ),
    AboutItem(
        icon = Res.drawable.feature_about_icon_twitter,
        title = Res.string.feature_about_support_twitter,
        id = AboutItems.TWITTER,
    ),
    AboutItem(
        icon = Res.drawable.feature_about_ic_source_code,
        title = Res.string.feature_about_support_github,
        id = AboutItems.SOURCE_CODE,
    ),
    AboutItem(
        icon = null,
        title = Res.string.feature_about_license,
        subtitle = Res.string.feature_about_license_sub,
        id = AboutItems.LICENSE,
    ),
)
