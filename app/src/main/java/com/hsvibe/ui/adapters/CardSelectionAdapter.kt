package com.hsvibe.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.hsvibe.R
import com.hsvibe.callbacks.OnAnyItemClickCallback
import com.hsvibe.databinding.InflateCreditCardSelectionBinding
import com.hsvibe.model.Const
import com.hsvibe.model.items.ItemCardList
import com.hsvibe.ui.bases.BaseBindingRecycler
import com.hsvibe.utilities.setOnSingleClickListener

/**
 * Created by Vincent on 2022/5/18.
 */
class CardSelectionAdapter(
    private val cardList: List<ItemCardList.CardData.CardDetail>,
    private val clickCallback: OnAnyItemClickCallback<ItemCardList.CardData.CardDetail>
    ) : BaseBindingRecycler<InflateCreditCardSelectionBinding>() {

    private var selectedIndex: Int = Const.NO_POSITION

    override fun getLayoutId(): Int = R.layout.inflate_credit_card_selection

    override fun getItemCount(): Int = cardList.size

    override fun onBindingViewHolder(holder: RecyclerView.ViewHolder, bindingView: InflateCreditCardSelectionBinding, position: Int) {
        bindingView.apply {
            detail = cardList[position]
            isSelected = selectedIndex == position

            layoutCardSelection.setOnSingleClickListener {
                clickCallback.onItemClick(cardList[position])
            }
        }
    }

    override fun onBindingViewHolder(holder: RecyclerView.ViewHolder, bindingView: InflateCreditCardSelectionBinding, position: Int, payload: Any?) {
        if (payload is Boolean) {
            bindingView.isSelected = selectedIndex == position
        }
        else {
            onBindingViewHolder(holder, bindingView, position)
        }
    }

    fun updateSelectedCard(cardKey: String?) {
        cardList.forEachIndexed loop@ { index, cardDetail ->
            if (cardDetail.key == cardKey) {
                selectedIndex = index
                return@loop
            }
        }
        this.notifyItemRangeChanged(0, itemCount, true)
    }
}