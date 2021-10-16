package com.example.stranded

import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.stranded.database.UserSave
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {

        val repository = Repository(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        val navController = navHostFragment.navController
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.chatPageFragment, R.id.powerOnFragment, R.id.noPowerFragment),
            drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        findViewById<NavigationView>(R.id.nav_view).setupWithNavController(navController)

        //creating and registering the notification channel
        createChannel(this, "main", "Main Channel")

        //initializing the in memory database with test data
        lifecycleScope.launch {
            // ending test spot is sequence 1 line 80
            // first prompt test spot is sequence 1 line 23
            val testSaveData = UserSave(1, true, 1, 0, "script")
            repository.updateUserSaveData(testSaveData)
            repository.insertTestScriptLines()
            repository.insertTestPromptLines()
            repository.insertTestTriggers()

/*
            repository.insertPromptResult(0)
            repository.insertPromptResult(0)
            repository.insertPromptResult(0)
            repository.insertPromptResult(0)
            repository.insertPromptResult(0)
            repository.insertPromptResult(0)
            repository.insertPromptResult(0)
            repository.insertPromptResult(0)
            repository.insertPromptResult(0)
            repository.insertPromptResult(0)
            repository.insertPromptResult(0)
            repository.insertPromptResult(0)
            repository.insertPromptResult(0)
            repository.insertPromptResult(0)
            repository.insertPromptResult(0)
            repository.insertPromptResult(0)
*/
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

}