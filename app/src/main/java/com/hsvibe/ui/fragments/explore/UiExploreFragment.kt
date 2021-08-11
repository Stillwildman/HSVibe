package com.hsvibe.ui.fragments.explore

import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.hsvibe.R
import com.hsvibe.callbacks.OnContentDataClickCallback
import com.hsvibe.databinding.FragmentExploreBinding
import com.hsvibe.model.ApiConst
import com.hsvibe.model.items.ItemContent
import com.hsvibe.ui.adapters.ExploreListAdapter
import com.hsvibe.ui.bases.BaseFragment
import com.hsvibe.ui.fragments.UiBasicWebFragment
import com.hsvibe.viewmodel.ContentViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by Vincent on 2021/8/9.
 */
class UiExploreFragment : BaseFragment<FragmentExploreBinding>(), OnContentDataClickCallback {

    private val viewModel by viewModels<ContentViewModel>()

    override fun getLayoutId(): Int = R.layout.fragment_explore

    override fun getLoadingView(): View = bindingView.loadingCircle

    override fun init() {
        initRecycler()
        startObserveLoadingStatus()
        startCollectContentFlow()
    }

    private fun initRecycler() {
        bindingView.recyclerExplore.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ExploreListAdapter(this@UiExploreFragment)
        }
    }

    private fun startObserveLoadingStatus() {
        viewModel.liveLoadingStatus.observe(viewLifecycleOwner) {
            handleLoadingStatus(it)
        }
    }

    private fun startCollectContentFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getContentFlow(ApiConst.CATEGORY_EXPLORE).collectLatest { pagingContentDataList ->
                getExploreListAdapter().submitData(pagingContentDataList)
            }
        }
    }

    private fun getExploreListAdapter(): ExploreListAdapter {
        return bindingView.recyclerExplore.adapter as ExploreListAdapter
    }

    override fun onContentDataClick(contentItem: ItemContent.ContentData, position: Int) {
        openDialogFragment(UiBasicWebFragment.newInstance(contentItem.share_url))
    }

    override fun onBackButtonPressed(): Boolean = false
}