package com.mifos.core.data.repository

import com.mifos.core.objects.checkerinboxandtasks.CheckerTask
import com.mifos.core.objects.checkerinboxandtasks.RescheduleLoansTask
import rx.Observable


/**
 * Created by Aditya Gupta on 21/03/24.
 */

interface CheckerInboxTasksRepository {

    fun getRescheduleLoansTaskList(): Observable<List<RescheduleLoansTask>>

    fun getCheckerTaskList(
        actionName: String? = null, entityName: String? = null,
        resourceId: Int? = null
    ): Observable<List<CheckerTask>>

}