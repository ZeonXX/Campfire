package com.dzen.campfire.screens.intro

import android.view.View
import android.widget.Button
import com.dzen.campfire.R
import com.google.firebase.auth.FirebaseAuth
import com.sayzen.campfiresdk.controllers.ControllerApi
import com.sayzen.campfiresdk.controllers.ControllerApiLogin
import com.sayzen.campfiresdk.controllers.ControllerTranslate
import com.sup.dev.android.libs.screens.Screen
import com.sup.dev.android.libs.screens.navigator.Navigator
import com.sup.dev.android.tools.ToolsResources
import com.sup.dev.android.tools.ToolsToast
import com.sup.dev.android.tools.ToolsView
import com.sup.dev.android.views.settings.SettingsField
import com.sup.dev.java.tools.ToolsCryptography

class SIntroEmail : Screen(R.layout.screen_intro_email){

    val vEmail: SettingsField = findViewById(R.id.vEmail)
    val vPass: SettingsField = findViewById(R.id.vPass)
    val vEnter: Button = findViewById(R.id.vEnter)
    val vRegistration: Button = findViewById(R.id.vRegistration)
    val vLogo: View = findViewById(R.id.vLogo)

    var bestHeight = 0

    init {
        activityRootBackground = ToolsResources.getColorAttr(R.attr.colorPrimary)
        disableNavigation()

        vEmail.setHint(R.string.app_email)
        vPass.setHint(R.string.app_password)
        vEnter.text = ToolsResources.s(R.string.app_login)
        vRegistration.text = ToolsResources.s(R.string.app_registration)

        vEmail.addOnTextChanged { updateEnterEnabled() }
        vPass.addOnTextChanged { updateEnterEnabled() }

        vEnter.setOnClickListener { enter() }
        vRegistration.setOnClickListener {
            ControllerTranslate.loadLanguage(ControllerApi.getLanguageId(),
                    { Navigator.to(SIntroEmailRegistration(), Navigator.Animation.ALPHA) },
                    { ToolsToast.show(R.string.connection_error) })
        }

        updateEnterEnabled()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if(height > 3000) return
        if(height > bestHeight) bestHeight = height
        vLogo.visibility = if(height < bestHeight) View.GONE else View.VISIBLE
    }

    fun updateEnterEnabled(){
        vEnter.isEnabled = vEmail.getText().length >= 3 && vPass.getText().length >= 6
    }

    val fbAuth by lazy { FirebaseAuth.getInstance() }

    fun enter(){
        val password = vPass.getText()
        val email = vEmail.getText()
        val passwordSha512 = ToolsCryptography.getSHA512(password)

        val progress = ToolsView.showProgressDialog()
        fbAuth.signInWithEmailAndPassword(email, passwordSha512)
            .addOnSuccessListener { authResult ->
                if (!authResult.user!!.isEmailVerified) {
                    Navigator.replace(SIntroEmailVerify(false))
                    return@addOnSuccessListener
                }
                authResult.user!!.getIdToken(false)
                    .addOnSuccessListener {
                        ControllerApiLogin.setEmailToken(it.token!!)
                        ControllerApiLogin.setLoginType(ControllerApiLogin.LOGIN_EMAIL)
                        Navigator.set(SIntroConnection())
                    }
                    .addOnFailureListener {
                        ToolsToast.show(R.string.app_error)
                    }
                    .addOnCompleteListener {
                        progress.hide()
                    }
            }
            .addOnFailureListener {
                ToolsToast.show(R.string.app_error)
                progress.hide()
            }
    }

}
