package com.dzen.campfire.screens.feed.filters

import android.view.View
import android.widget.Switch
import android.widget.TextView
import com.dzen.campfire.R
import com.dzen.campfire.api.API_RESOURCES
import com.dzen.campfire.api.API_TRANSLATE
import com.sayzen.campfiresdk.controllers.ControllerTranslate.t
import com.sup.dev.android.tools.ToolsResources
import com.sup.dev.android.views.cards.Card

class CardFiltersScreen(
        val index: Long,
        val splash: SplashFiltersScreens
) : Card(R.layout.screen_feed_splash_filters_card_screen) {

    override fun bindView(view: View) {
        val vTitle: TextView = view.findViewById(R.id.vTitle)
        val vSubTitle: TextView = view.findViewById(R.id.vSubTitle)
        val vSwitcher: Switch = view.findViewById(R.id.vSwitcher)
        val vArrow: View = view.findViewById(R.id.vArrow)

        vTitle.text = getTitle(index)
        vSubTitle.text = getSubTitle(index)

        vSwitcher.isChecked = splash.list.contains(index)
        vArrow.visibility = if(vSwitcher.isChecked && splash.list.indexOf(index) > 0) View.VISIBLE else View.INVISIBLE

        vSwitcher.setOnClickListener {
            if(vSwitcher.isChecked) splash.list.add(index)
            else splash.list.remove(index)
            splash.updateAll()
        }

        vArrow.setOnClickListener {
            val indexOf = splash.list.indexOf(index)
            if(indexOf > 0){
                splash.list.removeAt(indexOf)
                splash.list.add(indexOf-1, index)
            }
            splash.updateAll()
        }
    }

    private fun getTitle(index: Long): String {
        return when (index) {
            1L -> t(API_TRANSLATE.app_subscriptions)
            2L -> t(API_TRANSLATE.app_all)
            3L -> t(API_TRANSLATE.app_feed_best)
            4L -> t(API_TRANSLATE.app_feed_good)
            5L -> t(API_TRANSLATE.app_feed_abyss)
            6L -> t(API_TRANSLATE.app_all) +" (+" + t(API_TRANSLATE.app_subscriptions) +")"
            else -> t(API_TRANSLATE.app_error)
        }
    }
    private fun getSubTitle(index: Long): String {
        return when (index) {
            1L -> t(API_TRANSLATE.feed_subtitle_subscriptions)
            2L -> t(API_TRANSLATE.feed_subtitle_all)
            3L -> t(API_TRANSLATE.feed_subtitle_best)
            4L -> t(API_TRANSLATE.feed_subtitle_good)
            5L -> t(API_TRANSLATE.feed_subtitle_abyss)
            6L -> t(API_TRANSLATE.feed_subtitle_all_with_subscriptions)
            else -> t(API_TRANSLATE.app_error)
        }
    }


}