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

        object BaseValueChanged {
            const val EVENT = "base_value_changed"

            object Params {
                const val CODE = FirebaseAnalytics.Param.CURRENCY
                const val VALUE = FirebaseAnalytics.Param.VALUE
            }
        }

        object ButtonClicked {
            const val EVENT = "button_clicked"

            object Params {
                // TODO INTRODUCE COMMON PARAMS
                const val NAME = FirebaseAnalytics.Param.ITEM_NAME
            }
        }

        object MoreTabItemClicked {
            const val EVENT = "more_tab_item_clicked"

            object Params {
                // TODO INTRODUCE COMMON PARAMS
                const val NAME = FirebaseAnalytics.Param.ITEM_NAME
            }

            enum class MORE_ITEM {
                ABOUT, PRIVACY, RATE, FEEDBACK
            }
        }

        object ItemDragged {
            // todo
        }
    }

    /**
     *  List of user properties
     */
    object UserProperties {
        const val BASE_CURRENCY = "base_currency"
    }
}
