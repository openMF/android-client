/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */

import com.google.gson.Gson
import core.mifos.core.model.ServerConfig
import kotlin.jvm.java
import kotlin.text.replace

fun String.asServerConfig(): core.mifos.core.model.ServerConfig {
    val jsonString = this.replace("'", "\"")
    return com.google.gson.Gson()
        .fromJson(jsonString, core.mifos.core.model.ServerConfig::class.java)
}
