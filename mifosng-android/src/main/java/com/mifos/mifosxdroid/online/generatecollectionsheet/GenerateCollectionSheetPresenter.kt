package com.mifos.mifosxdroid.online.generatecollectionsheet

import android.content.Context
import com.mifos.api.DataManager
import com.mifos.api.GenericResponse
import com.mifos.api.datamanager.DataManagerCollectionSheet
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.base.BasePresenter
import com.mifos.mifosxdroid.injection.ActivityContext
import com.mifos.objects.PaymentTypeOption
import com.mifos.objects.collectionsheet.*
import com.mifos.objects.group.Center
import com.mifos.objects.group.CenterWithAssociations
import com.mifos.objects.group.Group
import com.mifos.objects.organisation.Office
import com.mifos.objects.organisation.Staff
import com.mifos.utils.MFErrorParser
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import java.util.*
import javax.inject.Inject

/**
 * Created by Rajan Maurya on 06/06/16.
 */
class GenerateCollectionSheetPresenter @Inject constructor(private val mDataManager: DataManager,
                                                           private val collectionDataManager: DataManagerCollectionSheet,
                                                           @param:ActivityContext private val c: Context) : BasePresenter<GenerateCollectionSheetMvpView?>() {
    private val mSubscription: CompositeSubscription
    override fun attachView(mvpView: GenerateCollectionSheetMvpView?) {
        super.attachView(mvpView)
    }

    override fun detachView() {
        super.detachView()
        mSubscription.clear()
    }

    fun loadOffices() {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscription.add(mDataManager.offices
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<Office?>?>() {
                    override fun onCompleted() {
                        mvpView!!.showProgressbar(false)
                    }

                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showError(MFErrorParser.errorMessage(e))
                    }

                    override fun onNext(offices: List<Office?>?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showOffices(offices)
                    }
                }))
    }

    fun loadStaffInOffice(officeId: Int) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscription.add(mDataManager.getStaffInOffice(officeId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<Staff?>?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showError(MFErrorParser.errorMessage(e))
                    }

                    override fun onNext(staffs: List<Staff?>?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showStaffInOffice(staffs, officeId)
                    }
                }))
    }

    fun loadCentersInOffice(id: Int, params: Map<String?, Any?>?) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscription.add(mDataManager.getCentersInOffice(id, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<Center?>?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showError(MFErrorParser.errorMessage(e))
                    }

                    override fun onNext(centers: List<Center?>?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showCentersInOffice(centers)
                    }
                }))
    }

    fun loadGroupsInOffice(office: Int, params: Map<String?, Any?>?) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscription.add(mDataManager.getGroupsByOffice(office, params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<Group?>?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showError(MFErrorParser.errorMessage(e))
                    }

                    override fun onNext(groups: List<Group?>?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showGroupsInOffice(groups)
                    }
                }))
    }

    fun loadGroupByCenter(centerId: Int) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscription.add(collectionDataManager.fetchGroupsAssociatedWithCenter(centerId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<CenterWithAssociations?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showError(MFErrorParser.errorMessage(e))
                    }

                    override fun onNext(centerWithAssociations: CenterWithAssociations?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showGroupByCenter(centerWithAssociations)
                    }
                }))
    }

    /**
     * Method to hit the endpoint so as to fetch Center Details which are required to retrieve
     * Productive CollectionSheet for that center for some meeting date.
     */
    fun loadCenterDetails(format: String?, locale: String?, meetingDate: String?,
                          officeId: Int, staffId: Int) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscription.add(collectionDataManager.fetchCenterDetails(format, locale,
                meetingDate, officeId, staffId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<List<CenterDetail?>?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showError(MFErrorParser.errorMessage(e))
                    }

                    override fun onNext(centerDetails: List<CenterDetail?>?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.onCenterLoadSuccess(centerDetails)
                    }
                }))
    }

    /**
     * Method to fetch Productive Collection Sheet
     */
    fun loadProductiveCollectionSheet(centerId: Int,
                                      payload: CollectionSheetRequestPayload?) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscription.add(collectionDataManager.fetchProductiveCollectionSheet(centerId, payload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<CollectionSheetResponse?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showError(MFErrorParser.errorMessage(e))
                    }

                    override fun onNext(collectionSheetResponse: CollectionSheetResponse?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showProductive(collectionSheetResponse)
                    }
                }))
    }

    fun loadCollectionSheet(groupId: Int,
                            payload: CollectionSheetRequestPayload?) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscription.add(collectionDataManager.fetchCollectionSheet(groupId, payload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<CollectionSheetResponse?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showError(MFErrorParser.errorMessage(e))
                    }

                    override fun onNext(collectionSheetResponse: CollectionSheetResponse?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showCollection(collectionSheetResponse)
                    }
                }))
    }

    /**
     * Method to submit the ProductiveCollectionSheet
     */
    fun submitProductiveSheet(centerId: Int, payload: ProductiveCollectionSheetPayload?) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscription.add(collectionDataManager.submitProductiveSheet(centerId, payload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<GenericResponse?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showError(MFErrorParser.errorMessage(e))
                    }

                    override fun onNext(genericResponse: GenericResponse?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showError(c.resources.getString(
                                R.string.collectionsheet_submit_success))
                    }
                }))
    }

    /**
     * Method to submit the CollectionSheet
     */
    fun submitCollectionSheet(groupId: Int, payload: CollectionSheetPayload?) {
        checkViewAttached()
        mvpView!!.showProgressbar(true)
        mSubscription.add(collectionDataManager.submitCollectionSheet(groupId, payload)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object : Subscriber<GenericResponse?>() {
                    override fun onCompleted() {}
                    override fun onError(e: Throwable) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showError(MFErrorParser.errorMessage(e))
                    }

                    override fun onNext(genericResponse: GenericResponse?) {
                        mvpView!!.showProgressbar(false)
                        mvpView!!.showError(c.resources.getString(
                                R.string.collectionsheet_submit_success))
                    }
                }))
    }

    /**
     * This method takes extracts the values of the office names and the corresponding ID
     * and returns a HashMap of the same.
     * It also fills the array officeNames with the names of the offices.
     * @param offices List of offices from which the values have to be extracted
     * @param officeNames List of Offices' names
     * @return HashMap of Office name string with the corresponding Id.
     */
    fun createOfficeNameIdMap(offices: List<Office>?,
                              officeNames: MutableList<String?>): HashMap<String, Int> {
        val officeMap = HashMap<String, Int>()
        officeMap[c.resources.getString(R.string.spinner_office)] = -1
        officeNames.clear()
        officeNames.add(c.resources.getString(R.string.spinner_office))
        Observable.from(offices)
                .subscribe { office ->
                    officeMap[office.name] = office.id
                    officeNames.add(office.name)
                }
        return officeMap
    }

    /**
     * This method takes a list of Staff and extracts Staff Name and Staff ID pair
     * in a HashMap.
     * It also fills the staffName array with the staff names
     * @param staffs List of Staff
     * @param staffNames List which is filled with the names of the staff
     * @return HashMap of Staff name string and the corresponding Id.
     */
    fun createStaffIdMap(staffs: List<Staff>?, staffNames: MutableList<String?>): HashMap<String, Int> {
        val staffMap = HashMap<String, Int>()
        staffMap[c.resources.getString(R.string.spinner_staff)] = -1
        staffNames.clear()
        staffNames.add(c.resources.getString(R.string.spinner_staff))
        Observable.from(staffs)
                .subscribe { staff ->
                    staffMap[staff.displayName] = staff.id
                    staffNames.add(staff.displayName)
                }
        return staffMap
    }

    /**
     * This method takes list of centers and extracts te Center Name and Center Id pair
     * in a HashMap.
     * It also fills the centerNames array wit the names of the center.
     * @param centers List of centers
     * @param centerNames List which is filled with the names of the centers.
     * @return HashMap of Center name string and the corresponding Id.
     */
    fun createCenterIdMap(centers: List<Center>?,
                          centerNames: MutableList<String?>): HashMap<String, Int> {
        val centerMap = HashMap<String, Int>()
        centerMap[c.resources.getString(R.string.spinner_center)] = -1
        centerNames.clear()
        centerNames.add(c.resources.getString(R.string.spinner_center))
        Observable.from(centers)
                .subscribe { center ->
                    centerMap[center.name] = center.id
                    centerNames.add(center.name)
                }
        return centerMap
    }

    /**
     * This method takes list of centers and extracts te Center Name and Center Id pair
     * in a HashMap.
     * It also fills the centerNames array wit the names of the center.
     * @param groups List of groups.
     * @param groupNames List which is filled with the names of the groups.
     * @return HashMap of Group name string and the corresponding Id.
     */
    fun createGroupIdMap(groups: List<Group>?, groupNames: MutableList<String?>): HashMap<String, Int> {
        val groupMap = HashMap<String, Int>()
        groupMap[c.resources.getString(R.string.spinner_group)] = -1
        groupNames.clear()
        groupNames.add(c.resources.getString(R.string.spinner_group))
        Observable.from(groups)
                .subscribe { group ->
                    groupMap[group.name] = group.id
                    groupNames.add(group.name)
                }
        return groupMap
    }

    fun filterAttendanceTypes(attendance: List<AttendanceTypeOption>?,
                              attendanceTypeNames: MutableList<String?>): HashMap<String, Int> {
        val options = HashMap<String, Int>()
        Observable.from(attendance)
                .subscribe { attendanceTypeOption ->
                    options[attendanceTypeOption.value] = attendanceTypeOption.id
                    attendanceTypeNames.add(attendanceTypeOption.value)
                }
        return options
    }

    fun filterPaymentTypes(paymentTypeOptions: List<PaymentTypeOption>?,
                           paymentTypeNames: MutableList<String?>): HashMap<String, Int> {
        val paymentMap = HashMap<String, Int>()
        paymentMap[c.resources.getString(R.string.payment_type)] = -1
        paymentTypeNames.clear()
        paymentTypeNames.add(c.resources.getString(R.string.payment_type))
        Observable.from(paymentTypeOptions)
                .subscribe { paymentTypeOption ->
                    paymentMap[paymentTypeOption.name] = paymentTypeOption.id
                    paymentTypeNames.add(paymentTypeOption.name)
                }
        return paymentMap
    }

    init {
        mSubscription = CompositeSubscription()
    }
}