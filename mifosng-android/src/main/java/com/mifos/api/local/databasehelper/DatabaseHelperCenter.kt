package com.mifos.api.local.databasehelper

import android.os.AsyncTask
import com.mifos.objects.accounts.CenterAccounts
import com.mifos.objects.client.Page
import com.mifos.objects.group.Center
import com.mifos.objects.group.CenterDate
import com.mifos.objects.group.CenterWithAssociations
import com.mifos.objects.group.Group
import com.mifos.objects.group.Group_Table
import com.mifos.objects.response.SaveResponse
import com.mifos.services.data.CenterPayload
import com.mifos.services.data.CenterPayload_Table
import com.raizlabs.android.dbflow.sql.language.Delete
import com.raizlabs.android.dbflow.sql.language.SQLite
import rx.Observable
import rx.Subscriber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Rajan Maurya on 28/6/16.
 */
@Singleton
class DatabaseHelperCenter @Inject constructor() {
    /**
     * Saving Centers in Database using DBFlow.
     * save() method save the value reference to primary key if its exist the update if not the
     * insert.
     *
     * @param centerPage
     * @return null
     */
    fun saveAllCenters(centerPage: Page<Center>): Observable<Void>? {
        AsyncTask.THREAD_POOL_EXECUTOR.execute {
            for (center in centerPage.pageItems) {
                center.save()
            }
        }
        return null
    }

    /**
     * Reading All Centers from table of Center and return the CenterList
     *
     * @return List Of Centers
     */
    //TODO Implement Observable Transaction to load Center List
    fun readAllCenters(): Observable<Page<Center>> {
        return Observable.create<Page<Center>> { subscriber ->
            val centerPage = Page<Center>()
            centerPage.pageItems = SQLite.select()
                .from(Center::class.java)
                .queryList()
            subscriber.onNext(centerPage)
            subscriber.onCompleted()
        }
    }

    fun saveCenterPayload(centerPayload: CenterPayload): Observable<SaveResponse> {
        return Observable.defer {
            centerPayload.save()
            Observable.just(SaveResponse())
        }
    }

    fun readAllCenterPayload(): Observable<List<CenterPayload>> {
        return Observable.defer {
            val centerPayloads = SQLite.select()
                .from(CenterPayload::class.java)
                .queryList()
            Observable.just(centerPayloads)
        }
    }

    /**
     * This Method Fetch the Groups that are attached to the Center.
     * @param centerId Center Id
     * @return CenterWithAssociations
     */
    fun getCenterAssociateGroups(centerId: Int): Observable<CenterWithAssociations> {
        return Observable.defer {
            val groups = SQLite.select()
                .from(Group::class.java)
                .where(Group_Table.centerId.eq(centerId))
                .queryList()
            val centerWithAssociations = CenterWithAssociations()
            centerWithAssociations.groupMembers = groups
            Observable.just(centerWithAssociations)
        }
    }

    /**
     * This Method Saving the Single Center in the Database
     *
     * @param center
     * @return Observable.just(Center)
     */
    fun saveCenter(center: Center): Observable<Center> {
        return Observable.defer {
            if (center.activationDate.size != 0) {
                val centerDate = CenterDate(
                    center.id.toLong(), 0,
                    center.activationDate[0],
                    center.activationDate[1],
                    center.activationDate[2]
                )
                center.centerDate = centerDate
            }
            center.save()
            Observable.just(center)
        }
    }

    /**
     * This Method for deleting the center payload from the Database according to Id and
     * again fetch the center List from the Database CenterPayload_Table
     * @param id is Id of the Center Payload in which reference center was saved into Database
     * @return List<CenterPayload></CenterPayload>>
     */
    fun deleteAndUpdateCenterPayloads(id: Int): Observable<List<CenterPayload>> {
        return Observable.defer {
            Delete.table(CenterPayload::class.java, CenterPayload_Table.id.eq(id))
            val groupPayloads = SQLite.select()
                .from(CenterPayload::class.java)
                .queryList()
            Observable.just(groupPayloads)
        }
    }

    fun updateDatabaseCenterPayload(
        centerPayload: CenterPayload
    ): Observable<CenterPayload> {
        return Observable.defer {
            centerPayload.update()
            Observable.just(centerPayload)
        }
    }

    /**
     * This Method  write the CenterAccounts in tho DB. According to Schema Defined in Model
     *
     * @param centerAccounts Model of List of LoanAccount and SavingAccount
     * @param centerId       Center Id
     * @return CenterAccounts
     */
    fun saveCenterAccounts(
        centerAccounts: CenterAccounts,
        centerId: Int
    ): Observable<CenterAccounts> {
        return Observable.defer {
            val loanAccounts = centerAccounts.loanAccounts
            val savingsAccounts = centerAccounts.savingsAccounts
            val memberLoanAccounts = centerAccounts.memberLoanAccounts
            for (loanAccount in loanAccounts) {
                loanAccount.centerId = centerId.toLong()
                loanAccount.save()
            }
            for (savingsAccount in savingsAccounts) {
                savingsAccount.centerId = centerId.toLong()
                savingsAccount.save()
            }
            for (memberLoanAccount in memberLoanAccounts) {
                memberLoanAccount.centerId = centerId.toLong()
                memberLoanAccount.save()
            }
            Observable.just(centerAccounts)
        }
    }
}