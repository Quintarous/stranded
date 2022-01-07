package com.example.stranded

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.stranded.chatpage.ChatPageViewModel
import com.example.stranded.database.UserSave
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {

        /**
         * android housekeeping stuff
         */
        val repository = Repository(this)

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val navHostFragment: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        val navController = navHostFragment.navController
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)


        // configures an app bar with a set of all the top level destinations
        // and the drawer layout from the xml layout file
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.chatPageFragment, R.id.powerOnFragment, R.id.noPowerFragment),
            drawerLayout
        )

        // applying the app bar
        setupActionBarWithNavController(navController, appBarConfiguration)
        findViewById<NavigationView>(R.id.nav_view).setupWithNavController(navController)


        // removing the app title from the appbar and adding a background to it
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        supportActionBar!!.setBackgroundDrawable(AppCompatResources
            .getDrawable(this, R.drawable.stranded_appbar_background))


        //creating and registering our one and only notification channel
        createChannel(this, "main", "Main Channel")


        //TODO delete this when code is finalized
        //initializing the in memory database with test data
        lifecycleScope.launch {
            // sequence: 1 line: 80 promptResults: 15
            val testSaveData = UserSave(1, true, 0, 6, 0,"script", false)
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

    /**
     * these lifecycle overrides are all used for managing the media player used to play
     * sound effects. The media player object is stored in the ChatPageViewModel and used by
     * the ChatPageFragment.
     *
     * When the app is stopped (put in the background) media is paused.
     * When the app is brought back to the foreground (resumed) media is resumed.
     * And finally when the app is destroyed (closed) the media player itself is released.
     *
     * This allows sound effects/music to play continuously while the app is open (regardless of
     * which fragment the user is on). While still cleaning up the media player when the app is
     * fully shut down.
     */
    override fun onResume() {
        val viewModel: ChatPageViewModel by viewModels()

        if (viewModel.mediaPlayer != null) {
            viewModel.mediaPlayer?.start()
        }

        super.onResume()
    }

    override fun onStop() {
        val viewModel: ChatPageViewModel by viewModels()

        if (viewModel.mediaPlayer != null) {
            viewModel.mediaPlayer?.pause()
        }

        super.onStop()
    }

    override fun onDestroy() {
        val viewModel: ChatPageViewModel by viewModels()

        if (viewModel.mediaPlayer != null) {
            viewModel.mediaPlayer?.release()
            viewModel.mediaPlayer = null
        }

        super.onDestroy()
    }
}