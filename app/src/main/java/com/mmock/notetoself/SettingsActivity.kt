package com.mmock.notetoself

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Switch

class SettingsActivity : AppCompatActivity() {
    private var showDividers: Boolean = true
    private lateinit var switch: Switch
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


        //initialize prefs
        val prefs = getSharedPreferences("Note to self", Context.MODE_PRIVATE)

        showDividers = prefs.getBoolean("dividers", true)

        //set the switch on or off as appropriate
        switch = findViewById(R.id.switch1)

        switch.isChecked = showDividers
        switch.setOnCheckedChangeListener {
                buttonView, isChecked ->
            showDividers = isChecked
        }
    }

    override fun onPause() {
        super.onPause()

        //save the settings here
        val prefs = getSharedPreferences("Note to self", Context.MODE_PRIVATE)
        val editor = prefs.edit()

        editor.putBoolean("dividers", showDividers)

        editor.apply()
    }
}