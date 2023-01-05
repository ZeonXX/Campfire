package com.dzen.campfire.app

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dzen.campfire.R
import com.dzen.campfire.api.API_TRANSLATE
import com.dzen.campfire.screens.feed.SFeed
import com.dzen.campfire.screens.feed.filters.SplashFilters
import com.dzen.campfire.screens.hello.SCampfireHello
import com.dzen.campfire.screens.intro.SIntro
import com.dzen.campfire.screens.settings.SOther
import com.sayzen.campfiresdk.app.CampfireConstants
import com.sayzen.campfiresdk.controllers.*
import com.sayzen.campfiresdk.models.events.account.EventAccountChanged
import com.sayzen.campfiresdk.models.events.account.EventAccountCurrentChanged
import com.sayzen.campfiresdk.models.events.activities.EventActivitiesCountChanged
import com.sayzen.campfiresdk.models.events.chat.EventChatMessagesCountChanged
import com.sayzen.campfiresdk.models.events.notifications.EventNotificationsCountChanged
import com.sayzen.campfiresdk.screens.account.profile.SProfile
import com.sayzen.campfiresdk.screens.account.search.SAccountSearch
import com.sayzen.campfiresdk.screens.achievements.SAchievements
import com.sayzen.campfiresdk.screens.activities.CardQuest
import com.sayzen.campfiresdk.screens.activities.SActivities
import com.sayzen.campfiresdk.screens.chat.SChat
import com.sayzen.campfiresdk.screens.chat.SChatSubscribers
import com.sayzen.campfiresdk.screens.chat.SChats
import com.sayzen.campfiresdk.screens.chat.create.SChatCreate
import com.sayzen.campfiresdk.screens.fandoms.search.SFandomsSearch
import com.sayzen.campfiresdk.screens.notifications.SNotifications
import com.sayzen.campfiresdk.screens.post.bookmarks.SBookmarks
import com.sayzen.campfiresdk.screens.post.drafts.SDrafts
import com.sayzen.campfiresdk.screens.quests.SQuests
import com.sayzen.campfiresdk.support.adapters.XAccount
import com.sayzen.devsupandroidgoogle.ControllerFirebaseAnalytics
import com.sup.dev.android.app.SupAndroid
import com.sup.dev.android.libs.screens.Screen
import com.sup.dev.android.libs.screens.activity.SActivity
import com.sup.dev.android.libs.screens.activity.SActivityType
import com.sup.dev.android.libs.screens.activity.SActivityTypeBottomNavigation
import com.sup.dev.android.libs.screens.activity.SActivityTypeDrawer
import com.sup.dev.android.libs.screens.navigator.Navigator
import com.sup.dev.android.libs.screens.navigator.NavigatorStack
import com.sup.dev.android.tools.*
import com.sup.dev.android.views.views.ViewAvatarTitle
import com.sup.dev.android.views.views.layouts.LayoutCorned
import com.sup.dev.java.libs.eventBus.EventBus

class AppActivity : SActivity() {

    private val eventBus = EventBus
            .subscribe(EventChatMessagesCountChanged::class) { updateMessagesCount() }
            .subscribe(EventNotificationsCountChanged::class) { updateNotificationsCount() }
            .subscribe(EventActivitiesCountChanged::class) { updateActivitiesCount() }
            .subscribe(EventAccountChanged::class) { if (it.accountId == ControllerApi.account.getId()) updateAccount() }
            .subscribe(EventAccountCurrentChanged::class) { App.activity().updateAccount() }

    private var vNavigationTitleView: ViewGroup? = null
    private var vNavigationTitle: ViewAvatarTitle? = null
    private var vExtra: SActivityType.NavigationItem? = null
    private var vChats: SActivityType.NavigationItem? = null
    private var vFeed: SActivityType.NavigationItem? = null
    private var vAchievements: SActivityType.NavigationItem? = null
    private var vNotifications: SActivityType.NavigationItem? = null
    private var vFandoms: SActivityType.NavigationItem? = null
    private var vActivities: SActivityType.NavigationItem? = null
    private var vUsers: SActivityType.NavigationItem? = null
    private var vDrafts: SActivityType.NavigationItem? = null
    private var vBookmarks: SActivityType.NavigationItem? = null
    private var vQuests: SActivityType.NavigationItem? = null
    private var vOther: SActivityType.NavigationItem? = null

    var stackChats: NavigatorStack? = null
    var stackFeed: NavigatorStack? = null
    var cardQuest = CardQuest()

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)

        parseNotifications = false

        vNavigationTitleView = ToolsView.inflate(R.layout.view_navigation_title)
        vNavigationTitle = vNavigationTitleView!!.findViewById(R.id.vNavigationTitle)
        val vTitleMenuContainer: ViewGroup = vNavigationTitleView!!.findViewById(R.id.vTitleMenuContainer)
        val vTitleMenuCorners: LayoutCorned = vNavigationTitleView!!.findViewById(R.id.vTitleMenuCorners)
        vTitleMenuContainer.setOnClickListener { SProfile.instance(ControllerApi.account.getAccount(), Navigator.TO) }
        vTitleMenuContainer.addView(cardQuest.createQuestView(vTitleMenuContainer), 1)
        vTitleMenuCorners.makeSoftware()
        vNavigationTitle?.setChipMode(false)
        vNavigationTitle?.setCornedBL(false)
        vNavigationTitle?.setCornedBR(false)
        vNavigationTitle?.setCornedTL(false)
        vNavigationTitle?.setCornedTR(false)
        vNavigationTitle?.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        type.addNavigationView(vNavigationTitleView!!, false)

        type.setIconsColor(if (ControllerSettings.styleTheme == 0) ToolsResources.getColor(R.color.grey_700) else ToolsResources.getColorAttr(R.attr.colorOnPrimary))

        vAchievements = type.addNavigationItem(R.drawable.ic_star_white_24dp, t(API_TRANSLATE.app_achievements), false, true) {
            ControllerFirebaseAnalytics.post("Root", "To Achievements")
            SAchievements.instance(false, Navigator.TO)
        }.setTargetScreen(SAchievements::class)
        vChats = type.addNavigationItem(R.drawable.ic_mode_comment_white_24dp, t(API_TRANSLATE.app_chats), false, true) {
            ControllerFirebaseAnalytics.post("Root", "To Chats")
            if (Navigator.currentStack == stackChats) {
                val last = Navigator.getLast(SChats::class)
                if (last != null) Navigator.set(last)
                else Navigator.set(SChats())
            } else {
                setChatStack()
                if (!Navigator.hasBackStack()) Navigator.set(SChats())
            }
        }.setTargetScreen(SChats::class, SChat::class)
        vFeed = type.addNavigationItem(R.drawable.ic_all_inclusive_white_24dp, t(API_TRANSLATE.app_feed), false, true,
        onClick = {
                ControllerFirebaseAnalytics.post("Root", "To Feed")
                if (Navigator.currentStack == stackFeed) {
                    val has = Navigator.getCurrent() is SNotifications || Navigator.getCurrent() is SAchievements
                    if (has) {
                        Navigator.removeAll(SNotifications::class)
                        Navigator.removeAll(SAchievements::class)
                    } else {
                        Navigator.set(SFeed())
                    }
                } else {
                    setFeedStack()
                    Navigator.removeAll(SNotifications::class)
                    Navigator.removeAll(SAchievements::class)
                    if (Navigator.getStackSize() == 0 || Navigator.getCurrent() is SIntro) Navigator.set(SFeed())
                }
        }, onLongClick = {
            SplashFilters { Navigator.replace(SFeed()) }.asSheetShow()
        }).makeDefaultTargetItem()
        vNotifications = type.addNavigationItem(R.drawable.ic_notifications_white_24dp, t(API_TRANSLATE.app_notifications), false, true,
                onClick = {
                    ControllerFirebaseAnalytics.post("Root", "To Notifications")
                    SNotifications.instance(Navigator.TO_BACK_STACK_OR_NEW)
                },
                onLongClick = {
                    ControllerApi.showSalientDialog()
                }).setTargetScreen(SNotifications::class)
        type.addNavigationDivider()
        vFandoms = type.addNavigationItem(R.drawable.ic_account_balance_white_24dp, t(API_TRANSLATE.app_fandoms), true, true) {
            ControllerFirebaseAnalytics.post("Root", "To Fandoms")
            SFandomsSearch.instance(Navigator.TO_BACK_STACK_OR_NEW)
        }
        vActivities = type.addNavigationItem(R.drawable.ic_rowing_white_24dp, t(API_TRANSLATE.app_activities), true, true) {
            ControllerFirebaseAnalytics.post("Root", "To Activity")
            Navigator.toBackStackOrNew(SActivities())
        }
        vUsers = type.addNavigationItem(R.drawable.ic_group_white_24dp, t(API_TRANSLATE.app_users), true, true) {
            ControllerFirebaseAnalytics.post("Root", "To Users")
            Navigator.toBackStackOrNew(SAccountSearch(false, false, false, true) { SProfile.instance(it, Navigator.TO) })
        }
        vDrafts = type.addNavigationItem(R.drawable.ic_mode_edit_white_24dp, t(API_TRANSLATE.app_drafts), true, true) {
            ControllerFirebaseAnalytics.post("Root", "To Drafts")
            Navigator.toBackStackOrNew(SDrafts())
        }
        vBookmarks = type.addNavigationItem(R.drawable.ic_bookmark_white_24dp, t(API_TRANSLATE.app_bookmarks), true, true) {
            ControllerFirebaseAnalytics.post("Root", "To Bookmarks")
            Navigator.toBackStackOrNew(SBookmarks())
        }
        vQuests = type.addNavigationItem(R.drawable.baseline_history_edu_white_24, t(API_TRANSLATE.quests), true, true) {
            ControllerFirebaseAnalytics.post("Root", "To Quests")
            Navigator.toBackStackOrNew(SQuests())
        }
        vOther = type.addNavigationItem(R.drawable.ic_settings_white_24dp, t(API_TRANSLATE.app_other), true, true) {
            ControllerFirebaseAnalytics.post("Root", "To Other")
            Navigator.toBackStackOrNew(SOther())
        }

        vExtra = type.getExtraNavigationItem()

        updateNotificationsCount()
        updateAccount()

        if (ToolsAndroid.getLanguage(this) != ControllerSettings.appLanguage.toLowerCase()) {
            ToolsAndroid.setLanguage(SupAndroid.appContext!!, ControllerSettings.appLanguage)
            ToolsAndroid.setLanguage(this, ControllerSettings.appLanguage)
            recreate()
        }
    }

    override fun getNavigationType(): SActivityType {
        if (ControllerSettings.interfaceType == 1) {
            return SActivityTypeDrawer(this)
        } else {
            return SActivityTypeBottomNavigation(this)
        }
    }

    fun setFeedStack() {
        if (stackFeed == null) stackFeed = NavigatorStack()
        Navigator.setStack(stackFeed!!)
        if(stackChats != null){
            val copy = ArrayList<Screen>(stackChats!!.stack)
            for(i in copy) if(i !is SChats && i !is SChat && i !is SChatSubscribers && i !is SChatCreate) stackChats!!.stack.remove(i)
        }
    }

    fun setChatStack() {
        if (stackChats == null) stackChats = NavigatorStack()
        Navigator.setStack(stackChats!!)
    }

    fun resetStacks() {
        stackChats?.clear()
    }

    private var labelLastAccountId = 0L
    private var profileLabel = ""
    private var xAccount = XAccount()

    fun updateAccount() {
        updateMessagesCount()

        if (!ControllerApi.isCurrentAccount(labelLastAccountId) && !ControllerApi.isCurrentAccount(0L)) {
            profileLabel = CampfireConstants.randomAccountText()
            labelLastAccountId = ControllerApi.account.getId()
        }

        if (vNavigationTitle != null) {
            xAccount = XAccount().setAccount(ControllerApi.account.getAccount()).setOnChanged {
                xAccount.setView(vNavigationTitle!!)
                vNavigationTitle?.setSubtitle(profileLabel)
            }
            xAccount.setView(vNavigationTitle!!)
            vNavigationTitle?.setSubtitle(profileLabel)
        }

    }

    override fun applyTheme() {

        val themeResource = when (ControllerSettings.styleTheme) {
            1 -> {;R.style.CampfireThemeDarkGrey; }
            2 -> {;R.style.CampfireRootThemeWhite; }

            3 -> {;R.style.CampfireThemeBlack_Red; }
            4 -> {;R.style.CampfireThemeBlack_Pink; }
            5 -> {;R.style.CampfireThemeBlack_Purple; }
            6 -> {;R.style.CampfireThemeBlack_DeepPurple; }
            7 -> {;R.style.CampfireThemeBlack_Indigo; }
            8 -> {;R.style.CampfireThemeBlack_Blue; }
            9 -> {;R.style.CampfireThemeBlack_LightBlue; }
            10 -> {;R.style.CampfireThemeBlack_Cyan; }
            11 -> {;R.style.CampfireThemeBlack_Teal; }
            12 -> {;R.style.CampfireThemeBlack_Green; }
            13 -> {;R.style.CampfireThemeBlack_LightGreen; }
            14 -> {;R.style.CampfireThemeBlack_Lime; }
            15 -> {;R.style.CampfireThemeBlack_DeepOrange; }
            16 -> {;R.style.CampfireThemeBlack_Brown; }
            17 -> {;R.style.CampfireThemeBlack_BlueGrey; }

            18 -> {;R.style.CampfireThemeWhite_Red; }
            19 -> {;R.style.CampfireThemeWhite_Pink; }
            20 -> {;R.style.CampfireThemeWhite_Purple; }
            21 -> {;R.style.CampfireThemeWhite_DeepPurple; }
            22 -> {;R.style.CampfireThemeWhite_Indigo; }
            23 -> {;R.style.CampfireThemeWhite_Blue; }
            24 -> {;R.style.CampfireThemeWhite_LightBlue; }
            25 -> {;R.style.CampfireThemeWhite_Cyan; }
            26 -> {;R.style.CampfireThemeWhite_Teal; }
            27 -> {;R.style.CampfireThemeWhite_Green; }
            28 -> {;R.style.CampfireThemeWhite_LightGreen; }
            29 -> {;R.style.CampfireThemeWhite_Lime; }
            30 -> {;R.style.CampfireThemeWhite_DeepOrange; }
            31 -> {;R.style.CampfireThemeWhite_Brown; }
            32 -> {;R.style.CampfireThemeWhite_BlueGrey; }

            else -> {;R.style.CampfireRootThemeBlack; }
        }
        setTheme(themeResource)

        if (ControllerSettings.styleTheme == 2 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (ControllerApi.account.getId() > 0) parseIntent_X(intent)
        setIntent(Intent())
    }

    fun parseStartAction(): Boolean {
        if (parseIntent_X(intent)) {
            intent = Intent()
            return true
        }
        return false
    }

    private fun parseIntent_X(intent: Intent?): Boolean {
        if (intent != null) {
            if (intent.action == Intent.ACTION_SEND) {
                val text = intent.getStringExtra(Intent.EXTRA_TEXT)
                val image = (intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)
                ControllerAttache.onAttache(text, image)
                return false
            }
            val data = intent.data
            if (data != null) {
                return ControllerLinks.parseLink(data.toString())
            }
            if (ToolsNotifications.parseNotification(intent)) {
                return true
            }
        }
        return false
    }

    override fun onFirstStart() {
        Navigator.set(SIntro())
        // Navigator.set(STest())
    }

    override fun toMainScreen() {
        setFeedStack()
        Navigator.set(SFeed())
    }

    fun updateNotificationsCount() {
        val n = ControllerNotifications.getNewNotificationsCount()
        if (n <= 0) vNotifications!!.setChipText("")
        if (n in 1..9) vNotifications!!.setChipText("$n")
        if (n > 9) vNotifications!!.setChipText("9+")
    }

    fun updateActivitiesCount() {
        vActivities?.setChipText(if (ControllerActivities.getActivitiesCount() > 0) "" + ControllerActivities.getActivitiesCount() else "")
        vExtra?.setChipText(if (ControllerActivities.getActivitiesCount() > 0) "" + ControllerActivities.getActivitiesCount() else "")
    }

    fun updateMessagesCount() {
        val count = ControllerChats.getMessagesCount_forNavigation()
        vChats!!.setChipText(if (count > 0) "" + count else "")
    }

    private var exitKey = 0L
    private var exitTimeout = 2000L
    private var toast:Toast? = null

    override fun onBackPressedScreen() {
        if (exitKey < System.currentTimeMillis() - exitTimeout && !Navigator.hasPrevious()) {

            if ((Navigator.currentStack == stackFeed && stackChats?.isEmpty() != false && Navigator.getCurrent() is SFeed)
                    || (Navigator.currentStack == stackChats && stackFeed?.isEmpty() != false  && Navigator.getCurrent() is SChats)) {
                exitKey = System.currentTimeMillis()
                ToolsToast.show(t(API_TRANSLATE.message_exit), onShowed = {this.toast = it})
                return

            }


        }
        toast?.cancel()
        super.onBackPressedScreen()
    }

    override fun onLastBackPressed(screen: Screen?): Boolean {

        if(screen !is SIntro && screen !is SCampfireHello) {

            if(Navigator.currentStack != stackFeed && Navigator.currentStack != stackChats){
                toMainScreen()
                return true
            }

            if (Navigator.currentStack == stackFeed) {
                if (stackChats?.isEmpty() == false) {
                    setChatStack()
                    return true
                }
                if (screen !is SFeed){
                    toMainScreen()
                    return true
                }
            }

            if (Navigator.currentStack == stackChats) {

                if (stackFeed?.isEmpty() == false) {
                    setFeedStack()
                    return true
                }
                if (screen !is SChats){
                    Navigator.to(SChats())
                    return true
                }
            }

        }
        return false
    }

}