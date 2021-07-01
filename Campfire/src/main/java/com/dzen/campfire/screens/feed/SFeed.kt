package com.dzen.campfire.screens.feed

import android.view.Gravity
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.dzen.campfire.R
import com.dzen.campfire.api.API_TRANSLATE
import com.dzen.campfire.app.App
import com.dzen.campfire.screens.feed.filters.SplashFilters
import com.dzen.campfire.screens.hello.SCampfireHello
import com.sayzen.campfiresdk.app.CampfireConstants
import com.sayzen.campfiresdk.controllers.ControllerApi
import com.sayzen.campfiresdk.controllers.ControllerScreenAnimations
import com.sayzen.campfiresdk.controllers.ControllerSettings
import com.sayzen.campfiresdk.controllers.t
import com.sayzen.campfiresdk.models.PostList
import com.sayzen.campfiresdk.models.cards.CardPost
import com.sayzen.campfiresdk.screens.achievements.SAchievements
import com.sayzen.campfiresdk.screens.fandoms.CardStoryQuest
import com.sayzen.campfiresdk.screens.fandoms.CardUpdate
import com.sayzen.campfiresdk.screens.fandoms.search.SFandomsSearch
import com.sayzen.campfiresdk.screens.post.create.SPostCreate
import com.sayzen.devsupandroidgoogle.ControllerFirebaseAnalytics
import com.sup.dev.android.libs.image_loader.ImageLoader
import com.sup.dev.android.libs.screens.Screen
import com.sup.dev.android.libs.screens.navigator.Navigator
import com.sup.dev.android.tools.ToolsAndroid
import com.sup.dev.android.tools.ToolsView
import com.sup.dev.android.views.support.adapters.pager.PagerCardAdapter
import com.sup.dev.android.views.views.ViewIcon
import com.sup.dev.android.views.views.pager.ViewPagerIndicatorTitles
import com.sup.dev.android.views.splash.SplashAlert
import com.sup.dev.java.libs.debug.log
import com.sup.dev.java.tools.ToolsThreads


class SFeed : Screen(R.layout.screen_feed), PostList {

    val vPager: ViewPager = findViewById(R.id.vPager)
    val vFilter: ViewIcon = findViewById(R.id.vFilter)
    private val vTitles: ViewPagerIndicatorTitles = findViewById(R.id.viIndicator)
    val pagerCardAdapter = PagerCardAdapter()

    val cardUpdate = CardUpdate()
    val cardStoryQuest = CardStoryQuest()
    val cardDonate = CardDonate()

    init {

        setTitle(t(API_TRANSLATE.app_feed))
        vPager.adapter = pagerCardAdapter

        vFilter.visibility = View.INVISIBLE
        vFilter.setOnClickListener { SplashFilters { Navigator.replace(SFeed()) }.asSheetShow() }

        val titles = ArrayList<String>()
        val feedOrder = ControllerSettings.feedOrder
        for (i in feedOrder) {

            var page: APage? = null

            if (i == 1L && (ControllerApi.hasSubscribes || feedOrder.isEmpty())) page = PageSubscriptions(this)
            if (i == 2L) page = PageAll(this)
            if (i == 3L) page = PageBest(this)
            if (i == 4L) page = PageGood(this)
            if (i == 5L) page = PageAbyss(this)
            if (i == 6L) page = PageAllWithSubscribes(this)

            if (page != null) {
                titles.add(page.getTitle())
                pagerCardAdapter.add(page)
            }
        }
        if(titles.isEmpty()){
            val page = PageAllWithSubscribes(this)
            titles.add(page.getTitle())
            pagerCardAdapter.add(page)
        }
        if (titles.size < 2) vFilter.visibility = View.VISIBLE
        if (titles.size == 1 && pagerCardAdapter.size() > 0 && titles[0] == ((pagerCardAdapter.get(0)) as APage).getTitle()) {
            vTitles.visibility = View.GONE
        }
        vTitles.setTitles(*titles.toTypedArray())

        ToolsThreads.main(true) { (pagerCardAdapter.get(0) as APage).markLoaded() }

        vPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                val page = pagerCardAdapter.get(position) as APage
                page.load()
                if (pagerCardAdapter.get(pagerCardAdapter.size() - 1) == page) ToolsView.fromAlpha(vFilter)
                else ToolsView.toAlpha(vFilter)
            }
        })

        findViewById<View>(R.id.vFab).setOnClickListener {
            ControllerFirebaseAnalytics.post("Screen_Feed", "CreateDraft")
            SFandomsSearch.instance(Navigator.TO, false) { fandom ->
                SPostCreate.instance(fandom.id, fandom.languageId, fandom.name, fandom.imageId, SPostCreate.PostParams(), Navigator.REPLACE)
            }
        }
        findViewById<View>(R.id.vFab).setOnLongClickListener {
            SPostCreate.instance(ControllerSettings.longPlusFandomId, ControllerSettings.longPlusFandomLanguageId, SPostCreate.PostParams(),
                    {

                    }, Navigator.TO)
            true
        }

        showLvlDialog()

        if (ToolsAndroid.isDebug()) {
            ToolsThreads.main(100){
            }
        }
    }

    override fun onFirstShow() {
        super.onFirstShow()
        App.activity().cardQuest.load()
    }

    fun getLoadingText() = CampfireConstants.randomFeedText()

    private fun showLvlDialog() {
        if (ControllerApi.account.getId() != 0L && ControllerApi.account.getLevel() / 100 > ControllerSettings.lvlDialogLvl) {
            ToolsThreads.main(1000) {
                if (ControllerApi.account.getId() != 0L && ControllerApi.account.getLevel() / 100 > ControllerSettings.lvlDialogLvl) {
                    ControllerScreenAnimations.fireworks()
                    ImageLoader.load(CampfireConstants.getLvlImage(ControllerApi.account.getLevel())).intoBitmap {
                        SplashAlert()
                                .setCancelable(false)
                                .setTitleImage(it)
                                .setTitle(t(API_TRANSLATE.app_congratulations))
                                .setTextGravity(Gravity.CENTER)
                                .setText(t(API_TRANSLATE.message_lvl_dialog))
                                .setTitleImageTopPadding(ToolsView.dpToPx(16))
                                .setOnCancel(t(API_TRANSLATE.app_close))
                                .setOnHide {
                                    ControllerSettings.lvlDialogLvl = ControllerApi.account.getLevel() / 100
                                }
                                .setOnEnter(t(API_TRANSLATE.app_privilege)) {
                                    ControllerSettings.lvlDialogLvl = ControllerApi.account.getLevel() / 100
                                    SAchievements.instance(true, Navigator.TO)
                                }
                                .setTitleGravity(Gravity.CENTER)
                                .asSheetShow()
                    }
                }
            }
        }

    }

    override fun contains(card: CardPost): Boolean {
        return (pagerCardAdapter.get(vPager.currentItem) as PostList).contains(card)
    }


}
