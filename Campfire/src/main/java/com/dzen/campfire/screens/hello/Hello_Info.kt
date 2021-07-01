package com.dzen.campfire.screens.hello

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.dzen.campfire.R
import com.dzen.campfire.api.API_RESOURCES
import com.dzen.campfire.api.API_TRANSLATE
import com.dzen.campfire.api.models.translate.Translate
import com.sayzen.campfiresdk.controllers.t
import com.sup.dev.android.libs.image_loader.ImageLoader
import com.sup.dev.android.tools.ToolsView
import com.sup.dev.android.views.views.ViewButton

class Hello_Info(
        private val image: Long,
        private val title: Translate,
        private val text: Translate,
        val screen: SCampfireHello
) {

    companion object{
        fun instnance_1(screen: SCampfireHello) = Hello_Info(API_RESOURCES.IMAGE_BACKGROUND_13, API_TRANSLATE.hello_1_title, API_TRANSLATE.hello_1_text, screen)
        fun instnance_2(screen: SCampfireHello) = Hello_Info(API_RESOURCES.IMAGE_BACKGROUND_9,  API_TRANSLATE.hello_2_title, API_TRANSLATE.hello_2_text, screen)
        fun instnance_3(screen: SCampfireHello) = Hello_Info(API_RESOURCES.IMAGE_BACKGROUND_3,  API_TRANSLATE.hello_3_title, API_TRANSLATE.hello_3_text, screen)
        fun instnance_4(screen: SCampfireHello) = Hello_Info(API_RESOURCES.IMAGE_BACKGROUND_14, API_TRANSLATE.hello_4_title, API_TRANSLATE.hello_4_text, screen)
    }

    val view: View = ToolsView.inflate(screen.vContainer, R.layout.screen_campfire_hello_info)
    val vImage: ImageView = view.findViewById(R.id.vImage)
    val vTitle: TextView = view.findViewById(R.id.vTitle)
    val vText: TextView = view.findViewById(R.id.vText)
    val vNext: ViewButton = view.findViewById(R.id.vNext)

    init {

        ImageLoader.load(image).noHolder().into(vImage)
        vTitle.text = t(title)
        vText.text = t(text)
        vNext.text = t(API_TRANSLATE.app_continue)

        view.setOnClickListener { screen.toNextScreen() }
        vNext.setOnClickListener { screen.toNextScreen() }
    }


}