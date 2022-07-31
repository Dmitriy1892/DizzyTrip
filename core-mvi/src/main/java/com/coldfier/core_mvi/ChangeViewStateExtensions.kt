package com.coldfier.core_mvi

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.*
import androidx.annotation.ColorInt
import androidx.annotation.IntDef
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior


// NEED TO CHECK THIS METHOD start region
fun View.changeBackgroundDrawable(drawable: Drawable) {
    if (this.background == drawable) return else this.background = drawable
}

fun ImageView.changeImageDrawable(drawable: Drawable) {
    if (this.drawable == drawable) return else this.setImageDrawable(drawable)
}

// end region

@IntDef(View.VISIBLE, View.GONE, View.INVISIBLE)
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Visibility

fun View.changeVisibility(@Visibility newVisibility: Int) {
    if (visibility == newVisibility) return else visibility = newVisibility
}

/**
 *  For [EditText], [Button] and etc. views which extend [TextView]
 */
fun TextView.changeText(text: String) {
    if (this.text.toString() == text) return else this.text = text
}

/**
 *  For [EditText], [Button] and etc. views which extend [TextView]
 */
fun TextView.changeTextColor(@ColorInt newColor: Int) {
    if (this.currentTextColor == newColor) return else this.setTextColor(newColor)
}

/**
 *  For [Switch], [SwitchCompat], [CheckBox] and etc. views which extend [CompoundButton]
 */
fun CompoundButton.changeCheckedState(isChecked: Boolean) {
    if (this.isChecked == isChecked) return else this.isChecked = isChecked
}

fun <V: View> BottomSheetBehavior<V>.changeState(@BottomSheetBehavior.State newState: Int) {
    if (this.state == newState) return else this.state = newState
}

