package com.mieszko.currencyconverter.presentation.util

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
// todo check out interpolators, Z index
        .withEndAction {
            text = newText
            animate().setDuration(fadeInDur)
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
        }
}
