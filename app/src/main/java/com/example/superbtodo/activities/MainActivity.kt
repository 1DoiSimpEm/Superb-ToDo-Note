package com.example.superbtodo.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import com.example.superbtodo.R


class MainActivity : AppCompatActivity() {
    private lateinit var actionBar: ActionBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar = supportActionBar!!
        actionBar.hide()
    }
}