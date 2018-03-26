package com.mathildeguillossou.thebluebattery

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.mathildeguillossou.thebluebattery.bluetooth.BluetoothService

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BlankFragment.OnFragmentInteractionListener {
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
            Helper.rotate(fab)
        }
    }

    override fun connect(address: String?, position: Int) {
        ble.connect(address, position)
    }

    override fun hide() {
        snackBar.dismiss()
        fab.clearAnimation()
    }
}
