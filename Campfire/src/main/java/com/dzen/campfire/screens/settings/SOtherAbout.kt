package com.dzen.campfire.screens.settings

import com.dzen.campfire.R
import com.dzen.campfire.api.API
import com.dzen.campfire.api.API_TRANSLATE
import com.dzen.campfire.screens.hello.SCampfireHello
import com.dzen.campfire.screens.settings.statistic.SStatistic
import com.sayzen.campfiresdk.controllers.ControllerApi
import com.sayzen.campfiresdk.controllers.t
import com.sayzen.campfiresdk.screens.activities.administration.admins_events.SAdministrationAdminsEvents
import com.sayzen.campfiresdk.screens.activities.administration.prison.SAdministrationPrison
import com.sayzen.campfiresdk.screens.other.about.SAboutCreators
import com.sayzen.campfiresdk.screens.other.gallery.SGallery
import com.sayzen.campfiresdk.screens.other.rules.SRulesModerators
import com.sayzen.campfiresdk.screens.other.rules.SRulesUser
import com.sayzen.campfiresdk.screens.wiki.SWikiList
import com.sup.dev.android.libs.screens.Screen
import com.sup.dev.android.libs.screens.navigator.Navigator
import com.sup.dev.android.tools.ToolsIntent
import com.sup.dev.android.views.settings.Settings
import com.sup.dev.android.views.settings.SettingsArrow

class SOtherAbout : Screen(R.layout.screen_other_about) {

    private val vAbout: Settings = findViewById(R.id.vAbout)
    private val vStatistic: Settings = findViewById(R.id.vStatistic)
    private val vRulesUser: Settings = findViewById(R.id.vRulesUser)
    private val vRulesModerator: Settings = findViewById(R.id.vRulesModerator)
    private val vStartScreen: Settings = findViewById(R.id.vStartScreen)
    private val vCreators: Settings = findViewById(R.id.vCreators)
    private val vAdminsEvents: SettingsArrow = findViewById(R.id.vAdminsEvents)
    private val vPrison: SettingsArrow = findViewById(R.id.vPrison)
    private val vGallery: SettingsArrow = findViewById(R.id.vGallery)
    private val vMail: Settings = findViewById(R.id.vMail)
    private val vPolicy: Settings = findViewById(R.id.vPolicy)

    init {
        disableShadows()
        disableNavigation()
        setTitle(t(API_TRANSLATE.app_info))


        vAdminsEvents.setTitle(t(API_TRANSLATE.administration_admins_events))
        vPrison.setTitle(t(API_TRANSLATE.moderation_screen_prison))
        vStartScreen.setTitle(t(API_TRANSLATE.settings_start_screen))
        vMail.setTitle(t(API_TRANSLATE.settings_mail))
        vRulesUser.setTitle(t(API_TRANSLATE.about_rules_user))
        vRulesModerator.setTitle(t(API_TRANSLATE.about_rules_moderator))
        vStatistic.setTitle(t(API_TRANSLATE.about_statistic))
        vCreators.setTitle(t(API_TRANSLATE.about_creators))
        vAbout.setTitle(t(API_TRANSLATE.app_wiki))
        vGallery.setTitle(t(API_TRANSLATE.app_gallery))

        vPolicy.setTitle(t(API_TRANSLATE.about_privacy_policy))

        vAbout.setOnClickListener { Navigator.to(SWikiList(API.FANDOM_CAMPFIRE_ID, ControllerApi.getLanguageId(), 0, "")) }
        vRulesUser.setOnClickListener { Navigator.to(SRulesUser()) }
        vRulesModerator.setOnClickListener { Navigator.to(SRulesModerators()) }
        vStartScreen.setOnClickListener { Navigator.to(SCampfireHello(true) { Navigator.back() }) }
        vCreators.setOnClickListener { Navigator.to(SAboutCreators()) }
        vStatistic.setOnClickListener { SStatistic.instance(Navigator.TO) }
        vAdminsEvents.setOnClickListener{ Navigator.to(SAdministrationAdminsEvents())}
        vPrison.setOnClickListener{ Navigator.to(SAdministrationPrison())}
        vGallery.setOnClickListener{ Navigator.to(SGallery())}
        vPolicy.setOnClickListener { ToolsIntent.openLink("http://campfiresayzen.net/eng.html") }
        vMail.setOnClickListener { ToolsIntent.startMail("zeooon@ya.ru") }
    }

}
