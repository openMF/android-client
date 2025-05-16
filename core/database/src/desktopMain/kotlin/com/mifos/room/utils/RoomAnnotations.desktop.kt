package com.mifos.room.utils

/*
 * Copyright 2025 Mifos Initiative
 *
 * This file provides actual implementations for expect annotations to satisfy
 * the compiler for non-Android targets like desktop. These are no-ops.
 */

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Delete
import androidx.room.Upsert
import androidx.room.ColumnInfo

actual typealias Dao = Dao

actual typealias Query = Query

actual typealias Insert = Insert

actual typealias PrimaryKey = PrimaryKey

actual typealias ForeignKey = ForeignKey

actual typealias Index = Index

actual typealias Entity = Entity

actual typealias Delete = Delete

actual typealias ColumnInfo = ColumnInfo

actual typealias Update = Upsert

