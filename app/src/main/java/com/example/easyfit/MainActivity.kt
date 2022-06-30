package com.example.easyfit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //activity_main = login page
        //activity_home = home page
        setContentView(R.layout.activity_main)

//        val mFragmentManager = supportFragmentManager
//        val mfGetStarted = getstarted()
//
//        mFragmentManager.findFragmentByTag(getstarted::class.java.simpleName)
//        mFragmentManager
//            .beginTransaction()
//            .add(R.id.frameContainer, mfGetStarted, getstarted::class.java.simpleName)
//            .commit()

    }
}