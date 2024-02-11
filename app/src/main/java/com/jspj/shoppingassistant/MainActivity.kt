package com.jspj.shoppingassistant

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jspj.shoppingassistant.Utils.LocaleManager
import java.util.Locale

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