package com.utap.tidy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.utap.tidy.ui.HomeFragment
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.ActionBar
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentTransaction
import com.beardedhen.androidbootstrap.TypefaceProvider


class MainActivity : AppCompatActivity() {
    private lateinit var homeFragment: HomeFragment

    // An Android nightmare
    // https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
    // https://stackoverflow.com/questions/7789514/how-to-get-activitys-windowtoken-without-view
    fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.rootView.windowToken, 0);
    }

    // https://stackoverflow.com/questions/24838155/set-onclick-listener-on-action-bar-title-in-android/29823008#29823008
    private fun initActionBar(actionBar: ActionBar) {
        // Disable the default and enable the custom
        actionBar.setDisplayShowTitleEnabled(false)
        actionBar.setDisplayShowCustomEnabled(true)
        val customView: View =
            layoutInflater.inflate(R.layout.action_bar, null)
        // Apply the custom view
        actionBar.customView = customView
    }

    private fun initHomeFragment() {
        supportFragmentManager
            .beginTransaction()
            // No back stack for home
            .add(R.id.main_frame, homeFragment)
            // TRANSIT_FRAGMENT_FADE calls for the Fragment to fade away
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Android-Bootstrap
        TypefaceProvider.registerDefaultIconSets();

        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.let{
            initActionBar(it)
        }
        homeFragment = HomeFragment.newInstance()
        initHomeFragment()
    }
}