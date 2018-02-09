package com.mathildeguillossou.thebluebattery

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BlankFragment.OnFragmentInteractionListener {
    private val TAG: String = MainActivity::class.java.simpleName

    lateinit var snackBar: Snackbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        supportFragmentManager.beginTransaction().replace(R.id.container, BlankFragment.newInstance()).commit()

        fab.setOnClickListener { view ->
            snackBar = Snackbar.make(view, "Processing ...", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Action", null)
            snackBar.show()
        }
    }

    override fun hide() {
        Log.d(TAG, "hide")
        snackBar.dismiss()
    }

    override fun onFragmentInteraction(id: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
