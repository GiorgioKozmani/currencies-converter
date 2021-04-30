package com.mieszko.currencyconverter.presentation.util

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView

fun TextView.fadeInText(newText: String, animDuration: Long? = null) {
    val dur = animDuration
        ?: resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

    val fadeOutDur = (dur * 0.25).toLong()
    val fadeInDur = (dur * 0.75).toLong()

    animate().setDuration(fadeOutDur)
        .alpha(0f)
        .scaleX(1.2f)
        .scaleY(1.2f)
        .withEndAction {
            text = newText
            animate().setDuration(fadeInDur)
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
        }
}

fun EditText.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}
