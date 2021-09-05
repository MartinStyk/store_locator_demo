package com.example.storelocator.screen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.storelocator.manager.backpress.BackPressedManager
import com.example.storelocator.manager.location.LocationRequestManager
import com.example.storelocator.manager.permission.PermissionManager
import javax.inject.Inject

abstract class StoreLocatorBaseActivity : AppCompatActivity() {

    @Inject
    lateinit var permissionManager: PermissionManager

    @Inject
    lateinit var locationRequestManager: LocationRequestManager

    @Inject
    lateinit var backPressedManager: BackPressedManager

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionManager.onRequestPermissionsResult(permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        locationRequestManager.onActivityResult(requestCode, resultCode)
    }

    override fun onBackPressed() {
        if (backPressedManager.onBackPressed()) {
            return
        }
        super.onBackPressed()
    }

}