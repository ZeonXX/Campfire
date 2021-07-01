package com.dzen.campfire.screens.settings

import com.dzen.campfire.BuildConfig
import com.dzen.campfire.R
import com.dzen.campfire.api.API_TRANSLATE
import com.sayzen.campfiresdk.controllers.ControllerCampfireSDK
import com.sayzen.campfiresdk.controllers.t
import com.sayzen.campfiresdk.screens.translates.STranslates
import com.sup.dev.android.libs.screens.Screen
import com.sup.dev.android.libs.screens.navigator.Navigator
import com.sup.dev.android.tools.ToolsIntent
import com.sup.dev.android.views.settings.Settings

class SOther constructor() : Screen(R.layout.screen_other) {

    private val vNotifications: Settings = findViewById(R.id.vNotifications)
    private val vLogout: Settings = findViewById(R.id.vLogout)
    private val vSettings: Settings = findViewById(R.id.vSettings)
    private val vAbout: Settings = findViewById(R.id.vAbout)
    private val vRate: Settings = findViewById(R.id.vRate)


    init {
        disableShadows()
        disableNavigation()
        setTitle(t(API_TRANSLATE.app_other))

        vRate.setTitle(t(API_TRANSLATE.settings_rate))
        vLogout.setTitle(t(API_TRANSLATE.settings_log_out))

        vNotifications.setTitle(t(API_TRANSLATE.app_notifications))
        vSettings.setTitle(t(API_TRANSLATE.app_settings))
        vAbout.setTitle(t(API_TRANSLATE.app_info))

        vNotifications.setOnClickListener { Navigator.to(SSettingsNotifications()) }
        vLogout.setOnClickListener { ControllerCampfireSDK.logoutWithAlert() }
        vSettings.setOnClickListener { Navigator.to(SSettings()) }
        vAbout.setOnClickListener { Navigator.to(SOtherAbout()) }
        vRate.setOnClickListener { ToolsIntent.startPlayMarket(BuildConfig.APPLICATION_ID) }
    }

}
