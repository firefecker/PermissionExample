package com.example.testexample

import android.Manifest
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            PermissionHelper.with(this)
                .addPermissions(Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA)
                .deniedShowTips("这是请求权限提示。。。。。。")
                .request { isGranted, permission, grantResults ->
                    Log.e("TAGTAG", "$isGranted")
                }
        }
    }
}
