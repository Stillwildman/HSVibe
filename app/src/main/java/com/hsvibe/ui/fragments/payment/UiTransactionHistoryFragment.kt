package com.hsvibe.ui.fragments.payment

import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.databinding.FragmentTransactionHistoryBinding
import com.hsvibe.ui.adapters.TransactionListAdapter
import com.hsvibe.ui.bases.BaseActionBarFragment
import com.hsvibe.utilities.getContextSafely
import com.hsvibe.utilities.init
import com.hsvibe.viewmodel.TransactionViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by Vincent on 2022/5/23.
 */
class UiTransactionHistoryFragment : BaseActionBarFragment<FragmentTransactionHistoryBinding>(), SwipeRefreshLayout.OnRefreshListener {

    override fun getFragmentLayoutId(): Int = R.layout.fragment_transaction_history

    override fun getTitleRes(): Int = R.string.hspay_transaction_history

    override fun getAnimType(): AnimType = AnimType.SlideFromRight

    override fun getActionBarBackgroundColor(): Int {
        return ContextCompat.getColor(AppController.getAppContext(), R.color.app_background_gradient_top)
    }

    private val viewModel by viewModels<TransactionViewModel>()

    override fun onInitCompleted() {
        initRecycler()
        startObserveLoadingStatus()
        startCollectTransactionFlow()
    }

    private fun initRecycler() {
        binding.recyclerTransactions.apply {
            layoutManager = LinearLayoutManager(getContextSafely())
            adapter = TransactionListAdapter()
        }

        binding.layoutSwipeRefresh.init(this)
    }

    private fun startObserveLoadingStatus() {
        viewModel.liveLoadingStatus.observe(viewLifecycleOwner) {
            handleLoadingStatus(it)
        }
    }

    private fun startCollectTransactionFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getContentFlow().collectLatest { pagingContentDataList ->
                getTransactionListAdapter()?.apply {
                    submitData(pagingContentDataList)
                    showNoDataHint(itemCount == 0)
                }
            }
        }
    }

    private fun getTransactionListAdapter(): TransactionListAdapter? {
        return binding.recyclerTransactions.adapter as? TransactionListAdapter
    }

    private fun showNoDataHint(isShow: Boolean) {
        binding.textNoDataHint.visibility = if (isShow) View.VISIBLE else View.GONE
        binding.layoutSwipeRefresh.isRefreshing = false
    }

    override fun onRefresh() {
        getTransactionListAdapter()?.refresh()
    }
}