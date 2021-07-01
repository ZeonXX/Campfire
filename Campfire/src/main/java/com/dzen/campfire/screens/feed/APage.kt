package com.dzen.campfire.screens.feed

import com.dzen.campfire.api.API
import com.dzen.campfire.api.API_RESOURCES
import com.dzen.campfire.api.API_TRANSLATE
import com.dzen.campfire.api.models.publications.Publication
import com.dzen.campfire.api.requests.post.RPostFeedGetAll
import com.sayzen.campfiresdk.R
import com.sayzen.campfiresdk.controllers.ControllerSettings
import com.sayzen.campfiresdk.controllers.api
import com.sayzen.campfiresdk.models.PostList
import com.sayzen.campfiresdk.models.cards.CardPost
import com.sayzen.campfiresdk.models.cards.CardPublication
import com.sayzen.campfiresdk.models.events.project.EventStoryQuestUpdated
import com.sup.dev.android.views.cards.CardScreenLoadingRecycler
import com.sup.dev.android.views.support.adapters.recycler_view.RecyclerCardAdapterLoading
import com.dzen.campfire.api.tools.client.Request
import com.sayzen.campfiresdk.app.CampfireConstants
import com.sayzen.campfiresdk.controllers.ControllerApi
import com.sayzen.campfiresdk.controllers.t
import com.sup.dev.java.libs.eventBus.EventBus
import com.sup.dev.java.tools.ToolsDate

abstract class APage(
        val screen: SFeed
) : CardScreenLoadingRecycler<CardPublication, Publication>(), PostList {


    private val eventBus = EventBus
            .subscribe(EventStoryQuestUpdated::class) { updateQuest() }

    var wasLoaded = false

    abstract fun getTitle(): String

    abstract fun getKarmaCategory(): Long

    abstract fun getNoSubscribes(): Boolean

    abstract fun getNoKarmaCategory(): Boolean

    fun markLoaded() {
        if (wasLoaded) return
        wasLoaded = true
        if (screen.pagerCardAdapter.indexOf(this) == 0) setTextProgress(screen.getLoadingText()) else setTextProgress(t(API_TRANSLATE.feed_loading_35))
        setTextEmpty(t(API_TRANSLATE.feed_empty))
        updateBackgroundImage()
    }

    fun updateBackgroundImage() {
        when {
            ControllerApi.isOldVersion() -> setBackgroundImage(null)
            CampfireConstants.getStoryQuest(ControllerSettings.storyQuestIndex) != null -> setBackgroundImage(null)
            screen.cardDonate.isVisible() -> setBackgroundImage(null)
            else -> setBackgroundImage(API_RESOURCES.IMAGE_BACKGROUND_3)
        }
    }

    fun load() {
        if (wasLoaded) return
        markLoaded()
        reload()
    }

    override fun reload() {
        super.reload()
        adapterSub?.remove(screen.cardUpdate)
        adapterSub?.remove(screen.cardStoryQuest)
        adapterSub?.remove(screen.cardDonate)
    }

    override fun contains(card: CardPost) = adapterSub != null && adapterSub!!.contains(card)


    override fun instanceAdapter(): RecyclerCardAdapterLoading<CardPublication, Publication> {
        val adapterX = RecyclerCardAdapterLoading<CardPublication, Publication>(CardPublication::class) { publication ->
            CardPublication.instance(publication, vRecycler, true)
        }
        adapterX.setShowLoadingCard(false)
        adapterX.addOnLoadedPack_NotEmpty {  }
        adapterX.addOnFinish {  }
        adapterX.setBottomLoader { onLoad, cards ->
            if (screen.pagerCardAdapter.get(screen.vPager.currentItem) != this) {
                onLoad.invoke(emptyArray())
                return@setBottomLoader
            }
            instanceRequest(cards, adapterX) {
                updateBackgroundImage()
                adapterX.setShowLoadingCard(true)
                onLoad.invoke(it)
                setTextProgress(t(API_TRANSLATE.feed_loading_35))
                if (screen.pagerCardAdapter.indexOf(this) == 0) {
                    if (!adapterX.contains(screen.cardStoryQuest)) adapterX.add(0, screen.cardStoryQuest)
                    if (!adapterX.contains(screen.cardUpdate)) adapterX.add(0, screen.cardUpdate)
                    if (!adapterX.contains(screen.cardDonate) && ControllerApi.account.getLevel() >= 200 && ToolsDate.getCurrentDayOfMonth() > 24) adapterX.add(0, screen.cardDonate)
                    updateQuest()
                }
            }
                    .onNetworkError {
                        onLoad.invoke(null)
                    }
                    .send(api)
        }

        return adapterX
    }

    fun updateQuest() {
        if (screen.pagerCardAdapter.indexOf(this) == 0) {
            if (ControllerSettings.storyQuestIndex < API.QUEST_STORY_FUTURE.index) {
                screen.cardStoryQuest.show()
            } else {
                screen.cardStoryQuest.hide()
            }
        }
    }

    open fun instanceRequest(
            cards: ArrayList<CardPublication>,
            adapterX: RecyclerCardAdapterLoading<CardPublication, Publication>,
            onLoad: (Array<Publication>) -> Unit
    ): Request<out Request.Response> {
        return RPostFeedGetAll(
                if (cards.isEmpty()) 0 else cards.get(cards.size - 1).xPublication.publication.dateCreate,
                ControllerSettings.feedLanguages,
                ControllerSettings.feedCategories,
                ControllerSettings.feedImportant,
                getKarmaCategory(),
                getNoSubscribes(),
                getNoKarmaCategory())
                .onComplete { r ->
                    onLoad.invoke(r.publications)
                }
    }

}
