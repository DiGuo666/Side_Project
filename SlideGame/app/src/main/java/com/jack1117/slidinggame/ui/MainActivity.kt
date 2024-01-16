package com.jack1117.slidinggame.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    private lateinit var gameView: GameView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gameMode = intent.getStringExtra("Game_Mode")
        gameView = GameView(this)
        gameView.setGameMode(gameMode!!)
        setContentView(gameView)
    }

    //Serializable data (was deprecated)
//    private fun <T : Serializable?> getSerializable(activity: Activity, name: String, clazz: Class<T>): T
//    {
//        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
//            activity.intent.getSerializableExtra(name, clazz)!!
//        else
//            activity.intent.getSerializableExtra(name) as T
//    }

    override fun onResume() {
        super.onResume()
        gameView._IntoForeGround()
    }

    override fun onStop() {
        super.onStop()
        gameView._IntoBackGround()
    }

    override fun onDestroy() {
        super.onDestroy()
        gameView._ClearPlayer()
    }
}

/*
1. main activity, view
2. landscape or portrait
3. Grid
4. buttons
5. pressed image(RectF check if the btn has pressed, pressed methods, onTouchEvent)
6. Token
7. observer pattern
8. falling token
9. game engine
10 update ui
11 keep track of winners
12 game mode
13 AI
14 SplashScreen
15 background
16 music
17 i18n, SettingActivity, icon
SettingActivity: package -> new -> Activity -> Settings Views Activity
icon: mipmap -> new image asset
i18n: res -> new
*/