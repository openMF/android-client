/*
 * Copyright 2026 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.model.objects.common

import kotlinx.serialization.Serializable

/**
 * Canonical Fineract coded-enum shape: `{ id, code, value }`.
 *
 * Replaces per-domain lookup classes that all carried the same three fields. Use
 * per-domain typealiases (e.g. `typealias DepositType = CodeValue`) when call-site
 * readability benefits from the domain name — they compile to the same type and
 * preserve existing import paths.
 */
@Serializable
data class CodeValue(
    val code: String? = null,
    val id: Int? = null,
    val value: String? = null,
)
