package com.dorokhov.jetpackapp.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.dorokhov.jetpackapp.R
import com.dorokhov.jetpackapp.ui.BaseActivity
import com.dorokhov.jetpackapp.ui.auth.AuthActivity
import com.dorokhov.jetpackapp.ui.main.account.ChangePasswordFragment
import com.dorokhov.jetpackapp.ui.main.account.UpdateAccountFragment
import com.dorokhov.jetpackapp.ui.main.blog.UpdateBlogFragment
import com.dorokhov.jetpackapp.ui.main.blog.ViewBlogFragment
import com.dorokhov.jetpackapp.util.BottomNavController
import com.dorokhov.jetpackapp.util.setUpNavigation
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), BottomNavController.NavGraphProvider,
    BottomNavController.OnNavigationGraphChanged,
    BottomNavController.OnNavigationReselectedListener {

    private lateinit var bottomNavigationView: BottomNavigationView

    private val bottomNavController: BottomNavController by lazy(LazyThreadSafetyMode.NONE) {
        BottomNavController(
            this,
            R.id.main_nav_host_fragment,
            R.id.nav_blog,
            this,
            this
        )
    }

    override fun getNavGraphId(itemId: Int) = when (itemId) {
        R.id.nav_blog -> {
            R.navigation.nav_blog
        }



        R.id.nav_account -> {
            R.navigation.nav_account
        }

        R.id.nav_create_blog -> {
            R.navigation.nav_create_blog
        }
        else -> R.navigation.nav_blog
    }

    override fun onGraphChange() {
        // "what needs to happen when nav graph changes?'w
        expandAppBar()
    }

    override fun onReselectNavItem(navController: NavController, fragment: Fragment) =
        when (fragment) {
            is ViewBlogFragment -> {
                navController.navigate(R.id.action_viewBlogFragment_to_home)
            }
            is UpdateBlogFragment -> {
                navController.navigate(R.id.action_updateBlogFragment_to_home)
            }
            is UpdateAccountFragment -> {
                navController.navigate(R.id.action_updateAccountFragment_to_home)
            }
            is ChangePasswordFragment -> {
                navController.navigate(R.id.action_changePasswordFragment_to_home)
            }
            else -> {
                // do nothing
            }
        }


    private fun setUpActionBar() {
        setSupportActionBar(tool_bar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() = bottomNavController.onBackPressed()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpActionBar()
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        bottomNavigationView.setUpNavigation(bottomNavController, this)

        // if this activity launched in the first time
        if (savedInstanceState == null) {
            bottomNavController.onNavigationItemSelected()
        }
        subscribeObserver()
    }

    private fun subscribeObserver() {
        sessionManager.cashedToken.observe(this, Observer { authToken ->
            Log.d(TAG, "MainActivity: subscribeObservers: AuthToken ${authToken} ")
            if (authToken == null || authToken.account_pk == -1 || authToken.token == null) {
                navAuthActivity()
            }
        })
    }

    private fun navAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun displayProgressBar(boolean: Boolean) {
        if (boolean) {
            progress_bar.visibility = View.VISIBLE
        } else {
            progress_bar.visibility = View.GONE
        }
    }

    override fun expandAppBar() {
        findViewById<AppBarLayout>(R.id.app_bar).setExpanded(true)
    }
}