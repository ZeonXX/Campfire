package com.dzen.campfire.screens.hello

import android.view.View
import android.view.ViewGroup
import com.dzen.campfire.R
import com.dzen.campfire.api.API_RESOURCES
import com.dzen.campfire.api.API_TRANSLATE
import com.dzen.campfire.api.requests.project.RProjectMakeHelloPost
import com.dzen.campfire.app.App
import com.sayzen.campfiresdk.app.CampfireConstants
import com.sayzen.campfiresdk.controllers.ControllerApi
import com.sayzen.campfiresdk.controllers.ControllerSettings
import com.sayzen.campfiresdk.controllers.api
import com.sayzen.campfiresdk.controllers.t
import com.sup.dev.android.libs.image_loader.ImageLoader
import com.sup.dev.android.libs.screens.Screen
import com.sup.dev.android.libs.screens.navigator.Navigator
import com.sup.dev.android.tools.ToolsResources
import com.sup.dev.android.tools.ToolsView
import com.sup.dev.android.views.splash.SplashAlert
import com.sup.dev.java.tools.ToolsCollections

class SCampfireHello(
        val demoMode: Boolean,
        val onHide: () -> Unit
) : Screen(R.layout.screen_campfire_hello) {

    companion object {

        fun showIfNeed(onHide: () -> Unit) {
            if (ControllerSettings.accountSettings.feedCategories.isNotEmpty()) onHide.invoke()   //  Для обратной совместимости
            else if (ControllerSettings.helloIsShowed) onHide.invoke()
            else if (ControllerSettings.helloShortIsShowed) onHide.invoke()
            else Navigator.set(SCampfireHello(false, onHide))
        }

    }

    var screenIndex = 1
    val vContainer: ViewGroup = findViewById(R.id.vContainer)
    var nextAvailable = true

    private var vCurrent: View = instanceScreen()

    init {
        disableNavigation()
        navigationBarColor = ToolsResources.getColorAttr(R.attr.colorPrimarySurface)

        vContainer.addView(vCurrent)

        resetBack()

        ImageLoader.load(API_RESOURCES.IMAGE_BACKGROUND_13).intoCash()
        ImageLoader.load(API_RESOURCES.IMAGE_BACKGROUND_9).intoCash()
        ImageLoader.load(API_RESOURCES.IMAGE_BACKGROUND_3).intoCash()
        ImageLoader.load(API_RESOURCES.IMAGE_BACKGROUND_14).intoCash()
        ImageLoader.load(API_RESOURCES.CAMPFIRE_IMAGE_3).intoCash()
        ImageLoader.load(API_RESOURCES.CAMPFIRE_IMAGE_2).intoCash()
    }

    private fun resetBack() {
        if (demoMode) return

        Navigator.addOnBack {
            if (Navigator.getCurrent() != this) return@addOnBack false

            SplashAlert()
                    .setText(t(API_TRANSLATE.message_close_app))
                    .setOnCancel(t(API_TRANSLATE.app_cancel))
                    .setOnEnter(t(API_TRANSLATE.app_close)) {
                        App.activity().finish()
                    }
                    .asSheetShow()

            resetBack()
            return@addOnBack true
        }
    }

    fun toNextScreen() {
        if (!nextAvailable) return
        screenIndex++
        val oldView = vCurrent
        vCurrent = instanceScreen()
        vCurrent.visibility = View.INVISIBLE
        vContainer.addView(vCurrent)
        nextAvailable = false
        ToolsView.toAlpha(oldView, 500) {
            nextAvailable = true
            vContainer.removeView(oldView)
        }
        ToolsView.fromAlpha(vCurrent, 500)
    }

    private fun instanceScreen(): View {

        if (screenIndex == 1) return Hello_Info.instnance_1(this).view
        if (screenIndex == 2) return Hello_Info.instnance_2(this).view
        if (screenIndex == 3) return Hello_Info.instnance_3(this).view
        if (screenIndex == 4) return Hello_Info.instnance_4(this).view
        if (screenIndex == 5) {
            if (!demoMode && !ControllerApi.account.getName().contains("#")) screenIndex++
            else return Hello_Sex(this, demoMode).view
        }
        if (screenIndex == 6) return Hello_Filters(this, demoMode).view
        if (screenIndex == 7) return Hello_Avatars(this, demoMode).view
        if (screenIndex == 8) return Hello_Rulles(this).view

        finish()
        return View(context)
    }

    private fun finish() {
        if (!demoMode) {
            ControllerSettings.rulesIsShowed = true

            ControllerSettings.helloIsShowed = true
            val text_1 = t(ToolsCollections.random(CampfireConstants.HELLO_TEXT), "@" + ControllerApi.account.getName())
            val text_2 = "(${t(API_TRANSLATE.campfire_hello_annotation)})"
            RProjectMakeHelloPost(text_1, true, text_2, ControllerApi.getLanguageId()).send(api)
        }
        onHide.invoke()
    }

}