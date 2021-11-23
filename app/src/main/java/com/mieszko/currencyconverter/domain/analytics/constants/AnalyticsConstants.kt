package com.mieszko.currencyconverter.domain.analytics.constants

import com.google.firebase.analytics.FirebaseAnalytics

object AnalyticsConstants {
    /**
     *  List of manually logged events
     *  Each event is having its own class.
     */
    object Events {
        object CommonParams {
            const val SEARCH_TERM = FirebaseAnalytics.Param.SEARCH_TERM
            const val CODE = FirebaseAnalytics.Param.CURRENCY
            const val VALUE = FirebaseAnalytics.Param.VALUE
            const val NAME = FirebaseAnalytics.Param.ITEM_NAME
        }

        object SearchTerm {
            const val EVENT = FirebaseAnalytics.Event.SEARCH
        }

        object CodeTracked {
            const val EVENT = "code_tracked"
        }

        object CodeUntracked {
            const val EVENT = "code_untracked"
        }

        object BaseValueChanged {
            const val EVENT = "base_value_changed"
        }

        object ButtonClicked {
            const val EVENT = "button_clicked"
        }

        object MoreTabItemClicked {
            const val EVENT = "more_tab_item_clicked"

            enum class MoreItem {
                ABOUT, PRIVACY, RATE, FEEDBACK
            }
        }

        object ScreenView {
            const val EVENT = FirebaseAnalytics.Event.SCREEN_VIEW

            object Params {
                const val SCREEN_NAME = FirebaseAnalytics.Param.SCREEN_NAME
            }

            enum class Screen {
                HOME, SELECTION, MORE
            }
        }

        object InAppRate {
            const val EVENT = "in_app_rate"
        }
    }

    /**
     *  List of user properties
     */
    object UserProperties {
        const val BASE_CURRENCY = "base_currency"
    }
}
