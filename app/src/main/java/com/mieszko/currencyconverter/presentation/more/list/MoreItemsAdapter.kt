package com.mieszko.currencyconverter.presentation.more.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.mieszko.currencyconverter.R

internal class MoreItemsAdapter(private val items: List<MoreListItem>) :
    RecyclerView.Adapter<MoreItemsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.more_tab_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    internal inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        private val title: TextView = mView.findViewById(R.id.title)
        private val description: TextView = mView.findViewById(R.id.description)
        private val leftIcon: ImageView = mView.findViewById(R.id.left_icon)

        fun bind(item: MoreListItem) {
            title.text = item.itemTitle
            description.text = item.itemDescription
            leftIcon.setImageDrawable(
                item.iconDrawable
                    .apply {
                        DrawableCompat.setTint(
                            this.mutate(),
                            ContextCompat.getColor(itemView.context, item.iconTintColorRes)
                        )
                    }
            )
            itemView.setOnClickListener(item.onClickListener)
        }
    }
}
