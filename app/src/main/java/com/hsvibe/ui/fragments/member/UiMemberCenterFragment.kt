package com.hsvibe.ui.fragments.member

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.callbacks.OnAnyItemClickCallback
import com.hsvibe.databinding.FragmentMemberCenterBinding
import com.hsvibe.model.Urls
import com.hsvibe.model.items.ItemMemberOption
import com.hsvibe.ui.adapters.MemberOptionListAdapter
import com.hsvibe.ui.bases.BaseActionBarFragment
import com.hsvibe.ui.fragments.coupons.UiCouponHistoryFragment
import com.hsvibe.utilities.DeviceUtil
import com.hsvibe.utilities.DialogHelper
import com.hsvibe.utilities.getContextSafely
import com.hsvibe.viewmodel.MainViewModel

/**
 * Created by Vincent on 2021/8/14.
 */
class UiMemberCenterFragment : BaseActionBarFragment<FragmentMemberCenterBinding>(), OnAnyItemClickCallback<ItemMemberOption> {

    companion object {
        private const val INDEX_PERSONAL_INFO = 0
        private const val INDEX_CHANGE_PASSWORD = 1
        private const val INDEX_POINT_RECORD = 2
        private const val INDEX_MY_COUPON = 3
        private const val INDEX_TERMS = 4
        private const val INDEX_CONTACT_US = 5
    }

    private val mainViewModel by activityViewModels<MainViewModel>()

    override fun getFragmentLayoutId(): Int = R.layout.fragment_member_center

    override fun getTitleRes(): Int = R.string.member_center

    override fun getAnimType(): AnimType = AnimType.SlideUp

    override fun onInitCompleted() {
        bindUserInfo()
        initRecycler()
    }

    private fun bindUserInfo() {
        binding.mainViewModel = mainViewModel
        binding.lifecycleOwner = this
    }

    private fun initRecycler() {
        binding.recyclerMemberOptions.apply {
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, RecyclerView.VERTICAL))
            adapter = MemberOptionListAdapter(getOptionList(), this@UiMemberCenterFragment)
        }
    }

    private fun getOptionList(): List<ItemMemberOption> {
        val titleArray = AppController.getAppContext().resources.getStringArray(R.array.member_center_options)

        return mutableListOf<ItemMemberOption>().apply {
            titleArray.forEachIndexed { i, title ->
                add(ItemMemberOption(i, title))
            }
            add(ItemMemberOption(size, DeviceUtil.getCombinedVersionName()))
        }
    }

    override fun onItemClick(item: ItemMemberOption) {
        when (item.index) {
            INDEX_PERSONAL_INFO -> openDialogFragment(UiMemberInfoFragment.newInstance(false))
            INDEX_CHANGE_PASSWORD -> openDialogFragment(UiPasswordModifyFragment())
            INDEX_POINT_RECORD -> openDialogFragment(UiPointHistoryFragment())
            INDEX_MY_COUPON -> openDialogFragment(UiCouponHistoryFragment())
            INDEX_TERMS -> showMemberTermsDialog()
            INDEX_CONTACT_US -> openWebDialogFragment(Urls.WEB_HS_VIBE)
        }
    }

    private fun showMemberTermsDialog() {
        DialogHelper.showMemberTermsDialog(getContextSafely(), {
            openWebDialogFragment(Urls.WEB_HS_VIBE_TERMS)
        }, {
            openWebDialogFragment(Urls.WEB_HS_VIBE_PRIVACY)
        })
    }
}