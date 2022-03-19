package com.mifos.api.mappers

import com.mifos.objects.client.Role
import org.apache.fineract.client.models.RoleData
import org.mifos.core.data.AbstractMapper

object UserRoleMapper: AbstractMapper<RoleData, Role>() {

    override fun mapFromEntity(entity: RoleData): Role {
        return Role().apply {
            id = entity.id?.toInt()!!
            name = entity.name
            description = "NAN"
        }
    }

    override fun mapToEntity(domainModel: Role): RoleData {
        return RoleData().apply {
            id = domainModel.id.toLong()
            name = domainModel.name
        }
    }
}