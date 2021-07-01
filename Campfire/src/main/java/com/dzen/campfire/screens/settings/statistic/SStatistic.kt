package com.dzen.campfire.screens.settings.statistic

import com.dzen.campfire.api.API_TRANSLATE
import com.dzen.campfire.api.requests.project.RProjectStatistic
import com.sayzen.campfiresdk.controllers.t
import com.sayzen.campfiresdk.support.ApiRequestsSupporter
import com.sup.dev.android.libs.screens.navigator.NavigationAction
import com.sup.dev.android.tools.ToolsToast
import com.sup.dev.android.views.screens.SRecycler
import com.sup.dev.android.views.support.adapters.recycler_view.RecyclerCardAdapter
import java.lang.IllegalArgumentException

class SStatistic private constructor(
        val r: RProjectStatistic.Response
) : SRecycler() {

    companion object {

        fun instance(action: NavigationAction) {

            ApiRequestsSupporter.executeInterstitial(action, RProjectStatistic()){
                if(it.accounts.size < 9 || it.posts.size < 9 || it.comments.size < 9 || it.messages.size < 9 || it.enters.size < 9 || it.rates.size < 9){
                    ToolsToast.show(t(API_TRANSLATE.error_network))
                    return@executeInterstitial null
                }

                SStatistic(it)
            }

        }
    }

    private val adapter = RecyclerCardAdapter()

    init {
        disableShadows()
        disableNavigation()
        setTitle(t(API_TRANSLATE.about_statistic))

        vRecycler.adapter = adapter

        adapter.add(CardStatistic(t(API_TRANSLATE.statistic_accounts), r.accounts))
        adapter.add(CardStatistic(t(API_TRANSLATE.statistic_posts), r.posts))
        adapter.add(CardStatistic(t(API_TRANSLATE.statistic_comments), r.comments))
        adapter.add(CardStatistic(t(API_TRANSLATE.statistic_messages), r.messages))
        adapter.add(CardStatistic(t(API_TRANSLATE.statistic_enters), r.enters))
        adapter.add(CardStatistic(t(API_TRANSLATE.statistic_rates), r.rates))

    }

}
