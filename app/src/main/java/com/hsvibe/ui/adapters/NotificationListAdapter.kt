package com.hsvibe.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.hsvibe.R
import com.hsvibe.callbacks.OnAnyItemClickCallback
import com.hsvibe.databinding.InflateNotificationBinding
import com.hsvibe.model.DifferItems
import com.hsvibe.model.items.ItemContent
import com.hsvibe.ui.bases.BaseBindingPagedRecycler
import com.hsvibe.utilities.SettingManager
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Vincent on 2021/8/13.
 */
class NotificationListAdapter(private val itemClickCallback: OnAnyItemClickCallback<Int>) :
    BaseBindingPagedRecycler<ItemContent.ContentData, InflateNotificationBinding>(DifferItems.ContentItemDiffer) {

    private val dateFormat by lazy { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) }
    private val lastNewestTime by lazy { SettingManager.getNewestNotificationTime() }

    override fun getLayoutId(): Int = R.layout.inflate_notification

    override fun onBindingViewHolder(holder: RecyclerView.ViewHolder, bindingView: InflateNotificationBinding, position: Int) {
        getItem(position)?.let {
            it.setUnreadStatus(lastNewestTime, dateFormat)

            bindingView.position = position
            bindingView.content = it
            bindingView.itemClickCallback = itemClickCallback
        }
    }

    override fun onBindingViewHolder(holder: RecyclerView.ViewHolder, bindingView: InflateNotificationBinding, position: Int, payload: Any?) {
        payload?.let {
            bindingView.content = getItem(position)
        }
    }

    fun getItemAndSetRead(position: Int): ItemContent.ContentData? {
        return getItem(position)?.apply {
            isUnread = false
            notifyItemChanged(position, true)
        }
    }

    fun getFirstItemTime(): String? {
        return if (itemCount > 0) getItem(0)?.updated_at else null
    }
}