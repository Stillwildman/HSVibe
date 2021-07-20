package com.hsvibe.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.hsvibe.R
import com.hsvibe.viewmodel.HomeViewModel
import kotlin.math.floor

/**
 * Created by Vincent on 2021/7/19.
 */
class ContentListAdapter(private val viewModel: HomeViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_LIST = 1
        const val LIST_POSITION_NEWS = 1
        const val LIST_POSITION_COUPON = 3
        const val LIST_POSITION_DISCOUNT = 5
        const val LIST_POSITION_FOODS = 7
        const val LIST_POSITION_HOTEL = 9
    }

    override fun getItemCount(): Int {
        return viewModel.headerList.size * 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (isHeaderType(position)) {
            true -> TYPE_HEADER
            false -> TYPE_LIST
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                HomeContentViewHolders.ContentHeaderViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.inflate_content_header, parent, false))
            }
            TYPE_LIST -> {
                HomeContentViewHolders.ContentListViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.inflate_recycler, parent, false))
            }
            else -> {
                HomeContentViewHolders.ContentListViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.inflate_recycler, parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }

    private fun bindContent(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HomeContentViewHolders.ContentHeaderViewHolder -> {
                holder.itemView.visibility = View.VISIBLE
                holder.bindingView.index = getItemIndex(position)
                holder.bindingView.viewModel = viewModel
            }
            is HomeContentViewHolders.ContentListViewHolder -> {
                when (position) {
                    LIST_POSITION_NEWS -> { holder.setupAdapter(HomeNewsListAdapter(viewModel)) }
                    LIST_POSITION_COUPON -> { holder.setupAdapter(HomeCouponListAdapter(viewModel)) }
                    LIST_POSITION_DISCOUNT -> { }
                    LIST_POSITION_FOODS -> { }
                    LIST_POSITION_HOTEL -> { }
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNullOrEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        }
        else {
            bindContent(holder, position)
        }
    }

    fun notifyDataGet(dataPosition: Int) {
        if (dataPosition > 0) {
            this.notifyItemRangeChanged(dataPosition - 1, 2, true)
        }
    }

    private fun isHeaderType(position: Int): Boolean {
        return position % 2 == 0
    }

    private fun getItemIndex(position: Int): Int {
        return floor(position.toDouble() / 2).toInt()
    }
}