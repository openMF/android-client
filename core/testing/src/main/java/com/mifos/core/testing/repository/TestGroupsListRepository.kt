/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.testing.repository

import com.mifos.core.common.utils.Page
import com.mifos.core.data.repository.GroupsListRepository
import com.mifos.room.entities.group.GroupEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow

const val ERROR_MESSAGE = "Unable to load data from server"

class TestGroupsListRepository : GroupsListRepository {
    private val data = MutableStateFlow<List<GroupEntity>?>(sampleGroups)

    override suspend fun getAllGroups(paged: Boolean, offset: Int, limit: Int): List<GroupEntity> {
        return if (data.value == null) {
            throw RuntimeException(ERROR_MESSAGE)
        } else {
            data.value!!.getPagedData(offset, limit)
        }
    }

    override fun getAllLocalGroups(): Flow<Page<GroupEntity>> {
        return emptyFlow()
    }

    fun setGroupsData(list: List<GroupEntity>?) {
        data.value = list
    }
}

val sampleGroups = listOf(
    GroupEntity(
        id = 1,
        accountNo = "ACC-001",
        name = "Group 1",
    ),
    GroupEntity(
        id = 2,
        accountNo = "ACC-002",
        name = "Group 2",
    ),
    GroupEntity(
        id = 3,
        accountNo = "ACC-003",
        name = "Group 3",
    ),
    GroupEntity(
        id = 4,
        accountNo = "ACC-004",
        name = "Group 4",
    ),
    GroupEntity(
        id = 5,
        accountNo = "ACC-005",
        name = "Group 5",
    ),
    GroupEntity(
        id = 6,
        accountNo = "ACC-006",
        name = "Group 6",
    ),
    GroupEntity(
        id = 7,
        accountNo = "ACC-007",
        name = "Group 7",
    ),
    GroupEntity(
        id = 8,
        accountNo = "ACC-008",
        name = "Group 8",
    ),
    GroupEntity(
        id = 9,
        accountNo = "ACC-009",
        name = "Group 9",
    ),
    GroupEntity(
        id = 10,
        accountNo = "ACC-010",
        name = "Group 10",
    ),
    GroupEntity(
        id = 11,
        accountNo = "ACC-011",
        name = "Group 11",
    ),
    GroupEntity(
        id = 12,
        accountNo = "ACC-012",
        name = "Group 12",
    ),
    GroupEntity(
        id = 13,
        accountNo = "ACC-013",
        name = "Group 13",
    ),
    GroupEntity(
        id = 14,
        accountNo = "ACC-014",
        name = "Group 14",
    ),
    GroupEntity(
        id = 15,
        accountNo = "ACC-015",
        name = "Group 15",
    ),
    GroupEntity(
        id = 16,
        accountNo = "ACC-016",
        name = "Group 16",
    ),
    GroupEntity(
        id = 17,
        accountNo = "ACC-017",
        name = "Group 17",
    ),
    GroupEntity(
        id = 18,
        accountNo = "ACC-018",
        name = "Group 18",
    ),
    GroupEntity(
        id = 19,
        accountNo = "ACC-019",
        name = "Group 19",
    ),
    GroupEntity(
        id = 20,
        accountNo = "ACC-020",
        name = "Group 20",
    ),
    GroupEntity(
        id = 21,
        accountNo = "ACC-021",
        name = "Group 21",
    ),
    GroupEntity(
        id = 22,
        accountNo = "ACC-022",
        name = "Group 22",
    ),
    GroupEntity(
        id = 23,
        accountNo = "ACC-023",
        name = "Group 23",
    ),
    GroupEntity(
        id = 24,
        accountNo = "ACC-024",
        name = "Group 24",
    ),
    GroupEntity(
        id = 25,
        accountNo = "ACC-025",
        name = "Group 25",
    ),
    GroupEntity(
        id = 26,
        accountNo = "ACC-026",
        name = "Group 26",
    ),
    GroupEntity(
        id = 27,
        accountNo = "ACC-027",
        name = "Group 27",
    ),
    GroupEntity(
        id = 28,
        accountNo = "ACC-028",
        name = "Group 28",
    ),
    GroupEntity(
        id = 29,
        accountNo = "ACC-029",
        name = "Group 29",
    ),
    GroupEntity(
        id = 30,
        accountNo = "ACC-030",
        name = "Group 30",
    ),
)

fun <T> List<T>.getPagedData(offset: Int, limit: Int): List<T> {
    println("getPagedData - offset - $offset && limit - $limit")
    // Adjust offset to include first item if needed
    val adjustedOffset = (offset - 1).coerceAtLeast(0)

    if (adjustedOffset >= size) {
        return emptyList()
    }
    val endIndex = (adjustedOffset + limit).coerceAtMost(size)
    return subList(adjustedOffset, endIndex)
}
