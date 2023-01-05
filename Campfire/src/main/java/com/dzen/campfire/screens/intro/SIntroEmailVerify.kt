package com.dzen.campfire.screens.intro

import com.dzen.campfire.R
import com.dzen.campfire.api.API_TRANSLATE
import com.dzen.campfire.api.requests.accounts.RAccountsFirebaseAdd
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sayzen.campfiresdk.controllers.ControllerApiLogin
import com.sayzen.campfiresdk.controllers.t
import com.sayzen.campfiresdk.support.ApiRequestsSupporter
import com.sup.dev.android.libs.screens.Screen
import com.sup.dev.android.libs.screens.navigator.Navigator
import com.sup.dev.android.tools.ToolsResources
import com.sup.dev.android.tools.ToolsToast
import com.sup.dev.android.tools.ToolsView
import com.sup.dev.android.views.views.ViewButton
import com.sup.dev.android.views.views.ViewText

class SIntroEmailVerify(
    private val addMode: Boolean
) : Screen(R.layout.screen_intro_email_verify) {
    private val vText: ViewText = findViewById(R.id.vText)
    private val vEnter: ViewButton = findViewById(R.id.vEnter)
    private val vLogout: ViewButton = findViewById(R.id.vLogout)

    private val fbAuth by lazy { Firebase.auth }

    init {
        activityRootBackground = ToolsResources.getColorAttr(R.attr.colorPrimary)
        disableNavigation()

        vText.setText(R.string.app_email_verify)
        vEnter.setText(R.string.retry)

        vEnter.setOnClickListener {
            val progress = ToolsView.showProgressDialog()
            fbAuth.currentUser!!.reload()
                .addOnSuccessListener {
                    if (!fbAuth.currentUser!!.isEmailVerified) {
                        ToolsToast.show(t(API_TRANSLATE.settings_email_not_verified))
                        progress.hide()
                        return@addOnSuccessListener
                    }
                    fbAuth.currentUser!!.getIdToken(true)
                        .addOnSuccessListener {
                            if (addMode) {
                                ApiRequestsSupporter.execute(RAccountsFirebaseAdd(it.token ?: "")) {
                                    ToolsToast.show(t(API_TRANSLATE.app_done))
                                    Navigator.remove(this)
                                }.onApiError(RAccountsFirebaseAdd.E_VERIFY) {
                                    ToolsToast.show(t(API_TRANSLATE.settings_email_not_verified))
                                }.onComplete {
                                    progress.hide()
                                }
                            } else {
                                ControllerApiLogin.setLoginType(ControllerApiLogin.LOGIN_EMAIL)
                                Navigator.set(SIntroConnection())

                                progress.hide()
                            }
                        }
                        .addOnFailureListener {
                            it.printStackTrace()
                            ToolsToast.show(it.localizedMessage ?: it.message)
                            progress.hide()
                        }
                }
                .addOnFailureListener {
                    it.printStackTrace()
                    ToolsToast.show(it.localizedMessage ?: it.message)
                    progress.hide()
                }
        }

        vLogout.text = t(API_TRANSLATE.settings_log_out)
        vLogout.setOnClickListener {
            fbAuth.signOut()
            Navigator.replace(SIntroEmailRegistration(addMode))
        }
    }
}