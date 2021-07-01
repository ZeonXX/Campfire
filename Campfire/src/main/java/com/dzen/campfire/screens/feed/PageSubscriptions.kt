package com.dzen.campfire.screens.feed

import com.dzen.campfire.R
import com.dzen.campfire.api.API_TRANSLATE
import com.dzen.campfire.api.models.publications.Publication
import com.dzen.campfire.api.requests.post.RPostFeedGetAllSubscribe
import com.sayzen.campfiresdk.models.cards.CardPublication
import com.sup.dev.android.tools.ToolsResources
import com.sup.dev.android.views.support.adapters.recycler_view.RecyclerCardAdapterLoading
import com.dzen.campfire.api.tools.client.Request
import com.sayzen.campfiresdk.controllers.t

class PageSubscriptions(screen: SFeed) : APage(screen) {

    override fun getTitle() = t(API_TRANSLATE.app_subscriptions)

    override fun getKarmaCategory() = -1L

    override fun instanceRequest(
            cards:ArrayList<CardPublication>,
            adapterX: RecyclerCardAdapterLoading<CardPublication, Publication>,
            onLoad:(Array<Publication>)->Unit): Request<out Request.Response> {
        return RPostFeedGetAllSubscribe(
                if (cards.isEmpty()) 0 else cards.get(cards.size - 1).xPublication.publication.dateCreate, 0)
                .onComplete { r -> onLoad.invoke(r.publications) }
    }

    override fun getNoKarmaCategory() = false

    override fun getNoSubscribes() = false

}
