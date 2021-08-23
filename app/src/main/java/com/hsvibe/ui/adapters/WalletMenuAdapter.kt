package com.hsvibe.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.hsvibe.R
import com.hsvibe.callbacks.OnAnyItemClickCallback
import com.hsvibe.databinding.InflateWalletMenuBinding
import com.hsvibe.ui.bases.BaseBindingRecycler

/**
 * Created by Vincent on 2021/8/23.
 */
class WalletMenuAdapter(
    private val iconTextPairList: List<Pair<Int, String>>,
    private val gridHeight: Int,
    private val onItemClickCallback: OnAnyItemClickCallback<Int>
) : BaseBindingRecycler<InflateWalletMenuBinding>() {

    override fun getLayoutId(): Int = R.layout.inflate_wallet_menu

    override fun onBindingViewHolder(holder: RecyclerView.ViewHolder, bindingView: InflateWalletMenuBinding, position: Int) {
        iconTextPairList[position].let {
            bindingView.onItemClickCallback = onItemClickCallback
            bindingView.iconRes = it.first
            bindingView.iconText = it.second
        }
        bindingView.root.layoutParams.height = gridHeight
    }

    override fun onBindingViewHolder(holder: RecyclerView.ViewHolder, bindingView: InflateWalletMenuBinding, position: Int, payload: Any?) {

    }

    override fun getItemCount(): Int = iconTextPairList.size
}