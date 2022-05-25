package com.hsvibe.ui.adapters

import android.annotation.SuppressLint
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
import com.hsvibe.databinding.InflateBrandRowBinding
import com.hsvibe.databinding.InflateBrandVerticalBinding
import com.hsvibe.model.Const
import com.hsvibe.model.items.ItemBrand
import com.hsvibe.utilities.L
import com.hsvibe.utilities.Utility
import com.hsvibe.utilities.setOnSingleClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Vincent on 2021/8/18.
 */
class CouponBrandListAdapter(
    private val layoutManage: LinearLayoutManager,
    private val brandList: MutableList<ItemBrand.ContentData>,
    private val onClickCallback: OnAnyItemClickCallback<ItemBrand.ContentData>,
    private val scope: CoroutineScope
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val screenWidth by lazy { Utility.getScreenWidth() }

    private val verticalLayoutMap by lazy { ArrayMap<Int, Int>() }

    private var columnSelectedIndex = Const.NO_POSITION
    private var lastColumnSelectedIndex = Const.NO_POSITION
    private var rowSelectedIndex = Const.NO_POSITION
    private var lastRowSelectedIndex = Const.NO_POSITION

    private var lastSelectedIndex: Int = Const.NO_POSITION

    companion object {
        private const val VIEW_TYPE_HORIZONTAL = 0
        private const val VIEW_TYPE_VERTICAL = 1
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(brandList: List<ItemBrand.ContentData>) {
        L.i("updateList! brandList size: ${brandList.size}")

        scope.launch {
            this@CouponBrandListAdapter.brandList.apply {
                clear()
                notifyDataSetChanged()

                addAll(brandList)

                if (!isRowType()) {
                    calculateVerticalLayout()
                }
                notifyItemRangeChanged(0, itemCount)
            }
        }
    }

    private fun isRowType(): Boolean {
        return layoutManage.orientation == RecyclerView.HORIZONTAL
    }

    private fun isPositionValid(position: Int): Boolean {
        return position != Const.NO_POSITION
    }

    private suspend fun calculateVerticalLayout(): Int {
        return withContext(Dispatchers.Default) {
            verticalLayoutMap.clear()

            val inflater = LayoutInflater.from(AppController.getAppContext())

            var totalWidth = 0
            var rowBrandSize = 0

            val marginSizeM = AppController.getAppContext().resources.getDimensionPixelSize(R.dimen.padding_size_m)

            L.i("calculateVerticalLayout! brandList size: ${brandList.size}")

            brandList.forEachIndexed { i, item ->
                val textView = inflater.inflate(R.layout.inflate_brand_row, null, false) as TextView
                textView.apply {
                    text = item.name
                    textView.measure(0, 0)
                }
                val textWidth = textView.measuredWidth

                totalWidth += textWidth.also { L.i("Text: ${item.name} Width: $it") } + (marginSizeM * 2)

                L.i("TotalWidth: $totalWidth/$screenWidth")

                if (totalWidth < screenWidth) {
                    rowBrandSize++
                    if (i == brandList.lastIndex) {
                        verticalLayoutMap[verticalLayoutMap.size] = rowBrandSize
                        L.d("VerticalListSize: LastIndex: ${verticalLayoutMap.size - 1} RowBrandSize: ${verticalLayoutMap[verticalLayoutMap.size - 1]}")
                    }
                }
                else {
                    verticalLayoutMap[verticalLayoutMap.size] = rowBrandSize
                    L.d("VerticalListSize: Index: ${verticalLayoutMap.size - 1} RowBrandSize: ${verticalLayoutMap[verticalLayoutMap.size - 1]}")

                    if (i == brandList.lastIndex) {
                        verticalLayoutMap[verticalLayoutMap.size] = 1
                        L.d("VerticalListSize: LastIndex: ${verticalLayoutMap.size - 1} RowBrandSize: ${verticalLayoutMap[verticalLayoutMap.size - 1]}")
                    }
                    else {
                        totalWidth = textWidth
                        rowBrandSize = 1
                    }
                }
            }
            verticalLayoutMap.size
        }
    }

    fun changeLayoutOrientation() {
        scope.launch {
            layoutManage.apply {
                if (isRowType()) {
                    this@CouponBrandListAdapter.notifyItemRangeRemoved(0, itemCount)
                    calculateVerticalLayout().also { L.i("LayoutCount: $it") }
                    orientation = RecyclerView.VERTICAL
                    lastSelectedIndex = rowSelectedIndex
                    this@CouponBrandListAdapter.notifyItemChanged(0)
                }
                else {
                    orientation = RecyclerView.HORIZONTAL
                    lastSelectedIndex = findSelectedItemIndex()
                    lastRowSelectedIndex = lastSelectedIndex

                    this@CouponBrandListAdapter.notifyItemRangeChanged(0, verticalLayoutMap.size.also { L.i("NotifyItemCount: $it") })
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (isRowType()) brandList.size else verticalLayoutMap.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (isRowType()) VIEW_TYPE_HORIZONTAL else VIEW_TYPE_VERTICAL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return if (viewType == VIEW_TYPE_HORIZONTAL) {
            val bindingView = DataBindingUtil.inflate<InflateBrandRowBinding>(inflater, R.layout.inflate_brand_row, parent, false)
            BrandRowViewHolder(bindingView)
        }
        else {
            val bindingView = DataBindingUtil.inflate<InflateBrandVerticalBinding>(inflater, R.layout.inflate_brand_vertical, parent, false)
            BrandVerticalViewHolder(bindingView)
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is BrandVerticalViewHolder) {
            scope.launch {
                holder.bindingView.layoutBrandTagRoot.removeAllViews()
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BrandRowViewHolder -> {
                brandList[position].let {
                    it.columnPosition = 0
                    it.rowPosition = position
                    holder.bindingView.item = it
                    holder.bindingView.itemClickCallback = onClickCallback
                    setRowItemSelected(holder, position)
                }
            }
            is BrandVerticalViewHolder -> {
                L.d("onBindViewHolder! inflateBrandTag: $position")
                holder.inflateBrandTag(position)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        }
        else {
            when (holder) {
                is BrandRowViewHolder -> {
                    setRowItemSelected(holder, position)
                }
                is BrandVerticalViewHolder -> {
                    (payloads[0] as? Int)?.let { setVerticalItemSelected(holder, position, it) }
                }
            }
        }
    }

    private val eachItemSingleClickListener = object : SingleClickListener() {
        override fun onSingleClick(v: View) {
            onClickCallback.onItemClick(brandList[v.tag as Int])
        }
    }

    private fun setRowItemSelected(holder: BrandRowViewHolder, position: Int) {
        if (isPositionValid(lastSelectedIndex)) {
            rowSelectedIndex = lastSelectedIndex
            lastSelectedIndex = Const.NO_POSITION
        }
        holder.bindingView.textBrand.isSelected = position == rowSelectedIndex
    }

    private fun setVerticalItemSelected(holder: BrandVerticalViewHolder, columnPosition: Int, rowPosition: Int) {
        holder.bindingView.layoutBrandTagRoot.also { L.i("ColumnPosition: $columnPosition RowPosition: $rowPosition ChildCount: ${it.childCount}") }.forEachIndexed { index, view ->
            view.isSelected = columnPosition == columnSelectedIndex && index == rowSelectedIndex
        }
    }

    fun setSelected(item: ItemBrand.ContentData) {
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
                brandList.forEachIndexed { index, item ->
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

    inner class BrandRowViewHolder(val bindingView: InflateBrandRowBinding): RecyclerView.ViewHolder(bindingView.root)

    inner class BrandVerticalViewHolder(val bindingView: InflateBrandVerticalBinding): RecyclerView.ViewHolder(bindingView.root) {

        fun inflateBrandTag(position: Int) {
            val inflater = LayoutInflater.from(bindingView.root.context)

            val marginSizeS = AppController.getAppContext().resources.getDimensionPixelSize(R.dimen.padding_size_s)
            val marginSizeM = AppController.getAppContext().resources.getDimensionPixelSize(R.dimen.padding_size_m)

            verticalLayoutMap[position]?.also { L.i("RowBrandSize: $it") }?.let { rowCount ->
                L.i("InflateBrandTag!!! position: $position")

                for (i in 0 until rowCount) {
                    val indexOfTotal = findIndexOfTotal(position, i)

                    val item = brandList[indexOfTotal]
                    L.i("IndexOfTotal: $indexOfTotal name: ${item.name}")

                    item.columnPosition = position
                    item.rowPosition = i

                    val textView = inflater.inflate(R.layout.inflate_brand_row, bindingView.layoutBrandTagRoot, false) as TextView
                    textView.text = item.name
                    textView.tag = indexOfTotal

                    LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                        when (i) {
                            0 -> setMargins(marginSizeM, marginSizeS, marginSizeS, marginSizeS)
                            rowCount - 1 -> setMargins(marginSizeS, marginSizeS, marginSizeM, marginSizeS)
                            else -> setMargins(marginSizeS, marginSizeS, marginSizeS, marginSizeS)
                        }
                        textView.layoutParams = this
                    }

                    textView.setOnSingleClickListener(eachItemSingleClickListener)

                    if (isPositionValid(lastSelectedIndex) && lastSelectedIndex == indexOfTotal) {
                        textView.isSelected = true
                        lastColumnSelectedIndex = position
                        lastRowSelectedIndex = i
                        lastSelectedIndex = Const.NO_POSITION
                    }
                    bindingView.layoutBrandTagRoot.addView(textView)
                    L.i("InflateBrandTag!!! childAdded!! Count: ${bindingView.layoutBrandTagRoot.childCount}")
                }
            }
        }

        private fun findIndexOfTotal(position: Int, index: Int): Int {
            var previousSum = 0
            for (i in 0 until position) {
                previousSum += verticalLayoutMap[i] ?: 0
            }
            return previousSum + index
        }

    }
}