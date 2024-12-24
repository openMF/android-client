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

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mifos.core.objects.client.Charges
import com.mifos.core.objects.client.Page
import rx.Observable


/**
 * Created by Rajan Maurya on 4/7/16.
 */
@Dao
interface DatabaseHelperCharge {
    /**
     * This Method save the All Client Charges in Database and save the Charge Due date in the
     * ClientDate as reference with Charge Id.
     *
     * @param chargesPage
     * @param clientId
     * @return null
     */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun saveClientCharges(charges: Page<Charges>, clientId: Int) : Observable<Void>?



    /**
     * This method Retrieve the Charges from Charges_Table and set the Charges Due date after
     * loading the Charge due date from the ChargeDate_table as reference with charge Id.
     *
     * @param clientId Client ID
     * @return Page of Charges
     */

    @Query("SELECT * FROM charges WHERE clientId = :clientId")
    fun readClientCharges(clientId: Int): Observable<Page<Charges>>

}
