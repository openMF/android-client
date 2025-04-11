package com.mifos.core.network.data

abstract class AbstractMapper<Entity, DomainModel>: EntityMapper<Entity, DomainModel> {

    override fun mapFromEntityList(entities: List<Entity>): List<DomainModel> {
        return entities.map { mapFromEntity(it) }
    }

    override fun mapToEntityList(domainModels: List<DomainModel>): List<Entity> {
        return domainModels.map { mapToEntity(it) }
    }
}