package com.dzen.campfire.screens.hello

import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.dzen.campfire.R
import com.dzen.campfire.api.API_RESOURCES
import com.dzen.campfire.api.API_TRANSLATE
import com.sayzen.campfiresdk.controllers.ControllerSettings
import com.sayzen.campfiresdk.controllers.t
import com.sayzen.campfiresdk.screens.other.rules.SGoogleRules
import com.sup.dev.android.libs.image_loader.ImageLoader
import com.sup.dev.android.tools.ToolsView
import com.sup.dev.android.views.views.ViewText

class Hello_Rulles(
        val screen: SCampfireHello
) {

    val view: View = ToolsView.inflate(screen.vContainer, R.layout.screen_campfire_hello_rules)
    private val vText: ViewText = view.findViewById(R.id.vText)
    private val vNext: TextView = view.findViewById(R.id.vNext)
    private val vCheck: CheckBox = view.findViewById(R.id.vCheck)
    private val vImage: ImageView = view.findViewById(R.id.vImage)

    init {
        vText.setText(SGoogleRules.instanceSpan())
        vNext.setOnClickListener {
            ControllerSettings.rulesIsShowed = true
            screen.toNextScreen()
        }
        vNext.isEnabled = false
        vCheck.text = t(API_TRANSLATE.app_i_agree)
        vNext.text = t(API_TRANSLATE.app_continue)
        vCheck.setOnCheckedChangeListener { _, b -> vNext.isEnabled = b }
        ToolsView.makeLinksClickable(vText)
        ImageLoader.load(API_RESOURCES.IMAGE_BACKGROUND_14).noHolder().into(vImage)
    }


}