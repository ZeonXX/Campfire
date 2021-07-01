package com.dzen.campfire.screens.intro

import android.widget.ImageView
import android.widget.TextView
import com.dzen.campfire.BuildConfig
import com.dzen.campfire.R
import com.dzen.campfire.api.API_RESOURCES
import com.dzen.campfire.api.API_TRANSLATE
import com.dzen.campfire.app.App
import com.sayzen.campfiresdk.controllers.t
import com.sup.dev.android.libs.screens.Screen
import com.sup.dev.android.libs.image_loader.ImageLoader
import com.sup.dev.android.tools.ToolsIntent
import com.sup.dev.android.tools.ToolsResources

class SUpdate : Screen(R.layout.screen_update) {

    private val vEmptyImage: ImageView = findViewById(R.id.vEmptyImage)
    private val vMessage: TextView = findViewById(R.id.vMessage)
    private val vSubMessage: TextView = findViewById(R.id.vSubMessage)
    private val vAction: TextView = findViewById(R.id.vAction)


    init {
        activityRootBackground = ToolsResources.getColorAttr(R.attr.colorPrimary)
        disableNavigation()
        isBackStackAllowed = false

        ImageLoader.load(API_RESOURCES.IMAGE_BACKGROUND_LEVEL_9).into(vEmptyImage)
        vMessage.setText(t(API_TRANSLATE.intro_update))
        vSubMessage.setText(t(API_TRANSLATE.intro_update_sub))
        vAction.setText(t(API_TRANSLATE.app_update))
        vAction.setOnClickListener { ToolsIntent.startPlayMarket(BuildConfig.APPLICATION_ID) }
    }

    override fun onBackPressed(): Boolean {
        App.activity().finish()
        return true
    }

}