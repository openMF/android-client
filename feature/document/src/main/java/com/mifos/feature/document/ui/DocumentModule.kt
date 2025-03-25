package com.mifos.feature.document.ui

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import com.mifos.feature.document.documentDialog.DocumentDialogViewModel
import com.mifos.feature.document.documentList.DocumentListViewModel

val DocumentModule = module {
    viewModelOf(::DocumentDialogViewModel)
    viewModelOf(::DocumentListViewModel)
}