package com.mifos.core.ui.util

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.mifos.core.objects.SearchedEntity
import com.mifos.core.ui.util.SearchResultPreviewData.searchResults

class SearchResultPreviewParameter : PreviewParameterProvider<List<SearchedEntity>> {
    override val values: Sequence<List<SearchedEntity>>
        get() = sequenceOf(searchResults)
}

object SearchResultPreviewData {
    val searchResults = listOf(
        SearchedEntity(
            entityId = 1,
            entityAccountNo = "123456789",
            entityName = "John Doe",
            entityType = "Clients",
            parentId = 0,
            parentName = null,
            entityStatus = null
        ),
        SearchedEntity(
            entityId = 2,
            entityAccountNo = "987654321",
            entityName = "Jane Smith",
            entityType = "Clients",
            parentId = 0,
            parentName = null,
            entityStatus = null
        ),
        SearchedEntity(
            entityId = 3,
            entityAccountNo = "456789123",
            entityName = "Acme Group",
            entityType = "Groups",
            parentId = 0,
            parentName = null,
            entityStatus = null
        ),
        SearchedEntity(
            entityId = 4,
            entityAccountNo = "789123456",
            entityName = "Bob Johnson",
            entityType = "Clients",
            parentId = 3,
            parentName = "Acme Group",
            entityStatus = null
        ),
        SearchedEntity(
            entityId = 5,
            entityAccountNo = "321654987",
            entityName = "Loan Account 1",
            entityType = "Loans",
            parentId = 1,
            parentName = "John Doe",
            entityStatus = null
        ),
        SearchedEntity(
            entityId = 6,
            entityAccountNo = "654987321",
            entityName = "Savings Account 1",
            entityType = "Savings",
            parentId = 2,
            parentName = "Jane Smith",
            entityStatus = null
        ),
        SearchedEntity(
            entityId = 7,
            entityAccountNo = "987321654",
            entityName = "Central Office",
            entityType = "Center",
            parentId = 0,
            parentName = null,
            entityStatus = null
        ),
        SearchedEntity(
            entityId = 8,
            entityAccountNo = "321987654",
            entityName = "Branch 1",
            entityType = "Center",
            parentId = 7,
            parentName = "Central Office",
            entityStatus = null
        ),
        SearchedEntity(
            entityId = 9,
            entityAccountNo = "654321987",
            entityName = "Branch 2",
            entityType = "Center",
            parentId = 7,
            parentName = "Central Office",
            entityStatus = null
        ),
        SearchedEntity(
            entityId = 10,
            entityAccountNo = "987654321",
            entityName = "Loan Account 2",
            entityType = "Loans",
            parentId = 4,
            parentName = "Bob Johnson",
            entityStatus = null
        ),
        SearchedEntity(
            entityId = 11,
            entityAccountNo = "123456789",
            entityName = "Savings Account 2",
            entityType = "Savings",
            parentId = 4,
            parentName = "Bob Johnson",
            entityStatus = null
        ),
        SearchedEntity(
            entityId = 12,
            entityAccountNo = "456789123",
            entityName = "New Client",
            entityType = "Clients",
            parentId = 0,
            parentName = null,
            entityStatus = null
        ),
        SearchedEntity(
            entityId = 13,
            entityAccountNo = "789123456",
            entityName = "Small Group",
            entityType = "Groups",
            parentId = 0,
            parentName = null,
            entityStatus = null
        ),
        SearchedEntity(
            entityId = 14,
            entityAccountNo = "321654987",
            entityName = "Loan Account 3",
            entityType = "Loans",
            parentId = 12,
            parentName = "New Client",
            entityStatus = null
        ),
        SearchedEntity(
            entityId = 15,
            entityAccountNo = "654987321",
            entityName = "Savings Account 3",
            entityType = "Savings",
            parentId = 13,
            parentName = "Small Group",
            entityStatus = null
        )
    )
}