package com.mifos.api.mappers

import com.mifos.objects.user.User
import org.apache.fineract.client.models.PostAuthenticationResponse
import org.mifos.core.data.AbstractMapper

object UserMapper: AbstractMapper<PostAuthenticationResponse, User>() {

    override fun mapFromEntity(entity: PostAuthenticationResponse): User {
        return User().apply {
            username = entity.username
            userId = entity.userId?.toInt()!!
            base64EncodedAuthenticationKey = entity.base64EncodedAuthenticationKey
            officeId = entity.officeId?.toInt()!!
            officeName = entity.officeName
            roles = UserRoleMapper.mapFromEntityList(entity.roles!!)
            permissions = entity.permissions
        }
    }

    override fun mapToEntity(domainModel: User): PostAuthenticationResponse {
        return PostAuthenticationResponse().apply {
            username = domainModel.username
            userId = domainModel.userId.toLong()
            base64EncodedAuthenticationKey = domainModel.base64EncodedAuthenticationKey
            officeId = domainModel.officeId.toLong()
            officeName = domainModel.officeName
            roles = UserRoleMapper.mapToEntityList(domainModel.roles)
            permissions = domainModel.permissions
        }
    }

}