package com.example.storelocator.util

import android.content.Context
import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.util.*

@Parcelize
data class TextInfo internal constructor(@StringRes private val stringResource: Int = 0,
                                          private val textData: @RawValue Array<out Any>) : Parcelable {


    companion object {

        fun empty(): TextInfo {
            return TextInfo(0, emptyArray())
        }

        fun from(@StringRes resId: Int): TextInfo {
            return TextInfo(resId, emptyArray())
        }

        fun from(text: CharSequence): TextInfo {
            return TextInfo(0, arrayOf(text))
        }

        fun from(@StringRes resId: Int, vararg textData: Any): TextInfo {
            return TextInfo(resId, textData)
        }
    }


    fun getText(context: Context): CharSequence {
        val text = if (stringResource == 0) "" else context.getString(stringResource)
        textData.takeIf { it.isNotEmpty() }?.let {
            try {
                val list = arrayListOf<Any>()
                it.mapTo(list) { (it as? TextInfo)?.getText(context) ?: it }
                return String.format(if (text.isEmpty()) "%s" else text, *list.toArray())

            } catch (illegalFormatException: IllegalFormatConversionException) {
            } catch (missingArgumentException: MissingFormatArgumentException) {
            }
        }

        return text
    }

}