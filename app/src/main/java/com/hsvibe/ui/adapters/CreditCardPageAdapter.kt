package com.hsvibe.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hsvibe.R
import com.hsvibe.callbacks.OnAnyItemClickCallback
import com.hsvibe.databinding.InflateAddCreditCardBinding
import com.hsvibe.databinding.InflateCreditCardBinding
import com.hsvibe.model.DifferItems
import com.hsvibe.model.actions.CreditCardAction
import com.hsvibe.model.items.ItemCardList
import com.hsvibe.utilities.setOnSingleClickListener

/**
 * Created by Vincent on 2022/5/10.
 */
class CreditCardPageAdapter(private val clickCallback: OnAnyItemClickCallback<CreditCardAction>) :
    ListAdapter<ItemCardList.CardData.CardDetail, RecyclerView.ViewHolder>(DifferItems.CardDetailDiffer) {

    companion object {
        private const val TYPE_CREDIT_CARD = 0
        private const val TYPE_ADD_NEW_CARD = 1
    }

    private inner class CreditCardViewHolder(val bindingView: InflateCreditCardBinding) : RecyclerView.ViewHolder(bindingView.root)

    private inner class AddNewCardViewHolder(val bindingView: InflateAddCreditCardBinding) : RecyclerView.ViewHolder(bindingView.root)

    override fun getItemCount(): Int {
        return super.getItemCount() + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == itemCount - 1) TYPE_ADD_NEW_CARD else TYPE_CREDIT_CARD
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_CREDIT_CARD) {
            val bindingView = DataBindingUtil.inflate<InflateCreditCardBinding>(LayoutInflater.from(parent.context), R.layout.inflate_credit_card, parent, false)
            CreditCardViewHolder(bindingView)
        }
        else {
            val bindingView = DataBindingUtil.inflate<InflateAddCreditCardBinding>(LayoutInflater.from(parent.context), R.layout.inflate_add_credit_card, parent, false)
            AddNewCardViewHolder(bindingView)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CreditCardViewHolder -> {
                holder.bindingView.apply {
                    detail = getItem(position)

                    root.setOnSingleClickListener {
                        clickCallback.onItemClick(CreditCardAction.OnCreditCardClick(getItem(position).key))
                    }

                    buttonDelete.setOnSingleClickListener {
                        clickCallback.onItemClick(CreditCardAction.OnDeleteCardClick(getItem(position).key))
                    }
                }
            }
            is AddNewCardViewHolder -> {
                holder.bindingView.root.setOnSingleClickListener {
                    clickCallback.onItemClick(CreditCardAction.OnAddNewCardClick)
                }
            }
        }
    }

    fun refresh() {
        this.notifyItemRangeChanged(0, itemCount - 1)
    }
}