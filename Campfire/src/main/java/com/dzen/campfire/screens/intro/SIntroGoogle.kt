package com.dzen.campfire.screens.intro

import com.dzen.campfire.R
import com.sayzen.campfiresdk.controllers.ControllerApiLogin
import com.sayzen.devsupandroidgoogle.ControllerGoogleAuth
import com.sup.dev.android.libs.screens.Screen
import com.sup.dev.android.libs.screens.navigator.Navigator
import com.sup.dev.android.tools.ToolsResources
import com.sup.dev.android.tools.ToolsToast

class SIntroGoogle : Screen(R.layout.screen_intro_google){

    init {
        activityRootBackground = ToolsResources.getColorAttr(R.attr.colorPrimary)
        disableNavigation()
        isBackStackAllowed = false
    }

    override fun onFirstShow() {
        super.onFirstShow()
        ControllerGoogleAuth.getToken {
            if(it == null){
                ToolsToast.show(R.string.connection_error)
                Navigator.set(SIntroAccount())
            }else {
                ControllerGoogleAuth.tokenPostExecutor.invoke(it) { token ->
                    if(token == null){
                        ToolsToast.show(R.string.connection_error)
                        Navigator.set(SIntroAccount())
                    }else {
                        ControllerApiLogin.setLoginType(ControllerApiLogin.LOGIN_GOOGLE)
                        Navigator.set(SIntroConnection())
                    }
                }
            }
        }
    }
}
