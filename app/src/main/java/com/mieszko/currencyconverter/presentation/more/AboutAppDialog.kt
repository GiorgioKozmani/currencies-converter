package com.mieszko.currencyconverter.presentation.more

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mieszko.currencyconverter.R

class AboutAppDialog : DialogFragment() {

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { activity ->
            MaterialAlertDialogBuilder(activity).apply {
                setTitle(R.string.about_dialog_title)
                setMessage(R.string.about_dialog_desc).setPositiveButton(R.string.okay) { dialog, _ -> dialog.cancel() }
            }.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
