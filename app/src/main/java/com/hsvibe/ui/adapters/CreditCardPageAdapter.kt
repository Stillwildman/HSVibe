package com.hsvibe.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.hsvibe.R
import com.hsvibe.callbacks.OnAnyItemClickCallback
import com.hsvibe.databinding.InflateAddCreditCardBinding
import com.hsvibe.databinding.InflateCreditCardBinding
import com.hsvibe.model.actions.CreditCardAction
import com.hsvibe.model.items.ItemCardList
import com.hsvibe.utilities.setOnSingleClickListener

/**
 * Created by Vincent on 2022/5/10.
 */
class CreditCardPageAdapter(
    private val cardList: MutableList<ItemCardList.CardData.CardDetail> = mutableListOf(),
    private val clickCallback: OnAnyItemClickCallback<CreditCardAction>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_CREDIT_CARD = 0
        private const val TYPE_ADD_NEW_CARD = 1
    }

    private inner class CreditCardViewHolder(val bindingView: InflateCreditCardBinding) : RecyclerView.ViewHolder(bindingView.root)

    private inner class AddNewCardViewHolder(val bindingView: InflateAddCreditCardBinding) : RecyclerView.ViewHolder(bindingView.root)

    override fun getItemCount(): Int {
        return cardList.size + 1
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
                    detail = cardList[position]

                    root.setOnSingleClickListener {
                        clickCallback.onItemClick(CreditCardAction.OnCreditCardClick(cardList[position].key))
                    }

                    buttonDelete.setOnSingleClickListener {
                        clickCallback.onItemClick(CreditCardAction.OnDeleteCardClick(cardList[position].key))
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

    fun updateList(cardList: List<ItemCardList.CardData.CardDetail>) {
        this.cardList.apply {
            clear()
            addAll(0, cardList)
        }
        refresh()
    }

    fun refresh() {
        this.notifyItemRangeChanged(0, itemCount)
    }
}