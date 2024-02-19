package com.mifos.core.network.mappers.clients

import com.mifos.core.data.model.client.Client
import com.mifos.core.data.model.client.Status
import com.mifos.core.model.ClientDb
import com.mifos.core.model.StatusDb
import org.mifos.core.data.AbstractMapper

object ClientToClientDbMapper : AbstractMapper<Client, ClientDb>() {
    override fun mapFromEntity(entity: Client): ClientDb {
        return ClientDb().apply {
            _id = entity.id
            accountNo = entity.accountNo
            fullname = entity.fullname
            firstname = entity.displayName
            lastname = entity.displayName
            displayName = entity.displayName
            officeId = entity.officeId
            officeName = entity.officeName
            active = entity.active
            status = StatusDb().apply {
                _id = entity.status?.id!!
                code = entity.status?.code
                value = entity.status?.value
            }
        }
    }

    override fun mapToEntity(domainModel: ClientDb): Client {
        return Client().apply {
            id = domainModel._id
            accountNo = domainModel.accountNo
            fullname = domainModel.fullname
            displayName = domainModel.displayName
            officeId = domainModel.officeId
            officeName = domainModel.officeName
            active = domainModel.active
            status = Status().apply {
                id = domainModel.status?._id!!
                code = domainModel.status?.code
                value = domainModel.status?.value
            }
        }
    }
}