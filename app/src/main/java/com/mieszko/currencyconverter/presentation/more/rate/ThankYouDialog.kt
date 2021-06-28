package com.mieszko.currencyconverter.presentation.more.rate

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mieszko.currencyconverter.R

class ThankYouDialog : DialogFragment() {
    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false

        return activity?.let { activity ->
            val dialogView =
                requireActivity().layoutInflater.inflate(R.layout.thank_you_dialog_layout, null)

            return MaterialAlertDialogBuilder(activity).apply {
                setTitle(R.string.thank_you_dialog_title)
                setView(dialogView)
                setPositiveButton(R.string.okay) { _, _ -> }
            }.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
