package com.tripleb.bbbcounter

import android.content.DialogInterface
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_game.*


class FragmentGame : Fragment() {
    var count_strike: Int = 0
    var count_ball : Int = 0
    var count_out : Int = 0
    var count_hit : Int = 0
    var count_error : Int = 0
    var count_bb : Int = 0
    var count_run : Int = 0
    var inning : Int = 0 // 0,2,4,6은 초공, 1,3,5,7은 말공을 의미
    var maxInning : Int = 0 // 최대 진행된 이닝을 의미
    var game : Boolean = true

    var gameData = Array(20) {IntArray(20) {0} }
    var gameData_total = Array(20) {IntArray(20) {0} }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

//        bundle!!.putInt("id",1234)
//        bundle!!.putParcelableArray("key",gameData as Array<out Parcelable>)
//        arguments = bundle
        return inflater.inflate(R.layout.fragment_game,container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setTeamName()       // 팀명 설정
        startOrEndGame()    // 경기 시작
        countCheck()        // 카운트 체크
        checkRHEB()         // 득점, 안타, 에러, 사사구 체크
        endInning()         // 이닝 종료
        revertInning()      // 되돌리기
        nextInning()        // 되돌리기 반대 버튼
    }

    // 게임 시작
    private fun startOrEndGame() {
        button_startGame.setOnClickListener {
            chronometer.base = SystemClock.elapsedRealtime()
            chronometer.start()

            if(game == true) { // 게임 시작 시
                //배열 초기화
                for (i in 0..8) {
                    for (j in 0..8) {
                        gameData[i][j] = 0
                        gameData_total[i][j] = 0
                    }
                }
                resetScoreBox() // 스코어박스 초기화
                counterReset()  // 카운터 리셋
                rhebReset()     // RHEB 리셋
                inning = 0      // 1회초를 의미
                gameSituation.setText("1회 초")
                (0..3).forEach { gameData_total[0][it] = 0 }
                (0..3).forEach { gameData_total[1][it] = 0 }

                button_endInning.setTextColor(ContextCompat.getColor(requireContext(), R.color.BLACK))
                button_endInning.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.GRAY))

                button_startGame.setText("경기 종료")
                button_startGame.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.RED))

                button_endInning.isEnabled = true
                button_endInning.isClickable = true

                nextInning.isClickable = true
                previousInning.isClickable = true

                game = false
            } else{ // 게임 종료 시
                chronometer.stop()
                scoreBox()
                counterReset()
                rhebReset()
                inning = 0
                maxInning = 0
                gameSituation.setText("게임 종료")

                //경기 종료를 누를 시 이닝 종료 버튼을 못누르게 바꿈
                button_endInning.setTextColor(ContextCompat.getColor(requireContext(), R.color.LIGHT_GRAY))
                button_endInning.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.WHITE))

                button_endInning.isEnabled = false
                button_endInning.isClickable = false

                button_startGame.setText("경기 시작")
                button_startGame.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.GRAY))
                game = true
            }
        }
    }

    // 팀명 설정
    private fun setTeamName(){
        teamName_top.setOnClickListener {
            var builder = AlertDialog.Builder(requireContext())

            builder.setView(layoutInflater.inflate(R.layout.team_name_dialog, null))

            var listener = object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    var alert = dialog as AlertDialog
                    var editText : EditText?= alert.findViewById<EditText>(R.id.team)

                    teamName_top.text = "" + editText?.text
                }
            }
            builder.setPositiveButton("취소", null)
            builder.setNegativeButton("확인", listener)

            builder.show()
        }
        teamName_bottom.setOnClickListener {
            var builder = AlertDialog.Builder(requireContext())

            builder.setView(layoutInflater.inflate(R.layout.team_name_dialog, null))

            var listener = object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    var alert = dialog as AlertDialog
                    var editText : EditText?= alert.findViewById<EditText>(R.id.team)

                    teamName_bottom.text = "" + editText?.text
                }
            }
            builder.setPositiveButton("취소", null)
            builder.setNegativeButton("확인", listener)

            builder.show()
        }
    }
    // 되돌리기
    private fun revertInning() {
        previousInning.setOnClickListener {
            nextInning.isEnabled = true
            nextInning.isClickable = true

            if (inning <= 0) {
                //1회초면 아무것도 안함
            } else {
                inning--
                showCurrentInning()
                count_run = gameData[inning][0]
                count_hit = gameData[inning][1]
                count_error = gameData[inning][2]
                count_bb = gameData[inning][3]

                text_run.setText("" + count_run)
                text_hit.setText("" + count_hit)
                text_error.setText("" + count_error)
                text_bb.setText("" + count_bb)

                if (inning % 2 == 0) { // 되돌아간 현재이닝이 초 라면
                    //이닝 종료를 누르면서 총합에 들어간 전 이닝 카운트를 다시 빼줌
                    gameData_total[0][0] -= count_run
                    gameData_total[0][1] -= count_hit
                    gameData_total[0][2] -= count_error
                    gameData_total[0][3] -= count_bb

                    //RHEB 스코어박스에 원래 숫자를 다시 표시해줌
                    score_top_run.setText("" + gameData_total[0][0])
                    score_top_hit.setText("" + gameData_total[0][1])
                    score_top_error.setText("" + gameData_total[0][2])
                    score_top_bb.setText("" + gameData_total[0][3])
                } else {
                    gameData_total[1][0] -= count_run
                    gameData_total[1][1] -= count_hit
                    gameData_total[1][2] -= count_error
                    gameData_total[1][3] -= count_bb

                    score_bottom_run.setText("" + gameData_total[1][0])
                    score_bottom_hit.setText("" + gameData_total[1][1])
                    score_bottom_error.setText("" + gameData_total[1][2])
                    score_bottom_bb.setText("" + gameData_total[1][3])
                }

                //스코어 박스에 점수도 지움
                when (inning) {
                    0 -> score_top_1.setText("")
                    1 -> score_bottom_1.setText("")
                    2 -> score_top_2.setText("")
                    3 -> score_bottom_2.setText("")
                    4 -> score_top_3.setText("")
                    5 -> score_bottom_3.setText("")
                    6 -> score_top_4.setText("")
                    7 -> score_bottom_4.setText("")
                    8 -> score_top_5.setText("")
                    9 -> score_bottom_5.setText("")
                    10 -> score_top_6.setText("")
                    11 -> score_bottom_6.setText("")
                    12 -> score_top_7.setText("")
                    13 -> score_bottom_7.setText("")
                    14 -> score_top_8.setText("")
                    15 -> score_bottom_8.setText("")
                    16 -> score_top_9.setText("")
                    17 -> score_bottom_9.setText("")

                }
            }
        }
    }

    // 다음 버튼(되돌리기 반대)
    private fun nextInning(){
        nextInning.setOnClickListener {
            if (maxInning <= inning ) {
                //가장 최근까지 진행된 이후는 눌러도 아무 일도 일어나지 않음
            } else{
                scoreBox()
                counterReset()
                rhebReset()
                inning++ // 이닝++
                showCurrentInning() //현재 이닝 표시

                count_run = gameData[inning][0]
                count_hit = gameData[inning][1]
                count_error = gameData[inning][2]
                count_bb = gameData[inning][3]

                text_run.setText("" + count_run)
                text_hit.setText("" + count_hit)
                text_error.setText("" + count_error)
                text_bb.setText("" + count_bb)
            }
        }
    }

    //현재 이닝 보여주기
    private fun showCurrentInning(){
        // 현재 이닝 표시
        if(inning % 2 == 0){
            gameSituation.setText("${inning / 2 + 1}회 초")
        }else
            gameSituation.setText("${inning / 2 + 1}회 말")

    }

    //스코어 박스 리셋
    private fun resetScoreBox() {
        score_top_run.setText("0")
        score_top_hit.setText("0")
        score_top_error.setText("0")
        score_top_bb.setText("0")

        score_bottom_run.setText("0")
        score_bottom_hit.setText("0")
        score_bottom_error.setText("0")
        score_bottom_bb.setText("0")

        score_top_1.setText("")
        score_top_2.setText("")
        score_top_3.setText("")
        score_top_4.setText("")
        score_top_5.setText("")
        score_top_6.setText("")
        score_top_7.setText("")
        score_top_8.setText("")
        score_top_9.setText("")

        score_bottom_1.setText("")
        score_bottom_2.setText("")
        score_bottom_3.setText("")
        score_bottom_4.setText("")
        score_bottom_5.setText("")
        score_bottom_6.setText("")
        score_bottom_7.setText("")
        score_bottom_8.setText("")
        score_bottom_9.setText("")



    }

    // 카운터 관리
    private fun countCheck(){
        checkBall()
        checkStrike()
        checkOut()
    }

    // R, H, E, B 관리
    private fun checkRHEB(){
        checkRun()
        checkHit()
        checkError()
        checkBB()

    }

    // 이닝 종료시 처리
    private fun endInning(){
        button_endInning.setOnClickListener {
            nextInning.isEnabled = true
            nextInning.isClickable = true
            scoreBox()
            counterReset()
            rhebReset()
            inning++ // 이닝++
            maxInning++
            showCurrentInning() //현재 이닝 표시
        }
    }

    // 볼 체크
    private fun checkBall(){
        button_B.setOnClickListener {
            //nextInning.isEnabled = false
            //nextInning.isClickable = false

            if(count_ball == 0){
                count_ball++
                ball_1.setBackgroundResource(R.drawable.circle_green)
            }else if(count_ball == 1){
                count_ball++
                ball_2.setBackgroundResource(R.drawable.circle_green)
            }else if(count_ball == 2){
                count_ball++
                ball_3.setBackgroundResource(R.drawable.circle_green)
            }else{
                count_ball = 0
                ball_1.setBackgroundResource(R.drawable.circle)
                ball_2.setBackgroundResource(R.drawable.circle)
                ball_3.setBackgroundResource(R.drawable.circle)
            }
        }
        ball_1.setOnClickListener {
            //nextInning.isEnabled = false
            //nextInning.isClickable = false

            if(count_ball == 0){
                count_ball++
                ball_1.setBackgroundResource(R.drawable.circle_green)
            }else if(count_ball == 1){
                count_ball++
                ball_2.setBackgroundResource(R.drawable.circle_green)
            }else if(count_ball == 2){
                count_ball++
                ball_3.setBackgroundResource(R.drawable.circle_green)
            }else{
                count_ball = 0
                ball_1.setBackgroundResource(R.drawable.circle)
                ball_2.setBackgroundResource(R.drawable.circle)
                ball_3.setBackgroundResource(R.drawable.circle)
            }
        }
        ball_2.setOnClickListener {
            //nextInning.isEnabled = false
            //nextInning.isClickable = false

            if(count_ball == 0){
                count_ball++
                ball_1.setBackgroundResource(R.drawable.circle_green)
            }else if(count_ball == 1){
                count_ball++
                ball_2.setBackgroundResource(R.drawable.circle_green)
            }else if(count_ball == 2){
                count_ball++
                ball_3.setBackgroundResource(R.drawable.circle_green)
            }else{
                count_ball = 0
                ball_1.setBackgroundResource(R.drawable.circle)
                ball_2.setBackgroundResource(R.drawable.circle)
                ball_3.setBackgroundResource(R.drawable.circle)
            }
        }
        ball_3.setOnClickListener {
            //nextInning.isEnabled = false
            //nextInning.isClickable = false

            if(count_ball == 0){
                count_ball++
                ball_1.setBackgroundResource(R.drawable.circle_green)
            }else if(count_ball == 1){
                count_ball++
                ball_2.setBackgroundResource(R.drawable.circle_green)
            }else if(count_ball == 2){
                count_ball++
                ball_3.setBackgroundResource(R.drawable.circle_green)
            }else{
                count_ball = 0
                ball_1.setBackgroundResource(R.drawable.circle)
                ball_2.setBackgroundResource(R.drawable.circle)
                ball_3.setBackgroundResource(R.drawable.circle)
            }
        }
    }

    // 스트라이크 체크
    private fun checkStrike(){
        //nextInning.isEnabled = false
        //nextInning.isClickable = false

        button_S.setOnClickListener {
            if(count_strike == 0){
                count_strike++
                strike_1.setBackgroundResource(R.drawable.circle_yellow)
            }else if(count_strike == 1){
                count_strike++
                strike_2.setBackgroundResource(R.drawable.circle_yellow)
            }else{
                count_strike = 0
                strike_1.setBackgroundResource(R.drawable.circle)
                strike_2.setBackgroundResource(R.drawable.circle)
            }
        }


        strike_1.setOnClickListener {
            if(count_strike == 0){
                count_strike++
                strike_1.setBackgroundResource(R.drawable.circle_yellow)
            }else if(count_strike == 1){
                count_strike++
                strike_2.setBackgroundResource(R.drawable.circle_yellow)
            }else{
                count_strike = 0
                strike_1.setBackgroundResource(R.drawable.circle)
                strike_2.setBackgroundResource(R.drawable.circle)
            }
        }

        strike_2.setOnClickListener {
            if(count_strike == 0){
                count_strike++
                strike_1.setBackgroundResource(R.drawable.circle_yellow)
            }else if(count_strike == 1){
                count_strike++
                strike_2.setBackgroundResource(R.drawable.circle_yellow)
            }else{
                count_strike = 0
                strike_1.setBackgroundResource(R.drawable.circle)
                strike_2.setBackgroundResource(R.drawable.circle)
            }
        }
    }

    // 아웃 체크
    private fun checkOut(){
        //nextInning.isEnabled = false
        //nextInning.isClickable = false

        button_O.setOnClickListener {
            if(count_out == 0){
                count_out++
                out_1.setBackgroundResource(R.drawable.circle_red)
            }else if(count_out == 1){
                count_out++
                out_2.setBackgroundResource(R.drawable.circle_red)
            }else{
                count_out = 0
                out_1.setBackgroundResource(R.drawable.circle)
                out_2.setBackgroundResource(R.drawable.circle)
            }
        }
        out_1.setOnClickListener {
            if(count_out == 0){
                count_out++
                out_1.setBackgroundResource(R.drawable.circle_red)
            }else if(count_out == 1){
                count_out++
                out_2.setBackgroundResource(R.drawable.circle_red)
            }else{
                count_out = 0
                out_1.setBackgroundResource(R.drawable.circle)
                out_2.setBackgroundResource(R.drawable.circle)
            }
        }
        out_2.setOnClickListener {
            if(count_out == 0){
                count_out++
                out_1.setBackgroundResource(R.drawable.circle_red)
            }else if(count_out == 1){
                count_out++
                out_2.setBackgroundResource(R.drawable.circle_red)
            }else{
                count_out = 0
                out_1.setBackgroundResource(R.drawable.circle)
                out_2.setBackgroundResource(R.drawable.circle)
            }
        }
    }

    // 안타 체크
    private fun checkHit(){
        //nextInning.isEnabled = false
        //nextInning.isClickable = false

        button_H_up.setOnClickListener {
            count_hit++
            text_hit.setText(count_hit.toString())
        }

        button_H_down.setOnClickListener {
            if(count_hit <= 0){
                count_hit = 0
            }else
                count_hit--
            text_hit.setText(count_hit.toString())
        }

    }

    // 에러 체크
    private fun checkError(){
        //nextInning.isEnabled = false
        //nextInning.isClickable = false

        button_E_up.setOnClickListener {
            count_error++
            text_error.setText(count_error.toString())
        }

        button_E_down.setOnClickListener {
            if(count_error <= 0){
                count_error = 0
            }else
                count_error--
            text_error.setText(count_error.toString())
        }

    }

    // 볼넷 체크
    private fun checkBB(){
        //nextInning.isEnabled = false
        //nextInning.isClickable = false

        button_B_up.setOnClickListener {
            count_bb++
            text_bb.setText(count_bb.toString())
        }

        button_B_down.setOnClickListener {
            if(count_bb <= 0){
                count_bb = 0
            }else
                count_bb--
            text_bb.setText(count_bb.toString())
        }

    }

    // 득점 체크
    private fun checkRun() {
        //nextInning.isEnabled = false
        //nextInning.isClickable = false
        button_R_up.setOnClickListener {
            count_run++

            text_run.setText(count_run.toString())
        }

        button_R_down.setOnClickListener {
            if (count_run <= 0) {
                count_run = 0
            } else
                count_run--
            text_run.setText(count_run.toString())
        }

    }

    // 스코어 박스 관리
    private fun scoreBox() {
        if(inning % 2 == 0){ // 초 공격이 끝났음을 의미
            // 안타, 에러 총합을 저장하는 배열
            gameData_total[0][0] += count_run
            gameData_total[0][1] += count_hit
            gameData_total[0][2] += count_error
            gameData_total[0][3] += count_bb
            // 안타, 에러 등 각 이닝 별로 저장하는 배열
            gameData[inning][0] = count_run
            gameData[inning][1] = count_hit
            gameData[inning][2] = count_error
            gameData[inning][3] = count_bb
            // 저장한 gameData를 화면에 출력함
            score_top_run.setText("" + gameData_total[0][0])
            score_top_hit.setText("" + gameData_total[0][1])
            score_top_error.setText("" + gameData_total[0][2])
            score_top_bb.setText("" + gameData_total[0][3])
            // 각 이닝에 득점 표시
            when(inning / 2 + 1){
                1 -> score_top_1.setText(""+count_run)
                2 -> score_top_2.setText(""+count_run)
                3 -> score_top_3.setText(""+count_run)
                4 -> score_top_4.setText(""+count_run)
                5 -> score_top_5.setText(""+count_run)
                6 -> score_top_6.setText(""+count_run)
                7 -> score_top_7.setText(""+count_run)
                8 -> score_top_8.setText(""+count_run)
                9 -> score_top_9.setText(""+count_run)
            }
        }else{
            gameData_total[1][0] += count_run
            gameData_total[1][1] += count_hit
            gameData_total[1][2] += count_error
            gameData_total[1][3] += count_bb
            gameData[inning][0] = count_run
            gameData[inning][1] = count_hit
            gameData[inning][2] = count_error
            gameData[inning][3] = count_bb
            score_bottom_run.setText("" + gameData_total[1][0])
            score_bottom_hit.setText("" + gameData_total[1][1])
            score_bottom_error.setText("" + gameData_total[1][2])
            score_bottom_bb.setText("" + gameData_total[1][3])
            when(inning / 2 + 1){
                1 -> score_bottom_1.setText(""+count_run)
                2 -> score_bottom_2.setText(""+count_run)
                3 -> score_bottom_3.setText(""+count_run)
                4 -> score_bottom_4.setText(""+count_run)
                5 -> score_bottom_5.setText(""+count_run)
                6 -> score_bottom_6.setText(""+count_run)
                7 -> score_bottom_7.setText(""+count_run)
                8 -> score_bottom_8.setText(""+count_run)
                9 -> score_bottom_9.setText(""+count_run)
            }
        }



    }

    // 카운터기 리셋
    private fun counterReset(){
        count_strike = 0
        count_ball = 0
        count_out = 0

        out_1?.setBackgroundResource(R.drawable.circle)
        out_2?.setBackgroundResource(R.drawable.circle)
        strike_1?.setBackgroundResource(R.drawable.circle)
        strike_2?.setBackgroundResource(R.drawable.circle)
        ball_1?.setBackgroundResource(R.drawable.circle)
        ball_2?.setBackgroundResource(R.drawable.circle)
        ball_3?.setBackgroundResource(R.drawable.circle)
    }

    // RHEB 리셋
    private fun rhebReset() {
        text_hit?.setText("0")
        text_bb?.setText("0")
        text_error?.setText("0")
        text_run?.setText("0")
        count_hit = 0
        count_run = 0
        count_error = 0
        count_bb = 0
    }




}