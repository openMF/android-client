/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.room.utils

import kotlin.reflect.KClass

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect annotation class Dao()

@Suppress("NO_ACTUAL_FOR_EXPECT")
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.BINARY)
expect annotation class Query(
    val value: String,
)

@Suppress("NO_ACTUAL_FOR_EXPECT")
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
expect annotation class Update(
    val entity: KClass<*>,
)

@Suppress("NO_ACTUAL_FOR_EXPECT")
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
expect annotation class Delete(
    val entity: KClass<*>,
)

@Suppress("NO_ACTUAL_FOR_EXPECT")
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
expect annotation class Insert(
    val entity: KClass<*>,
    val onConflict: Int,
)

@Suppress("NO_ACTUAL_FOR_EXPECT")
@Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
expect annotation class PrimaryKey(
    val autoGenerate: Boolean,
)

@Suppress("NO_ACTUAL_FOR_EXPECT")
@Target(allowedTargets = []) // Complex annotation target
@Retention(AnnotationRetention.BINARY)
expect annotation class Index

@Suppress("NO_ACTUAL_FOR_EXPECT")
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
expect annotation class Entity(
    val tableName: String,
    val indices: Array<Index>,
    val inheritSuperIndices: Boolean,
    val primaryKeys: Array<String>,
    val foreignKeys: Array<ForeignKey>,
    val ignoredColumns: Array<String>,
)

@Suppress("NO_ACTUAL_FOR_EXPECT")
@Target(allowedTargets = []) // Complex annotation target
@Retention(AnnotationRetention.BINARY)
expect annotation class ForeignKey(
    val entity: KClass<*>,
    val parentColumns: Array<String>,
    val childColumns: Array<String>,
    val onDelete: Int,
    val onUpdate: Int,
    val deferred: Boolean,
)

@Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
expect annotation class ColumnInfo(
    val name: String,
    val typeAffinity: Int,
    val index: Boolean,
    val collate: Int,
    val defaultValue: String,
)

const val INHERIT_FIELD_NAME: String = "[field-name]"
const val UNSPECIFIED: Int = 1
const val UNDEFINED: Int = 1
const val VALUE_UNSPECIFIED: String = "[value-unspecified]"

object ForeignKeyAction {
    const val NO_ACTION = 1
    const val RESTRICT = 2
    const val SET_NULL = 3
    const val SET_DEFAULT = 4
    const val CASCADE = 4
}

object OnConflictStrategy {
    const val NONE = 0
    const val REPLACE = 1
    const val ROLLBACK = 2
    const val ABORT = 3
    const val FAIL = 4
    const val IGNORE = 5
}
