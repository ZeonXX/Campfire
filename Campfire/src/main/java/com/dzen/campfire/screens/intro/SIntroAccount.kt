package com.dzen.campfire.screens.intro

import android.widget.TextView
import com.dzen.campfire.R
import com.dzen.campfire.app.App
import com.sayzen.campfiresdk.controllers.ControllerApi
import com.sayzen.campfiresdk.controllers.ControllerApiLogin
import com.sup.dev.android.libs.screens.Screen
import com.sup.dev.android.libs.screens.navigator.Navigator
import com.sup.dev.android.tools.ToolsResources

class SIntroAccount : Screen(R.layout.screen_intro_account){

    private val vMessage: TextView = findViewById(R.id.vMessage)
    private val vGoogle: TextView = findViewById(R.id.vGoogle)
    private val vEmail: TextView = findViewById(R.id.vEmail)

    init {
        activityRootBackground = ToolsResources.getColorAttr(R.attr.colorPrimary)
        disableNavigation()

        vMessage.text = ToolsResources.s(R.string.google_message)
        vGoogle.text = ToolsResources.s(R.string.app_google_account)
        vEmail.text = ToolsResources.s(R.string.app_email_account)

        ControllerApiLogin.setLoginType(ControllerApiLogin.LOGIN_NONE)

        vGoogle.setOnClickListener {
            Navigator.to(SIntroGoogle(), Navigator.Animation.NONE)
        }
        vEmail.setOnClickListener {
            Navigator.to(SIntroEmail(), Navigator.Animation.ALPHA)
        }

    }

    override fun onBackPressed(): Boolean {
        App.activity().finish()
        return true
    }

}