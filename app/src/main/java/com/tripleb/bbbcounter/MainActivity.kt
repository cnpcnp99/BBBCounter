package com.tripleb.bbbcounter

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_game.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var fragmentGame = FragmentGame()
        var fragmentRecord = FragmentRecord()

        // 제일 먼저 띄워줄 뷰를 설정 commit()까지 해줘야 함
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_layout, fragmentGame)
            .commitAllowingStateLoss()

        bottomNavigationView.setOnNavigationItemSelectedListener { view->
            when(view.itemId){
                R.id.tab_game -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.main_layout, fragmentGame)
                        .commitAllowingStateLoss()
                }
                R.id.tab_record ->
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.main_layout, fragmentRecord)
                        .commitAllowingStateLoss()

            }
            false
        }
    }
}
