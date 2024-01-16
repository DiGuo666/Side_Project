package com.jack1117.slidinggame

import android.content.Context
import android.content.res.TypedArray
import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreference

private fun SeekBarPreference.onPreferenceChangeListener(any: Any, function: () -> Unit) {

}

class SettingsActivity : AppCompatActivity() {
    companion object{
        private val MUSIC_KEY = "MUSIC_PREF"
        private val VELOCITY_KEY = "VELOCITY_PREF"

        fun _PlayMusic(context: Context) : Boolean{
            return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(MUSIC_KEY, true)
        }

        fun _GameVelocity(context: Context) : Int{
            return PreferenceManager.getDefaultSharedPreferences(context).getInt(VELOCITY_KEY, 5)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
    }

    class SettingsFragment : PreferenceFragmentCompat(){
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            val context = preferenceManager.context
            val screen = preferenceManager.createPreferenceScreen(context)

            var seekBar: SeekBar? = null
            var v_textView: TextView? = null

            val music = SwitchPreference(context)
            val velocity = SeekBarPreference(context)

            //music set
            music.setTitle(R.string.Music_Title)
            music.summaryOn = resources.getString(R.string.Play_Music)
            music.summaryOff = resources.getString(R.string.Stop_Music)
            music.isChecked = true
            music.key = MUSIC_KEY

            //velocity set
            velocity.setTitle(R.string.velocity_Title)
            velocity.min = 1
            velocity.max = 10
            velocity.showSeekBarValue = true
            velocity.summary = resources.getString(R.string.Now_velocity)
            velocity.key = VELOCITY_KEY

            screen.addPreference(music)
            screen.addPreference(velocity)

            preferenceScreen = screen
        }

    }
}