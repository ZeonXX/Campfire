package com.dzen.campfire.screens.feed.filters

import android.widget.Button
import com.dzen.campfire.R
import com.dzen.campfire.api.API
import com.dzen.campfire.api.API_RESOURCES
import com.dzen.campfire.api.API_TRANSLATE
import com.sayzen.campfiresdk.app.CampfireConstants
import com.sayzen.campfiresdk.controllers.*
import com.sup.dev.android.views.settings.Settings
import com.sup.dev.android.views.settings.SettingsCheckBox
import com.sup.dev.android.views.splash.Splash
import com.sup.dev.android.views.splash.SplashCheckBoxes

class SplashFilters(var onChanged: () -> Unit) : Splash(R.layout.screen_feed_splash_filters) {

    private val vImportant: SettingsCheckBox = findViewById(R.id.vImportant)
    private val vOrder: Settings = findViewById(R.id.vOrder)
    private val vLanguage: Settings = findViewById(R.id.vLanguage)
    private val vCategories: Settings = findViewById(R.id.vCategories)
    private val vEnter: Button = findViewById(R.id.vEnter)
    private val vCancel: Button = findViewById(R.id.vCancel)
    private var languages = arrayListOf(*ControllerSettings.feedLanguages)
    private var categories = arrayListOf(*ControllerSettings.feedCategories)

    init {
        vCancel.setOnClickListener { hide() }
        vEnter.setOnClickListener { saveAndHide() }
        vLanguage.setOnClickListener { showLanguages() }
        vCategories.setOnClickListener { showCategories() }
        vOrder.setOnClickListener { SplashFiltersScreens().asSheetShow() }

        vImportant.setChecked(ControllerSettings.feedImportant)
        vImportant.setTitle(t(API_TRANSLATE.feed_important))
        vLanguage.setTitle(t(API_TRANSLATE.feed_language))
        vCategories.setTitle(t(API_TRANSLATE.feed_categories))
        vOrder.setTitle(t(API_TRANSLATE.app_order))
        vCancel.text = t(API_TRANSLATE.app_cancel)
        vEnter.text = t(API_TRANSLATE.app_accept)

        update()

        ControllerStoryQuest.incrQuest(API.QUEST_STORY_FILTERS)
    }

    private fun showLanguages() {
       ControllerCampfireSDK.createLanguageCheckMenu(languages)
                .setOnEnter(t(API_TRANSLATE.app_save))
                .setOnHide { update() }
                .asSheetShow()
    }

    private fun showCategories() {
        val w = SplashCheckBoxes()
                .setOnEnter(t(API_TRANSLATE.app_save))
                .setOnHide { update() } as SplashCheckBoxes
        for (i in CampfireConstants.CATEGORIES) {
            w.add(i.name).checked(categories.contains(i.index)).onChange {
                if (it.isChecked) {
                    if (!categories.contains(i.index)) categories.add(i.index)
                } else {
                    categories.remove(i.index)
                }
            }
        }
        w.asSheetShow()
    }

    private fun update() {

        var languagesString = ""
        for (i in 0 until languages.size) {
            if (i != 0) languagesString += ", "
            languagesString += ControllerApi.getLanguage(languages[i]).name
        }
        vLanguage.setSubtitle(languagesString)

        var categoryString = ""
        for (i in 0 until categories.size) {
            if (i != 0) categoryString += ", "
            categoryString += CampfireConstants.getCategory(categories[i]).name
        }
        vCategories.setSubtitle(categoryString)
    }

    private fun saveAndHide() {
        ControllerSettings.feedImportant = vImportant.isChecked()
        ControllerSettings.feedLanguages = languages.toTypedArray()
        ControllerSettings.feedCategories = categories.toTypedArray()
        onChanged.invoke()
        hide()
    }

}