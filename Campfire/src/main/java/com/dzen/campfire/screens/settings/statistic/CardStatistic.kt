package com.dzen.campfire.screens.settings.statistic

import android.view.View
import android.widget.TextView
import com.dzen.campfire.R
import com.dzen.campfire.api.API_TRANSLATE
import com.sayzen.campfiresdk.controllers.t
import com.sup.dev.android.tools.ToolsResources
import com.sup.dev.android.views.cards.Card
import com.sup.dev.android.views.views.ViewIcon

class CardStatistic(
        val title: String,
        val array: Array<Long>
) : Card(R.layout.screen_statistic_card) {

    override fun bindView(view: View) {
        super.bindView(view)

        val vTitle: TextView = view.findViewById(R.id.vTitle)
        val vTitle_7: TextView = view.findViewById(R.id.vTitle_7)
        val vTitle_30: TextView = view.findViewById(R.id.vTitle_30)
        val vTitle_90: TextView = view.findViewById(R.id.vTitle_90)
        val vTitle_180: TextView = view.findViewById(R.id.vTitle_180)
        val vNow: TextView = view.findViewById(R.id.vNow)

        vTitle_7.setText(t(API_TRANSLATE.statistic_7_day))
        vTitle_30.setText(t(API_TRANSLATE.statistic_30_day))
        vTitle_90.setText(t(API_TRANSLATE.statistic_90_day))
        vTitle_180.setText(t(API_TRANSLATE.statistic_180_day))

        val v7Count: TextView = view.findViewById(R.id.v7Count)
        val v7CountOld: TextView = view.findViewById(R.id.v7CountOld)
        val v7Percent: TextView = view.findViewById(R.id.v7Percent)
        val v7Icon: ViewIcon = view.findViewById(R.id.v7Icon)

        val v30Count: TextView = view.findViewById(R.id.v30Count)
        val v30CountOld: TextView = view.findViewById(R.id.v30CountOld)
        val v30Percent: TextView = view.findViewById(R.id.v30Percent)
        val v30Icon: ViewIcon = view.findViewById(R.id.v30Icon)

        val v90Count: TextView = view.findViewById(R.id.v90Count)
        val v90CountOld: TextView = view.findViewById(R.id.v90CountOld)
        val v90Percent: TextView = view.findViewById(R.id.v90Percent)
        val v90Icon: ViewIcon = view.findViewById(R.id.v90Icon)

        val v180Count: TextView = view.findViewById(R.id.v180Count)
        val v180CountOld: TextView = view.findViewById(R.id.v180CountOld)
        val v180Percent: TextView = view.findViewById(R.id.v180Percent)
        val v180Icon: ViewIcon = view.findViewById(R.id.v180Icon)

        vTitle.text = title
        vNow.text = "${array[0]}"

        v7Count.text = "${array[1]}"
        v7CountOld.text = "${array[2]}"

        v30Count.text = "${array[3]}"
        v30CountOld.text = "${array[4]}"

        v90Count.text = "${array[5]}"
        v90CountOld.text = "${array[6]}"

        v180Count.text = "${array[7]}"
        v180CountOld.text = "${array[8]}"

        setValues(array[1], array[2], v7Percent, v7Icon)
        setValues(array[3], array[4], v30Percent, v30Icon)
        setValues(array[5], array[6], v90Percent, v90Icon)
        setValues(array[7], array[8], v180Percent, v180Icon)
    }

    private fun setValues(last:Long, old:Long, vPercent:TextView, vIcon:ViewIcon){
        when {
            last > old -> {
                vIcon.setImageResource(R.drawable.ic_trending_up_white_18dp)
                vIcon.setFilter(ToolsResources.getColor(R.color.green_700))
                vPercent.text = "${(last/(old/100f)).toInt() - 100}%"
                vPercent.setTextColor(ToolsResources.getColor(R.color.green_700))
            }
            last < old -> {
                vIcon.setImageResource(R.drawable.ic_trending_down_white_18dp)
                vIcon.setFilter(ToolsResources.getColor(R.color.red_700))
                vPercent.text = "${(last/(old/100f)).toInt() - 100}%"
                vPercent.setTextColor(ToolsResources.getColor(R.color.red_700))
            }
            else -> {
                vIcon.setImageResource(R.drawable.ic_trending_flat_white_24dp)
                vIcon.setFilter(ToolsResources.getColor(R.color.grey_400))
                vPercent.text = "0%"
                vPercent.setTextColor(ToolsResources.getColor(R.color.grey_400))
            }
        }
    }

}