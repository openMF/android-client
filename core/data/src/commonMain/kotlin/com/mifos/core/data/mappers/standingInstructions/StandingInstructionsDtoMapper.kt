package com.mifos.core.data.mappers.standingInstructions

import com.mifos.core.model.objects.standingInstructions.StandingInstruction
import com.mifos.core.model.objects.standingInstructions.StandingInstructionAccount
import com.mifos.core.model.objects.standingInstructions.StandingInstructionClient
import com.mifos.core.network.dto.standingInstruction.ClientDto
import com.mifos.core.network.dto.standingInstruction.StandingInstructionAccountDto
import com.mifos.core.network.dto.standingInstruction.StandingInstructionDto

fun StandingInstructionDto.toDomain(): StandingInstruction {
    return StandingInstruction(
        id = this.id,
        amount = this.amount,
        validFrom = this.validFrom,
        fromClient = this.fromClient?.toDomain(),
        toClient = this.toClient?.toDomain(),
        fromAccount = this.fromAccount?.toDomain(),
        toAccount = this.toAccount?.toDomain()
    )
}

fun ClientDto.toDomain(): StandingInstructionClient {
    return StandingInstructionClient(
        id = this.id,
        displayName = this.displayName
    )
}

fun StandingInstructionAccountDto.toDomain(): StandingInstructionAccount {
    return StandingInstructionAccount(
        id = this.id,
        accountNo = this.accountNo,
        productName = this.productName
    )
}
