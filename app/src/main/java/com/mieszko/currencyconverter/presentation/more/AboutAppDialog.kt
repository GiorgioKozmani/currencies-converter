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
                setTitle(R.string.about_title)
                setMessage(
                    "(Fajnie by bylo ladnie to wystylować) Tutaj napiszę kim jestem, dlaczego zrobilem ta apke, dla kogo ona jest, co mnie motywowalo. Podkreśl ze nie kradnę danych bo mnie wkurzlao ze inne kradna, credits dla ikonek, datasource?, podkresl plany na przyszlosc " +
                        "tj wiecej currencies. Podziekuj za uzycie aplikacji, zachęć do sharowania"
                ).setPositiveButton(R.string.okay) { dialog, _ -> dialog.cancel() }
            }.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
