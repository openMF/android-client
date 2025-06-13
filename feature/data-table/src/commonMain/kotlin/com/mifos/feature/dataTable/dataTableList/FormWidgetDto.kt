/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
import com.mifos.feature.dataTable.dataTableList.FormSpinnerCompose
import com.mifos.feature.dataTable.dataTableList.FormWidget
import kotlinx.serialization.Serializable

@Serializable
sealed class FormWidgetDTO {
    abstract val propertyName: String
    abstract val displayText: String
    abstract val priority: Int
    abstract val returnType: String
    abstract val value: String?
}

@Serializable
data class FormSpinnerDTO(
    override val propertyName: String,
    override val displayText: String,
    override val priority: Int,
    override val returnType: String,
    override val value: String? = null,
    val columnValues: List<String>,
    val columnValueIds: List<Int>,
) : FormWidgetDTO()

// Extension Functions to Convert Between Widget & DTO
fun FormWidget.toDTO(): FormSpinnerDTO? = when (this) {
    is FormSpinnerCompose -> FormSpinnerDTO(
        propertyName = this.propertyName,
        displayText = this.displayText,
        priority = this.priority,
        returnType = this.returnType,
        value = this.value,
        columnValues = this.spinnerValueIdMap.keys.toList(),
        columnValueIds = this.spinnerValueIdMap.values.toList(),
    )

    else -> null
}
