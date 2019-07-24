package com.example.testexample

import android.content.Context

/**
 * Description：权限申请辅助类
 * </b>
 * Author：houzhenghong
 * Create Time：2019-07-23
 */
class PermissionHelper(private val context: Context) {

    private val arrays = arrayListOf<String>()

    private var tips: String? = null

    private var positiveText: String? = null

    private var negativeText: String? = null

    /**
     * @param permission 权限可变参数
     */
    fun addPermissions(vararg permission: String): PermissionHelper {
        permission.forEach {
            arrays.add(it)
        }
        return this
    }

    /**
     * @param permission 权限列表
     */
    fun addPermissions(permission: List<String>): PermissionHelper {
        arrays.addAll(permission)
        return this
    }

    /**
     * 拒绝权限后再次申请提示用户因为什么原因才能申请
     * @param tips
     */
    fun deniedShowTips(
        tips: String,
        positiveText: String = context.getString(R.string.action_confirm),
        negativeText: String = context.getString(R.string.action_cancel)
    ): PermissionHelper {
        this.tips = tips
        this.positiveText = positiveText
        this.negativeText = negativeText
        return this
    }

    /**
     * @param onResult 返回结果，结果中返回参数有是否申请成功，申请的权限数组，申请的权限结果返回
     */
    fun request(onResult: ((isGranted: Boolean, permissions: Array<out String>, grantResults: IntArray) -> Unit)? = null) {
        PermissionActivity.starRequestPermissions(context, tips, positiveText,negativeText,arrays, onResult)
    }

    companion object {
        /**
         * @param context
         */
        fun with(context: Context): PermissionHelper {
            return PermissionHelper(context)
        }
    }

}