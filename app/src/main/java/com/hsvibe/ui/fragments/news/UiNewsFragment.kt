package com.hsvibe.ui.fragments.news

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hsvibe.R
import com.hsvibe.callbacks.OnContentDataClickCallback
import com.hsvibe.databinding.InflateNewsListBinding
import com.hsvibe.model.ApiConst
import com.hsvibe.model.Const
import com.hsvibe.model.items.ItemContent
import com.hsvibe.ui.adapters.NewsListAdapter
import com.hsvibe.ui.bases.BaseActionBarFragment
import com.hsvibe.ui.fragments.UiBasicWebFragment
import com.hsvibe.viewmodel.ContentViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by Vincent on 2021/8/4.
 */
class UiNewsFragment private constructor() : BaseActionBarFragment<InflateNewsListBinding>(), OnContentDataClickCallback {

    companion object {
        fun newInstance(specificPosition: Int = Const.NO_POSITION): UiNewsFragment {
            return UiNewsFragment().apply {
                arguments = Bundle(1).also { it.putInt(Const.BUNDLE_SPECIFIC_POSITION, specificPosition) }
            }
        }
    }

    private val newsViewModel by viewModels<ContentViewModel>()

    private var hasInitialScrolled = false

    override fun getFragmentLayoutId(): Int = R.layout.inflate_news_list

    override fun getTitleRes(): Int = R.string.hot_news

    override fun getAnimType(): AnimType = AnimType.SlideFromRight

    override fun onInitCompleted() {
        initRecycler()
        startObserveLoadingStatus()
        startCollectNewsFlow()
    }

    private fun initRecycler() {
        val layoutOrientation = if (arguments?.getInt(Const.BUNDLE_SPECIFIC_POSITION) ?: Const.NO_POSITION == Const.NO_POSITION) {
            RecyclerView.VERTICAL
        } else {
            RecyclerView.HORIZONTAL
        }

        binding.recyclerNews.apply {
            layoutManager = LinearLayoutManager(context, layoutOrientation, false)
            adapter = NewsListAdapter(layoutManager as LinearLayoutManager, this@UiNewsFragment)
        }
    }

    private fun startObserveLoadingStatus() {
        newsViewModel.liveLoadingStatus.observe(viewLifecycleOwner) {
            handleLoadingStatus(it)
        }
    }

    private fun startCollectNewsFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            newsViewModel.getContentFlow(ApiConst.CATEGORY_NEWS).collectLatest { pagingContentDataList ->
                scrollIfNeed()
                getNewsListAdapter().submitData(pagingContentDataList)
            }
        }
    }

    private fun scrollIfNeed() {
        if (!hasInitialScrolled) {
            val specificPosition = arguments?.getInt(Const.BUNDLE_SPECIFIC_POSITION) ?: Const.NO_POSITION
            if (specificPosition != Const.NO_POSITION) {
                binding.recyclerNews.postDelayed({
                    scrollToSpecificPosition(specificPosition)
                }, 250)
            }
            hasInitialScrolled = true
        }
    }

    private fun scrollToSpecificPosition(specificPosition: Int) {
        binding.recyclerNews.layoutManager.let {
            (it as? LinearLayoutManager)?.scrollToPositionWithOffset(specificPosition, if (specificPosition > 0) 1 else 0)
        }
    }

    private fun getNewsListAdapter(): NewsListAdapter {
        return binding.recyclerNews.adapter as NewsListAdapter
    }

    override fun onContentDataClick(contentItem: ItemContent.ContentData, position: Int) {
        if (position == Const.NO_POSITION) {
            openDialogFragment(UiBasicWebFragment.newInstance(contentItem.share_url))
        }
        else {
            getNewsListAdapter().changeLayout()
            binding.recyclerNews.postDelayed({
                scrollToSpecificPosition(position)
            }, 200)
        }
    }

    private fun popBackOrResumeToVertical() {
        if (getNewsListAdapter().isDetailView()) {
            getNewsListAdapter().changeLayout()
        }
        else {
            popBack()
        }
    }

    override fun onDialogBackPressed(): Boolean {
        popBackOrResumeToVertical()
        return true
    }
}