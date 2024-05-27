package com.mifos.core.objects.noncore

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Created by Rajan Maurya on 01/10/16.
 */
@Parcelize
class IdentifierTemplate(
    var allowedDocumentTypes: List<DocumentType>? = ArrayList()

) : Parcelable