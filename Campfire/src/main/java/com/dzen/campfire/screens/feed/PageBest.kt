package com.dzen.campfire.screens.feed

import com.dzen.campfire.api.API
import com.dzen.campfire.api.API_TRANSLATE
import com.sayzen.campfiresdk.R
import com.sayzen.campfiresdk.controllers.t
import com.sup.dev.android.tools.ToolsResources

class PageBest(screen: SFeed) : APage(screen) {

    override fun getTitle() = t(API_TRANSLATE.app_feed_best)

    override fun getKarmaCategory() = API.KARMA_CATEGORY_BEST

    override fun getNoKarmaCategory() = false

    override fun getNoSubscribes() = true

}