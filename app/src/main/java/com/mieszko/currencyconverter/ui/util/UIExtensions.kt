package com.mieszko.currencyconverter.ui.util

import android.widget.TextView

//todo either use or remove
fun TextView.fadeInText(newText: String, animDuration: Long? = null) {
    val dur = animDuration
        ?: resources.getInteger(android.R.integer.config_shortAnimTime).toLong()

    val fadeOutDur = (dur * 0.25).toLong()
    val fadeInDur = (dur * 0.75).toLong()

    animate().setDuration(fadeOutDur).alpha(0f).withEndAction {
        text = newText
        animate().setDuration(fadeInDur).alpha(1f)
    }
}