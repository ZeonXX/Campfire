package com.dzen.campfire.screens.settings


import android.text.InputType
import com.dzen.campfire.R
import com.dzen.campfire.api.API
import com.dzen.campfire.api.API_RESOURCES
import com.dzen.campfire.api.API_TRANSLATE
import com.dzen.campfire.api.requests.accounts.RAccountsAddGoogle
import com.dzen.campfire.api.requests.accounts.RAccountsChangePassword
import com.dzen.campfire.api.requests.accounts.RAccountsGetEmail
import com.dzen.campfire.api.requests.accounts.RAccountsRegistration.Companion.E_GOOGLE_ID_EXIST
import com.dzen.campfire.api.requests.fandoms.RFandomsGet
import com.dzen.campfire.screens.intro.SIntroAccount
import com.dzen.campfire.screens.intro.SIntroConnection
import com.dzen.campfire.screens.intro.SIntroEmailRegistration
import com.sayzen.campfiresdk.controllers.*
import com.sayzen.campfiresdk.models.events.account.EventAccountEmailChanged
import com.sayzen.campfiresdk.models.events.account.EventAccountGoogleIdChanged
import com.sayzen.campfiresdk.screens.fandoms.search.SFandomsSearch
import com.sayzen.campfiresdk.screens.fandoms.view.SFandom
import com.sayzen.campfiresdk.support.ApiRequestsSupporter
import com.sayzen.devsupandroidgoogle.ControllerGoogleAuth
import com.sup.dev.android.app.SupAndroid
import com.sup.dev.android.libs.image_loader.ImageLoader
import com.sup.dev.android.libs.screens.Screen
import com.sup.dev.android.libs.screens.navigator.NavigationAction
import com.sup.dev.android.libs.screens.navigator.Navigator
import com.sup.dev.android.tools.ToolsAndroid
import com.sup.dev.android.tools.ToolsToast
import com.sup.dev.android.views.settings.Settings
import com.sup.dev.android.views.settings.SettingsSwitcher
import com.sup.dev.android.views.splash.SplashAlert
import com.sup.dev.android.views.splash.SplashFieldTwo
import com.sup.dev.java.libs.eventBus.EventBus
import com.sup.dev.java.tools.ToolsCryptography

class SSettingsSecurity(
        var email: String,
        var googleId: String
) : Screen(R.layout.screen_settings_security) {

    companion object {

        fun instance(action: NavigationAction) {
            ApiRequestsSupporter.executeInterstitial(action, RAccountsGetEmail()) { r -> SSettingsSecurity(r.email, r.googleId) }
        }
    }

    private val vEmail: Settings = findViewById(R.id.vEmail)
    private val vGoogle: Settings = findViewById(R.id.vGoogle)

    private val eventBus = EventBus
            .subscribe(EventAccountEmailChanged::class) {
                email = it.email
                update()
            }
            .subscribe(EventAccountGoogleIdChanged::class) {
                googleId = it.googleId
                update()
            }

    init {
        disableShadows()
        disableNavigation()
        setTitle(t(API_TRANSLATE.app_security))

        vEmail.setTitle(t(API_TRANSLATE.app_email))
        vGoogle.setTitle(t(API_TRANSLATE.settings_email_google_account))

        update()
    }

    fun update() {
        if (email.isNotEmpty()) {
            vEmail.setSubtitle(email)
            vEmail.setOnClickListener { changePassword() }
        } else {
            vEmail.setSubtitle(t(API_TRANSLATE.settings_email_is_empty))
            vEmail.setOnClickListener { Navigator.to(SIntroEmailRegistration(true)) }
        }

        if (googleId.isNotEmpty()) {
            vGoogle.setSubtitle(t(API_TRANSLATE.settings_email_google_account_added))
            vGoogle.setOnClickListener { }
        } else {
            vGoogle.setSubtitle(t(API_TRANSLATE.settings_email_google_account_not_added))
            vGoogle.setOnClickListener {addGoogle() }
        }

    }

    fun changePassword() {
        SplashFieldTwo()
                .setTitle(t(API_TRANSLATE.settings_email_change_password))
                .setInputType_1(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                .setInputType_2(InputType.TYPE_TEXT_VARIATION_PASSWORD)
                .setHint_1(t(API_TRANSLATE.settings_email_old_password))
                .setHint_2(t(API_TRANSLATE.settings_email_new_password))
                .setMin_1(API.ACCOUNT_PASSOWRD_L_MIN)
                .setMin_2(API.ACCOUNT_PASSOWRD_L_MIN)
                .setOnCancel(t(API_TRANSLATE.app_cancel))
                .setOnEnter(t(API_TRANSLATE.app_change)) { w, t1, t2 ->
                    val passwordOldMD5 = ToolsCryptography.md5(t1)
                    val passwordNewMD5 = ToolsCryptography.md5(t2)
                    ApiRequestsSupporter.executeEnabled(w, RAccountsChangePassword(email, passwordOldMD5, passwordNewMD5)) { r ->
                        ToolsToast.show(t(API_TRANSLATE.app_done))
                        ControllerApiLogin.setEmailToken(email, passwordNewMD5)
                    }
                }
                .asSheetShow()
    }

    fun addGoogle() {
        ControllerGoogleAuth.logout {
            ControllerGoogleAuth.getToken {
                if (it == null) {
                    ToolsToast.show(R.string.connection_error)
                } else {
                    ControllerGoogleAuth.tokenPostExecutor.invoke(it) { token ->
                        if (token == null) {
                            ToolsToast.show(R.string.connection_error)
                        } else {
                            ApiRequestsSupporter.executeProgressDialog(RAccountsAddGoogle(token)
                                    .onApiError(RAccountsAddGoogle.E_GOOGLE_ID_EXIST){
                                        ToolsToast.show(t(API_TRANSLATE.settings_email_google_account_used))
                                    })
                            { r ->
                                ToolsToast.show(t(API_TRANSLATE.app_done))
                                EventBus.post(EventAccountGoogleIdChanged(r.googleId))
                            }
                        }
                    }
                }
            }
        }
    }


}
