package com.hsvibe.utilities

import android.net.Uri
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.ktx.Firebase
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.model.Urls
import com.hsvibe.model.items.ItemCoupon
import java.text.MessageFormat

/**
 * Created by Vincent on 2022/6/10.
 */
object LinkSharingHelper {

    private const val IOS_APP_BUNDLE_ID = "com.hs.vibe"
    private const val IOS_APP_STORE_ID = "1449947800"

    fun prepareCouponSharingLink(coupon: ItemCoupon.ContentData, onLinkReady: (link: String) -> Unit) {
        val couponDescription = AppController.getAppContext().getString(R.string.coupon_share_format, coupon.title, coupon.brief)

        val deepLink = Uri.parse(MessageFormat.format(Urls.HSVIBE_COUPON_SHARING_LINK, coupon.uuid, coupon.uuid))

        Firebase.dynamicLinks.shortLinkAsync(ShortDynamicLink.Suffix.SHORT) {
            link = deepLink
            domainUriPrefix = Urls.HSVIBE_DYNAMIC_LINK
            androidParameters(AppController.getAppContext().packageName) {
                fallbackUrl = Uri.parse(Urls.HSVIBE_DYNAMIC_LINK_FALLBACK)
            }
            iosParameters(IOS_APP_BUNDLE_ID) {
                appStoreId = IOS_APP_STORE_ID
                setFallbackUrl(Uri.parse(Urls.HSVIBE_DYNAMIC_LINK_FALLBACK))
            }
            socialMetaTagParameters {
                title = "${coupon.title} ${coupon.subtitle}"
                description = "${coupon.brief} ${coupon.content}"
                coupon.getMediumUrl().takeIf { it.isNotNullOrEmpty() }?.let { imageUrl = Uri.parse(it) }
            }
        }.addOnSuccessListener { (shortLink, flowchartLink) ->
            val fullLink = couponDescription + "\n\n" + shortLink
            onLinkReady(fullLink)
        }.addOnFailureListener {
            it.printStackTrace()
        }
    }

}