package com.mxingo.driver.module.upload

/**
 * Created by zhouwei on 2017/10/18.
 */
interface UploadFileResult {

    fun uploadSuccess()

    fun uploadFail()

    fun uploadProgress(percent: Double)
}