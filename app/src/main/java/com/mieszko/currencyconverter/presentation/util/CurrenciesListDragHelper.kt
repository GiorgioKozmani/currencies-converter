package com.mieszko.currencyconverter.presentation.util

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class CurrenciesListDragHelper(itemMovedAction: (from: Int, to: Int) -> Unit) {
    // helper flag for managing scrolling to top
    private var isUserDraggingItem = false

    fun isUserDraggingItem(): Boolean {
        return isUserDraggingItem
    }

    fun attachToRecyclerView(recyclerView: RecyclerView) {
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN,
        0
    ) {
            var lastFrom = -1
            var lastTo = -1

            override fun onSelectedChanged(
                viewHolder: RecyclerView.ViewHolder?,
                actionState: Int
            ) {
                when (actionState) {
                    ItemTouchHelper.ACTION_STATE_DRAG -> {
                        isUserDraggingItem = true
                        val view = viewHolder?.itemView
                        view?.animate()?.scaleX(1.015f)?.scaleY(1.015f)

                        if (view is MaterialCardView) {
                            view.isDragged = true
                        }
                    }
                }
                super.onSelectedChanged(viewHolder, actionState)
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                val view = viewHolder.itemView
                view.animate()?.scaleX(1f)?.scaleY(1f)

                if (view is MaterialCardView) {
                    view.isDragged = false
                }
                isUserDraggingItem = false

                lastFrom = -1
                lastTo = -1
                super.clearView(recyclerView, viewHolder)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val from = viewHolder.absoluteAdapterPosition
                val to = target.absoluteAdapterPosition

                // when fast scrolled towards top and released press it was emitting same move event twice that's a workaround
                if (lastFrom != from && lastTo != to) {
                    lastFrom = from
                    lastTo = to

                    itemMovedAction(lastFrom, lastTo)
                }

                return true
            }

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                //    ItemTouchHelper handles horizontal swipe as well, but
                //    it is not relevant with reordering. Ignoring here.
            }
        })
}
