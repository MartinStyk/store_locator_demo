package com.example.storelocator.util

import android.view.View
import androidx.databinding.BindingAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.util.*

@BindingAdapter("bottom_sheet_ignore_gesture_insets")
fun setIgnoreGestureInsets(view: View, ignoreInsets: Boolean) {
    // BottomSheet is automatically increasing systemMandatoryInsetsBottom from Android 10
    // but we can disable it by setting isGestureInsetBottomIgnored, it's a little hack
    BottomSheetBehavior.from(view).isGestureInsetBottomIgnored = ignoreInsets
}

@BindingAdapter("bottom_sheet_hideable")
fun setHideable(view: View, hideable: Boolean) {
    BottomSheetBehavior.from(view).isHideable = hideable
}

@BindingAdapter("bottom_sheet_draggable")
fun setDraggable(view: View, draggable: Boolean) {
    BottomSheetBehavior.from(view).isDraggable = draggable

}

@BindingAdapter("bottom_sheet_callback")
fun setBottomSheetCallback(view: View, callback: BottomSheetBehavior.BottomSheetCallback) {
    BottomSheetBehavior.from(view).addBottomSheetCallback(callback)
}

@BindingAdapter("bottom_sheet_peekHeight")
fun setPeekHeight(view: View, peekHeight: Int) {
    BottomSheetBehavior.from(view).peekHeight = peekHeight
}

@BindingAdapter("bottom_sheet_peekHeight")
fun setPeekHeight(view: View, peekHeight: Float) {
    BottomSheetBehavior.from(view).peekHeight = peekHeight.toInt()
}

@BindingAdapter("bottom_sheet_state")
fun setState(view: View, state: Int) {
    BottomSheetBehavior.from(view).state = state
}
