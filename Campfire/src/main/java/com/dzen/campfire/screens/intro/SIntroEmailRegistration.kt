package com.dzen.campfire.screens.intro

import android.view.View
import android.widget.Button
import android.widget.CheckBox
import com.dzen.campfire.R
import com.dzen.campfire.api.API
import com.dzen.campfire.api.API_TRANSLATE
import com.dzen.campfire.api.requests.accounts.RAccountsAddEmail
import com.dzen.campfire.api.requests.accounts.RAccountsGetCaptchaSiteKey
import com.dzen.campfire.api.requests.accounts.RAccountsRegistrationEmail
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sayzen.campfiresdk.app.CampfireConstants
import com.sayzen.campfiresdk.controllers.ControllerApi
import com.sayzen.campfiresdk.controllers.ControllerApiLogin
import com.sayzen.campfiresdk.controllers.ControllerCaptcha
import com.sayzen.campfiresdk.controllers.t
import com.sayzen.campfiresdk.screens.other.rules.SGoogleRules
import com.sayzen.campfiresdk.support.ApiRequestsSupporter
import com.sup.dev.android.libs.screens.Screen
import com.sup.dev.android.libs.screens.navigator.Navigator
import com.sup.dev.android.tools.ToolsResources
import com.sup.dev.android.tools.ToolsStorage
import com.sup.dev.android.tools.ToolsToast
import com.sup.dev.android.tools.ToolsView
import com.sup.dev.android.views.settings.SettingsField
import com.sup.dev.android.views.views.ViewText
import com.sup.dev.java.tools.ToolsCryptography
import com.sup.dev.java.tools.ToolsText

class SIntroEmailRegistration(
        val addMode:Boolean = false
) : Screen(R.layout.screen_intro_email_registration){

    val vEmail:SettingsField = findViewById(R.id.vEmail)
    val vPass1:SettingsField = findViewById(R.id.vPass1)
    val vPass2:SettingsField = findViewById(R.id.vPass2)
    val vText: ViewText = findViewById(R.id.vText)
    val vCheck: CheckBox = findViewById(R.id.vCheck)
    val vEnter: Button = findViewById(R.id.vEnter)
    val vLogo: View = findViewById(R.id.vLogo)

    var bestHeight = 0
    private val fbAuth by lazy { Firebase.auth }

    init {
        activityRootBackground = ToolsResources.getColorAttr(R.attr.colorPrimary)
        disableNavigation()

        vEmail.setHint(R.string.app_email)
        vPass1.setHint(R.string.app_password)
        vPass2.setHint(R.string.app_password_repeat)
        vEnter.text = ToolsResources.s(R.string.app_registration)

        vEmail.addOnTextChanged { updateEnterEnabled() }
        vPass1.addOnTextChanged { updateEnterEnabled() }
        vPass2.addOnTextChanged { updateEnterEnabled() }


        vText.setText(SGoogleRules.instanceSpan())
        ToolsView.makeLinksClickable(vText)
        vCheck.text = t(API_TRANSLATE.app_i_agree)
        vCheck.setOnCheckedChangeListener { _, b -> updateEnterEnabled() }
        vEnter.setOnClickListener { onEnterNew() }

        if(addMode){
            vText.visibility = View.GONE
            vCheck.visibility = View.GONE
            vEnter.text = t(API_TRANSLATE.app_add)
        } else {
            vLogo.visibility =  View.GONE
        }

        updateEnterEnabled()
    }
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if(height > 3000) return
        if(height > bestHeight) bestHeight = height
        vLogo.visibility = if(height < bestHeight || !addMode) View.GONE else View.VISIBLE
    }

    fun updateEnterEnabled(){
        vEnter.isEnabled = ToolsText.isValidEmailAddress(vEmail.getText()) && vPass1.getText().length >= API.ACCOUNT_PASSOWRD_L_MIN && vPass2.getText() == vPass1.getText() && (addMode || vCheck.isChecked)
    }

    fun onEnterNew() {
        val password = vPass1.getText()
        val email = vEmail.getText()
        val passwordSha512 = ToolsCryptography.getSHA512(password)

        val progressDialog = ToolsView.showProgressDialog()
        fbAuth.createUserWithEmailAndPassword(email, passwordSha512)
            .addOnSuccessListener { authResult ->
                authResult.user!!.sendEmailVerification()
                    .addOnSuccessListener {
                        Navigator.to(SIntroEmailVerify(addMode))
                    }
                    .addOnFailureListener {
                        it.printStackTrace()
                        ToolsToast.show(t(API_TRANSLATE.app_error))
                    }
                    .addOnCompleteListener {
                        progressDialog.hide()
                    }
            }
            .addOnFailureListener {
                it.printStackTrace()
                ToolsToast.show(t(API_TRANSLATE.app_error))
                progressDialog.hide()
            }
    }

    @Deprecated("migrated to onEnterNew, archive")
    fun onEnter() {
        val password = vPass1.getText()
        val email = vEmail.getText()
        val passwordSha512 = ToolsCryptography.getSHA512(password)

        if(addMode){
            ApiRequestsSupporter.executeProgressDialog(RAccountsAddEmail(email, passwordSha512)) { r ->
                ControllerApiLogin.setEmailToken(email, passwordSha512)
                ControllerApiLogin.setLoginType(ControllerApiLogin.LOGIN_EMAIL)
                Navigator.remove(this)
            }
        } else {
            val progressDialog = ToolsView.showProgressDialog()
            ApiRequestsSupporter.execute(RAccountsGetCaptchaSiteKey()) {
                ControllerCaptcha.setSiteKey(it.siteKey)
                ControllerCaptcha.showChallenge(
                    context = this.context,
                    onSuccess = { captchaKey ->
                        ApiRequestsSupporter.execute(
                            RAccountsRegistrationEmail(
                                email, passwordSha512,
                                ControllerApi.getLanguageId(),
                                captchaKey
                            )
                        ) {
                            ControllerApiLogin.setEmailToken(email, passwordSha512)
                            ControllerApiLogin.setLoginType(ControllerApiLogin.LOGIN_EMAIL)
                            ToolsStorage.put(CampfireConstants.CHECK_RULES_ACCEPTED, true)
                            Navigator.set(SIntroConnection())
                        }.onFinish {
                            progressDialog.hide()
                        }
                    },
                    onError = { captchaError ->
                        progressDialog.hide()
                        when (captchaError) {
                            ControllerCaptcha.CaptchaError.NETWORK ->
                                ToolsToast.show(t(API_TRANSLATE.error_network))
                            ControllerCaptcha.CaptchaError.TIMEOUT ->
                                ToolsToast.show(t(API_TRANSLATE.error_unknown))
                            ControllerCaptcha.CaptchaError.CLOSED -> {}
                            ControllerCaptcha.CaptchaError.OTHER ->
                                ToolsToast.show(t(API_TRANSLATE.error_unknown))
                        }
                    }
                )
            }.onError {
                it.printStackTrace()
                progressDialog.hide()
            }
        }

    }

}
