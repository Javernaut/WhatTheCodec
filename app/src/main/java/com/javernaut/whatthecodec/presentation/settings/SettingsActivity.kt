package com.javernaut.whatthecodec.presentation.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.javernaut.whatthecodec.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.simple_fragment_container)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager.apply {
            if (findFragmentById(R.id.fragmentContainer) == null) {
                commit {
                    add(R.id.fragmentContainer, SettingsFragment())
                }
            }
        }
    }

    companion object {

        fun start(context: Context) {
            context.startActivity(Intent(context, SettingsActivity::class.java))
        }

    }
}