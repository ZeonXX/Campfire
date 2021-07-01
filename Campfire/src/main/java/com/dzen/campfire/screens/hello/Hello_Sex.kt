package com.dzen.campfire.screens.hello

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.dzen.campfire.R
import com.dzen.campfire.api.API
import com.dzen.campfire.api.API_RESOURCES
import com.dzen.campfire.api.API_TRANSLATE
import com.sayzen.campfiresdk.controllers.ControllerApi
import com.sayzen.campfiresdk.controllers.ControllerCampfireSDK
import com.sayzen.campfiresdk.controllers.t
import com.sup.dev.android.tools.ToolsBitmap
import com.sup.dev.android.libs.image_loader.ImageLoader
import com.sup.dev.android.tools.ToolsResources
import com.sup.dev.android.tools.ToolsView
import com.sup.dev.android.views.support.watchers.TextWatcherChanged
import com.sup.dev.java.tools.ToolsText

class Hello_Sex(
        val screen: SCampfireHello,
        val demoMode: Boolean
) {

    val view: View = ToolsView.inflate(screen.vContainer, R.layout.screen_campfire_hello_sex)
    val vHe: View = view.findViewById(R.id.vHe)
    val vShe: View = view.findViewById(R.id.vShe)
    val vText_he: Button = view.findViewById(R.id.vText_he)
    val vText_she: Button = view.findViewById(R.id.vText_she)
    val vLogin: EditText = view.findViewById(R.id.vLogin)
    val vNext: Button = view.findViewById(R.id.vNext)
    val vImage_he: ImageView = view.findViewById(R.id.vImage_he)
    val vImage_she: ImageView = view.findViewById(R.id.vImage_she)
    val vText_1: TextView = view.findViewById(R.id.vText_1)

    var isHe = false
    var isShe = false

    init {
        ImageLoader.load(API_RESOURCES.CAMPFIRE_IMAGE_3).into(vImage_he)
        ImageLoader.load(API_RESOURCES.CAMPFIRE_IMAGE_2).intoBitmap { vImage_she.setImageBitmap(ToolsBitmap.mirror(it!!)) }

        vNext.text = t(API_TRANSLATE.app_continue)
        vLogin.hint = t(API_TRANSLATE.app_name_s)

        vText_he.text = " " + t(API_TRANSLATE.he)
        vText_she.text = t(API_TRANSLATE.she)
        vText_1.text = t(API_TRANSLATE.into_hello_sex)
        vText_he.setOnClickListener {
            ToolsView.hideKeyboard(vLogin)
            isHe = true
            isShe = false
            vText_he.setTextColor(ToolsResources.getColor(R.color.green_700))
            vText_she.setTextColor(ToolsResources.getColor(R.color.focus_dark))
            updateFinishEnabled()
        }
        vText_she.setOnClickListener {
            ToolsView.hideKeyboard(vLogin)
            isHe = false
            isShe = true
            vText_he.setTextColor(ToolsResources.getColor(R.color.focus_dark))
            vText_she.setTextColor(ToolsResources.getColor(R.color.green_700))
            updateFinishEnabled()
        }

        vHe.setOnClickListener { vText_he.performClick() }
        vShe.setOnClickListener { vText_she.performClick() }
        vNext.setOnClickListener { send() }

        vLogin.addTextChangedListener(TextWatcherChanged {
            updateFinishEnabled()
        })
        updateFinishEnabled()
    }

    private fun updateFinishEnabled() {
        val name = vLogin.text.toString()
        if (!ToolsText.checkStringChars(name, API.ACCOUNT_LOGIN_CHARS)) {
            vLogin.error = t(API_TRANSLATE.profile_change_name_error)
            vNext.isEnabled = false
        } else if (name.length < API.ACCOUNT_NAME_L_MIN || name.length > API.ACCOUNT_NAME_L_MAX) {
            vLogin.error = null
            vNext.isEnabled = false
        } else {
            vLogin.error = null
            vNext.isEnabled = isHe || isShe
        }
    }

    private fun send() {
        if (demoMode) {
            screen.toNextScreen()
        } else {
            val name = vLogin.text.toString()
            ControllerCampfireSDK.changeLoginNow(name, false) {
                ControllerCampfireSDK.setSex(if (isHe) 0 else 1) {
                    ControllerApi.account.setName(name)
                    screen.toNextScreen()
                }
            }
        }
    }


}