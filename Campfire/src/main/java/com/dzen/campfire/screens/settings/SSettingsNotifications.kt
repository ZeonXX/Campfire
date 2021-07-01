package com.dzen.campfire.screens.settings

import com.dzen.campfire.R
import com.dzen.campfire.api.API_TRANSLATE
import com.sayzen.campfiresdk.controllers.ControllerApi
import com.sayzen.campfiresdk.controllers.ControllerSettings
import com.sayzen.campfiresdk.controllers.t
import com.sayzen.campfiresdk.models.events.project.EventSalientTimeChanged
import com.sup.dev.android.libs.screens.Screen
import com.sup.dev.android.tools.ToolsResources
import com.sup.dev.android.views.settings.Settings
import com.sup.dev.android.views.settings.SettingsCheckBox
import com.sup.dev.android.views.settings.SettingsSwitcher
import com.sup.dev.android.views.settings.SettingsTitle
import com.sup.dev.android.views.splash.SplashCheckBoxes
import com.sup.dev.android.views.splash.SplashChooseTimeRange
import com.sup.dev.java.libs.eventBus.EventBus
import com.sup.dev.java.tools.ToolsDate

class SSettingsNotifications : Screen(R.layout.screen_settings_notifications) {

    private val eventBus = EventBus.subscribe(EventSalientTimeChanged::class){updateParams()}

    private val vPM: Settings = findViewById(R.id.vPM)
    private val vChatTitle: Settings = findViewById(R.id.vChatTitle)
    private val vFiltersTitle: Settings = findViewById(R.id.vFiltersTitle)
    private val vAppTitle: Settings = findViewById(R.id.vAppTitle)
    private val vPublicationsTitle: Settings = findViewById(R.id.vPublicationsTitle)
    private val vNotifications: SettingsSwitcher = findViewById(R.id.vNotifications)
    private val vSalient: Settings = findViewById(R.id.vSalient)
    private val vSalientAll: SettingsSwitcher = findViewById(R.id.vSalientAll)
    private val vSalientOnTime: SettingsSwitcher = findViewById(R.id.vSalientOnTime)
    private val vWatchPost: SettingsSwitcher = findViewById(R.id.vWatchPost)
    private val vAutoReadNotifications: SettingsSwitcher = findViewById(R.id.vAutoReadNotifications)
    private val vOther: SettingsCheckBox = findViewById(R.id.vOther)
    private val vComments: SettingsCheckBox = findViewById(R.id.vComments)
    private val vCommentsAnswers: SettingsCheckBox = findViewById(R.id.vCommentsAnswers)
    private val vKarma: SettingsCheckBox = findViewById(R.id.vKarma)
    private val vFollows: SettingsCheckBox = findViewById(R.id.vFollows)
    private val vImportant: SettingsCheckBox = findViewById(R.id.vImportant)
    private val vAchievements: SettingsCheckBox = findViewById(R.id.vAchievements)
    private val vFollowsPosts: SettingsCheckBox = findViewById(R.id.vFollowsPosts)
    private val vChatMessages: SettingsCheckBox = findViewById(R.id.vChatMessages)
    private val vChatAnswers: SettingsCheckBox = findViewById(R.id.vChatAnswers)
    private val vChatPM: SettingsCheckBox = findViewById(R.id.vPM)
    private val vUserActivitiesSubscribe: SettingsSwitcher = findViewById(R.id.vUserActivitiesSubscribe)
    private val vUserActivitiesAllowed: Settings = findViewById(R.id.vUserActivitiesAllowed)
    private val vAddRelayRacesTitle: SettingsTitle = findViewById(R.id.vAddRelayRacesTitle)

    init {
        disableShadows()
        disableNavigation()
        setTitle(t(API_TRANSLATE.app_notifications))

        vFiltersTitle.setTitle(t(API_TRANSLATE.settings_notifications_filter_main))
        vNotifications.setTitle(t(API_TRANSLATE.settings_notifications_notifications))
        vSalient.setTitle(t(API_TRANSLATE.settings_notifications_silent))
        vSalientAll.setTitle(t(API_TRANSLATE.settings_notifications_silent_all))
        vSalientOnTime.setTitle(t(API_TRANSLATE.settings_notifications_silent_time))
        vWatchPost.setSubtitle(t(API_TRANSLATE.settings_notifications_watch))
        vWatchPost.setTitle(t(API_TRANSLATE.settings_notifications_watch_title))
        vAutoReadNotifications.setSubtitle(t(API_TRANSLATE.settings_notifications_auto_read))
        vAutoReadNotifications.setTitle(t(API_TRANSLATE.settings_notifications_auto_read_title))
        vUserActivitiesSubscribe.setSubtitle(t(API_TRANSLATE.settings_user_activities_subscribe))
        vUserActivitiesSubscribe.setTitle(t(API_TRANSLATE.settings_user_activities_subscribe_title))
        vUserActivitiesAllowed.setTitle(t(API_TRANSLATE.settings_user_activities_allows))
        vAppTitle.setTitle(t(API_TRANSLATE.settings_notifications_filter_app))
        vFollows.setTitle(t(API_TRANSLATE.settings_notifications_filter_follows))
        vOther.setTitle(t(API_TRANSLATE.settings_notifications_filter_app_other))
        vPublicationsTitle.setTitle(t(API_TRANSLATE.settings_notifications_filter_publications))
        vComments.setTitle(t(API_TRANSLATE.settings_notifications_filter_comments))
        vKarma.setTitle(t(API_TRANSLATE.settings_notifications_filter_karma))
        vCommentsAnswers.setTitle(t(API_TRANSLATE.settings_notifications_filter_answers))
        vFollowsPosts.setTitle(t(API_TRANSLATE.settings_notifications_filter_follows_publications))
        vImportant.setTitle(t(API_TRANSLATE.settings_notifications_filter_important))
        vChatTitle.setTitle(t(API_TRANSLATE.settings_notifications_filter_chat))
        vChatMessages.setTitle(t(API_TRANSLATE.settings_notifications_filter_chat_messages))
        vChatAnswers.setTitle(t(API_TRANSLATE.settings_notifications_filter_chat_answers))
        vPM.setTitle(t(API_TRANSLATE.settings_notifications_filter_pm))
        vAddRelayRacesTitle.setTitle(t(API_TRANSLATE.app_relay_races))
        vAchievements.setTitle(t(API_TRANSLATE.app_achievements))

        vNotifications.addSubSettings(vSalient)
        vNotifications.addSubSettings(vSalientOnTime)
        vNotifications.addSubSettings(vSalientAll)
        vSalientAll.addSubSettings(vSalientOnTime)
        vSalientAll.setReversSubSettingsEnabled(true)

        vSalient.setOnClickListener { ControllerApi.showSalientDialog() }
        vNotifications.setOnClickListener { ControllerSettings.notifications = vNotifications.isChecked() }
        vWatchPost.setOnClickListener { ControllerSettings.watchPost = vWatchPost.isChecked() }
        vAutoReadNotifications.setOnClickListener { ControllerSettings.autoReadNotifications = vAutoReadNotifications.isChecked() }
        vComments.setOnClickListener { ControllerSettings.notificationsComments = vComments.isChecked() }
        vOther.setOnClickListener { ControllerSettings.notificationsOther = vOther.isChecked() }
        vCommentsAnswers.setOnClickListener { ControllerSettings.notificationsCommentsAnswers = vCommentsAnswers.isChecked() }
        vKarma.setOnClickListener { ControllerSettings.notificationsKarma = vKarma.isChecked() }
        vFollows.setOnClickListener { ControllerSettings.notificationsFollows = vFollows.isChecked() }
        vImportant.setOnClickListener { ControllerSettings.notificationsImportant = vImportant.isChecked() }
        vAchievements.setOnClickListener { ControllerSettings.notificationsAchievements = vAchievements.isChecked() }
        vFollowsPosts.setOnClickListener { ControllerSettings.notificationsFollowsPosts = vFollowsPosts.isChecked() }
        vChatMessages.setOnClickListener { ControllerSettings.notificationsChatMessages = vChatMessages.isChecked() }
        vChatAnswers.setOnClickListener { ControllerSettings.notificationsChatAnswers = vChatAnswers.isChecked() }
        vChatPM.setOnClickListener { ControllerSettings.notificationsPM = vChatPM.isChecked() }
        vSalientAll.setOnClickListener { ControllerSettings.notificationsSalientAll = vSalientAll.isChecked() }
        vUserActivitiesSubscribe.setOnClickListener { ControllerSettings.userActivitiesAutoSubscribe = vUserActivitiesSubscribe.isChecked() }
        vUserActivitiesAllowed.setOnClickListener { changeActivitiesAllowed() }

        vSalientOnTime.setOnClickListener {
            ControllerSettings.notificationsSalientOnTimeEnabled = vSalientOnTime.isChecked()
            if (vSalientOnTime.isChecked()) salientOnTime()
        }

        updateParams()
    }


    private fun salientOnTime() {
        SplashChooseTimeRange()
                .setTimeStart(ControllerSettings.notificationsSalientOnTimeStartH, ControllerSettings.notificationsSalientOnTimeStartM)
                .setTimeEnd(ControllerSettings.notificationsSalientOnTimeEndH, ControllerSettings.notificationsSalientOnTimeEndM)
                .setOnEnter(t(API_TRANSLATE.app_save)) { _, h1, m1, h2, m2 ->
                    ControllerSettings.notificationsSalientOnTimeStartH = h1
                    ControllerSettings.notificationsSalientOnTimeStartM = m1
                    ControllerSettings.notificationsSalientOnTimeEndH = h2
                    ControllerSettings.notificationsSalientOnTimeEndM = m2
                    updateParams()
                }
                .asSheetShow()
    }

    private fun updateParams() {
        vNotifications.setChecked(ControllerSettings.notifications)
        vWatchPost.setChecked(ControllerSettings.watchPost)
        vAutoReadNotifications.setChecked(ControllerSettings.autoReadNotifications)
        vComments.setChecked(ControllerSettings.notificationsComments)
        vOther.setChecked(ControllerSettings.notificationsOther)
        vCommentsAnswers.setChecked(ControllerSettings.notificationsCommentsAnswers)
        vKarma.setChecked(ControllerSettings.notificationsKarma)
        vFollows.setChecked(ControllerSettings.notificationsFollows)
        vImportant.setChecked(ControllerSettings.notificationsImportant)
        vAchievements.setChecked(ControllerSettings.notificationsAchievements)
        vFollowsPosts.setChecked(ControllerSettings.notificationsFollowsPosts)
        vChatMessages.setChecked(ControllerSettings.notificationsChatMessages)
        vChatAnswers.setChecked(ControllerSettings.notificationsChatAnswers)
        vChatPM.setChecked(ControllerSettings.notificationsPM)
        vSalientAll.setChecked(ControllerSettings.notificationsSalientAll)
        vUserActivitiesSubscribe.setChecked(ControllerSettings.userActivitiesAutoSubscribe)

        vSalientOnTime.setChecked(ControllerSettings.notificationsSalientOnTimeEnabled)
        vSalientOnTime.setSubtitle(ToolsDate.timeToString(ControllerSettings.notificationsSalientOnTimeStartH, ControllerSettings.notificationsSalientOnTimeStartM) + " - " + ToolsDate.timeToString(ControllerSettings.notificationsSalientOnTimeEndH, ControllerSettings.notificationsSalientOnTimeEndM))


        val salientTime = ControllerSettings.salientTime
        if (salientTime < System.currentTimeMillis())
            vSalient.setSubtitle(t(API_TRANSLATE.settings_notifications_enabled))
        else
            vSalient.setSubtitle(String.format(t(API_TRANSLATE.settings_notifications_disabled), ToolsDate.dateToString(salientTime)))


        var v = ""
        if (ControllerSettings.userActivitiesAllowed_all) {
            v = t(API_TRANSLATE.app_all_person)
        } else {
            if (ControllerSettings.userActivitiesAllowed_followedFandoms) {
                v = t(API_TRANSLATE.app_fandoms)
                if (ControllerSettings.userActivitiesAllowed_followedUsers) {
                    v += ", " + t(API_TRANSLATE.app_users)
                }
            } else {
                if (ControllerSettings.userActivitiesAllowed_followedUsers) {
                    v = t(API_TRANSLATE.app_users)
                } else {
                    v = t(API_TRANSLATE.app_nobody)
                }
            }
        }
        vUserActivitiesAllowed.setSubtitle(v)
    }

    private fun changeActivitiesAllowed() {
        SplashCheckBoxes()
                .add(t(API_TRANSLATE.app_all_person)).checked(ControllerSettings.userActivitiesAllowed_all)
                .add(t(API_TRANSLATE.settings_user_activities_allows_fandoms)).checked(ControllerSettings.userActivitiesAllowed_followedFandoms)
                .add(t(API_TRANSLATE.settings_user_activities_allows_users)).checked(ControllerSettings.userActivitiesAllowed_followedUsers)
                .setOnCancel(t(API_TRANSLATE.app_cancel))
                .setOnEnter(t(API_TRANSLATE.app_save)) {
                    ControllerSettings.userActivitiesAllowed_all = it.contains(0)
                    ControllerSettings.userActivitiesAllowed_followedFandoms = it.contains(1)
                    ControllerSettings.userActivitiesAllowed_followedUsers = it.contains(2)
                    updateParams()
                }
                .asSheetShow()
    }


}
