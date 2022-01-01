package com.dzen.campfire.screens.settings

import com.dzen.campfire.R
import com.dzen.campfire.api.API
import com.dzen.campfire.api.API_RESOURCES
import com.dzen.campfire.api.API_TRANSLATE
import com.sayzen.campfiresdk.screens.fandoms.search.SFandomsSearch
import com.sayzen.campfiresdk.controllers.ControllerApi
import com.sayzen.campfiresdk.controllers.ControllerCampfireSDK
import com.sayzen.campfiresdk.controllers.ControllerSettings
import com.sayzen.campfiresdk.controllers.t
import com.sup.dev.android.app.SupAndroid
import com.sup.dev.android.libs.image_loader.ImageLoader
import com.sup.dev.android.libs.screens.Screen
import com.sup.dev.android.libs.screens.navigator.Navigator
import com.sup.dev.android.tools.ToolsAndroid
import com.sup.dev.android.views.settings.Settings
import com.sup.dev.android.views.settings.SettingsSwitcher
import com.sup.dev.android.views.splash.SplashAlert

class SSettings : Screen(R.layout.screen_settings_actions) {


    private val vAppTitle: Settings = findViewById(R.id.vAppTitle)
    private val vActionsTitle: Settings = findViewById(R.id.vActionsTitle)
    private val vVoiceTitle: Settings = findViewById(R.id.vVoiceTitle)
    private val vStyle: Settings = findViewById(R.id.vStyle)
    private val vSecurity: Settings = findViewById(R.id.vSecurity)
    private val vLongPlus: Settings = findViewById(R.id.vLongPlus)
    private val vFastPost: Settings = findViewById(R.id.vFastPost)
    private val vLanguage: Settings = findViewById(R.id.vLanguage)
    private val vVoiceAutoLock: SettingsSwitcher = findViewById(R.id.vVoiceAutoLock)
    private val vVoiceAutoSend: SettingsSwitcher = findViewById(R.id.vVoiceAutoSend)
    private val vVoiceIgnore: SettingsSwitcher = findViewById(R.id.vVoiceIgnore)
    private val vAnonRates: SettingsSwitcher = findViewById(R.id.vAnonRates)
    private val vConferenceAllow: SettingsSwitcher = findViewById(R.id.vConferenceAllow)

    init {
        disableShadows()
        disableNavigation()
        setTitle(t(API_TRANSLATE.app_settings))

        vSecurity.setTitle(t(API_TRANSLATE.app_security))
        vStyle.setTitle(t(API_TRANSLATE.app_style))
        vAppTitle.setTitle(t(API_TRANSLATE.settings_app))
        vLanguage.setTitle(t(API_TRANSLATE.settings_language))
        vAnonRates.setTitle(t(API_TRANSLATE.settings_anon_rate))
        vAnonRates.setSubtitle(t(API_TRANSLATE.settings_anon_rate_hint))
        vActionsTitle.setTitle(t(API_TRANSLATE.settings_actions))
        vLongPlus.setTitle(t(API_TRANSLATE.settings_actions_long_plus))
        vFastPost.setTitle(t(API_TRANSLATE.settings_actions_fast_publications))
        vVoiceTitle.setTitle(t(API_TRANSLATE.settings_voice_messages))
        vVoiceAutoLock.setTitle(t(API_TRANSLATE.settings_voice_messages_auto_lock))
        vVoiceAutoSend.setTitle(t(API_TRANSLATE.settings_voice_messages_auto_send))
        vVoiceIgnore.setTitle(t(API_TRANSLATE.settings_voice_messages_dont_receive))
        vConferenceAllow.setTitle(t(API_TRANSLATE.settings_allow_adding_to_conferences))

        ImageLoader.load(API_RESOURCES.CAMPFIRE_IMAGE_4).into(vAnonRates.vIcon)

        vLanguage.setOnClickListener { changeLanguage() }

        vLongPlus.setOnClickListener {
            SFandomsSearch.instance(Navigator.TO, true) { fandom ->
                vLongPlus.setSubtitle(fandom.name)
                ControllerSettings.longPlusFandomId = fandom.id
                ControllerSettings.longPlusFandomLanguageId = fandom.languageId
                ControllerSettings.longPlusFandomName = fandom.name
            }
        }
        vFastPost.setOnClickListener {
            SFandomsSearch.instance(Navigator.TO, true) { fandom ->
                vFastPost.setSubtitle(fandom.name)
                ControllerSettings.fastPublicationFandomId = fandom.id
                ControllerSettings.fastPublicationFandomLanguageId = fandom.languageId
                ControllerSettings.fastPublicationFandomName = fandom.name
            }
        }
        vSecurity.setOnClickListener {SSettingsSecurity.instance(Navigator.TO) }
        vStyle.setOnClickListener { Navigator.to(SSettingsStyle()) }
        vVoiceAutoLock.setOnClickListener { ControllerSettings.voiceMessagesAutoLock = vVoiceAutoLock.isChecked() }
        vVoiceAutoSend.setOnClickListener { ControllerSettings.voiceMessagesAutoSend = vVoiceAutoSend.isChecked() }
        vVoiceIgnore.setOnClickListener { ControllerSettings.voiceMessagesIgnore = vVoiceIgnore.isChecked() }
        vAnonRates.setOnClickListener { ControllerSettings.anonRates = vAnonRates.isChecked() }
        vConferenceAllow.setOnClickListener { ControllerSettings.allowAddingToConferences = vConferenceAllow.isChecked() }

        updateValues()
    }

    private fun updateValues() {
        vLongPlus.setSubtitle(ControllerSettings.longPlusFandomName)
        vFastPost.setSubtitle(ControllerSettings.fastPublicationFandomName)

        vVoiceAutoLock.setChecked(ControllerSettings.voiceMessagesAutoLock)
        vVoiceAutoSend.setChecked(ControllerSettings.voiceMessagesAutoSend)
        vVoiceIgnore.setChecked(ControllerSettings.voiceMessagesIgnore)
        vAnonRates.setChecked(ControllerSettings.anonRates)
        vConferenceAllow.setChecked(ControllerSettings.allowAddingToConferences)

        vAnonRates.isEnabled = ControllerApi.can(API.LVL_ANONYMOUS)
    }

    private fun changeLanguage() {
        ControllerCampfireSDK.createLanguageMenu(ControllerApi.getLanguage(ControllerSettings.appLanguage).id) { languageId ->
            ControllerSettings.appLanguage = ControllerApi.getLanguage(languageId).code
            ToolsAndroid.setLanguage(SupAndroid.appContext!!, ControllerSettings.appLanguage)
            SplashAlert()
                    .setText(t(API_TRANSLATE.settings_restatr_alert))
                    .setOnCancel(t(API_TRANSLATE.app_not_now))
                    .setOnEnter(t(API_TRANSLATE.app_yes)) {
                        SupAndroid.activity!!.recreate()
                    }
                    .asSheetShow()
        }.asSheetShow()
    }


}
