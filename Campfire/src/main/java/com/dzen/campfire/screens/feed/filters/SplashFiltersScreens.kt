package com.dzen.campfire.screens.feed.filters

import com.dzen.campfire.screens.feed.SFeed
import com.sayzen.campfiresdk.controllers.ControllerSettings
import com.sup.dev.android.libs.screens.navigator.Navigator
import com.sup.dev.android.views.splash.SplashMenu

class SplashFiltersScreens : SplashMenu() {

    val list = ArrayList<Long>()

    init {

        val array = ControllerSettings.feedOrder
        for (i in array) if(i == 1L || i == 2L || i == 3L || i == 4L || i == 5L || i == 6L) list.add(i)


        updateAll()
    }

    fun updateAll() {
        myAdapter.clear()
        for (i in list) myAdapter.add(CardFiltersScreen(i, this))

        if (!list.contains(1)) myAdapter.add(CardFiltersScreen(1L, this))
        if (!list.contains(2)) myAdapter.add(CardFiltersScreen(2L, this))
        if (!list.contains(3)) myAdapter.add(CardFiltersScreen(3L, this))
        if (!list.contains(4)) myAdapter.add(CardFiltersScreen(4L, this))
        if (!list.contains(5)) myAdapter.add(CardFiltersScreen(5L, this))
        if (!list.contains(6)) myAdapter.add(CardFiltersScreen(6L, this))

        myAdapter.add(CardFiltersScreenButton(onSave = { save() }, onCancel = { hide() }))
    }

    private fun save() {
        val array = Array(list.size) { list[it] }
        ControllerSettings.feedOrder = array
        if (Navigator.getCurrent() is SFeed) Navigator.replace(SFeed())
    }

}