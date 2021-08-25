package com.hsvibe.ui.fragments.news

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hsvibe.R
import com.hsvibe.callbacks.OnAnyItemClickCallback
import com.hsvibe.databinding.FragmentNotificationListBinding
import com.hsvibe.model.ApiConst
import com.hsvibe.model.Const
import com.hsvibe.model.items.ItemContent
import com.hsvibe.ui.adapters.NotificationListAdapter
import com.hsvibe.ui.bases.BaseFragment
import com.hsvibe.utilities.L
import com.hsvibe.utilities.SettingManager
import com.hsvibe.viewmodel.ContentViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Vincent on 2021/8/13.
 */
class UiNotificationPage private constructor() : BaseFragment<FragmentNotificationListBinding>(),
    SwipeRefreshLayout.OnRefreshListener,
    OnAnyItemClickCallback<Int> {

    companion object {
        fun newInstance(category: Int): UiNotificationPage {
            return UiNotificationPage().apply {
                arguments = Bundle().also { it.putInt(Const.BUNDLE_CATEGORY, category) }
            }
        }
    }

    private val viewModel by viewModels<ContentViewModel>()

    private val dateFormat by lazy { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) }
    private val lastNewestTime by lazy { SettingManager.getNewestNotificationTime() }

    override fun getLayoutId(): Int = R.layout.fragment_notification_list

    override fun getLoadingView(): View? = null

    override fun init() {
        initSwipeRefreshLayout()
        initRecycler()
        startObserveLoadingStatus()
        startCollectContentFlow()
    }

    private fun initSwipeRefreshLayout() {
        bindingView.layoutSwipeRefresh.apply {
            setColorSchemeResources(
                R.color.md_green_500, R.color.md_amber_400,
                R.color.md_light_blue_A700, R.color.md_red_500
            )
            setOnRefreshListener(this@UiNotificationPage)
        }
    }

    private fun initRecycler() {
        bindingView.recyclerNotification.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = NotificationListAdapter(this@UiNotificationPage)
        }
    }

    private fun startObserveLoadingStatus() {
        viewModel.liveLoadingStatus.observe(viewLifecycleOwner) {
            handleLoadingStatus(it)
        }
    }

    override fun showLoading() {
        showRefreshing()
    }

    override fun hideLoading() {
        hideRefreshing()
    }

    private fun showRefreshing() {
        bindingView.layoutSwipeRefresh.isRefreshing = true
    }

    private fun hideRefreshing() {
        bindingView.layoutSwipeRefresh.isRefreshing = false
    }

    private fun startCollectContentFlow() {
        val category = arguments?.getInt(Const.BUNDLE_CATEGORY) ?: ApiConst.CATEGORY_PERSONAL_NOTIFICATION

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.getContentFlow(category).collectLatest { pagingContentData ->
                updateUnreadStatus(pagingContentData)
                getNotificationAdapter().submitData(pagingContentData)
            }
        }
    }

    private suspend fun updateUnreadStatus(pagingContentData: PagingData<ItemContent.ContentData>) {
        withContext(Dispatchers.IO) {
            pagingContentData.map {
                it.setUnreadStatus(lastNewestTime, dateFormat)
            }
        }
    }

    private fun getNotificationAdapter(): NotificationListAdapter {
        return bindingView.recyclerNotification.adapter as NotificationListAdapter
    }

    override fun onRefresh() {
        getNotificationAdapter().refresh()
    }

    override fun onItemClick(item: Int) {
        getNotificationAdapter().getItemAndSetRead(item)?.let { openWebDialogFragment(it.share_url) }
    }

    override fun onDestroy() {
        super.onDestroy()
        saveNewestItemTime()
    }

    private fun saveNewestItemTime() {
        getNotificationAdapter().getFirstItemTime()?.let { it ->
            try {
                val itemTime = dateFormat.parse(it)?.time ?: 0
                SettingManager.setNewestNotificationTime(itemTime.also { time -> L.i("SaveNewestNotificationTime: $time") })
            }
            catch (e: ParseException) {
                e.printStackTrace()
            }
        }
    }

    override fun onBackButtonPressed(): Boolean = true
}