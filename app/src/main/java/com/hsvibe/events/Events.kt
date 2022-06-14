package com.hsvibe.events

/**
 * Created by Vincent on 2022/6/6.
 */
class Events {

    class OnCouponUsed

    class OnBonusGet(val amount: String, val rewardPoint: String)

    class OnPointReceived

}