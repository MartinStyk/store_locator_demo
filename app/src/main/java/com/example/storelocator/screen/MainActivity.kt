package com.example.storelocator.screen

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.storelocator.R
import com.example.storelocator.databinding.ActivityMainBinding
import com.example.storelocator.screen.results.StoresFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : StoreLocatorBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container, StoresFragment())
                .commit()
        }
    }

}