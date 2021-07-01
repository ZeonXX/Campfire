package com.dzen.campfire.screens.feed.filters

import android.view.View
import android.widget.Button
import com.dzen.campfire.R
import com.dzen.campfire.api.API_TRANSLATE
import com.sayzen.campfiresdk.controllers.t
import com.sup.dev.android.views.cards.Card

class CardFiltersScreenButton(
        val onSave:()->Unit,
        val onCancel:()->Unit
) : Card(R.layout.screen_feed_splash_filters_card_screen_button){

    override fun bindView(view: View) {
        val vCancel:Button = view.findViewById(R.id.vCancel)
        val vSave: Button = view.findViewById(R.id.vSave)

        vCancel.text = t(API_TRANSLATE.app_cancel)
        vSave.text = t(API_TRANSLATE.app_save)

        vCancel.setOnClickListener { onCancel.invoke() }
        vSave.setOnClickListener { onSave.invoke() }
    }


}