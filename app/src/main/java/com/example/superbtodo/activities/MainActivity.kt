package com.example.superbtodo.activities


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
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

        setContentView(binding.root)

    }
    private fun bottomNavigationSetup() {
        navController = binding.fragmentContainer.getFragment<NavHostFragment>().navController
        appBarConfiguration = AppBarConfiguration(setOf(R.id.homeFragment,R.id.doneFragment,R.id.calendarFragment))
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