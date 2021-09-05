package com.example.storelocator.util.components

import android.content.Context
import android.view.View
import android.widget.Toast
import com.example.storelocator.util.TextInfo
import com.google.android.material.snackbar.Snackbar

data class ToastComponent(val text: TextInfo, val isLong: Boolean = false)

fun ToastComponent.showIn(context: Context) {
    Toast.makeText(context, text.getText(context), if (isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
}


data class SnackBarComponent(val text: TextInfo,
                             val action: TextInfo?,
                             val callback: View.OnClickListener,
                             val length: Int = Snackbar.LENGTH_LONG)

fun SnackBarComponent.createIn(view: View): Snackbar {

    return Snackbar.make(view, text.getText(view.context), length).apply {
        if (action != null) {
            setAction(action.getText(view.context), callback)
        }
    }
}