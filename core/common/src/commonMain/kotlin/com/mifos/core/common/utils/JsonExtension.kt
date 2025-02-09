/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package utils


//import core.mifos.core.model.ServerConfig

import com.google.gson.Gson
fun String.asServerConfig(): ServerConfig {
    val jsonString = this.replace("'", "\"") // Ensuring valid JSON
    return Json.decodeFromString(jsonString)
}