package com.dzen.campfire.screens.feed

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.dzen.campfire.R
import com.dzen.campfire.api.API_RESOURCES
import com.dzen.campfire.api.API_TRANSLATE
import com.dzen.campfire.api.requests.project.RProjectSupportGetInfo
import com.sayzen.campfiresdk.controllers.ControllerDonates
import com.sayzen.campfiresdk.controllers.api
import com.sayzen.campfiresdk.controllers.t
import com.sayzen.campfiresdk.screens.activities.support.SDonateMake
import com.sup.dev.android.libs.image_loader.ImageLoader
import com.sup.dev.android.libs.screens.navigator.Navigator
import com.sup.dev.android.tools.ToolsStorage
import com.sup.dev.android.views.cards.Card
import com.sup.dev.android.views.views.ViewProgressLine
import com.sup.dev.java.tools.ToolsDate

class CardDonate() : Card(R.layout.screen_feed_card_donate){

    val date = ToolsDate.getStartOfMonth()
    val KEY = "CardDonate_removed__$date"
    var totalCount = 0L
    var loaded = false
    var loadind = false

    override fun bindView(view: View) {
        super.bindView(view)

        if(!loadind){
            loadind = true
            load()
        }

        val vCounter: TextView = view.findViewById(com.sayzen.campfiresdk.R.id.vCounter)
        val vLine: ViewProgressLine = view.findViewById(com.sayzen.campfiresdk.R.id.vLine)
        val vImage:ImageView = view.findViewById(R.id.vImage)
        val vRemove:View = view.findViewById(R.id.vRemove)
        val vContainer:View = view.findViewById(R.id.vContainer)
        val vTitle:TextView = view.findViewById(R.id.vTitle)
        val vText:TextView = view.findViewById(R.id.vText)

        vTitle.text = t(API_TRANSLATE.activities_support_donate_card_title)
        vText.text = t(API_TRANSLATE.activities_support_donate_card_text)

        if(isVisible()) vContainer.visibility = View.GONE
        else vContainer.visibility = View.VISIBLE

        ImageLoader.load(API_RESOURCES.IMAGE_BACKGROUND_25).noHolder().into(vImage)
        ControllerDonates.setupLine(totalCount, vLine, vCounter)

        vContainer.setOnClickListener { Navigator.to(SDonateMake()) }
        vRemove.setOnClickListener {
            ToolsStorage.put(KEY, true)
            update()
        }

    }

    fun isVisible() = ToolsStorage.getBoolean(KEY, false) || !loaded || totalCount >= ControllerDonates.NEED*100

    fun load(){
        RProjectSupportGetInfo()
                .onComplete {
                    totalCount = it.totalCount
                    loaded = true
                    update()
                }
                .send(api)
    }


}