package com.example.superbtodo.activities

import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.ActionBar
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
        if (Build.VERSION.SDK_INT in 19..20) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }
        setContentView(binding.root)

    }
    private fun setWindowFlag(bits: Int, on: Boolean) {
        val win = window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }
    private fun bottomNavigationSetup() {
        navController = binding.fragmentContainer.getFragment<NavHostFragment>().navController
        appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment,R.id.doneFragment))
        setupActionBarWithNavController(navController,appBarConfiguration)
        hideBottomNavigation()
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    private fun hideBottomNavigation() {
        navController.addOnDestinationChangedListener{_,destination,_ ->
            if(destination.id==R.id.addingFragment){
                binding.bottomNavigationView.visibility = View.GONE
            }
            else{
                binding.bottomNavigationView.visibility = View.VISIBLE
            }
        }
    }

    private fun hideStuff() {
        actionBar = supportActionBar!!
        actionBar.hide()
    }
}