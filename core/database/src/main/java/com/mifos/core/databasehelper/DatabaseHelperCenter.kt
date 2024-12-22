/*
 * Copyright 2024 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.core.databasehelper

import android.os.AsyncTask
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.mifos.core.data.CenterPayload
import com.mifos.core.objects.accounts.CenterAccounts
import com.mifos.core.objects.accounts.loan.LoanAccount
import com.mifos.core.objects.accounts.savings.SavingsAccount
import com.mifos.core.objects.client.Page
import com.mifos.core.objects.group.Center
import com.mifos.core.objects.group.CenterWithAssociations

import com.mifos.core.objects.response.SaveResponse
import rx.Observable

/**
 * Created by Rajan Maurya on 28/6/16.
 */
@Dao
interface DatabaseHelperCenter{
    /**
     * Saving Centers in Database using DBFlow.
     * save() method save the value reference to primary key if its exist the update if not the
     * insert.
     *
     * @param centerPage
     * @return null
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAllCenters(centerPage: Page<Center>) : Observable<Void>?

    /**
     * Reading All Centers from table of Center and return the CenterList
     *
     * @return List Of Centers
     */
    // TODO Implement Observable Transaction to load Center List
    @Query("SELECT * FROM Center")
    fun readAllCenters(): Observable<Page<Center>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveCenterPayload(centerPayload: CenterPayload) : Observable<SaveResponse>

    @Query("SELECT * FROM CenterPayload")
    fun readAllCenterPayload(): Observable<List<CenterPayload>>

    @Transaction
    fun deleteAndUpdateCenterPayloads(id: Int): Observable<List<CenterPayload>> {
        return Observable.fromCallable {
            deleteCenterPayloads(id)

            getAllCenterPayloads()
        }
    }

    @Query("SELECT * FROM CenterPayload")
    fun getAllCenterPayloads(): List<CenterPayload>

    /**
     * This Method Fetch the Groups that are attached to the Center.
     * @param centerId Center Id
     * @return CenterWithAssociations
     */
    @Query("SELECT * FROM `Group` WHERE centerId = :centerId")
    fun getCenterAssociateGroups(centerId: Int): Observable<CenterWithAssociations>

    /**
     * This Method Saving the Single Center in the Database
     *
     * @param center
     * @return Observable.just(Center)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveCenter(center: Center) : Observable<Center>

    /**
     * This Method for deleting the center payload from the Database according to Id and
     * again fetch the center List from the Database CenterPayload_Table
     * @param id is Id of the Center Payload in which reference center was saved into Database
     * @return List<CenterPayload></CenterPayload>>
     */
    @Query("DELETE FROM CenterPayload WHERE id = :id")
     fun deleteCenterPayloads(id: Int)

    @Update
     fun updateDatabaseCenterPayload(centerPayload: CenterPayload) : Observable<CenterPayload>

    /**
     * This Method  write the CenterAccounts in tho DB. According to Schema Defined in Model
     *
     * @param centerAccounts Model of List of LoanAccount and SavingAccount
     * @param centerId       Center Id
     * @return CenterAccounts
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveCenterAccounts(
        loanAccounts: List<LoanAccount>,
        savingsAccounts: List<SavingsAccount>,
        memberLoanAccounts: List<LoanAccount>
    ) : Observable<CenterAccounts>
}
