package com.hsvibe.ui.fragments.payment

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hsvibe.R
import com.hsvibe.callbacks.OnAnyItemClickCallback
import com.hsvibe.databinding.FragmentCardSelectionBinding
import com.hsvibe.model.items.ItemCardList
import com.hsvibe.ui.adapters.CardSelectionAdapter
import com.hsvibe.ui.bases.BaseActionBarFragment
import com.hsvibe.utilities.getContextSafely
import com.hsvibe.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by Vincent on 2022/5/18.
 */
class UiCardSelectionFragment : BaseActionBarFragment<FragmentCardSelectionBinding>(), OnAnyItemClickCallback<ItemCardList.CardData.CardDetail> {

    override fun getFragmentLayoutId(): Int = R.layout.fragment_card_selection

    override fun getTitleRes(): Int = R.string.card

    override fun getAnimType(): AnimType = AnimType.SlideFromRight

    private val mainViewModel by activityViewModels<MainViewModel>()

    override fun onInitCompleted() {
        observeCardData()
    }

    private fun observeCardData() {
        mainViewModel.liveCreditCards.observe(viewLifecycleOwner) {
            initCardList(it.cardData.cardDetailList)
        }
    }

    private fun initCardList(cardList: List<ItemCardList.CardData.CardDetail>) {
        binding.recyclerCardSelection.apply {
            layoutManager = LinearLayoutManager(getContextSafely())
            adapter = CardSelectionAdapter(cardList, this@UiCardSelectionFragment)
        }
        observeSelectedCard()
    }

    private fun observeSelectedCard() {
        mainViewModel.livePaymentDisplay.observe(viewLifecycleOwner) {
            getCardSelectionAdapter()?.updateSelectedCard(it.selectedCardKey)
        }
    }

    private fun updateSelectedCardAndFinish(selectedCard: ItemCardList.CardData.CardDetail) {
        mainViewModel.updatePaymentCard(selectedCard.key, selectedCard.name, selectedCard.display, selectedCard.getBrandIconRes())

        lifecycleScope.launch {
            delay(500)
            popBack()
        }
    }

    private fun getCardSelectionAdapter(): CardSelectionAdapter? {
        return binding.recyclerCardSelection.adapter as? CardSelectionAdapter
    }

    override fun onItemClick(item: ItemCardList.CardData.CardDetail) {
        updateSelectedCardAndFinish(item)
    }
}