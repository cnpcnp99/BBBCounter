package com.tripleb.bbbcounter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(){
    var fragmentGame = FragmentGame()
    var fragmentRecord = FragmentRecord()
    var fragmentInformation = FragmentInformation()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 제일 먼저 띄워줄 뷰를 설정 commit()까지 해줘야 함
        supportFragmentManager
            .beginTransaction()
            .add(R.id.main_layout, fragmentRecord)
            .commitAllowingStateLoss()
        supportFragmentManager
            .beginTransaction()
            .add(R.id.main_layout, fragmentInformation)
            .commitAllowingStateLoss()
        supportFragmentManager
            .beginTransaction()
            .hide(fragmentRecord)
            .commitAllowingStateLoss()
        supportFragmentManager
            .beginTransaction()
            .hide(fragmentInformation)
            .commitAllowingStateLoss()
        supportFragmentManager
            .beginTransaction()
            .add(R.id.main_layout, fragmentGame)
            .commitAllowingStateLoss()

        bottomNavigationView.setOnNavigationItemSelectedListener { view->
            when(view.itemId){
                R.id.tab_game -> {
                    supportFragmentManager
                        .beginTransaction()
                        .hide(fragmentRecord).commitAllowingStateLoss()
                    supportFragmentManager
                        .beginTransaction()
                        .hide(fragmentInformation).commitAllowingStateLoss()
                    supportFragmentManager
                        .beginTransaction()
                        .show(fragmentGame).commitAllowingStateLoss()
                }
                R.id.tab_record -> {
                    //var str = Intent().getStringExtra("key")
                    //Log.d("abcd","$str")
                    var a = intent.getStringExtra("key")
                    Log.d("MainActivity","$a")

                    var bundle = Bundle()
                    bundle.putInt("inning",fragmentGame.inning)
                    bundle.putIntArray("gameData0", fragmentGame.gameData[0])
                    bundle.putIntArray("gameData1", fragmentGame.gameData[1])
                    bundle.putIntArray("gameData2", fragmentGame.gameData[2])
                    bundle.putIntArray("gameData3", fragmentGame.gameData[3])
                    bundle.putIntArray("gameData4", fragmentGame.gameData[4])
                    bundle.putIntArray("gameData5", fragmentGame.gameData[5])
                    bundle.putIntArray("gameData6", fragmentGame.gameData[6])
                    bundle.putIntArray("gameData7", fragmentGame.gameData[7])
                    bundle.putIntArray("gameData8", fragmentGame.gameData[8])
                    bundle.putIntArray("gameData9", fragmentGame.gameData[9])
                    bundle.putIntArray("gameData10", fragmentGame.gameData[10])
                    bundle.putIntArray("gameData11", fragmentGame.gameData[11])
                    bundle.putIntArray("gameData12", fragmentGame.gameData[12])
                    bundle.putIntArray("gameData13", fragmentGame.gameData[13])
                    bundle.putIntArray("gameData14", fragmentGame.gameData[14])
                    bundle.putIntArray("gameData15", fragmentGame.gameData[15])
                    bundle.putIntArray("gameData16", fragmentGame.gameData[16])
                    bundle.putIntArray("gameData17", fragmentGame.gameData[17])
                    fragmentRecord.arguments = bundle


                    supportFragmentManager
                        .beginTransaction()
                        .hide(fragmentGame).commitAllowingStateLoss()
                    supportFragmentManager
                        .beginTransaction()
                        .hide(fragmentInformation).commitAllowingStateLoss()
                    supportFragmentManager
                        .beginTransaction()
                        .show(fragmentRecord).commitAllowingStateLoss()
                }
                R.id.tab_information -> {
                    supportFragmentManager
                        .beginTransaction()
                        .hide(fragmentGame).commitAllowingStateLoss()
                    supportFragmentManager
                        .beginTransaction()
                        .hide(fragmentRecord).commitAllowingStateLoss()
                    supportFragmentManager
                        .beginTransaction()
                        .show(fragmentInformation).commitAllowingStateLoss()
                }
            }
            false
        }

    }

}
