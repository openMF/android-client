package com.sparklead.feature.checker_inbox_task.checker_inbox_and_task.di

import com.mifos.core.network.datamanager.DataManagerCheckerInbox
import com.sparklead.feature.checker_inbox_task.checker_inbox_and_task.data.CheckerInboxTasksRepositoryImp
import com.sparklead.feature.checker_inbox_task.checker_inbox_and_task.domain.repository.CheckerInboxTasksRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class CheckerInboxTasksModule {

    @Provides
    fun providesCheckerInboxTasksRepository(dataManagerCheckerInbox: DataManagerCheckerInbox): CheckerInboxTasksRepository =
        CheckerInboxTasksRepositoryImp(dataManagerCheckerInbox)

}