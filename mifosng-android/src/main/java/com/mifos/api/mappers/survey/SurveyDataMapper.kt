package com.mifos.api.mappers.survey

import com.mifos.objects.survey.ComponentDatas
import com.mifos.objects.survey.Survey
import org.apache.fineract.client.models.ComponentData
import org.apache.fineract.client.models.SurveyData
import org.mifos.core.data.AbstractMapper

object SurveyDataMapper: AbstractMapper<SurveyData, Survey>() {
    override fun mapFromEntity(entity: SurveyData): Survey {
        return Survey().apply {
            id = entity.id!!.toInt()
            key = entity.key
            name = entity.name
            description = entity.description
            countryCode = entity.countryCode
            questionDatas = entity.questionDatas?.let { QuestionDataMapper.mapFromEntityList(it) }
                ?: listOf()
            componentDatas = entity.componentDatas?.let { mp ->
                mp.map {
                    ComponentDatas().apply {
                        id = it.id!!.toInt()
                        text = it.text
                        key = it.key
                        description = it.description
                        sequenceNo = it.sequenceNo!!
                    }
                }
            }
        }
    }

    override fun mapToEntity(domainModel: Survey): SurveyData {
        return SurveyData().apply {
            id = domainModel.id.toLong()
            key = domainModel.key
            name = domainModel.name
            description = domainModel.description
            countryCode = domainModel.countryCode
            questionDatas = domainModel.questionDatas?.let { QuestionDataMapper.mapToEntityList(it) }
                ?: listOf()
            componentDatas = domainModel.componentDatas?.let { mp ->
                mp.map {
                    ComponentData().apply {
                        id = it.id.toLong()
                        text = it.text
                        key = it.key
                        description = it.description
                        sequenceNo = it.sequenceNo
                    }
                }
            }
        }
    }
}