package com.mieszko.currencyconverter.presentation.more

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mieszko.currencyconverter.R

class PrivacyPolicyDialog : DialogFragment() {

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { activity ->
            val dialogView =
                requireActivity().layoutInflater.inflate(R.layout.rate_dialog_layout, null)

            MaterialAlertDialogBuilder(activity).apply {
                setTitle(R.string.privacy_policy_title)
                setMessage("Tutaj napisać w punktach co śledzę i podkreślić że to jest wylko i wylacznie po to zeby zrozumieć jak uzytkownicy uzywaja aplikacji. However research ejst potrzebny zeby zobaczyc czy firebase analytics nie wymaga wiecej by default")
//                setView(dialogView)
                    // Add action buttons
                    .setPositiveButton(R.string.got_it) { dialog, _ -> dialog.cancel() }
            }.create()

        } ?: throw IllegalStateException("Activity cannot be null")
    }
}