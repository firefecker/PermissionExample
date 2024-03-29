package com.example.testexample

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat

/**
 * Description：申请权限的activity
 * </b>
 * @author：houzhenghong
 * Create Time：2019-07-24
 */
class PermissionActivity : AppCompatActivity() {

    private val listExtra: ArrayList<String>?
        get() = intent?.getStringArrayListExtra(PERMISSION_LIST)

    private val tips: String?
        get() = intent?.getStringExtra(PERMISSION_TIPS)

    private val positiveText: String
        get() = intent?.getStringExtra(PERMISSION_POSITIVE) ?: getString(R.string.action_confirm)

    private val negativeText: String
        get() = intent?.getStringExtra(PERMISSION_NEGATIVE) ?: getString(R.string.action_cancel)

    /**
     * 是否请求权限
     */
    private var hasPermissions: Boolean = true

    /**
     * 显示tips
     */
    private var hasShowTips: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionStatus()
    }

    /**
     * 判断权限状态并执行相关操作
     */
    private fun permissionStatus() {
        if (listExtra.isNullOrEmpty()) {
            finishAndResult(true, arrayOf(), intArrayOf())
            return
        }
        listExtra?.forEach { permission ->
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                hasPermissions = false
            }
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                hasShowTips = true
            }
        }
        if (hasPermissions) {
            finishAndResult(true, listExtra?.toTypedArray() ?: arrayOf(), intArrayOf())
            return
        }
        when {
            tips.isNullOrEmpty() -> if (!hasPermissions and !hasShowTips) {
                SettingPage.start(this, requestCode = PERMISSION_SETTING_REQUEST_CODE)
            } else {
                requestPermission()
            }
            else -> showTips()
        }
    }

    /**
     * 显示提示框
     */
    private fun showTips() {
        AlertDialog.Builder(this)
            .setMessage(tips)
            .setCancelable(false)
            .setNegativeButton(
                negativeText
            ) { dialog, _ ->
                dialog.dismiss()
                finishAndResult(false, listExtra?.toTypedArray() ?: arrayOf(), intArrayOf())
            }
            .setPositiveButton(
                positiveText
            ) { dialog, _ ->
                dialog.dismiss()
                if (!hasPermissions and !hasShowTips) {
                    SettingPage.start(this, requestCode = PERMISSION_SETTING_REQUEST_CODE)
                    return@setPositiveButton
                }
                requestPermission()
            }
            .show()
    }

    /**
     * 请求权限
     */
    private fun requestPermission() {
        val strings = listExtra?.toTypedArray() ?: arrayOf()
        if (strings.isNullOrEmpty()) {
            finishAndResult(true, arrayOf(), intArrayOf())
            return
        }
        ActivityCompat.requestPermissions(
            this,
            strings,
            PERMISSION_REQUEST_CODE
        )
    }


    /**权限请求回调
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    when {
                        grantResults[0] == PackageManager.PERMISSION_GRANTED -> finishAndResult(
                            !grantResults.contains(-1),
                            permissions,
                            grantResults
                        )
                        grantResults[0] == PackageManager.PERMISSION_DENIED -> {
                            if (grantResults.contains(-1)) {
                                SettingPage.start(this, requestCode = PERMISSION_SETTING_REQUEST_CODE)
                                return
                            }
                            finishAndResult(
                                false,
                                permissions,
                                grantResults
                            )
                        }
                        else -> SettingPage.start(this, requestCode = PERMISSION_SETTING_REQUEST_CODE)
                    }
                }
            }
        }
    }

    /**接收设置界面返回请求code
     * @param requestCode
     * @param resultCode
     * @param data
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PERMISSION_SETTING_REQUEST_CODE) {
            requestPermission()
        }
    }

    /**
     * 关闭当前页面并返回状态
     * @param isGranted
     * @param permissions
     * @param grantResults
     */
    private fun finishAndResult(isGranted: Boolean, permissions: Array<out String>, grantResults: IntArray) {
        finish()
        onResult?.invoke(isGranted, permissions, grantResults)
    }

    companion object {
        const val PERMISSION_REQUEST_CODE = 43222
        const val PERMISSION_SETTING_REQUEST_CODE = 43221

        const val PERMISSION_LIST = "permission_list"
        const val PERMISSION_TIPS = "permission_tips"
        const val PERMISSION_POSITIVE = "permission_positive"
        const val PERMISSION_NEGATIVE = "permission_negative"

        var onResult: ((isGranted: Boolean, permissions: Array<out String>, grantResults: IntArray) -> Unit)? = null

        fun starRequestPermissions(
            context: Context,
            tips: String? = null,
            positiveText: String? = null,
            negativeText: String? = null,
            permissions: ArrayList<String>,
            onResult: ((isGranted: Boolean, permissions: Array<out String>, grantResults: IntArray) -> Unit)? = null
        ) {
            this.onResult = onResult
            context.startActivity(
                Intent(context, PermissionActivity::class.java)
                    .putExtra(PERMISSION_LIST, permissions)
                    .putExtra(PERMISSION_TIPS, tips)
                    .putExtra(PERMISSION_POSITIVE, positiveText)
                    .putExtra(PERMISSION_NEGATIVE, negativeText)
            )
        }
    }
}