package com.mifos.core.network.datamanager

import com.mifos.core.data.CenterPayload
import com.mifos.core.databasehelper.DatabaseHelperCenter
import com.mifos.core.datastore.PrefManager
import com.mifos.core.network.BaseApiManager
import com.mifos.core.network.mappers.centers.GetCentersResponseMapper
import com.mifos.core.network.mappers.offices.GetOfficeResponseMapper
import com.mifos.core.objects.accounts.CenterAccounts
import com.mifos.core.objects.client.ActivatePayload
import com.mifos.core.objects.client.Page
import com.mifos.core.objects.group.Center
import com.mifos.core.objects.group.CenterWithAssociations
import com.mifos.core.objects.organisation.Office
import com.mifos.core.objects.response.SaveResponse
import org.apache.fineract.client.models.PostCentersCenterIdRequest
import org.apache.fineract.client.models.PostCentersCenterIdResponse
import rx.Observable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This DataManager is for Managing Center API, In which Request is going to Server
 * and In Response, We are getting Center API Observable Response using Retrofit2.
 * DataManagerCenter saving response in Database and response to Presenter as accordingly.
 * Created by Rajan Maurya on 28/6/16.
 */
@Singleton
class DataManagerCenter @Inject constructor(
    val mBaseApiManager: BaseApiManager,
    private val mDatabaseHelperCenter: DatabaseHelperCenter,
    private val baseApiManager: org.mifos.core.apimanager.BaseApiManager,
    private val prefManager: PrefManager
) {
    /**
     * This Method sending the Request to REST API if UserStatus is 0 and
     * get list of the centers. The response will pass Presenter to show in the view
     *
     *
     * If the offset is zero and UserStatus is 1 then fetch all Center list from Database and show
     * on the view.
     *
     * else if offset is not zero and UserStatus is 1 then return default empty response to
     * presenter
     *
     * @param paged  True Enable the Pagination of the center list REST API
     * @param offset Value give from which position Fetch CentersList
     * @param limit  Maximum Number of centers will come in response
     * @return Centers List page from offset to max Limit
     */
    fun getCenters(paged: Boolean, offset: Int, limit: Int): Observable<Page<Center>> {
        return when (prefManager.userStatus) {
            false -> baseApiManager.getCenterApi()
                .retrieveAll23(
                    null, null, null, null, null, paged,
                    offset, limit, null, null, null, null, null
                ).map(GetCentersResponseMapper::mapFromEntity)

            true -> {
                /**
                 * Return All Centers List from DatabaseHelperCenter only one time.
                 * If offset is zero this means this is first request and
                 * return all centers from DatabaseHelperCenter
                 */
                if (offset == 0) mDatabaseHelperCenter.readAllCenters() else Observable.just(Page())
            }
        }
    }

    /**
     * This method save the single Center in Database.
     *
     * @param center Center
     * @return Center
     */
    fun syncCenterInDatabase(center: Center): Observable<Center> {
        return mDatabaseHelperCenter.saveCenter(center)
    }

    /**
     * This Method Fetching the Center Accounts (Loan, saving, etc Accounts ) from REST API
     * and then Saving all Accounts into the Database and then returns the Center Group Accounts
     *
     * @param centerId Center Id
     * @return CenterAccounts
     */
    fun syncCenterAccounts(centerId: Int): Observable<CenterAccounts> {
        return mBaseApiManager.centerApi.getCenterAccounts(centerId)
            .concatMap { centerAccounts ->
                mDatabaseHelperCenter.saveCenterAccounts(
                    centerAccounts,
                    centerId
                )
            }
    }

    /**
     * Method Fetching CollectionSheet of the Center from :
     * demo.openmf.org/fineract-provider/api/v1/centers/{centerId}
     * ?associations=groupMembers,collectionMeetingCalendar
     *
     * @param id of the center
     * @return Collection Sheet
     */
    fun getCentersGroupAndMeeting(id: Int): Observable<CenterWithAssociations> {
        return mBaseApiManager
            .centerApi
            .getCenterWithGroupMembersAndCollectionMeetingCalendar(id)
    }

    fun createCenter(centerPayload: CenterPayload): Observable<SaveResponse> {
        return when (prefManager.userStatus) {
            false -> mBaseApiManager.centerApi.createCenter(centerPayload)
            true ->
                /**
                 * Save CenterPayload in Database table.
                 */
                mDatabaseHelperCenter.saveCenterPayload(centerPayload)
        }
    }

    /**
     * This Method Fetch the Groups that are attached to the Centers.
     * @param centerId Center Id
     * @return CenterWithAssociations
     */
    fun getCenterWithAssociations(centerId: Int): Observable<CenterWithAssociations> {
        return when (prefManager.userStatus) {
            false -> mBaseApiManager.centerApi.getAllGroupsForCenter(centerId)
            true ->
                /**
                 * Return Groups from DatabaseHelperGroups.
                 */
                mDatabaseHelperCenter.getCenterAssociateGroups(centerId)
        }
    }

    /**
     * This Method Request to the DatabaseHelperCenter and DatabaseHelperCenter Read the All
     * centers from Center_Table and give the response Page of List of Center
     *
     * @return Page of Center List
     */
    val allDatabaseCenters: Observable<Page<Center>>
        get() = mDatabaseHelperCenter.readAllCenters()

    val offices: Observable<List<Office>>
        get() = baseApiManager.getOfficeApi().retrieveOffices(null, null, null)
            .map(GetOfficeResponseMapper::mapFromEntityList)

    /**
     * This method loading the all CenterPayloads from the Database.
     *
     * @return List<CenterPayload>
    </CenterPayload> */
    val allDatabaseCenterPayload: Observable<List<CenterPayload>>
        get() = mDatabaseHelperCenter.readAllCenterPayload()

    /**
     * This method will called when user is syncing the Database center.
     * whenever a center is synced then request goes to Database to delete that center form
     * Database and reload the list from Database and update the list in UI
     *
     * @param id of the centerPayload in Database
     * @return List<CenterPayload></CenterPayload>>
     */
    fun deleteAndUpdateCenterPayloads(id: Int): Observable<List<CenterPayload>> {
        return mDatabaseHelperCenter.deleteAndUpdateCenterPayloads(id)
    }

    /**
     * This Method updating the CenterPayload in Database and return the same CenterPayload
     *
     * @param centerPayload CenterPayload
     * @return CenterPayload
     */
    fun updateCenterPayload(centerPayload: CenterPayload): Observable<CenterPayload> {
        return mDatabaseHelperCenter.updateDatabaseCenterPayload(centerPayload)
    }

    /**
     * This method is activating the center
     *
     * @param centerId
     * @return GenericResponse
     */
    fun activateCenter(
        centerId: Int,
        activatePayload: ActivatePayload?
    ): Observable<PostCentersCenterIdResponse> {
        return baseApiManager.getCenterApi().activate2(
            centerId.toLong(),
            PostCentersCenterIdRequest().apply {
                closureDate = activatePayload?.activationDate
                dateFormat = activatePayload?.dateFormat
                locale = activatePayload?.locale
            }, "activate"
        )
    }
}