package com.dzen.campfire.app

import android.app.Application
import com.dzen.campfire.BuildConfig
import com.dzen.campfire.R
import com.dzen.campfire.api.API
import com.dzen.campfire.api.API_RESOURCES
import com.sayzen.campfiresdk.screens.fandoms.search.SFandomsSearch
import com.dzen.campfire.screens.intro.SIntro
import com.google.firebase.FirebaseApp
import com.sayzen.campfiresdk.controllers.ControllerCampfireSDK
import com.sayzen.campfiresdk.controllers.ControllerLinks
import com.sayzen.campfiresdk.controllers.ControllerPost
import com.sayzen.campfiresdk.controllers.ControllerSettings
import com.sayzen.campfiresdk.screens.other.rules.SGoogleRules
import com.sayzen.campfiresdk.support.ApiRequestsSupporter
import com.sup.dev.android.app.SupAndroid
import com.sup.dev.android.libs.image_loader.ImageLoaderId
import com.sup.dev.android.libs.screens.activity.SActivity
import com.sup.dev.android.libs.screens.navigator.Navigator
import com.sup.dev.android.tools.ToolsAndroid

class App : Application() {

    companion object {

        fun activity() = SupAndroid.activity as AppActivity
        fun isDebug() = true && ToolsAndroid.isDebug()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        SupAndroid.onLowMemory()
    }

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(applicationContext)

        SupAndroid.init(applicationContext, BuildConfig.APPLICATION_ID, AppActivity::class.java)
        SupAndroid.imgErrorGone = ImageLoaderId(API_RESOURCES.IMAGE_BACKGROUND_17).noHolder()
        SupAndroid.imgErrorNetwork = ImageLoaderId(API_RESOURCES.IMAGE_BACKGROUND_20).noHolder()
        ApiRequestsSupporter.USE_ID_RESOURCES = true

        initSdk()

        ToolsAndroid.setLanguage(applicationContext, ControllerSettings.appLanguage)

        SActivity.onUrlClicked = {
            if (!ControllerLinks.parseLink(it)) ControllerLinks.openLink(it)
        }

        ImageLoaderId(API_RESOURCES.IMAGE_BACKGROUND_17).immortalCash().intoCash()
        ImageLoaderId(API_RESOURCES.IMAGE_BACKGROUND_20).immortalCash().intoCash()
        ImageLoaderId(API_RESOURCES.EMOJI_1).immortalCash().intoCash()
        ImageLoaderId(API_RESOURCES.EMOJI_2).immortalCash().intoCash()
        ImageLoaderId(API_RESOURCES.EMOJI_3).immortalCash().intoCash()
        ImageLoaderId(API_RESOURCES.EMOJI_4).immortalCash().intoCash()
        ImageLoaderId(API_RESOURCES.EMOJI_5).immortalCash().intoCash()
        ImageLoaderId(API_RESOURCES.EMOJI_6).immortalCash().intoCash()
        ImageLoaderId(API_RESOURCES.EMOJI_7).immortalCash().intoCash()
    }

    private fun initSdk() {

        SGoogleRules.CHECK_FLAG_IN_ACCOUNT_SETTINGS = true

        ControllerCampfireSDK.IS_DEBUG = isDebug()
        ControllerCampfireSDK.ENABLE_CLOSE_FANDOM_ALERT = true

        ControllerCampfireSDK.ON_SCREEN_CHAT_START = { activity().setChatStack() }

        ControllerCampfireSDK.SEARCH_FANDOM = {
            SFandomsSearch.instance(Navigator.TO, true) { fandom -> it.invoke(fandom) }
        }

        ControllerPost.ENABLED_BOOKMARK = true
        ControllerPost.ENABLED_WATCH = true
        ControllerPost.ENABLED_SHARE = true
        ControllerPost.ENABLED_COPY_LINK = true
        ControllerPost.ENABLED_NOTIFY_FOLLOWERS = true
        ControllerPost.ENABLED_CHANGE = true
        ControllerPost.ENABLED_CHANGE_TAGS = true
        ControllerPost.ENABLED_REMOVE = true
        ControllerPost.ENABLED_TO_DRAFTS = true
        ControllerPost.ENABLED_CHANGE_FANDOM = true
        ControllerPost.ENABLED_REPORT = true
        ControllerPost.ENABLED_CLEAR_REPORTS = true
        ControllerPost.ENABLED_BLOCK = true
        ControllerPost.ENABLED_MODER_TO_DRAFT = true
        ControllerPost.ENABLED_MODER_CHANGE_TAGS = true
        ControllerPost.ENABLED_INPORTANT = true
        ControllerPost.ENABLED_MAKE_MODER = true
        ControllerPost.ENABLED_MODER_CHANGE_FANDOM = true
        ControllerPost.ENABLED_PIN_FANDOM = true
        ControllerPost.ENABLED_PIN_PROFILE = true
        ControllerPost.ENABLED_MAKE_MULTILINGUAL = true
        ControllerPost.ENABLED_HISTORY = true
        ControllerPost.ENABLED_CLOSE = true

        ControllerCampfireSDK.init(
                API.PROJECT_KEY_CAMPFIRE,
                R.drawable.logo_alpha_no_margins,
                R.drawable.logo_alpha_black_and_white_no_margins)
        {
            if(SupAndroid.activity != null) //  Были падения у пользователей https://console.firebase.google.com/project/campfire-2fb33/crashlytics/app/android:com.dzen.campfire/issues/1d10004f361cafcda1b7285e17fac8ec?time=last-seven-days&sessionEventKey=5F99B6920050000170668061FB885783_1467185714548475633
                Navigator.set(SIntro())
        }

    }

}
