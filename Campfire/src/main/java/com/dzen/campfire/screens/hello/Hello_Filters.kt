package com.dzen.campfire.screens.hello

import android.view.View
import android.widget.Button
import android.widget.TextView
import com.dzen.campfire.R
import com.dzen.campfire.api.API
import com.dzen.campfire.api.API_TRANSLATE
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sayzen.campfiresdk.controllers.ControllerSettings
import com.sayzen.campfiresdk.controllers.t
import com.sup.dev.android.tools.ToolsResources
import com.sup.dev.android.tools.ToolsView
import com.sup.dev.android.views.settings.SettingsCheckBox
import com.sup.dev.android.views.views.ViewChip

class Hello_Filters(
        val screen: SCampfireHello,
        val demoMode: Boolean
) {

    val view: View = ToolsView.inflate(screen.vContainer, R.layout.screen_campfire_hello_filters)
    val vGames: SettingsCheckBox = view.findViewById(R.id.vGames)
    val vAnime: SettingsCheckBox = view.findViewById(R.id.vAnime)
    val vMusic: SettingsCheckBox = view.findViewById(R.id.vMusic)
    val vMovies: SettingsCheckBox = view.findViewById(R.id.vMovies)
    val vNext: Button = view.findViewById(R.id.vNext)
    val vTextFilters: TextView = view.findViewById(R.id.vTextFilters)

    private val list = arrayListOf(vGames, vAnime, vMusic, vMovies)


    init {
        for (i in list) i.setOnClickListener { updateFinishEnabled() }

        vTextFilters.text = t(API_TRANSLATE.into_feed_filters)
        vNext.text = t(API_TRANSLATE.app_continue)
        vGames.setTitle(t(API_TRANSLATE.category_games))
        vAnime.setTitle(t(API_TRANSLATE.category_anime))
        vMovies.setTitle(t(API_TRANSLATE.category_movies))
        vMusic.setTitle(t(API_TRANSLATE.category_music))

        vNext.setOnClickListener { finish() }

        updateFinishEnabled()
    }

    private fun updateFinishEnabled() {
        var b = false
        for (i in list) if (i.isChecked()) b = true
        vNext.isEnabled = b
    }

    private fun finish() {
        val categories = ArrayList<Long>()
        if (vGames.isChecked()) categories.add(API.CATEGORY_GAMES)
        if (vAnime.isChecked()) categories.add(API.CATEGORY_ANIME)
        if (vMusic.isChecked()) categories.add(API.CATEGORY_MUSIC)
        if (vMovies.isChecked()) categories.add(API.CATEGORY_MOVIES)
        if (!demoMode) {
            categories.add(API.CATEGORY_OTHER)
            ControllerSettings.feedCategories = categories.toTypedArray()
        }
        screen.toNextScreen()
    }

}