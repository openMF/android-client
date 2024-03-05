package com.mifos.core.network.datamanager

import com.mifos.core.network.BaseApiManager
import com.mifos.core.network.GenericResponse
import com.mifos.core.objects.checkerinboxandtasks.CheckerInboxSearchTemplate
import com.mifos.core.objects.checkerinboxandtasks.CheckerTask
import com.mifos.core.objects.checkerinboxandtasks.RescheduleLoansTask
import rx.Observable
import javax.inject.Inject


class DataManagerCheckerInbox @Inject constructor(
    private val mBaseApiManager: BaseApiManager
) {


    fun getCheckerTaskList(
        actionName: String? = null, entityName: String? = null,
        resourceId: Int? = null
    ): Observable<List<CheckerTask>> {
        return mBaseApiManager.checkerInboxApi.getCheckerList(
            actionName, entityName, resourceId
        )
    }

    fun approveCheckerEntry(auditId: Int): Observable<GenericResponse> {
        return mBaseApiManager.checkerInboxApi.approveCheckerEntry(auditId)
    }

    fun rejectCheckerEntry(auditId: Int): Observable<GenericResponse> {
        return mBaseApiManager.checkerInboxApi.rejectCheckerEntry(auditId)
    }

    fun deleteCheckerEntry(auditId: Int): Observable<GenericResponse> {
        return mBaseApiManager.checkerInboxApi.deleteCheckerEntry(auditId)
    }

    fun getRechdeduleLoansTaskList(): Observable<List<RescheduleLoansTask>> {
        return mBaseApiManager.checkerInboxApi.getRescheduleLoansTaskList()
    }

    fun getCheckerInboxSearchTemplate(): Observable<CheckerInboxSearchTemplate> {
        return mBaseApiManager.checkerInboxApi.getCheckerInboxSearchTempalate()
    }

    fun getCheckerTaskFromResourceId(
        actionName: String? = null, entityName: String? = null,
        resourceId: Int? = null
    ): Observable<List<CheckerTask>> {
        return mBaseApiManager.checkerInboxApi.getCheckerTasksFromResourceId(
            actionName, entityName, resourceId
        )
    }
}
