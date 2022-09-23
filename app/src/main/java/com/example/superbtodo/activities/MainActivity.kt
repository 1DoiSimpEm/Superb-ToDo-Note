package com.example.superbtodo.activities


import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.superbtodo.R
import com.example.superbtodo.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var actionBar: ActionBar
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        hideStuff()
        bottomNavigationSetup()
        setContentView(binding.root)
        val window: Window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.statusBarColor = this.resources.getColor(R.color.white_bg)

    }
    private fun bottomNavigationSetup() {
        navController = binding.fragmentContainer.getFragment<NavHostFragment>().navController
        appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment,R.id.doneFragment,R.id.calendarFragment))
        setupActionBarWithNavController(navController,appBarConfiguration)
        binding.bottomNavigationView.setupWithNavController(navController)
    }
    private fun hideStuff() {
        actionBar = supportActionBar!!
        actionBar.hide()
    }
}