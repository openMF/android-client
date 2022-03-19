package com.mifos.api.mappers.survey

import com.mifos.objects.survey.Scorecard
import com.mifos.objects.survey.ScorecardValues
import org.apache.fineract.client.models.ScorecardData
import org.apache.fineract.client.models.ScorecardValue
import org.mifos.core.data.AbstractMapper

object ScorecardDataMapper: AbstractMapper<ScorecardData, Scorecard>() {
    override fun mapFromEntity(entity: ScorecardData): Scorecard {
        return Scorecard().apply {
            clientId = entity.clientId!!.toInt()
            userId = entity.userId!!.toInt()
            scorecardValues = entity.scorecardValues?.let {
                it.map {
                ScorecardValues().apply {
                    questionId = it.questionId!!.toInt()
                    responseId = it.responseId!!.toInt()
                    value = it.value
                }
            }
            }
        }
    }

    override fun mapToEntity(domainModel: Scorecard): ScorecardData {
        return ScorecardData().apply {
            clientId = domainModel.clientId.toLong()
            userId = domainModel.userId.toLong()
            scorecardValues = domainModel.scorecardValues.map {
                ScorecardValue().apply {
                    questionId = it.questionId.toLong()
                    responseId = it.responseId.toLong()
                    value = it.value
                }
            }
        }
    }
}