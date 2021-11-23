package com.mieszko.currencyconverter.presentation.util.views

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import com.google.android.material.textfield.TextInputEditText

/**
 * This wrapper class is implemented to simplify the process of clearing ET selection on back click.
 */
class ClearingFocusEditText : TextInputEditText {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            clearFocus()
        }

        return super.onKeyPreIme(keyCode, event)
    }
}
