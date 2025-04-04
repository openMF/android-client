/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.domain.useCases

import com.mifos.core.data.repository.GroupsListRepository
import com.mifos.room.entities.group.GroupEntity
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException

actual class GroupsListPagingDataSource(
    private val repository: GroupsListRepository,
    private val limit: Int,
) {
    fun load(currentOffset: Int): List<GroupEntity> {
        return try {
            repository.getAllGroups(paged = true, currentOffset, limit)
        } catch (e: ClientRequestException) {
            emptyList()
        } catch (e: ServerResponseException) {
            emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
