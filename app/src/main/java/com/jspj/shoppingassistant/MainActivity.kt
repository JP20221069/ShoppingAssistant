package com.jspj.shoppingassistant

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val actionbar = supportActionBar;
        actionbar?.setDisplayUseLogoEnabled(true);
        actionbar?.setDisplayShowHomeEnabled(true);
        actionbar?.setIcon(R.drawable.cart)
    }
}