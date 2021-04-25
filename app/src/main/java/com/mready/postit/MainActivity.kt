package com.mready.postit

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.frameLayout_homePage,
                FeedFragment.newInstance(),
                getString(R.string.feedFragment)
            ).commit()
    }

}