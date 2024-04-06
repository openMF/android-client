package com.sparklead.feature.checker_inbox_task.checker_inbox_and_task.di

import com.sparklead.feature.checker_inbox_task.checker_inbox_and_task.data.CheckerInboxTasksRepositoryImp
import com.sparklead.feature.checker_inbox_task.checker_inbox_and_task.domain.repository.CheckerInboxTasksRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CheckerInboxTasksModule {

    @Binds
    abstract fun bindCheckerInboxTasksRepository(impl: CheckerInboxTasksRepositoryImp): CheckerInboxTasksRepository
}