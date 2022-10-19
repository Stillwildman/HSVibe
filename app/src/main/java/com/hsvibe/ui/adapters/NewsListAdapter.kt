package com.hsvibe.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hsvibe.R
import com.hsvibe.callbacks.OnContentDataClickCallback
import com.hsvibe.databinding.InflateNewsDetailBinding
import com.hsvibe.databinding.InflateNewsRowBinding
import com.hsvibe.model.DifferItems
import com.hsvibe.model.items.ItemContent
import com.hsvibe.utilities.Utility

/**
 * Created by Vincent on 2021/8/5.
 */
class NewsListAdapter(
    private val layoutManager: LinearLayoutManager,
    private val onItemClickCallback: OnContentDataClickCallback) : PagingDataAdapter<ItemContent.ContentData, RecyclerView.ViewHolder>(DifferItems.ContentItemDiffer) {

    companion object {
        private const val VIEW_TYPE_ROW = 1
        private const val VIEW_TYPE_DETAIL = 2
    }

    val detailWidth: Int by lazy { (Utility.getScreenWidth() * 0.75).toInt() }

    fun changeLayout() {
        layoutManager.apply {
            orientation = if (orientation == RecyclerView.VERTICAL) {
                RecyclerView.HORIZONTAL
            } else {
                RecyclerView.VERTICAL
            }
        }
        notifyItemRangeChanged(0, itemCount)
    }

    fun isDetailView(): Boolean {
        return layoutManager.orientation == RecyclerView.HORIZONTAL
    }

    override fun getItemViewType(position: Int): Int {
        return if (layoutManager.orientation == RecyclerView.VERTICAL) VIEW_TYPE_ROW else VIEW_TYPE_DETAIL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ROW -> {
               val bindingView = DataBindingUtil.inflate<InflateNewsRowBinding>(LayoutInflater.from(parent.context), R.layout.inflate_news_row, parent, false)
                NewsRowViewHolder(bindingView)
            }
            else -> {
                val bindingView = DataBindingUtil.inflate<InflateNewsDetailBinding>(LayoutInflater.from(parent.context), R.layout.inflate_news_detail, parent, false)
                NewsDetailViewHolder(bindingView)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NewsRowViewHolder -> {
                holder.bindingView.position = position
                holder.bindingView.content = getItem(position)
                holder.bindingView.callback = onItemClickCallback
            }
            is NewsDetailViewHolder -> {
                holder.bindingView.content = getItem(position)
                holder.bindingView.callback = onItemClickCallback
            }
        }
    }

    inner class NewsRowViewHolder(val bindingView: InflateNewsRowBinding): RecyclerView.ViewHolder(bindingView.root)
    inner class NewsDetailViewHolder(val bindingView: InflateNewsDetailBinding): RecyclerView.ViewHolder(bindingView.root) {
        init {
            itemView.layoutParams.width = detailWidth
        }
    }
}