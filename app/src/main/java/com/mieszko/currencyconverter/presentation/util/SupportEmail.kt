package com.mieszko.currencyconverter.presentation.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import com.mieszko.currencyconverter.R

object EmailHelper {

    fun openFeedbackEmail(context: Context) {
        var appVersion = "-----"

        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            appVersion = "" + pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        val addresses = arrayOfNulls<String>(1)
        addresses[0] = context.getString(R.string.support_email)
        intent.putExtra(Intent.EXTRA_EMAIL, addresses)
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.support_mail_title))

        val body = context.getString(R.string.support_mail_prompt_text)

        intent.putExtra(
            Intent.EXTRA_TEXT,
            """$body
________________







________________

Device: ${Build.MANUFACTURER} ${Build.MODEL}
OS version: ${Build.VERSION.RELEASE}
App version: $appVersion"""
        )

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }
}
