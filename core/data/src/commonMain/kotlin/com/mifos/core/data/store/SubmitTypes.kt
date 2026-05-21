/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/mifos-x-field-officer-app/blob/master/LICENSE.md
 */
package com.mifos.core.data.store

import kotlinx.coroutines.CoroutineScope

typealias SubmitHandler<R> = template.core.base.store.submit.SubmitHandler<R>
typealias SubmitState<R> = template.core.base.store.submit.SubmitState<R>

fun <R> CoroutineScope.submitHandler(): SubmitHandler<R> =
    template.core.base.store.submit.submitHandler()
