package com.mathildeguillossou.thebluebattery

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.mathildeguillossou.thebluebattery.bluetooth.BindRequest
import com.mathildeguillossou.thebluebattery.bluetooth.BluetoothService

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BlankFragment.OnFragmentInteractionListener, BindRequest.BindRequestListener {
    override fun onBindingSuccess() {
        Log.d("onBindingSuccess", "onBindingSuccess")
    }

    override fun onBindingFailed() {
        Log.d("onBindingFailed", "onBindingFailed")
    }

    override fun discover() {
        Log.d("SCAN", "discover")

    }

    private val TAG: String = MainActivity::class.java.simpleName

    lateinit var snackBar: Snackbar

    lateinit var ble : BluetoothService
    lateinit var fragment : Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        ble = BluetoothService(this)

        supportFragmentManager.beginTransaction().replace(R.id.container, BlankFragment.newInstance()).commit()


        fab.setOnClickListener { view ->
            snackBar = Snackbar.make(view, "Processing ...", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Action", null)
            snackBar.show()
            fragment = supportFragmentManager.findFragmentById(R.id.container)
            (fragment as BlankFragment).scan(ble)
        }
    }

    override fun hide() {
        Log.d(TAG, "hide")
        snackBar.dismiss()
    }

    override fun onFragmentInteraction(macAddress: String) {
        //To change body of created functions use File | Settings | File Templates.
        //ble.bindDevice(macAddress, this)
    }

}
