package com.mifos.core.databasehelper

import android.os.AsyncTask
import com.mifos.core.objects.client.Charges
import com.mifos.core.objects.client.Charges_Table
import com.mifos.core.objects.client.ClientDate
import com.mifos.core.objects.client.Page
import com.raizlabs.android.dbflow.sql.language.SQLite
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Rajan Maurya on 4/7/16.
 */
@Singleton
class DatabaseHelperCharge @Inject constructor() {
    /**
     * This Method save the All Client Charges in Database and save the Charge Due date in the
     * ClientDate as reference with Charge Id.
     *
     * @param chargesPage
     * @param clientId
     * @return null
     */
    fun saveClientCharges(
        chargesPage: Page<Charges>,
        clientId: Int
    ): Observable<Void>? {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(Runnable {
            for (charges: Charges in chargesPage.pageItems) {
                charges.clientId = clientId
                val clientDate = charges.id?.toLong()?.let {
                    ClientDate(
                        0, it,
                        charges.dueDate[2],
                        charges.dueDate[1],
                        charges.dueDate[0]
                    )
                }
                charges.chargeDueDate = clientDate
                charges.save()
            }
        })
        return null
    }

    /**
     * This method Retrieve the Charges from Charges_Table and set the Charges Due date after
     * loading the Charge due date from the ChargeDate_table as reference with charge Id.
     *
     * @param clientId Client ID
     * @return Page of Charges
     */
    fun readClientCharges(clientId: Int): Observable<Page<Charges>> {
        return Observable.create<Page<Charges>> { subscriber -> //Loading All charges from Charges_Table as reference to client id
            val chargesList = SQLite.select()
                .from(Charges::class.java)
                .where(Charges_Table.clientId.eq(clientId))
                .queryList()

            //Setting the Charge Due Date
            for (i in chargesList.indices) {
                chargesList[i].dueDate = listOf(
                    chargesList[i].chargeDueDate!!.year,
                    chargesList[i].chargeDueDate!!.month,
                    chargesList[i].chargeDueDate!!.day
                )
            }
            val chargePage = Page<Charges>()
            chargePage.pageItems = chargesList
            subscriber.onNext(chargePage)
        }
    }
}