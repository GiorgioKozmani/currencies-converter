package com.mieszko.currencyconverter.presentation.more.list

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import com.mieszko.currencyconverter.R

class MoreListItem private constructor() {
    var itemTitle: String = ""
        private set
    var itemDescription: String = ""
        private set
    var iconDrawable: Drawable = ColorDrawable(Color.TRANSPARENT)
        private set

    @ColorRes
    var textColorRes: Int = R.color.black
        private set

    @ColorRes
    var iconTintColorRes: Int = R.color.colorPrimary
        private set
    var onClickListener: View.OnClickListener = View.OnClickListener { }
        private set
    var showCondition: (() -> Boolean) = { true }
        private set

    class Builder(val context: Context) {
        private var itemTitle: String? = null
        private var itemDescription: String? = null
        private var iconDrawable: Drawable? = null

        @ColorRes
        private var textTintColorRes: Int? = null

        @ColorRes
        private var iconTintColorRes: Int? = null
        private var showCondition: (() -> Boolean)? = null
        private var onClickListener: View.OnClickListener? = null

        fun setTitle(@StringRes textRes: Int): Builder {
            this.itemTitle = context.resources.getString(textRes)
            return this
        }

        fun setTitle(text: String): Builder {
            this.itemTitle = text
            return this
        }

        fun setDescription(@StringRes textRes: Int): Builder {
            this.itemDescription = context.resources.getString(textRes)
            return this
        }

        fun setDescription(text: String): Builder {
            this.itemDescription = text
            return this
        }

        fun setIcon(@DrawableRes iconDrawableRes: Int): Builder {
            this.iconDrawable = ResourcesCompat.getDrawable(context.resources, iconDrawableRes, null)
            return this
        }

        fun setTextColor(@ColorRes textColorRes: Int): Builder {
            this.textTintColorRes = textColorRes
            return this
        }

        fun setIconColor(@ColorRes iconColorRes: Int): Builder {
            this.iconTintColorRes = iconColorRes
            return this
        }

        fun showIfTrue(showCondition: () -> Boolean): Builder {
            this.showCondition = showCondition
            return this
        }

        fun doOnClick(onClickListener: View.OnClickListener): Builder {
            this.onClickListener = onClickListener
            return this
        }

        fun build(): MoreListItem {
            val moreListItem = MoreListItem()
            this.itemTitle?.let { moreListItem.itemTitle = it }
            this.itemDescription?.let { moreListItem.itemDescription = it }
            this.iconDrawable?.let { moreListItem.iconDrawable = it }
            this.textTintColorRes?.let { moreListItem.textColorRes = it }
            this.iconTintColorRes?.let { moreListItem.iconTintColorRes = it }
            this.showCondition?.let { moreListItem.showCondition = it }
            this.onClickListener?.let { moreListItem.onClickListener = it }

            return moreListItem
        }
    }
}
