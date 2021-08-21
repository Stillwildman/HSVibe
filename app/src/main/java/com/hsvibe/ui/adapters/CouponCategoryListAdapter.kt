package com.hsvibe.ui.adapters

import android.util.ArrayMap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.forEachIndexed
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.callbacks.OnAnyItemClickCallback
import com.hsvibe.callbacks.SingleClickListener
import com.hsvibe.databinding.InflateCategoryRowBinding
import com.hsvibe.databinding.InflateCategoryVerticalBinding
import com.hsvibe.model.Const
import com.hsvibe.model.items.ItemCouponCategories
import com.hsvibe.utilities.Extensions.setOnSingleClickListener
import com.hsvibe.utilities.L
import com.hsvibe.utilities.Utility
import kotlinx.coroutines.*

/**
 * Created by Vincent on 2021/8/18.
 */
class CouponCategoryListAdapter(
    private val layoutManage: LinearLayoutManager,
    private val categoryList: List<ItemCouponCategories.ContentData>,
    private val onClickCallback: OnAnyItemClickCallback<ItemCouponCategories.ContentData>,
    private val scope: CoroutineScope
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val screenWidth by lazy { Utility.getScreenWidth() }

    private val verticalCategorySize by lazy { ArrayMap<Int, Int>() }

    private var columnSelectedIndex = Const.NO_POSITION
    private var lastColumnSelectedIndex = Const.NO_POSITION
    private var rowSelectedIndex = Const.NO_POSITION
    private var lastRowSelectedIndex = Const.NO_POSITION

    private var currentIndex: Int = 0

    private var lastSelectedIndex: Int = Const.NO_POSITION

    companion object {
        private const val VIEW_TYPE_HORIZONTAL = 0
        private const val VIEW_TYPE_VERTICAL = 1
    }

    private fun isRowType(): Boolean {
        return layoutManage.orientation == RecyclerView.HORIZONTAL
    }

    private fun isPositionValid(position: Int): Boolean {
        return position != Const.NO_POSITION
    }

    private suspend fun calculateVerticalLayout(): Int {
        return withContext(Dispatchers.Default) {
            val inflater = LayoutInflater.from(AppController.getAppContext())

            var totalWidth = 0

            var rowSize = 0
            var tempWidth = 0

            val marginSizeM = AppController.getAppContext().resources.getDimensionPixelSize(R.dimen.padding_size_m)

            categoryList.forEachIndexed { i, item ->
                if (tempWidth != 0) {
                    totalWidth = tempWidth
                    tempWidth = 0
                    rowSize = 1
                }

                val tagView = inflater.inflate(R.layout.inflate_category_row, null, false) as TextView
                tagView.text = item.name
                tagView.measure(0, 0)
                val textWidth = tagView.measuredWidth

                totalWidth += textWidth.also { L.i("Text: ${item.name} Width: $it") } + (marginSizeM * 2)

                L.i("TotalWidth: $totalWidth/$screenWidth")

                if (totalWidth < screenWidth) {
                    rowSize++
                    if (i == categoryList.lastIndex) {
                        verticalCategorySize[verticalCategorySize.size] = rowSize
                    }
                }
                else {
                    if (i == categoryList.lastIndex) {
                        rowSize++
                    }
                    verticalCategorySize[verticalCategorySize.size] = rowSize
                    L.i("VerticalListSize: Index: ${verticalCategorySize.size - 1} RowSize: ${verticalCategorySize[verticalCategorySize.size - 1]}")
                    totalWidth = 0
                    tempWidth = textWidth
                }
            }
            verticalCategorySize.size
        }
    }

    fun changeLayoutOrientation() {
        scope.launch {
            layoutManage.apply {
                if (isRowType()) {
                    orientation = RecyclerView.VERTICAL
                    currentIndex = 0
                    calculateVerticalLayout().also { L.i("LayoutCount: $it") }
                    lastSelectedIndex = rowSelectedIndex
                    this@CouponCategoryListAdapter.notifyItemRangeChanged(0, itemCount)
                }
                else {
                    orientation = RecyclerView.HORIZONTAL
                    lastSelectedIndex = findSelectedItemIndex()
                    lastRowSelectedIndex = lastSelectedIndex
                    this@CouponCategoryListAdapter.notifyItemRangeChanged(0, itemCount.also { L.i("NotifyItemCount: $it") })

                    verticalCategorySize.clear()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (isRowType()) categoryList.size else verticalCategorySize.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (isRowType()) VIEW_TYPE_HORIZONTAL else VIEW_TYPE_VERTICAL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return if (viewType == VIEW_TYPE_HORIZONTAL) {
            val bindingView = DataBindingUtil.inflate<InflateCategoryRowBinding>(inflater, R.layout.inflate_category_row, parent, false)
            CategoryRowViewHolder(bindingView)
        }
        else {
            val bindingView = DataBindingUtil.inflate<InflateCategoryVerticalBinding>(inflater, R.layout.inflate_category_vertical, parent, false)
            CategoryVerticalViewHolder(bindingView)
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is CategoryVerticalViewHolder) {
            scope.launch {
                holder.bindingView.layoutCategoryTagRoot.removeAllViews()
            }
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
                holder.inflateCategoryTag(position)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        }
        else {
            when (holder) {
                is CategoryRowViewHolder -> {
                    setRowItemSelected(holder, position)
                }
                is CategoryVerticalViewHolder -> {
                    (payloads[0] as? Int)?.let { setVerticalItemSelected(holder, position, it) }
                }
            }
        }
    }

    private val eachItemSingleClickListener = object : SingleClickListener() {
        override fun onSingleClick(v: View) {
            onClickCallback.onItemClick(categoryList[v.tag as Int])
        }
    }

    private fun setRowItemSelected(holder: CategoryRowViewHolder, position: Int) {
        if (isPositionValid(lastSelectedIndex)) {
            rowSelectedIndex = lastSelectedIndex
            lastSelectedIndex = Const.NO_POSITION
        }
        holder.bindingView.textCategory.isSelected = position == rowSelectedIndex
    }

    private fun setVerticalItemSelected(holder: CategoryVerticalViewHolder, columnPosition: Int, rowPosition: Int) {
        holder.bindingView.layoutCategoryTagRoot.also { L.i("ColumnPosition: $columnPosition RowPosition: $rowPosition ChildCount: ${it.childCount}") }.forEachIndexed { index, view ->
            view.isSelected = columnPosition == columnSelectedIndex && index == rowSelectedIndex
        }
    }

    fun setSelected(item: ItemCouponCategories.ContentData) {
        columnSelectedIndex = item.columnPosition
        rowSelectedIndex = item.rowPosition

        if (isRowType()) {
            this.notifyItemChanged(item.rowPosition, true)
            if (isPositionValid(lastRowSelectedIndex)) {
                this.notifyItemChanged(lastRowSelectedIndex, true)
            }
        }
        else {
            this.notifyItemChanged(item.columnPosition, item.rowPosition)
            if (isPositionValid(lastColumnSelectedIndex) && isPositionValid(lastRowSelectedIndex)) {
                this.notifyItemChanged(lastColumnSelectedIndex, lastRowSelectedIndex)
            }
        }

        lastColumnSelectedIndex = columnSelectedIndex
        lastRowSelectedIndex = rowSelectedIndex
    }

    private suspend fun findSelectedItemIndex(): Int {
        return withContext(Dispatchers.Default) {
            if (isPositionValid(columnSelectedIndex) && isPositionValid(rowSelectedIndex)) {
                categoryList.forEachIndexed { index, item ->
                    if (item.columnPosition == columnSelectedIndex && item.rowPosition == rowSelectedIndex) {
                        return@withContext index
                    }
                }
                Const.NO_POSITION
            }
            else {
                Const.NO_POSITION
            }
        }
    }

    inner class CategoryRowViewHolder(val bindingView: InflateCategoryRowBinding): RecyclerView.ViewHolder(bindingView.root)

    inner class CategoryVerticalViewHolder(val bindingView: InflateCategoryVerticalBinding): RecyclerView.ViewHolder(bindingView.root) {

        fun inflateCategoryTag(position: Int) {
            val inflater = LayoutInflater.from(bindingView.root.context)

            val marginSizeS = AppController.getAppContext().resources.getDimensionPixelSize(R.dimen.padding_size_s)
            val marginSizeM = AppController.getAppContext().resources.getDimensionPixelSize(R.dimen.padding_size_m)

            verticalCategorySize[position]?.also { L.i("RowItemSize: $it") }?.let { rowCount ->
                L.i("InflateCategoryTag!!! position: $position")

                for (i in 0 until rowCount) {
                    L.i("InflateCategoryTag!!! CurrentIndex: $currentIndex")
                    if (currentIndex < categoryList.size) {
                        val item = categoryList[currentIndex]
                        item.columnPosition = position
                        item.rowPosition = i

                        val tagView = inflater.inflate(R.layout.inflate_category_row, bindingView.layoutCategoryTagRoot, false) as TextView
                        tagView.text = item.name
                        tagView.tag = currentIndex

                        LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                            when (i) {
                                0 -> setMargins(marginSizeM, marginSizeS, marginSizeS, marginSizeS)
                                rowCount - 1 -> setMargins(marginSizeS, marginSizeS, marginSizeM, marginSizeS)
                                else -> setMargins(marginSizeS, marginSizeS, marginSizeS, marginSizeS)
                            }
                            tagView.layoutParams = this
                        }

                        tagView.setOnSingleClickListener(eachItemSingleClickListener)

                        if (isPositionValid(lastSelectedIndex) && lastSelectedIndex == currentIndex) {
                            tagView.isSelected = true
                            lastColumnSelectedIndex = position
                            lastRowSelectedIndex = i
                            lastSelectedIndex = Const.NO_POSITION
                        }
                        bindingView.layoutCategoryTagRoot.addView(tagView)
                        L.i("InflateCategoryTag!!! childAdded!! Count: ${bindingView.layoutCategoryTagRoot.childCount}")

                        currentIndex++
                    }
                }
            }
        }

    }
}