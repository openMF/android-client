package com.mifos.api.datamanager

import com.mifos.api.BaseApiManager
import javax.inject.Singleton
import javax.inject.Inject
import com.mifos.api.local.databasehelper.DatabaseHelperCenter
import org.apache.fineract.client.services.CentersApi
import org.apache.fineract.client.services.OfficesApi
import com.mifos.objects.group.Center
import com.mifos.utils.PrefManager
import com.mifos.objects.accounts.CenterAccounts
import com.mifos.objects.group.CenterWithAssociations
import com.mifos.services.data.CenterPayload
import com.mifos.objects.response.SaveResponse
import com.mifos.objects.client.ActivatePayload
import com.mifos.api.GenericResponse
import com.mifos.api.mappers.centers.GetCentersCenterIdResponseMapper
import com.mifos.api.mappers.centers.GetCentersResponseMapper
import com.mifos.api.mappers.office.GetOfficeResponseMapper
import com.mifos.objects.client.Page
import com.mifos.objects.organisation.Office
import rx.Observable

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
    private val sdkBaseApiManager: org.mifos.core.apimanager.BaseApiManager
) {
    private val centersApi: CentersApi
        get() = sdkBaseApiManager.getCenterApi()
    private val officeApi: OfficesApi
        get() = sdkBaseApiManager.getOfficeApi()

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
        return when (PrefManager.getUserStatus()) {
            0 -> centersApi.retrieveAll23(
                null, null, null,
                null, null, paged,
                offset, limit, null,
                null, null, null, null
            )
                .map { GetCentersResponseMapper.mapFromEntity(it) }
            1 -> {
                /**
                 * Return All Centers List from DatabaseHelperCenter only one time.
                 * If offset is zero this means this is first request and
                 * return all centers from DatabaseHelperCenter
                 */
                if (offset == 0) mDatabaseHelperCenter.readAllCenters() else Observable.just(Page())
            }
            else -> Observable.just(Page())
        }
    }

    /**
     * This method save the single Center in Database.
     *
     * @param center Center
     * @return Center
     */
    fun syncCenterInDatabase(center: Center?): Observable<Center> {
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
        return centersApi.retrieveGroupAccount(centerId.toLong())
            .map<CenterAccounts> { GetCentersCenterIdResponseMapper.mapFromEntity(it) }
            .concatMap { centerAccounts: CenterAccounts? ->
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

    fun createCenter(centerPayload: CenterPayload?): Observable<SaveResponse> {
        return when (PrefManager.getUserStatus()) {
            0 ->                 // todo: activationDate,dateFormat,locale etc. fields missing in PostCentersRequest
                mBaseApiManager.centerApi.createCenter(centerPayload)
            1 ->
                /**
                 * Save CenterPayload in Database table.
                 */
                mDatabaseHelperCenter.saveCenterPayload(centerPayload)
            else -> Observable.just(SaveResponse())
        }
    }

    /**
     * This Method Fetch the Groups that are attached to the Centers.
     * @param centerId Center Id
     * @return CenterWithAssociations
     */
    fun getCenterWithAssociations(centerId: Int): Observable<CenterWithAssociations> {
        return when (PrefManager.getUserStatus()) {
            0 ->                 // todo: centers/{centerId}?association endpoint missing
                mBaseApiManager.centerApi.getAllGroupsForCenter(centerId)
            1 ->
                /**
                 * Return Groups from DatabaseHelperGroups.
                 */
                mDatabaseHelperCenter.getCenterAssociateGroups(centerId)
            else -> Observable.just(CenterWithAssociations())
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
        get() = officeApi.retrieveOffices(null, null, null)
            .map { GetOfficeResponseMapper.mapFromEntityList(it) }

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
    fun updateCenterPayload(centerPayload: CenterPayload?): Observable<CenterPayload> {
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
    ): Observable<GenericResponse> {
        /**
         * todo: miss match body fields
         *
         * In Swagger(not working): {
         * "closureReasonId": 32,
         * "closureDate": "05 May 2014",
         * "locale": "en",
         * "dateFormat": "dd MMMM yyyy"
         * }
         *
         * In Html Doc(working): {
         * "locale": "en",
         * "dateFormat": "dd MMMM yyyy",
         * "activationDate": "01 March 2011"
         * }
         *
         */
        return mBaseApiManager.centerApi.activateCenter(centerId, activatePayload)
    }
}