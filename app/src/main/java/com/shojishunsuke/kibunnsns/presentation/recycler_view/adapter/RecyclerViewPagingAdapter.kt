package com.shojishunsuke.kibunnsns.presentation.recycler_view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shojishunsuke.kibunnsns.R
import com.shojishunsuke.kibunnsns.domain.model.Post
import com.shojishunsuke.kibunnsns.presentation.custom_view.StaggeredPostCellView
import com.shojishunsuke.kibunnsns.presentation.custom_view.VerticalPostCellView
import com.shojishunsuke.kibunnsns.presentation.recycler_view.view_type.RecyclerViewType

class RecyclerViewPagingAdapter(
    private val context: Context,
    private val listener: (Post) -> Unit
) : BasePagingAdapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(context)
        when (viewType) {
            RecyclerViewType.Staggered.ordinal -> {
                val mView =
                    inflater.inflate(R.layout.item_post, parent, false) as StaggeredPostCellView
                return StaggeredRecyclerViewHolder(mView)
            }
            RecyclerViewType.Vertical.ordinal -> {
                val mView = inflater.inflate(
                    R.layout.item_post_vertical,
                    parent,
                    false
                ) as VerticalPostCellView
                return VerticalRecyclerViewHolder(mView)
            }
            else -> {
                throw IllegalArgumentException()
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val post = posts[position]
        when (viewType) {
            RecyclerViewType.Staggered.ordinal -> {
                holder as StaggeredRecyclerViewHolder
                holder.view.build(post, listener)
            }
            RecyclerViewType.Vertical.ordinal -> {
                holder as VerticalRecyclerViewHolder
                holder.view.build(post, listener)
            }
        }
    }

    inner class StaggeredRecyclerViewHolder(val view: StaggeredPostCellView) :
        RecyclerView.ViewHolder(view)

    inner class VerticalRecyclerViewHolder(val view: VerticalPostCellView) :
        RecyclerView.ViewHolder(view)
}