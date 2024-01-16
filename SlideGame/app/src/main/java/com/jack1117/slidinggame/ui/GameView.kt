package com.jack1117.slidinggame.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import com.jack1117.slidinggame.R
import com.jack1117.slidinggame.SettingsActivity
import com.jack1117.slidinggame.rule.GameBoard
import com.jack1117.slidinggame.rule.Player
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class GameView(c: Context) : AppCompatImageView(c), TickListener {
    //player1 Scooby-Doo player2 Husky
    private val p = Paint()
    private val p2 = Paint()
    private val buttons: ArrayList<Btn> = ArrayList()
    private val tokens: ArrayList<Token> = ArrayList()
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private val systemHandler: Handler = Handler(Looper.getMainLooper())

    private var sidemargin = 0f     //左右寬度
    private var verticalmargin = 0f //上下寬度
    private var gridlength = 0f     //grid長度
    private var mathdone = false    //是否設定介面
    private var w = 0f
    private var h = 0f
    private var engine = GameBoard(resources)
    private var timer = Timer(Looper.getMainLooper())
    private var player1woncount = 0
    private var player2woncount = 0
    private lateinit var mode: String
    private var bgm: MediaPlayer? = null
    private var areTouchEventsEnabled = true

    init {
        p2.textSize = 50f
        p2.color = Color.BLACK
        //set background
        setImageResource(R.drawable.background2)
        scaleType = ScaleType.FIT_XY
        //set bgm
        bgm = MediaPlayer.create(context, R.raw.relaxing)
        bgm!!.isLooping = true
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if(!mathdone){
            //計算sidemargin, verticalmargin, gridlength
            p.strokeWidth = width * 0.015f //線條粗度
            sidemargin = width * 0.2f
            verticalmargin = (height - width + 2 * sidemargin) / 2
            gridlength = (width - 2 * sidemargin) / 5
            w = width.toFloat()
            h = height.toFloat()
            _MakeButtons()
            mathdone = true
            timer._Register(this)
            setEnableTouchEvents(true)
        }
        //繪製所有格線
        _DrawGrid(canvas)
        //繪製所有按鈕
        for(button in buttons){
            button._DrawBtn(canvas)
        }
        //檢查Token是否出框
        for(token in tokens){
            if(!token._TokenIsVisible(h)){
                //改變速度
                token._ChangeVelocity(0f, 0f)
                //timer unregister, remove
                timer._UnRegister(token)
                tokens.remove(token)
                //concurrent modification
                break
            }
        }
        //繪製Token
        for(token in tokens){
            token._DrawToken(canvas)
        }
        //寫出勝場數
        canvas.drawText(resources.getString(R.string.Winner_Scooby_Doo) + player1woncount, 50f, 100f, p2)
        canvas.drawText(resources.getString(R.string.Winner_Husky) + player2woncount, 50f, 150f, p2)

        //當token靜止時判斷獲勝者，並且決定是否再開一局
        if(!Token._TokenIsMoving()){
            val winner = engine._CheckWinner()
            if(winner != Player.BLANK){
                //pause the timer
                timer.setPause()
                setEnableTouchEvents(false)
                Toast.makeText(context, resources.getString(R.string.Game_Over), Toast.LENGTH_SHORT).show()
                executor.execute{
                    Thread.sleep(1500)
                    systemHandler.post{
                        //alert dialog
                        val ab = AlertDialog.Builder(context)
                        ab.setTitle(resources.getString(R.string.Game_Over))
                        when (winner) {
                            Player.TIE -> {
                                ab.setMessage(resources.getString(R.string.Tie))
                            }
                            Player.X -> {
                                ab.setMessage(resources.getString(R.string.Scooby_Doo_Play_Again))
                            }
                            else -> {
                                ab.setMessage(resources.getString(R.string.Husky_Play_Again))
                            }
                        }
                        ab.setCancelable(false)
                        ab.setPositiveButton(resources.getString(R.string.Play_Again)) {_, _ ->
                            _RestartGame()
                        }
                        ab.setNegativeButton(resources.getString(R.string.No_Play_Again)) {_, _ ->
                            (context as Activity).finish()
                        }
                        ab.create().show()

                        if(winner == Player.X){
                            player1woncount++
                        } else if(winner == Player.O){
                            player2woncount++
                        }
                    }
                }
            }
            //AI下棋時間點；為OnePlayer ,current player is AI and winner is BLANK
            else if(mode == resources.getString(R.string.Choose_One_Player) && Token.player %2 != 0){
                val aiChoise = engine._AIMove()
                val button = buttons[aiChoise]
                engine._SubmitMove(button.getBtnTag())
                //0~4 = '1' ~ '5', 5~9 = 'A' ~ 'E'
                val token = if(aiChoise < 5){
                    Token(resources, gridlength.toInt(), button.getX(), button.getY(), ('A'.code - 1).toChar(), button.getBtnTag())
                } else{
                    Token(resources, gridlength.toInt(), button.getX(), button.getY(), button.getBtnTag(), '0')
                }
                tokens.add(token)
                timer._Register(token)
                //移動token以及周圍tokens
                val neighbors: ArrayList<Token> = ArrayList()
                neighbors.add(token)
                if(button._InColumn()){
                    _MoveVerticalNeighbors(button, neighbors)
                } else{
                    _MoveHorizontalNeighbors(button, neighbors)
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        //如果有token正在移動，則不能按按鈕
        if(Token._TokenIsMoving()){
            for(button in buttons){
                button._BtnUnpressed()
            }
            return true
        }
        if(event!!.action == MotionEvent.ACTION_DOWN){
            var buttontouching = false
            //檢查按鈕是否被按下
            for(button in buttons){
                if(button._Contains(event.x, event.y)){
                    engine._SubmitMove(button.getBtnTag())
                    buttontouching = true
                    button._BtnPressed()
                    //製作出新的Token
                    val token: Token = if(button._InColumn()) {
                        Token(resources, gridlength.toInt(), button.getX(), button.getY(), ('A'.code - 1).toChar(), button.getBtnTag())
                    } else{
                        Token(resources, gridlength.toInt(), button.getX(), button.getY(), button.getBtnTag(), '0')
                    }
                    tokens.add(token)
                    timer._Register(token)
                    //移動token以及周圍tokens
                    val neighbors: ArrayList<Token> = ArrayList()
                    neighbors.add(token)
                    if(button._InColumn()){
                        _MoveVerticalNeighbors(button, neighbors)
                    } else{
                        _MoveHorizontalNeighbors(button, neighbors)
                    }
                    break
                }
            }
            if(!buttontouching){
                Toast.makeText(context, resources.getString(R.string.btn_notification), Toast.LENGTH_SHORT).show()
            }
        }
        invalidate()
        return true
    }

    //設定畫面可點擊
    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        return if(areTouchEventsEnabled) {
            super.dispatchTouchEvent(event)
        } else{
            true
        }
    }

    private fun _MakeButtons(){
        //製作所有按鈕
        val charArray = arrayOf('1', '2', '3', '4', '5')
        val charArray2 = arrayOf('A', 'B', 'C', 'D', 'E')
        for(i in 0..4){
            buttons.add(Btn(resources, charArray[i], gridlength.toInt(), sidemargin + gridlength * i, verticalmargin - gridlength))
        }
        for(i in 0..4){
            buttons.add(Btn(resources, charArray2[i], gridlength.toInt(), sidemargin - gridlength, verticalmargin + gridlength * i))
        }
    }

    private fun _DrawGrid(c: Canvas){
        //繪製所有格線
        for(i in 0..5) { //水平線
            c.drawLine(sidemargin, verticalmargin + gridlength * i, w - sidemargin, verticalmargin + gridlength * i, p)
        }
        for(i in 0..5){ //垂直線
            c.drawLine(sidemargin + gridlength  * i, verticalmargin, sidemargin + gridlength * i, h - verticalmargin, p)
        }
    }

    private fun _MoveVerticalNeighbors(button: Btn, neighborlist: ArrayList<Token>){
        val verticalletters = arrayOf('A', 'B', 'C', 'D', 'E')
        for(i in verticalletters.indices){
            //所有遇到的neighbor
            val dog_token: Token? = _FindToken(verticalletters[i], button.getBtnTag())
            if (dog_token != null) {
                neighborlist.add(dog_token)
            } else{
                break
            }
        }
        for(token in neighborlist){
            token.setDestination(0f, gridlength)
            println(SettingsActivity._GameVelocity(context))
            token._ChangeVelocity(0f, SettingsActivity._GameVelocity(context).toFloat())
            token.row = token.row + 1
        }
    }

    private fun _MoveHorizontalNeighbors(button: Btn, neighborlist: ArrayList<Token>){
        val columnletters = arrayOf('1', '2', '3', '4', '5')
        for(i in columnletters.indices){
            val dog_token: Token? = _FindToken(button.getBtnTag(), columnletters[i])
            if (dog_token != null) {
                neighborlist.add(dog_token)
            } else{
                break
            }
        }
        for(token in neighborlist){
            token.setDestination(gridlength, 0f)
            token._ChangeVelocity(SettingsActivity._GameVelocity(context).toFloat(), 0f)
            token.column = token.column + 1
        }
    }

    private fun _FindToken(row: Char, column: Char) : Token? {
        for(token in tokens){
            if(token.row == row && token.column == column){
                return token
            }
        }
        return null
    }

    private fun _RestartGame(){
        timer.setUnPause()
        mathdone = false
        Token.player = 0
        timer._ClearAll()
        tokens.clear()
        buttons.clear()
        engine = GameBoard(resources)
        invalidate()
    }

    override fun Tick() {
        invalidate()
    }

    //解決背景化之後timer繼續跑
    fun _IntoBackGround(){
        timer.setPause()
        if(bgm!!.isPlaying) {
            bgm!!.pause()
        }
    }
    fun _IntoForeGround(){
        timer.setUnPause()
        if(SettingsActivity._PlayMusic(context)){
            bgm!!.start()
        }
    }

    //解決選擇不玩後token改變的問題.when app destroyed
    fun _ClearPlayer(){
        Token.player = 0
        bgm!!.release()
        bgm = null
    }

    //Game mode
    fun setGameMode(m: String){
        mode = m
    }

    fun setEnableTouchEvents(enable: Boolean){
        areTouchEventsEnabled = enable
    }
}