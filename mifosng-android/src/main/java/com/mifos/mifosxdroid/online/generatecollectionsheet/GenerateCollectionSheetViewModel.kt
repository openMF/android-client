package com.mifos.mifosxdroid.online.generatecollectionsheet

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.collectionsheet.AttendanceTypeOption
import com.mifos.core.objects.collectionsheet.CenterDetail
import com.mifos.core.objects.collectionsheet.CollectionSheetPayload
import com.mifos.core.objects.collectionsheet.CollectionSheetRequestPayload
import com.mifos.core.objects.collectionsheet.CollectionSheetResponse
import com.mifos.core.objects.collectionsheet.ProductiveCollectionSheetPayload
import com.mifos.core.objects.group.Center
import com.mifos.core.objects.group.CenterWithAssociations
import com.mifos.core.objects.group.Group
import com.mifos.core.objects.organisation.Office
import com.mifos.core.objects.organisation.Staff
import com.mifos.mifosxdroid.R
import com.mifos.mifosxdroid.online.collectionsheetindividual.NewIndividualCollectionSheetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Aditya Gupta on 12/08/23.
 */
@HiltViewModel
class GenerateCollectionSheetViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: GenerateCollectionSheetRepository,
    private val repositoryNewInd: NewIndividualCollectionSheetRepository,
) : ViewModel() {

    private val _generateCollectionSheetUiState = MutableLiveData<GenerateCollectionSheetUiState>()

    val generateCollectionSheetUiState: LiveData<GenerateCollectionSheetUiState>
        get() = _generateCollectionSheetUiState

    fun loadOffices() {
        _generateCollectionSheetUiState.value = GenerateCollectionSheetUiState.ShowProgressbar
        repositoryNewInd.offices()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<Office>>() {
                override fun onCompleted() {
                }

                override fun onError(e: Throwable) {
                    _generateCollectionSheetUiState.value =
                        GenerateCollectionSheetUiState.ShowError(e.message.toString())
                }

                override fun onNext(offices: List<Office>) {
                    _generateCollectionSheetUiState.value =
                        GenerateCollectionSheetUiState.ShowOffices(offices)
                }
            })
    }

    fun loadStaffInOffice(officeId: Int) {
        _generateCollectionSheetUiState.value = GenerateCollectionSheetUiState.ShowProgressbar
        repositoryNewInd.getStaffInOffice(officeId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<Staff>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _generateCollectionSheetUiState.value =
                        GenerateCollectionSheetUiState.ShowError(e.message.toString())
                }

                override fun onNext(staffs: List<Staff>) {
                    _generateCollectionSheetUiState.value =
                        GenerateCollectionSheetUiState.ShowStaffInOffice(staffs, officeId)
                }
            })

    }

    fun loadCentersInOffice(id: Int, params: Map<String, String>) {
        _generateCollectionSheetUiState.value = GenerateCollectionSheetUiState.ShowProgressbar
        repository.getCentersInOffice(id, params)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<Center>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _generateCollectionSheetUiState.value =
                        GenerateCollectionSheetUiState.ShowError(e.message.toString())
                }

                override fun onNext(centers: List<Center>) {
                    _generateCollectionSheetUiState.value =
                        GenerateCollectionSheetUiState.ShowCentersInOffice(centers)
                }
            })
    }

    fun loadGroupsInOffice(office: Int, params: Map<String, String>) {
        _generateCollectionSheetUiState.value = GenerateCollectionSheetUiState.ShowProgressbar
        repository.getGroupsByOffice(office, params)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<Group>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _generateCollectionSheetUiState.value =
                        GenerateCollectionSheetUiState.ShowError(e.message.toString())
                }

                override fun onNext(groups: List<Group>) {
                    _generateCollectionSheetUiState.value =
                        GenerateCollectionSheetUiState.ShowGroupsInOffice(groups)
                }
            })
    }

    fun loadGroupByCenter(centerId: Int) {
        _generateCollectionSheetUiState.value = GenerateCollectionSheetUiState.ShowProgressbar
        repository.fetchGroupsAssociatedWithCenter(centerId)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<CenterWithAssociations>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _generateCollectionSheetUiState.value =
                        GenerateCollectionSheetUiState.ShowError(e.message.toString())
                }

                override fun onNext(centerWithAssociations: CenterWithAssociations) {
                    _generateCollectionSheetUiState.value =
                        GenerateCollectionSheetUiState.ShowGroupByCenter(centerWithAssociations)
                }
            })

    }

    /**
     * Method to hit the endpoint so as to fetch Center Details which are required to retrieve
     * Productive CollectionSheet for that center for some meeting date.
     */
    fun loadCenterDetails(
        format: String?, locale: String?, meetingDate: String?,
        officeId: Int, staffId: Int
    ) {
        _generateCollectionSheetUiState.value = GenerateCollectionSheetUiState.ShowProgressbar
        repository.fetchCenterDetails(
            format, locale,
            meetingDate, officeId, staffId
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<List<CenterDetail>>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _generateCollectionSheetUiState.value =
                        GenerateCollectionSheetUiState.ShowError(e.message.toString())
                }

                override fun onNext(centerDetails: List<CenterDetail>) {
                    _generateCollectionSheetUiState.value =
                        GenerateCollectionSheetUiState.OnCenterLoadSuccess(centerDetails)
                }
            })

    }

    /**
     * Method to fetch Productive Collection Sheet
     */
    fun loadProductiveCollectionSheet(
        centerId: Int,
        payload: CollectionSheetRequestPayload?
    ) {
        _generateCollectionSheetUiState.value = GenerateCollectionSheetUiState.ShowProgressbar
        repository.fetchProductiveCollectionSheet(centerId, payload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<CollectionSheetResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _generateCollectionSheetUiState.value =
                        GenerateCollectionSheetUiState.ShowError(e.message.toString())
                }

                override fun onNext(collectionSheetResponse: CollectionSheetResponse) {
                    _generateCollectionSheetUiState.value =
                        GenerateCollectionSheetUiState.ShowProductive(collectionSheetResponse)
                }
            })

    }

    fun loadCollectionSheet(
        groupId: Int,
        payload: CollectionSheetRequestPayload?
    ) {
        _generateCollectionSheetUiState.value = GenerateCollectionSheetUiState.ShowProgressbar
        repository.fetchCollectionSheet(groupId, payload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<CollectionSheetResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _generateCollectionSheetUiState.value =
                        GenerateCollectionSheetUiState.ShowError(e.message.toString())
                }

                override fun onNext(collectionSheetResponse: CollectionSheetResponse) {
                    _generateCollectionSheetUiState.value =
                        GenerateCollectionSheetUiState.ShowCollection(collectionSheetResponse)
                }
            })

    }

    /**
     * Method to submit the ProductiveCollectionSheet
     */
    fun submitProductiveSheet(centerId: Int, payload: ProductiveCollectionSheetPayload?) {
        _generateCollectionSheetUiState.value = GenerateCollectionSheetUiState.ShowProgressbar
        repository.submitProductiveSheet(centerId, payload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GenericResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _generateCollectionSheetUiState.value =
                        GenerateCollectionSheetUiState.ShowError(e.message.toString())
                }

                override fun onNext(genericResponse: GenericResponse) {
                    _generateCollectionSheetUiState.value =
                        GenerateCollectionSheetUiState.ShowError(context.resources.getString(R.string.collectionsheet_submit_success))
                }
            })

    }

    /**
     * Method to submit the CollectionSheet
     */
    fun submitCollectionSheet(groupId: Int, payload: CollectionSheetPayload?) {
        _generateCollectionSheetUiState.value = GenerateCollectionSheetUiState.ShowProgressbar
        repository.submitCollectionSheet(groupId, payload)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(object : Subscriber<GenericResponse>() {
                override fun onCompleted() {}
                override fun onError(e: Throwable) {
                    _generateCollectionSheetUiState.value =
                        GenerateCollectionSheetUiState.ShowError(e.message.toString())
                }

                override fun onNext(genericResponse: GenericResponse) {
                    _generateCollectionSheetUiState.value =
                        GenerateCollectionSheetUiState.ShowError(context.resources.getString(R.string.collectionsheet_submit_success))
                }
            })

    }

    /**
     * This method takes extracts the values of the office names and the corresponding ID
     * and returns a HashMap of the same.
     * It also fills the array officeNames with the names of the offices.
     * @param offices List of offices from which the values have to be extracted
     * @param officeNames List of Offices' names
     * @return HashMap of Office name string with the corresponding Id.
     */
    fun createOfficeNameIdMap(
        offices: List<Office>?,
        officeNames: MutableList<String?>
    ): HashMap<String?, Int?> {
        val officeMap = HashMap<String?, Int?>()
        officeMap[context.resources.getString(R.string.spinner_office)] = -1
        officeNames.clear()
        officeNames.add(context.resources.getString(R.string.spinner_office))
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
    fun createStaffIdMap(
        staffs: List<Staff>?,
        staffNames: MutableList<String?>
    ): HashMap<String?, Int?> {
        val staffMap = HashMap<String?, Int?>()
        staffMap[context.resources.getString(R.string.spinner_staff)] = -1
        staffNames.clear()
        staffNames.add(context.resources.getString(R.string.spinner_staff))
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
    fun createCenterIdMap(
        centers: List<Center>?,
        centerNames: MutableList<String?>
    ): HashMap<String?, Int?> {
        val centerMap = HashMap<String?, Int?>()
        centerMap[context.resources.getString(R.string.spinner_center)] = -1
        centerNames.clear()
        centerNames.add(context.resources.getString(R.string.spinner_center))
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
    fun createGroupIdMap(
        groups: List<Group>?,
        groupNames: MutableList<String?>
    ): HashMap<String?, Int?> {
        val groupMap = HashMap<String?, Int?>()
        groupMap[context.resources.getString(R.string.spinner_group)] = -1
        groupNames.clear()
        groupNames.add(context.resources.getString(R.string.spinner_group))
        Observable.from(groups)
            .subscribe { group ->
                groupMap[group.name] = group.id
                groupNames.add(group.name)
            }
        return groupMap
    }

    fun filterAttendanceTypes(
        attendance: List<AttendanceTypeOption>?,
        attendanceTypeNames: MutableList<String?>
    ): HashMap<String?, Int> {
        val options = HashMap<String?, Int>()
        Observable.from(attendance)
            .subscribe { attendanceTypeOption ->
                options[attendanceTypeOption.value] = attendanceTypeOption.id
                attendanceTypeNames.add(attendanceTypeOption.value)
            }
        return options
    }

    fun filterPaymentTypes(
        paymentTypeOptions: List<com.mifos.core.objects.PaymentTypeOption>?,
        paymentTypeNames: MutableList<String?>
    ): HashMap<String, Int> {
        val paymentMap = HashMap<String, Int>()
        paymentMap[context.resources.getString(R.string.payment_type)] = -1
        paymentTypeNames.clear()
        paymentTypeNames.add(context.resources.getString(R.string.payment_type))
        Observable.from(paymentTypeOptions)
            .subscribe { paymentTypeOption ->
                paymentMap[paymentTypeOption.name] = paymentTypeOption.id
                paymentTypeNames.add(paymentTypeOption.name)
            }
        return paymentMap
    }
}