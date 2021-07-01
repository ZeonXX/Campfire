package com.dzen.campfire.screens.settings

import android.view.View
import com.dzen.campfire.R
import com.dzen.campfire.api.API_TRANSLATE
import com.dzen.campfire.api.models.account.AccountSettings
import com.sayzen.campfiresdk.controllers.ControllerHoliday
import com.sayzen.campfiresdk.controllers.ControllerScreenAnimations
import com.sayzen.campfiresdk.controllers.ControllerSettings
import com.sayzen.campfiresdk.controllers.t
import com.sayzen.campfiresdk.models.animations.DrawAnimationSnow
import com.sayzen.campfiresdk.models.events.fandom.EventFandomBackgroundImageChangedModeration
import com.sup.dev.android.app.SupAndroid
import com.sup.dev.android.models.EventStyleChanged
import com.sup.dev.android.libs.screens.Screen
import com.sup.dev.android.tools.ToolsResources
import com.sup.dev.android.tools.ToolsView
import com.sup.dev.android.views.settings.*
import com.sup.dev.android.views.splash.SplashAlert
import com.sup.dev.java.libs.eventBus.EventBus

class SSettingsStyle : Screen(R.layout.screen_settings_style) {

    private val eventBus = EventBus.subscribe(EventStyleChanged::class) { updateValues() }

    private val vCircles: SettingsSwitcher = findViewById(R.id.vCircles)
    private val vRounding: SettingsSeek = findViewById(R.id.vRounding)
    private val vChatBackground: SettingsSwitcher = findViewById(R.id.vChatBackground)
    private val vRoundingChat: SettingsSeek = findViewById(R.id.vRoundingChat)
    private val vDefault: Settings = findViewById(R.id.vDefault)
    private val vTheme: SettingsSelection = findViewById(R.id.vTheme)
    private val vActivityType: SettingsSelection = findViewById(R.id.vActivityType)
    private val vFullscreen: SettingsSwitcher = findViewById(R.id.vFullscreen)
    private val vProfileListStyle: SettingsSwitcher = findViewById(R.id.vProfileListStyle)
    private val vNewYearAvatars: SettingsSwitcher = findViewById(R.id.vNewYearAvatars)
    private val vNewYearSnow: SettingsSeek = findViewById(R.id.vNewYearSnow)
    private val vNewYearProfileAnimation: SettingsSwitcher = findViewById(R.id.vNewYearProfileAnimation)
    private val vHolidayTitle: Settings = findViewById(R.id.vHolidayTitle)
    private val vGrapTitle: Settings = findViewById(R.id.vGrapTitle)
    private val vAppOtherTitle: Settings = findViewById(R.id.vAppOtherTitle)

    init {
        disableShadows()
        disableNavigation()
        setTitle(t(API_TRANSLATE.app_style))

        vTheme.setTitle(t(API_TRANSLATE.settings_style_theme))
        vActivityType.setTitle(t(API_TRANSLATE.settings_style_interface))
        vFullscreen.setTitle(t(API_TRANSLATE.settings_style_fullscreen))
        vProfileListStyle.setTitle(t(API_TRANSLATE.settings_style_profile_list))
        vGrapTitle.setTitle(t(API_TRANSLATE.settings_style_graphics_title))
        vCircles.setTitle(t(API_TRANSLATE.settings_style_circles))
        vRounding.setTitle(t(API_TRANSLATE.settings_style_rounding))
        vRoundingChat.setTitle(t(API_TRANSLATE.settings_style_rounding_chat))
        vChatBackground.setTitle(t(API_TRANSLATE.settings_fandom_background))
        vHolidayTitle.setTitle(t(API_TRANSLATE.settings_style_holidays))
        vNewYearAvatars.setTitle(t(API_TRANSLATE.settings_style_new_year_avatars))
        vNewYearSnow.setTitle(t(API_TRANSLATE.settings_style_new_year_snow))
        vNewYearProfileAnimation.setTitle(t(API_TRANSLATE.settings_style_new_year_profile_animation))
        vDefault.setTitle(t(API_TRANSLATE.settings_style_default))
        vAppOtherTitle.setTitle(t(API_TRANSLATE.app_other))

        vFullscreen.visibility = View.GONE
        if(!ControllerHoliday.isHoliday()){
            vHolidayTitle.visibility = View.GONE
        }
        if(!ControllerHoliday.isNewYear()) {
            vNewYearAvatars.visibility = View.GONE
            vNewYearSnow.visibility = View.GONE
            vNewYearProfileAnimation.visibility = View.GONE
        }

        vCircles.setOnClickListener {
            ControllerSettings.styleAvatarsSquare = !vCircles.isChecked()
            vRounding.isEnabled = !vCircles.isChecked()
        }
        vNewYearAvatars.setOnClickListener {  ControllerSettings.styleNewYearAvatars = vNewYearAvatars.isChecked()}
        vNewYearProfileAnimation.setOnClickListener {  ControllerSettings.styleNewYearProfileAnimation = vNewYearProfileAnimation.isChecked()}
        vNewYearSnow.setOnProgressChanged {
            ControllerSettings.styleNewYearSnow = vNewYearSnow.progress
            val animation = ControllerScreenAnimations.getAnimation()
            if(animation is DrawAnimationSnow) animation.arg = vNewYearSnow.progress
        }
        vNewYearSnow.setMaxProgress(1000)
        vFullscreen.setOnClickListener {
            ControllerSettings.fullscreen = vFullscreen.isChecked()
            SplashAlert()
                    .setText(t(API_TRANSLATE.settings_style_theme_restatr))
                    .setOnCancel(t(API_TRANSLATE.app_not_now))
                    .setOnEnter(t(API_TRANSLATE.app_yes)) {
                        SupAndroid.activity!!.recreate()
                    }
                    .asSheetShow()
        }
        vProfileListStyle.setOnClickListener {
            ControllerSettings.isProfileListStyle = vProfileListStyle.isChecked()
        }

        vRounding.setMaxProgress(18)
        vRounding.setOnProgressChanged {
            ControllerSettings.styleAvatarsRounding = vRounding.progress
        }

        vChatBackground.setOnClickListener {
            ControllerSettings.fandomBackground = vChatBackground.isChecked()
            EventBus.post(EventFandomBackgroundImageChangedModeration(0, 0, 0))
        }

        vRoundingChat.setMaxProgress(28)
        vRoundingChat.setOnProgressChanged {
            ControllerSettings.styleChatRounding = vRoundingChat.progress
        }

        vDefault.setOnClickListener {
            SplashAlert()
                    .setText(t(API_TRANSLATE.settings_style_default_alert))
                    .setOnCancel(t(API_TRANSLATE.app_cancel))
                    .setOnEnter(t(API_TRANSLATE.app_continue)) {
                        val s = AccountSettings()

                        ControllerSettings.styleAvatarsSquare = s.styleSquare
                        ControllerSettings.styleAvatarsRounding = s.styleCorned
                        ControllerSettings.fandomBackground = s.fandomBackground
                        ControllerSettings.styleChatRounding = s.styleChatCorned
                        ControllerSettings.styleTheme = s.theme
                        ControllerSettings.interfaceType = s.interfaceType
                        ControllerSettings.fullscreen = s.fullscreen
                    }
                    .asSheetShow()
        }

        vTheme.onSelected {
            ControllerSettings.styleTheme = it
            val dialog = ToolsView.showProgressDialog()
            ControllerSettings.setSettingsNow(
                    onFinish = {
                        dialog.hide()
                        SplashAlert()
                                .setText(t(API_TRANSLATE.settings_style_theme_restatr))
                                .setOnCancel(t(API_TRANSLATE.app_not_now))
                                .setOnEnter(t(API_TRANSLATE.app_yes)) {
                                    SupAndroid.activity!!.recreate()
                                }
                                .asSheetShow()
                    },
                    onError = {
                        dialog.hide()
                    }
            )

        }

        vTheme.add(t(API_TRANSLATE.settings_style_theme_1))
        vTheme.add(t(API_TRANSLATE.settings_style_theme_2))
        vTheme.add(t(API_TRANSLATE.settings_style_theme_3))

        vTheme.add(t(API_TRANSLATE.settings_style_theme_1) + ": Red")
        vTheme.add(t(API_TRANSLATE.settings_style_theme_1) + ": Pink")
        vTheme.add(t(API_TRANSLATE.settings_style_theme_1) + ": Purple")
        vTheme.add(t(API_TRANSLATE.settings_style_theme_1) + ": DeepPurple")
        vTheme.add(t(API_TRANSLATE.settings_style_theme_1) + ": Indigo")
        vTheme.add(t(API_TRANSLATE.settings_style_theme_1) + ": Blue")
        vTheme.add(t(API_TRANSLATE.settings_style_theme_1) + ": LightBlue")
        vTheme.add(t(API_TRANSLATE.settings_style_theme_1) + ": Cyan")
        vTheme.add(t(API_TRANSLATE.settings_style_theme_1) + ": Teal")
        vTheme.add(t(API_TRANSLATE.settings_style_theme_1) + ": Green")
        vTheme.add(t(API_TRANSLATE.settings_style_theme_1) + ": LightGreen")
        vTheme.add(t(API_TRANSLATE.settings_style_theme_1) + ": Lime")
        vTheme.add(t(API_TRANSLATE.settings_style_theme_1) + ": DeepOrange")
        vTheme.add(t(API_TRANSLATE.settings_style_theme_1) + ": Brown")
        vTheme.add(t(API_TRANSLATE.settings_style_theme_1) + ": BlueGrey")

        vTheme.add(t(API_TRANSLATE.settings_style_theme_3) + ": Red")
        vTheme.add(t(API_TRANSLATE.settings_style_theme_3) + ": Pink")
        vTheme.add(t(API_TRANSLATE.settings_style_theme_3) + ": Purple")
        vTheme.add(t(API_TRANSLATE.settings_style_theme_3) + ": DeepPurple")
        vTheme.add(t(API_TRANSLATE.settings_style_theme_3) + ": Indigo")
        vTheme.add(t(API_TRANSLATE.settings_style_theme_3) + ": Blue")
        vTheme.add(t(API_TRANSLATE.settings_style_theme_3) + ": LightBlue")
        vTheme.add(t(API_TRANSLATE.settings_style_theme_3) + ": Cyan")
        vTheme.add(t(API_TRANSLATE.settings_style_theme_3) + ": Teal")
        vTheme.add(t(API_TRANSLATE.settings_style_theme_3) + ": Green")
        vTheme.add(t(API_TRANSLATE.settings_style_theme_3) + ": LightGreen")
        vTheme.add(t(API_TRANSLATE.settings_style_theme_3) + ": Lime")
        vTheme.add(t(API_TRANSLATE.settings_style_theme_3) + ": DeepOrange")
        vTheme.add(t(API_TRANSLATE.settings_style_theme_3) + ": Brown")
        vTheme.add(t(API_TRANSLATE.settings_style_theme_3) + ": BlueGrey")



        vActivityType.onSelected {
            ControllerSettings.interfaceType = it
            SplashAlert()
                    .setText(t(API_TRANSLATE.settings_style_theme_restatr))
                    .setOnCancel(t(API_TRANSLATE.app_not_now))
                    .setOnEnter(t(API_TRANSLATE.app_yes)) {
                        SupAndroid.activity!!.recreate()
                    }
                    .asSheetShow()
        }

        vActivityType.add(t(API_TRANSLATE.settings_style_interface_1))
        vActivityType.add(t(API_TRANSLATE.settings_style_interface_2))

        updateValues()
    }

    private fun updateValues() {
        vCircles.setChecked(!ControllerSettings.styleAvatarsSquare)
        vNewYearAvatars.setChecked(ControllerSettings.styleNewYearAvatars)
        vNewYearProfileAnimation.setChecked(ControllerSettings.styleNewYearProfileAnimation)
        vNewYearSnow.progress = ControllerSettings.styleNewYearSnow
        vFullscreen.setChecked(ControllerSettings.fullscreen)
        vProfileListStyle.setChecked(ControllerSettings.isProfileListStyle)
        vRounding.progress = ControllerSettings.styleAvatarsRounding
        vRounding.isEnabled = !vCircles.isChecked()
        vChatBackground.setChecked(ControllerSettings.fandomBackground)
        vRoundingChat.progress = ControllerSettings.styleChatRounding
        if (vTheme.getTitles().size > ControllerSettings.styleTheme) vTheme.setCurrentIndex(ControllerSettings.styleTheme) else vTheme.setCurrentIndex(0)
        vActivityType.setCurrentIndex(ControllerSettings.interfaceType)
    }


}