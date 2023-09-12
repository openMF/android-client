/*
 * This project is licensed under the open source MPL V2.
 * See https://github.com/openMF/android-client/blob/master/LICENSE.md
 */
package com.mifos.mifosxdroid.views

import android.graphics.Typeface
import com.mifos.application.App
import com.mifos.application.App.Companion.instance
import java.io.File

/**
 * @author fomenkoo
 */
enum class Font(pathToFont: String, val fontName: String) {
    ROBOTO_LIGHT("fonts", "Roboto-Light.ttf"), ROBOTO_MEDIUM(
        "fonts",
        "Roboto-Regular.ttf"
    ),
    ROBOTO_BOLD("fonts", "Roboto-Bold.ttf");

    var typeface: Typeface?

    init {
        typeface = App.typefaceManager[fontName.hashCode()]
        if (typeface == null) {
            typeface = Typeface.createFromAsset(
                instance!!.assets,
                pathToFont + File.separator + fontName
            )
            App.typefaceManager[fontName.hashCode()] = typeface
        }
    }

    companion object {
        @JvmStatic
        fun getFont(typeface: Int): Font {
            return values()[typeface]
        }
    }
}