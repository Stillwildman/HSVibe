package com.hsvibe.model.actions

/**
 * Created by Vincent on 2022/5/10.
 */
sealed class CreditCardAction {

    object OnAddNewCardClick : CreditCardAction()

    class OnCreditCardClick(val key: String) : CreditCardAction()

    class OnDeleteCardClick(val key: String) : CreditCardAction()

}