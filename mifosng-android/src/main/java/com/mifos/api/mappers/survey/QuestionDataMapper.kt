package com.mifos.api.mappers.survey

import com.mifos.objects.survey.QuestionDatas
import com.mifos.objects.survey.ResponseDatas
import org.apache.fineract.client.models.QuestionData
import org.apache.fineract.client.models.ResponseData
import org.mifos.core.data.AbstractMapper

object QuestionDataMapper:AbstractMapper<QuestionData, QuestionDatas>() {
    override fun mapFromEntity(entity: QuestionData): QuestionDatas {
        return QuestionDatas().apply {
            id = entity.id!!.toInt()
            componentKey = entity.componentKey
            key = entity.key
            text = entity.text
            description = entity.description
            sequenceNo = entity.sequenceNo!!
            responseDatas = entity.responseDatas!!.map {
                ResponseDatas().apply {
                    id = it.id!!.toInt()
                    text = it.text
                    value = it.value!!
                    sequenceNo = it.sequenceNo!!
                }
            }
        }
    }

    override fun mapToEntity(domainModel: QuestionDatas): QuestionData {
        return QuestionData().apply {
            id = domainModel.id.toLong()
            componentKey = domainModel.componentKey
            key = domainModel.key
            text = domainModel.text
            description = domainModel.description
            sequenceNo = domainModel.sequenceNo
            responseDatas = domainModel.responseDatas.map {
                ResponseData().apply {
                    id = it.questionId.toLong()
                    text = it.text
                    value = it.value
                    sequenceNo = it.sequenceNo
                }
            }
        }
    }
}