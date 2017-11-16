package com.mathildeguillossou.thebluebattery

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log

import kotlinx.android.synthetic.main.activity_main2.*

class Main2Activity : AppCompatActivity(), BlankFragment.OnFragmentInteractionListener {
    override fun onFragmentInteraction(id: String) {
        Log.d("onFragmentInteraction", id)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        setSupportActionBar(toolbar)

        supportFragmentManager.beginTransaction().replace(R.id.container, BlankFragment.newInstance()).commit()

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

}
