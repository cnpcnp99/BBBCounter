package com.tripleb.bbbcounter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_record.*

class FragmentRecord : Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_record,container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    refresh.setOnClickListener {
        if(arguments !=null) {
            var inning = arguments?.getInt("inning")
            var gameData_0 = arguments?.getIntArray("gameData0")
            var gameData_1 = arguments?.getIntArray("gameData1")
            var gameData_2 = arguments?.getIntArray("gameData2")
            var gameData_3 = arguments?.getIntArray("gameData3")
            var gameData_4 = arguments?.getIntArray("gameData4")
            var gameData_5 = arguments?.getIntArray("gameData5")
            var gameData_6 = arguments?.getIntArray("gameData6")
            var gameData_7 = arguments?.getIntArray("gameData7")
            var gameData_8 = arguments?.getIntArray("gameData8")
            var gameData_9 = arguments?.getIntArray("gameData9")
            var gameData_10 = arguments?.getIntArray("gameData10")
            var gameData_11 = arguments?.getIntArray("gameData11")
            var gameData_12 = arguments?.getIntArray("gameData12")
            var gameData_13 = arguments?.getIntArray("gameData13")
            var gameData_14 = arguments?.getIntArray("gameData14")
            var gameData_15 = arguments?.getIntArray("gameData15")
            var gameData_16 = arguments?.getIntArray("gameData16")
            var gameData_17 = arguments?.getIntArray("gameData17")

            var gameDataArray = arrayOf(gameData_0, gameData_1, gameData_2, gameData_3, gameData_4, gameData_5
                                        ,gameData_6, gameData_7, gameData_8, gameData_9, gameData_10, gameData_11
                                        ,gameData_12, gameData_13, gameData_14, gameData_15, gameData_16, gameData_17)

            var contentArray = arrayOf(content_top_1, content_bottom_1,content_top_2, content_bottom_2
                                        ,content_top_3, content_bottom_3,content_top_4, content_bottom_4
                                        ,content_top_5, content_bottom_5,content_top_6, content_bottom_6
                                        ,content_top_7, content_bottom_7,content_top_8, content_bottom_8
                                        ,content_top_9, content_bottom_9)
            for(i in 0 until contentArray.size){
                contentArray[i].text =""
            }

            for(i in 0 until inning!!){
                contentArray[i].text = "${gameDataArray[i]?.get(0)}            ${gameDataArray[i]?.get(1)}      " +
                        "      ${gameDataArray[i]?.get(2)}            ${gameDataArray[i]?.get(3)}"
            }
            contentArray[inning].text = "현재 진행 중"

        }
    }


    }
}