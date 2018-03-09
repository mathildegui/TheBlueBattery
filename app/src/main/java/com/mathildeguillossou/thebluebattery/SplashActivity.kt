package com.mathildeguillossou.thebluebattery

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log


/**
 * @author mathildeguillossou on 09/03/2018
 */
class SplashActivity : Activity() {
    private val TAG: String = SplashActivity::class.java.simpleName
    private val LOCATION_REQUEST_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupPermissions()
    }

    fun start() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "Permission has been denied by user")
                } else {
                    this.start()
                    Log.i(TAG, "Permission has been granted by user")
                }
            }
        }
    }

    fun setupPermissions () {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        if(permission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions()
        } else {
            this.start()
        }
    }

    fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_REQUEST_CODE)
    }
}