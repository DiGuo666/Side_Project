package com.jack1117.slidinggame.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.jack1117.slidinggame.R
import com.jack1117.slidinggame.SettingsActivity

class GameActivity : AppCompatActivity(){
    private lateinit var singlePlayerButton: ImageView
    private lateinit var dualPlayerButton: ImageView
    private lateinit var questionButton: ImageView
    private lateinit var settingButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_activity)
        singlePlayerButton = findViewById(R.id.imageView1P)
        dualPlayerButton = findViewById(R.id.imageView2P)
        questionButton = findViewById(R.id.question_mark)
        settingButton = findViewById(R.id.setting)

        //intent object
        singlePlayerButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("Game_Mode", resources.getString(R.string.Choose_One_Player))
            startActivity(intent)
        }
        dualPlayerButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("Game_Mode", resources.getString(R.string.Choose_Two_Player))
            startActivity(intent)
        }

        questionButton.setOnClickListener{
            val ab: AlertDialog.Builder = AlertDialog.Builder(this)
            ab.setTitle(R.string.About_Game)
            ab.setMessage(R.string.About_Description)
            ab.setCancelable(false)
            ab.setPositiveButton(R.string.Okay_Button){_,_ ->
                println("OK")
            }
            ab.create().show()
        }

        settingButton.setOnClickListener{
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}