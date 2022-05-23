package com.hsvibe.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.hsvibe.R
import com.hsvibe.databinding.InflateTransactionRowBinding
import com.hsvibe.model.DifferItems
import com.hsvibe.model.items.ItemTransactions
import com.hsvibe.ui.bases.BaseBindingPagedRecycler

/**
 * Created by Vincent on 2022/5/23.
 */
class TransactionListAdapter : BaseBindingPagedRecycler<ItemTransactions.ContentData, InflateTransactionRowBinding>(DifferItems.TransactionDiffer) {

    override fun getLayoutId(): Int = R.layout.inflate_transaction_row

    override fun onBindingViewHolder(holder: RecyclerView.ViewHolder, bindingView: InflateTransactionRowBinding, position: Int) {
        bindingView.item = getItem(position)
    }

    override fun onBindingViewHolder(holder: RecyclerView.ViewHolder, bindingView: InflateTransactionRowBinding, position: Int, payload: Any?) {}
}