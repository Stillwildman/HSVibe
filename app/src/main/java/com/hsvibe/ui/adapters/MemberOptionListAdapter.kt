package com.hsvibe.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.hsvibe.R
import com.hsvibe.callbacks.OnAnyItemClickCallback
import com.hsvibe.databinding.InflateMemberOptionBinding
import com.hsvibe.model.items.ItemMemberOption
import com.hsvibe.ui.bases.BaseBindingRecycler

/**
 * Created by Vincent on 2021/8/14.
 */
class MemberOptionListAdapter(
    private val optionList: List<ItemMemberOption>,
    private val itemClickCallback: OnAnyItemClickCallback<ItemMemberOption>) : BaseBindingRecycler<InflateMemberOptionBinding>() {

    override fun getLayoutId(): Int = R.layout.inflate_member_option

    override fun onBindingViewHolder(holder: RecyclerView.ViewHolder, bindingView: InflateMemberOptionBinding, position: Int) {
        bindingView.item = optionList[position]
        bindingView.itemClickCallback = itemClickCallback
    }

    override fun onBindingViewHolder(holder: RecyclerView.ViewHolder, bindingView: InflateMemberOptionBinding, position: Int, payload: Any?) {

    }

    override fun getItemCount(): Int = optionList.size
}