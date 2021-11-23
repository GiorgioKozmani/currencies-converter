package com.mieszko.currencyconverter.presentation.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import com.mieszko.currencyconverter.R

object FeedbackEmailHelper {

    fun openFeedbackEmail(context: Context) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.support_email)))
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.support_mail_title))
            putExtra(Intent.EXTRA_TEXT, makeInitialEmailBody(context))
        }

        context.startActivity(intent)
    }

    private fun makeInitialEmailBody(context: Context): String {
        val promptText = context.getString(R.string.support_mail_prompt_text)
        var appVersion = "-----"

        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            appVersion = "" + pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return """$promptText

________________







________________

Device: ${Build.MANUFACTURER} ${Build.MODEL}
OS version: ${Build.VERSION.RELEASE}
App version: $appVersion"""
    }
}
