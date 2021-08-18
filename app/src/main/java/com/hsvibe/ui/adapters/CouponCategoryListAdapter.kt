package com.hsvibe.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hsvibe.R
import com.hsvibe.callbacks.OnAnyItemClickCallback
import com.hsvibe.databinding.InflateCategoryRowBinding
import com.hsvibe.databinding.InflateCategoryVerticalBinding
import com.hsvibe.model.Const
import com.hsvibe.model.items.ItemCouponCategories
import com.hsvibe.utilities.Utility

/**
 * Created by Vincent on 2021/8/18.
 */
class CouponCategoryListAdapter(
    private val layoutManage: LinearLayoutManager,
    private val categoryList: List<ItemCouponCategories.ContentData>,
    private val onClickCallback: OnAnyItemClickCallback<ItemCouponCategories.ContentData>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val screenWidth = Utility.getScreenWidth()

    private var columnSelectedIndex = Const.NO_POSITION
    private var columnLastSelectedIndex = Const.NO_POSITION
    private var rowSelectedIndex = Const.NO_POSITION
    private var rowLastSelectedIndex = Const.NO_POSITION

    private fun isRowType(): Boolean {
        return layoutManage.orientation == RecyclerView.HORIZONTAL
    }

    private fun isPositionValid(position: Int): Boolean {
        return position != Const.NO_POSITION
    }

    fun changeLayoutOrientation() {
        layoutManage.apply {
            orientation = if (isRowType()) {
                RecyclerView.VERTICAL
            } else {
                RecyclerView.HORIZONTAL
            }
        }
        this.notifyItemRangeChanged(0, itemCount)
    }

    override fun getItemCount(): Int = categoryList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return if (isRowType()) {
            val bindingView = DataBindingUtil.inflate<InflateCategoryRowBinding>(inflater, R.layout.inflate_category_row, parent, false)
            CategoryRowViewHolder(bindingView)
        }
        else {
            val bindingView = DataBindingUtil.inflate<InflateCategoryVerticalBinding>(inflater, R.layout.inflate_category_vertical, parent, false)
            CategoryVerticalViewHolder(bindingView, inflater, parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CategoryRowViewHolder -> {
                categoryList[position].let {
                    it.columnPosition = 0
                    it.rowPosition = position
                    holder.bindingView.item = it
                    holder.bindingView.itemClickCallback = onClickCallback
                    setRowItemSelected(holder, position)
                }
            }
            is CategoryVerticalViewHolder -> {
                holder.rowBinding.item = categoryList[position]
                holder.rowBinding.itemClickCallback = onClickCallback
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        }
        else {
            if (isRowType()) {
                (holder as? CategoryRowViewHolder)?.let { setRowItemSelected(it, position) }
            }
            else {

            }
        }
    }

    private fun setRowItemSelected(holder: CategoryRowViewHolder, position: Int) {
        if (isPositionValid(position)) {
            holder.bindingView.textCategory.isSelected = position == rowSelectedIndex
        }
    }

    private fun setVerticalItemSelected(holder: CategoryVerticalViewHolder, position: Int) {

    }

    fun setSelected(item: ItemCouponCategories.ContentData) {
        columnSelectedIndex = item.columnPosition
        rowSelectedIndex = item.rowPosition

        if (isRowType()) {
            this.notifyItemChanged(item.rowPosition, true)
            if (isPositionValid(rowLastSelectedIndex)) {
                this.notifyItemChanged(rowLastSelectedIndex, true)
            }
        }
        else {

        }

        columnLastSelectedIndex = columnSelectedIndex
        rowLastSelectedIndex = rowSelectedIndex
    }

    inner class CategoryRowViewHolder(val bindingView: InflateCategoryRowBinding): RecyclerView.ViewHolder(bindingView.root)

    inner class CategoryVerticalViewHolder(val bindingView: InflateCategoryVerticalBinding, inflater: LayoutInflater, parent: ViewGroup): RecyclerView.ViewHolder(bindingView.root) {

        val rowBinding: InflateCategoryRowBinding

        init {
            rowBinding = DataBindingUtil.inflate(inflater, R.layout.inflate_category_row, parent, false)
            val categoryTextView: TextView = rowBinding.textCategory

        }
    }
}