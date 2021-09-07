package com.hsvibe.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.hsvibe.R
import com.hsvibe.databinding.InflateBonusRowBinding
import com.hsvibe.model.DifferItems
import com.hsvibe.model.items.ItemAccountBonus
import com.hsvibe.ui.bases.BaseBindingPagedRecycler

/**
 * Created by Vincent on 2021/9/8.
 */
class BonusListAdapter : BaseBindingPagedRecycler<ItemAccountBonus.ContentData, InflateBonusRowBinding>(DifferItems.BonusItemDiffer) {

    override fun getLayoutId(): Int = R.layout.inflate_bonus_row

    override fun onBindingViewHolder(holder: RecyclerView.ViewHolder, bindingView: InflateBonusRowBinding, position: Int) {
        bindingView.item = getItem(position)
    }

    override fun onBindingViewHolder(holder: RecyclerView.ViewHolder, bindingView: InflateBonusRowBinding, position: Int, payload: Any?) {

    }
}