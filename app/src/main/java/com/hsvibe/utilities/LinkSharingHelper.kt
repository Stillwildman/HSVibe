package com.hsvibe.utilities

import android.net.Uri
import com.google.firebase.dynamiclinks.ShortDynamicLink
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.ktx.Firebase
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.model.Urls

/**
 * Created by Vincent on 2022/6/10.
 */
object LinkSharingHelper {

    fun prepareCouponSharingLink(title: String, brief: String, couponUuid: String, onLinkReady: (link: String) -> Unit) {
        val sharedText = AppController.getAppContext().getString(R.string.coupon_share_format, title, brief)
        val deepLink = Uri.parse("${Urls.WEB_HS_VIBE}?type=coupon&id=${couponUuid}")

        Firebase.dynamicLinks.shortLinkAsync(ShortDynamicLink.Suffix.SHORT) {
            link = deepLink
            domainUriPrefix = Urls.HSVIBE_DYNAMIC_LINK
            androidParameters(AppController.getAppContext().packageName) {
                fallbackUrl = Uri.parse(Urls.HSVIBE_DYNAMIC_LINK_FALLBACK)
            }
            iosParameters("com.hs.vibe") {
                appStoreId = "1449947800"
                setFallbackUrl(Uri.parse(Urls.HSVIBE_DYNAMIC_LINK_FALLBACK))
            }
        }.addOnSuccessListener { (shortLink, flowchartLink) ->
            val fullLink = sharedText + "\n\n" + shortLink
            onLinkReady(fullLink)
        }.addOnFailureListener {
            L.e("${it.printStackTrace()}")
        }
    }

}