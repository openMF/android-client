package com.mifos.api.mappers.groups

import com.mifos.api.utils.toArray
import com.mifos.api.utils.toDate
import com.mifos.api.utils.toIntArray
import com.mifos.objects.Timeline
import org.apache.fineract.client.models.GetGroupsGroupIdTimeline
import org.mifos.core.data.AbstractMapper

object GroupsTimelineMapper: AbstractMapper<GetGroupsGroupIdTimeline, Timeline>() {
    override fun mapFromEntity(entity: GetGroupsGroupIdTimeline): Timeline {
        return Timeline().apply {
            activatedOnDate = entity.activatedOnDate?.toArray()
            activatedByUsername = entity.activatedByUsername
            activatedByFirstname = entity.activatedByFirstname
            activatedByLastname = entity.activatedByLastname
        }
    }

    override fun mapToEntity(domainModel: Timeline): GetGroupsGroupIdTimeline {
        return GetGroupsGroupIdTimeline().apply {
            activatedOnDate = domainModel.activatedOnDate.toDate()
            activatedByUsername = domainModel.activatedByUsername
            activatedByFirstname = domainModel.activatedByFirstname
            activatedByLastname = domainModel.activatedByLastname
        }
    }
}