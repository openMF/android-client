/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.feature.document.documentList

import com.mifos.core.modelobjects.noncoreobjects.Document

/**
 * Created by Aditya Gupta on 08/08/23.
 */
sealed class DocumentListUiState {

    data object Loading : DocumentListUiState()

    data class Error(val message: Int) : DocumentListUiState()

    data class DocumentList(val documents: List<Document>) : DocumentListUiState()
}
