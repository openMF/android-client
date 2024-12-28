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

import com.mifos.core.data.repository.GroupsListRepository
import com.mifos.core.dbobjects.group.Group
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow

const val ERROR_MESSAGE = "Unable to load data from server"

class TestGroupsListRepository : GroupsListRepository {
    private val data = MutableStateFlow<List<Group>?>(sampleGroups)

    override suspend fun getAllGroups(paged: Boolean, offset: Int, limit: Int): List<Group> {
        return if (data.value == null) {
            throw RuntimeException(ERROR_MESSAGE)
        } else {
            data.value!!.getPagedData(offset, limit)
        }
    }

    override fun getAllLocalGroups(): Flow<List<Group>> {
        return emptyFlow()
    }

    fun setGroupsData(list: List<Group>?) {
        data.value = list
    }
}

val sampleGroups = listOf(
    Group(
        id = 1,
        accountNo = "ACC-001",
        name = "Group 1",
    ),
    Group(
        id = 2,
        accountNo = "ACC-002",
        name = "Group 2",
    ),
    Group(
        id = 3,
        accountNo = "ACC-003",
        name = "Group 3",
    ),
    Group(
        id = 4,
        accountNo = "ACC-004",
        name = "Group 4",
    ),
    Group(
        id = 5,
        accountNo = "ACC-005",
        name = "Group 5",
    ),
    Group(
        id = 6,
        accountNo = "ACC-006",
        name = "Group 6",
    ),
    Group(
        id = 7,
        accountNo = "ACC-007",
        name = "Group 7",
    ),
    Group(
        id = 8,
        accountNo = "ACC-008",
        name = "Group 8",
    ),
    Group(
        id = 9,
        accountNo = "ACC-009",
        name = "Group 9",
    ),
    Group(
        id = 10,
        accountNo = "ACC-010",
        name = "Group 10",
    ),
    Group(
        id = 11,
        accountNo = "ACC-011",
        name = "Group 11",
    ),
    Group(
        id = 12,
        accountNo = "ACC-012",
        name = "Group 12",
    ),
    Group(
        id = 13,
        accountNo = "ACC-013",
        name = "Group 13",
    ),
    Group(
        id = 14,
        accountNo = "ACC-014",
        name = "Group 14",
    ),
    Group(
        id = 15,
        accountNo = "ACC-015",
        name = "Group 15",
    ),
    Group(
        id = 16,
        accountNo = "ACC-016",
        name = "Group 16",
    ),
    Group(
        id = 17,
        accountNo = "ACC-017",
        name = "Group 17",
    ),
    Group(
        id = 18,
        accountNo = "ACC-018",
        name = "Group 18",
    ),
    Group(
        id = 19,
        accountNo = "ACC-019",
        name = "Group 19",
    ),
    Group(
        id = 20,
        accountNo = "ACC-020",
        name = "Group 20",
    ),
    Group(
        id = 21,
        accountNo = "ACC-021",
        name = "Group 21",
    ),
    Group(
        id = 22,
        accountNo = "ACC-022",
        name = "Group 22",
    ),
    Group(
        id = 23,
        accountNo = "ACC-023",
        name = "Group 23",
    ),
    Group(
        id = 24,
        accountNo = "ACC-024",
        name = "Group 24",
    ),
    Group(
        id = 25,
        accountNo = "ACC-025",
        name = "Group 25",
    ),
    Group(
        id = 26,
        accountNo = "ACC-026",
        name = "Group 26",
    ),
    Group(
        id = 27,
        accountNo = "ACC-027",
        name = "Group 27",
    ),
    Group(
        id = 28,
        accountNo = "ACC-028",
        name = "Group 28",
    ),
    Group(
        id = 29,
        accountNo = "ACC-029",
        name = "Group 29",
    ),
    Group(
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
