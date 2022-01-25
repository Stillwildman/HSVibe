package com.hsvibe.ui.fragments.member

import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.databinding.FragmentPointHistoryBinding
import com.hsvibe.ui.adapters.BonusListAdapter
import com.hsvibe.ui.bases.BaseActionBarFragment
import com.hsvibe.utilities.init
import com.hsvibe.viewmodel.BonusViewModel
import com.hsvibe.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * Created by Vincent on 2021/8/23.
 */
class UiPointHistoryFragment : BaseActionBarFragment<FragmentPointHistoryBinding>(), SwipeRefreshLayout.OnRefreshListener {

    private val mainViewModel by activityViewModels<MainViewModel>()

    private val bonusViewModel by viewModels<BonusViewModel>()

    override fun getFragmentLayoutId(): Int = R.layout.fragment_point_history

    override fun getTitleRes(): Int = R.string.point_record

    override fun getAnimType(): AnimType = AnimType.SlideFromRight

    override fun getActionBarBackgroundColor(): Int {
        return ContextCompat.getColor(AppController.getAppContext(), R.color.app_background_gradient_top)
    }

    override fun onInitCompleted() {
        bindPointsRecord()
        initLayouts()
        refreshUserBonus()
        observeLoadingStatus()
        startCollectBonusFlow()
    }

    private fun bindPointsRecord() {
        binding.mainViewModel = mainViewModel
        binding.lifecycleOwner = this
    }

    private fun initLayouts() {
        binding.layoutSwipeRefresh.init(this)

        binding.recyclerPointHistory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = BonusListAdapter()
        }
    }

    private fun refreshUserBonus() {
        mainViewModel.getUserBonus()
    }

    private fun observeLoadingStatus() {
        bonusViewModel.liveLoadingStatus.observe(viewLifecycleOwner) {
            handleLoadingStatus(it)
        }
    }

    override fun showLoadingCircle() {
        binding.layoutSwipeRefresh.isRefreshing = true
    }

    override fun hideLoadingCircle() {
        binding.layoutSwipeRefresh.isRefreshing = false
    }

    private fun startCollectBonusFlow() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            bonusViewModel.getBonusFlow().collectLatest {
                getBonusListAdapter().submitData(it)
            }
        }
    }

    private fun getBonusListAdapter(): BonusListAdapter {
        return binding.recyclerPointHistory.adapter as BonusListAdapter
    }

    override fun onRefresh() {
        refreshUserBonus()
        getBonusListAdapter().refresh()
    }
}