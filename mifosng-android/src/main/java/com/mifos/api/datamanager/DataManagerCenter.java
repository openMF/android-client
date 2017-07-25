package com.mifos.api.datamanager;

import com.mifos.api.BaseApiManager;
import com.mifos.api.GenericResponse;
import com.mifos.api.local.databasehelper.DatabaseHelperCenter;
import com.mifos.objects.accounts.CenterAccounts;
import com.mifos.objects.client.ActivatePayload;
import com.mifos.objects.client.Page;
import com.mifos.objects.group.Center;
import com.mifos.objects.group.CenterWithAssociations;
import com.mifos.objects.organisation.Office;
import com.mifos.objects.response.SaveResponse;
import com.mifos.services.data.CenterPayload;
import com.mifos.utils.PrefManager;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.functions.Func1;

/**
 * This DataManager is for Managing Center API, In which Request is going to Server
 * and In Response, We are getting Center API Observable Response using Retrofit2.
 * DataManagerCenter saving response in Database and response to Presenter as accordingly.
 * Created by Rajan Maurya on 28/6/16.
 */
@Singleton
public class DataManagerCenter {

    public final BaseApiManager mBaseApiManager;
    public final DatabaseHelperCenter mDatabaseHelperCenter;

    @Inject
    public DataManagerCenter(BaseApiManager baseApiManager,
                             DatabaseHelperCenter databaseHelperCenter) {
        mBaseApiManager = baseApiManager;
        mDatabaseHelperCenter = databaseHelperCenter;
    }


    /**
     * This Method sending the Request to REST API if UserStatus is 0 and
     * get list of the centers. The response will pass Presenter to show in the view
     * <p/>
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
    public Observable<Page<Center>> getCenters(boolean paged, int offset, int limit) {
        switch (PrefManager.getUserStatus()) {
            case 0:
                return mBaseApiManager.getCenterApi().getCenters(paged, offset, limit);

            case 1:
                /**
                 * Return All Centers List from DatabaseHelperCenter only one time.
                 * If offset is zero this means this is first request and
                 * return all centers from DatabaseHelperCenter
                 */
                if (offset == 0)
                    return mDatabaseHelperCenter.readAllCenters();

            default:
                return Observable.just(new Page<Center>());
        }
    }

    /**
     * This method save the single Center in Database.
     *
     * @param center Center
     * @return Center
     */
    public Observable<Center> syncCenterInDatabase(Center center) {
        return mDatabaseHelperCenter.saveCenter(center);
    }

    /**
     * This Method Fetching the Center Accounts (Loan, saving, etc Accounts ) from REST API
     * and then Saving all Accounts into the Database and then returns the Center Group Accounts
     *
     * @param centerId Center Id
     * @return CenterAccounts
     */
    public Observable<CenterAccounts> syncCenterAccounts(final int centerId) {
        return mBaseApiManager.getCenterApi().getCenterAccounts(centerId)
                .concatMap(new Func1<CenterAccounts, Observable<? extends CenterAccounts>>() {
                    @Override
                    public Observable<? extends CenterAccounts> call(CenterAccounts
                                                                             centerAccounts) {
                        return mDatabaseHelperCenter.saveCenterAccounts(centerAccounts, centerId);
                    }
                });
    }

    /**
     * Method Fetching CollectionSheet of the Center from :
     * demo.openmf.org/fineract-provider/api/v1/centers/{centerId}
     * ?associations=groupMembers,collectionMeetingCalendar
     *
     * @param id of the center
     * @return Collection Sheet
     */
    public Observable<CenterWithAssociations> getCentersGroupAndMeeting(int id) {
        return mBaseApiManager
                .getCenterApi()
                .getCenterWithGroupMembersAndCollectionMeetingCalendar(id);
    }

    public Observable<SaveResponse> createCenter(CenterPayload centerPayload) {
        switch (PrefManager.getUserStatus()) {
            case 0:
                return mBaseApiManager.getCenterApi().createCenter(centerPayload);
            case 1:
                /**
                 * Save CenterPayload in Database table.
                 */
                return mDatabaseHelperCenter.saveCenterPayload(centerPayload);

            default:
                return Observable.just(new SaveResponse());
        }
    }

    /**
     * This Method Fetch the Groups that are attached to the Centers.
     * @param centerId Center Id
     * @return CenterWithAssociations
     */
    public Observable<CenterWithAssociations> getCenterWithAssociations(int centerId) {
        switch (PrefManager.getUserStatus()) {
            case 0:
                return mBaseApiManager.getCenterApi().getAllGroupsForCenter(centerId);
            case 1:
                /**
                 * Return Groups from DatabaseHelperGroups.
                 */
                return mDatabaseHelperCenter.getCenterAssociateGroups(centerId);

            default:
                return Observable.just(new CenterWithAssociations());
        }
    }

    /**
     * This Method Request to the DatabaseHelperCenter and DatabaseHelperCenter Read the All
     * centers from Center_Table and give the response Page of List of Center
     *
     * @return Page of Center List
     */
    public Observable<Page<Center>> getAllDatabaseCenters() {
        return mDatabaseHelperCenter.readAllCenters();
    }

    public Observable<List<Office>> getOffices() {
        return mBaseApiManager.getOfficeApi().getAllOffices();
    }

    /**
     * This method loading the all CenterPayloads from the Database.
     *
     * @return List<CenterPayload>
     */
    public Observable<List<CenterPayload>> getAllDatabaseCenterPayload() {
        return mDatabaseHelperCenter.readAllCenterPayload();
    }

    /**
     * This method will called when user is syncing the Database center.
     * whenever a center is synced then request goes to Database to delete that center form
     * Database and reload the list from Database and update the list in UI
     *
     * @param id of the centerPayload in Database
     * @return List<CenterPayload></>
     */
    public Observable<List<CenterPayload>> deleteAndUpdateCenterPayloads(int id) {
        return mDatabaseHelperCenter.deleteAndUpdateCenterPayloads(id);
    }

    /**
     * This Method updating the CenterPayload in Database and return the same CenterPayload
     *
     * @param centerPayload CenterPayload
     * @return CenterPayload
     */
    public Observable<CenterPayload> updateCenterPayload(CenterPayload centerPayload) {
        return mDatabaseHelperCenter.updateDatabaseCenterPayload(centerPayload);
    }

    /**
     * This method is activating the center
     *
     * @param centerId
     * @return GenericResponse
     */
    public Observable<GenericResponse> activateCenter(int centerId,
                                                      ActivatePayload activatePayload) {
        return mBaseApiManager.getCenterApi().activateCenter(centerId, activatePayload);
    }
}
