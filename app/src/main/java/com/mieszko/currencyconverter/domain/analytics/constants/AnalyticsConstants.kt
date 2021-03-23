package com.mieszko.currencyconverter.domain.analytics.constants

import com.google.firebase.analytics.FirebaseAnalytics

object AnalyticsConstants {
    /**
     *  List of manually logged events
     *  Each event is having its own class.
     */
    object Events {
        object ScreenView {
            const val EVENT = FirebaseAnalytics.Event.SCREEN_VIEW // this is event name.

            object Params {
                const val SCREEN_NAME = FirebaseAnalytics.Param.SCREEN_NAME
            }

            enum class Screen {
                HOME, SELECTION, MORE
            }
        }

        object SearchTerm {
            const val EVENT = FirebaseAnalytics.Event.SEARCH

            object Params {
                const val SEARCH_TERM = FirebaseAnalytics.Param.SEARCH_TERM
            }
        }

        object CodeTracked {
            const val EVENT = "code_tracked"

            object Params { // this is an object which contains possible parameters. Note: it's not a final list of possible parameters,
                // some parameters can be re-used from other events. The point is to keep all possible constants in this class
                const val CODE = FirebaseAnalytics.Param.CURRENCY
                // todo reuse this code
                const val SEARCH_TERM = FirebaseAnalytics.Param.SEARCH_TERM
            }
        }

        object CodeUntracked {
            const val EVENT = "code_untracked"

            object Params { // this is an object which contains possible parameters. Note: it's not a final list of possible parameters,
                // some parameters can be re-used from other events. The point is to keep all possible constants in this class
                const val CODE = FirebaseAnalytics.Param.CURRENCY
                // todo reuse this code
                const val SEARCH_TERM = FirebaseAnalytics.Param.SEARCH_TERM
            }
        }

        object ItemClicked {
            // todo
        }

        object ItemDragged {
            // todo
        }

        // todo Manually track screens

        object BaseValueChanged {
            // todo if params will be applied properly, this will not need currency parameter
        }

        object SendClicked {
            const val EVENT = "send_clicked"

            object Params {
                const val TIME_BETWEEN_CLICKS = "time_between_clicks"
            }
        }
    }

    /**
     *  List of user properties
     */
    object UserProperties {
        const val BASE_CURRENCY = "base_currency"
    }
}
