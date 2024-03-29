package com.mieszko.currencyconverter.presentation.more.rate

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import com.mieszko.currencyconverter.R
import com.mieszko.currencyconverter.data.persistance.ISharedPrefsManager
import com.mieszko.currencyconverter.domain.analytics.IFirebaseEventsLogger
import com.mieszko.currencyconverter.domain.analytics.events.InAppRateEvent
import org.koin.android.ext.android.inject
import kotlin.math.ceil

class RateAppDialog : DialogFragment() {

    private companion object {
        const val SELECTION_ANIMATION_DURATION = 250L
        const val RATE_BUTTON_ALPHA_ANIMATION_DURATION = 300L
        const val SELECTED_SIZE_SCALE = 1.2f
        const val UNSELECTED_SIZE_SCALE = 1f
        const val ENABLED_BUTTON_ALPHA = 1f
        const val DISABLED_BUTTON_ALPHA = 0.5f
        const val THANK_YOU_DIALOG_TAG = "thank_you_dialog"
    }

    private val eventsLogger: IFirebaseEventsLogger by inject()
    private val sharedPrefsManager: ISharedPrefsManager by inject()

    private val interpolator = OvershootInterpolator()
    private var ratesItems = mutableListOf<RateStarModel>()

    private lateinit var rateSlider: Slider
    private lateinit var rateButton: Button

    private val sliderValueChangeListener = Slider.OnChangeListener { _, value, _ ->
        if (!rateButton.isEnabled && value > 0) {
            setRateButtonEnabled(true)
        } else if (value == 0.0f) {
            setRateButtonEnabled(false)
        }

        val selectedIndex = roundUpToInt(value) - 1
        ratesItems.forEachIndexed { index, item ->
            if (index <= selectedIndex && !item.isSelected) {
                ratesItems[ratesItems.indexOf(item)] = item.copy(isSelected = true)
                animateSelected(item.view)
            } else if (index > selectedIndex && item.isSelected) {
                ratesItems[ratesItems.indexOf(item)] = item.copy(isSelected = false)
                animateUnselected(item.view)
            }
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false

        return activity?.let { activity ->
            val dialogView =
                requireActivity().layoutInflater.inflate(R.layout.rate_dialog_layout, null)

            setupSlider(dialogView)

            val dialog = MaterialAlertDialogBuilder(activity).apply {
                setTitle(R.string.rate_app_dialog_title)
                setMessage(R.string.rate_app_dialog_description)
                setView(dialogView)
                    // Add action buttons
                    .setPositiveButton(R.string.rate_app_dialog_positive_button_text) { _, _ ->
                        val rateValue = roundUpToInt(rateSlider.value)
                        eventsLogger.logEvent(InAppRateEvent(rateValue))
                        sharedPrefsManager.put(ISharedPrefsManager.Key.InAppRate, rateValue)
                        ThankYouDialog().show(parentFragmentManager, THANK_YOU_DIALOG_TAG)
                    }
                    .setNegativeButton(R.string.rate_app_dialog_negative_button_text) { dialog, _ ->
                        dialog.cancel()
                    }
            }.create()

            dialog.setOnShowListener {
                rateButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                // by default we want to disable rate button, so we don't allow 0 rate to be sent
                setRateButtonEnabled(false)
            }

            return dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun setupSlider(dialogView: View) {
        rateSlider = dialogView.findViewById(R.id.rate_slider)

        ratesItems.clear()
        ratesItems.addAll(
            listOf(
                RateStarModel(
                    Pair(
                        dialogView.findViewById(R.id.rate1),
                        dialogView.findViewById(R.id.rate1_overlay)
                    )
                ),
                RateStarModel(
                    Pair(
                        dialogView.findViewById(R.id.rate2),
                        dialogView.findViewById(R.id.rate2_overlay)
                    )
                ),
                RateStarModel(
                    Pair(
                        dialogView.findViewById(R.id.rate3),
                        dialogView.findViewById(R.id.rate3_overlay)
                    )
                ),
                RateStarModel(
                    Pair(
                        dialogView.findViewById(R.id.rate4),
                        dialogView.findViewById(R.id.rate4_overlay)
                    )
                ),
                RateStarModel(
                    Pair(
                        dialogView.findViewById(R.id.rate5),
                        dialogView.findViewById(R.id.rate5_overlay)
                    )
                )
            )
        )

        rateSlider.removeOnChangeListener(sliderValueChangeListener)
        rateSlider.addOnChangeListener(sliderValueChangeListener)
    }

    private fun animateSelected(rateItem: Pair<View, View>) {
        rateItem.first.animate()
            .scaleX(SELECTED_SIZE_SCALE)
            .scaleY(SELECTED_SIZE_SCALE)
            .alpha(1f)
            .setDuration(SELECTION_ANIMATION_DURATION)
            .setInterpolator(interpolator)
            .withStartAction {
                rateItem.second.animate()
                    .scaleX(SELECTED_SIZE_SCALE)
                    .scaleY(SELECTED_SIZE_SCALE)
                    .setInterpolator(interpolator)
                    .alpha(0f).duration = SELECTION_ANIMATION_DURATION
            }
    }

    private fun animateUnselected(rateItem: Pair<View, View>) {
        rateItem.first.animate()
            .scaleX(UNSELECTED_SIZE_SCALE)
            .scaleY(UNSELECTED_SIZE_SCALE)
            .alpha(0f)
            .setInterpolator(interpolator)
            .setDuration(SELECTION_ANIMATION_DURATION)
            .withStartAction {
                rateItem.second.animate()
                    .setInterpolator(interpolator)
                    .scaleX(UNSELECTED_SIZE_SCALE)
                    .scaleY(UNSELECTED_SIZE_SCALE)
                    .alpha(1f).duration = SELECTION_ANIMATION_DURATION
            }
    }

    private fun setRateButtonEnabled(isEnabled: Boolean) {
        rateButton.isEnabled = isEnabled
        rateButton.animate()
            .alpha(
                if (isEnabled) {
                    ENABLED_BUTTON_ALPHA
                } else {
                    DISABLED_BUTTON_ALPHA
                }
            )
            .setInterpolator(interpolator).duration = RATE_BUTTON_ALPHA_ANIMATION_DURATION
    }

    private fun roundUpToInt(value: Float) = ceil(value).toInt()

    data class RateStarModel(val view: Pair<View, View>, val isSelected: Boolean = false)
}
