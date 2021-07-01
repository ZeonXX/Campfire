package com.dzen.campfire.screens.intro

import com.dzen.campfire.R
import com.dzen.campfire.app.App
import com.sayzen.campfiresdk.controllers.ControllerApi
import com.sayzen.campfiresdk.controllers.ControllerApiLogin
import com.sayzen.campfiresdk.controllers.api
import com.sup.dev.android.libs.screens.Screen
import com.sup.dev.android.libs.screens.navigator.Navigator
import com.sup.dev.android.tools.ToolsResources

class SIntro : Screen(R.layout.screen_intro) {

    init {
        activityRootBackground = ToolsResources.getColorAttr(R.attr.colorPrimary)
        disableNavigation()
        isBackStackAllowed = false
    }

    override fun onFirstShow() {
        super.onFirstShow()

        if(ControllerApiLogin.isLoginNone()){
            Navigator.set(SIntroAccount())
        }else{
            Navigator.set(SIntroConnection())
        }
    }

    override fun onBackPressed(): Boolean {
        App.activity().finish()
        return true
    }



}