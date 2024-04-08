package com.mifos.feature.groupsList.di

import com.mifos.feature.groupsList.data.repositoryImp.FakeGroupsListRepository
import com.mifos.feature.groupsList.domain.repository.GroupsListRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [GroupModule::class],
)
internal interface TestGroupModule {
    @Binds
    fun bindsFakeGroupListRepository(
        fakeGroupsListRepository: FakeGroupsListRepository,
    ): GroupsListRepository
}