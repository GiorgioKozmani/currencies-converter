package com.mieszko.currencyconverter.presentation.more

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import com.mieszko.currencyconverter.R
import kotlin.math.ceil

class RateAppDialog : DialogFragment() {

    private companion object {
        const val SELECTION_ANIMATION_DURATION = 250L
        const val SELECTED_SIZE_SCALE = 1.2f
        const val UNSELECTED_SIZE_SCALE = 1f
    }

    private val interpolator = OvershootInterpolator()

    private var ratesItems = mutableListOf<RateView>()

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { activity ->
            val dialogView =
                requireActivity().layoutInflater.inflate(R.layout.rate_dialog_layout, null)

            setupSlider(dialogView)

            MaterialAlertDialogBuilder(activity).apply {
                setTitle(R.string.rate_app_dialog_title)
                setMessage(R.string.rate_app_dialog_description)
                setView(dialogView)
                    // Add action buttons
                    .setPositiveButton(R.string.rate_app_dialog_positive_button_text) { _, _ -> }
                    .setNegativeButton(R.string.rate_app_dialog_negative_button_text) { dialog, _ -> dialog.cancel() }
            }.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    // TODO THINK OF CUSTOM VIEWS FOR FOR EXAMPLE OVERLAY + STAR
    private fun setupSlider(dialogView: View) {
        val slider = dialogView.findViewById<Slider>(R.id.rate_slider)

        ratesItems.addAll(
            listOf(
                RateView(
                    Pair(
                        dialogView.findViewById(R.id.rate1),
                        dialogView.findViewById(R.id.rate1_overlay)
                    )
                ),
                RateView(
                    Pair(
                        dialogView.findViewById(R.id.rate2),
                        dialogView.findViewById(R.id.rate2_overlay)
                    )
                ),
                RateView(
                    Pair(
                        dialogView.findViewById(R.id.rate3),
                        dialogView.findViewById(R.id.rate3_overlay)
                    )
                ),
                RateView(
                    Pair(
                        dialogView.findViewById(R.id.rate4),
                        dialogView.findViewById(R.id.rate4_overlay)
                    )
                ),
                RateView(
                    Pair(
                        dialogView.findViewById(R.id.rate5),
                        dialogView.findViewById(R.id.rate5_overlay)
                    )
                )
            )
        )

        slider.addOnChangeListener { _, value, _ ->
            val selectedIndex = ceil(value).toInt() - 1
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

    data class RateView(val view: Pair<View, View>, val isSelected: Boolean = false)
}
