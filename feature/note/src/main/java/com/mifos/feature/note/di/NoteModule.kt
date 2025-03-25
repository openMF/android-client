package com.mifos.feature.note.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import com.mifos.feature.note.NoteViewModel

val NoteModule = module {
    viewModelOf(::NoteViewModel)
}