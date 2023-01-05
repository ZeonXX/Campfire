package com.dzen.campfire.screens.settings


import android.text.InputType
import com.dzen.campfire.R
import com.dzen.campfire.api.API
import com.dzen.campfire.api.API_TRANSLATE
import com.dzen.campfire.api.requests.accounts.RAccountsAddGoogle
import com.dzen.campfire.api.requests.accounts.RAccountsGetEmail
import com.dzen.campfire.screens.intro.SIntroEmailRegistration
import com.dzen.campfire.screens.intro.SIntroEmailVerify
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.sayzen.campfiresdk.controllers.t
import com.sayzen.campfiresdk.models.events.account.EventAccountEmailChanged
import com.sayzen.campfiresdk.models.events.account.EventAccountGoogleIdChanged
import com.sayzen.campfiresdk.support.ApiRequestsSupporter
import com.sayzen.devsupandroidgoogle.ControllerGoogleAuth
import com.sup.dev.android.libs.screens.Screen
import com.sup.dev.android.libs.screens.navigator.NavigationAction
import com.sup.dev.android.libs.screens.navigator.Navigator
import com.sup.dev.android.tools.ToolsToast
import com.sup.dev.android.tools.ToolsView
import com.sup.dev.android.views.settings.Settings
import com.sup.dev.android.views.splash.SplashFieldTwo
import com.sup.dev.java.libs.eventBus.EventBus
import com.sup.dev.java.tools.ToolsCryptography

class SSettingsSecurity(
    var resp: RAccountsGetEmail.Response,
) : Screen(R.layout.screen_settings_security) {

    companion object {

        fun instance(action: NavigationAction) {
            ApiRequestsSupporter.executeInterstitial(action, RAccountsGetEmail()) { r ->
                SSettingsSecurity(r)
            }
        }
    }

    private val vEmail: Settings = findViewById(R.id.vEmail)
    private val vGoogle: Settings = findViewById(R.id.vGoogle)

    private val eventBus = EventBus
            .subscribe(EventAccountEmailChanged::class) {
                resp.email = it.email
                update()
            }
            .subscribe(EventAccountGoogleIdChanged::class) {
                resp.googleId = it.googleId
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
        if (resp.email.isNotEmpty()) {
            if (resp.emailMigrated) vEmail.setSubtitle(t(API_TRANSLATE.settings_email_migrated, resp.email))
            else vEmail.setSubtitle(resp.email)
            vEmail.setOnClickListener { changePassword() }
        } else {
            vEmail.setSubtitle(t(API_TRANSLATE.settings_email_is_empty))
            vEmail.setOnClickListener {
                if (FirebaseAuth.getInstance().currentUser == null)
                    Navigator.to(SIntroEmailRegistration(true))
                else Navigator.to(SIntroEmailVerify(true))
            }
        }

        if (resp.googleId.isNotEmpty()) {
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
                    val passwordOldSha512 = ToolsCryptography.getSHA512(t1)
                    val passwordNewSha512 = ToolsCryptography.getSHA512(t2)
                    val auth = FirebaseAuth.getInstance()
                    val progress = ToolsView.showProgressDialog()

                    val updatePassword = {
                        auth.currentUser!!.updatePassword(passwordNewSha512)
                            .addOnSuccessListener {
                                ToolsToast.show(t(API_TRANSLATE.app_done))
                            }
                            .addOnFailureListener {
                                ToolsToast.show(t(API_TRANSLATE.app_error))
                            }
                            .addOnCompleteListener {
                                progress.hide()
                            }
                    }

                    if (auth.currentUser == null) {
                        auth.signInWithEmailAndPassword(resp.email, passwordOldSha512)
                            .addOnSuccessListener {
                                updatePassword()
                            }
                            .addOnFailureListener {
                                ToolsToast.show(t(API_TRANSLATE.settings_email_change_password_error))
                                progress.hide()
                            }
                        return@setOnEnter
                    }

                    auth.currentUser!!.reauthenticate(EmailAuthProvider.getCredential(
                        auth.currentUser!!.email!!, passwordOldSha512
                    ))
                        .addOnSuccessListener {
                            updatePassword()
                        }
                        .addOnFailureListener {
                            ToolsToast.show(t(API_TRANSLATE.settings_email_change_password_error))
                            progress.hide()
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
