package com.mxingo.driver.module

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.mxingo.driver.R
import com.mxingo.driver.dialog.MessageDialog
import com.mxingo.driver.dialog.SelectImageTypeDialog
import com.mxingo.driver.model.DriverInfoEntity
import com.mxingo.driver.model.HesuanResultEntity
import com.mxingo.driver.model.QiNiuTokenEntity
import com.mxingo.driver.module.base.http.ComponentHolder
import com.mxingo.driver.module.base.http.MyPresenter
import com.mxingo.driver.module.base.log.LogUtils
import com.mxingo.driver.module.upload.UploadFile
import com.mxingo.driver.module.upload.UploadFileResult
import com.mxingo.driver.utils.BitmapUtil
import com.mxingo.driver.utils.Constants
import com.mxingo.driver.widget.MyProgress
import com.mxingo.driver.widget.ShowToast
import com.squareup.otto.Subscribe
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class JkmReportActivity:BaseActivity() {

    private var token = ""
    private lateinit var driverInfo: DriverInfoEntity
    private lateinit var ivJkmAdd: ImageView
    private lateinit var btnJkm: Button
    private lateinit var imgTmp: ImageView
    private var bitJkm: Bitmap? = null
    private var imgJkmUri: Uri? = null
    private var urlJkm: String = ""
    private var keyJkm: String = ""
    private var cameraFile: File? = null
    private var outputUri: Uri? = null
    private val upload = UploadFile()

    @Inject
    lateinit var presenter: MyPresenter

    private lateinit var progress: MyProgress

    companion object {
        const val PERMISSION_CAMERA = 101
        const val START_CAMERA = 101
        const val REQUEST_IMAGE = 1000
        const val START_ALBUM = 102
        const val CROP = 100
        @JvmStatic
        fun startJkmReportActivity(context: Context, info: DriverInfoEntity) {
            context.startActivity(Intent(context, JkmReportActivity::class.java).putExtra(Constants.DRIVER_INFO, info))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jkm_report)
        ComponentHolder.appComponent!!.inject(this)
        presenter.register(this)
        progress = MyProgress(this)
        initView()
    }

    private fun initView() {
        setToolbar(toolbar = findViewById(R.id.toolbar) as Toolbar)
        (findViewById(R.id.tv_toolbar_title) as TextView).text = "健康码"
        driverInfo = intent.getSerializableExtra(Constants.DRIVER_INFO) as DriverInfoEntity
        ivJkmAdd = findViewById(R.id.iv_jkm_add) as ImageView
        btnJkm = findViewById(R.id.btn_jkm) as Button

        ivJkmAdd.setOnClickListener {
            if (bitJkm==null){
                imgTmp= ivJkmAdd
                showImageDialog()
            }else{
                LogUtils.d("url", urlJkm)
                ImageActivity.startImageActivity(this, imgJkmUri, urlJkm, 0, REQUEST_IMAGE)
            }
        }

        btnJkm.setOnClickListener {
            if (bitJkm==null){
                ShowToast.showCenter(this, "请选择健康码凭证")
                return@setOnClickListener
            }
            progress.show()
            presenter.getQiNiuToken()
        }
    }

    @Subscribe
    fun loadData(any: Any) {
        when {
            any::class == QiNiuTokenEntity::class -> getQiNiuToken(any)
            any::class == HesuanResultEntity::class -> getCheckInfo(any)
        }
    }

    private fun getCheckInfo(any: Any) {
        val data = any as HesuanResultEntity
        if (data.rspCode=="2006"){
            ShowToast.showCenter(this, "上传完成")
        }else{
            ShowToast.showCenter(this, "上传失败")
        }
    }

    private fun getQiNiuToken(any: Any) {
        val data = any as QiNiuTokenEntity
        if (data.rspCode == "00") {
            token = data.qiniuToken
            LogUtils.d("token", token)
            handler.sendEmptyMessage(2)
        } else {
            progress.dismiss()
        }
    }

    private fun showImageDialog() {
        val dialog = SelectImageTypeDialog(this)

        //打开系统相册
        dialog.setOnAlbumClickListener {
            val intent = Intent("android.intent.action.PICK")
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), START_ALBUM)
        }

        dialog.setOnCameraClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    val message = MessageDialog(this)
                    message.setMessageText("您的权限申请失败，请前往应用信息打开相机权限")
                    message.setOnOkClickListener {
                        val packageURI = Uri.parse("package:" + this@JkmReportActivity.packageName)
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI)
                        startActivity(intent)
                        message.dismiss()
                    }
                    message.show()
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), PERMISSION_CAMERA)
                }

            }
        }
        dialog.show()
    }

    private fun startCamera() {
        if (cameraFile == null) {
            cameraFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), getPhotoFileName())
        }
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // 指定调用相机拍照后照片的储存路径
        cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        //适配Android 7.0文件权限，通过FileProvider
        if (Build.VERSION.SDK_INT >= 24) {
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, this.packageName + ".fileprovider", cameraFile!!))
        } else {
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile!!))
        }
        startActivityForResult(cameraIntent, START_CAMERA)
    }

    // 使用系统当前日期加以调整作为照片的名称
    @SuppressLint("SimpleDateFormat")
    private fun getPhotoFileName(): String {
        val date = Date(System.currentTimeMillis())
        val dateFormat = SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss")
        return dateFormat.format(date) + ".png"
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CAMERA && grantResults[0] == 0) {
            startCamera()
        }
    }
    private val handler = Handler(Handler.Callback {
        if (it.what == 1) {
            ivJkmAdd.setImageBitmap(BitmapUtil.compressUri(this, outputUri, CROP, CROP))
            bitJkm = BitmapUtil.compressUri(this, outputUri, 720, 1080)
            btnJkm.setBackgroundResource(R.drawable.btn_blue_round)
            imgJkmUri = outputUri
        }else if (it.what == 2){
            //progress.setLabel("上传中")
            progress.setDetailsLabel("健康码上传中...")
            keyJkm = getKey()
            upload(bitJkm!!, keyJkm, token, 0)
        }else if (it.what == 3) {
            progress.dismiss()
            //ShowToast.showCenter(this, "上传完成")
            //progress.setLabel("请稍等")
            //progress.setDetailsLabel("请求中")
            presenter.jkmUpload(driverInfo.driver.cuuid,keyJkm)
        }else if (it.what == 7) {
            progress.dismiss()
        }
        true
    })

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (START_CAMERA == requestCode && resultCode == Activity.RESULT_OK) {
            outputUri = Uri.fromFile(cameraFile)
            handler.sendEmptyMessage(1)
        } else if (START_ALBUM == requestCode && resultCode == Activity.RESULT_OK) {
            if (data!!.data != null) {
                outputUri = data.data
                handler.sendEmptyMessage(1)
            }
        } else if (REQUEST_IMAGE == requestCode && resultCode == Activity.RESULT_OK) {
            clearIndexImage(data!!.getIntExtra(Constants.INDEX, 0))
        }
    }

    private fun clearIndexImage(intExtra: Int) {
        imgTmp.setImageResource(R.drawable.ic_add)
        btnJkm.setBackgroundResource(R.drawable.btn_gray_round)
        when(intExtra){
            0 -> {
                bitJkm = null
                imgJkmUri = null
            }
        }
    }

    private fun getKey(): String {
        return "rxdriver/${System.currentTimeMillis()}.png"
    }

    private fun upload(bitmap: Bitmap, key: String, token: String, index: Int) {
        Thread(Runnable {
            upload.uploadFileByte(
                    BitmapUtil.compressImage(bitmap),
                    key,
                    token,
                    UploadResult(index)
            )
        }).start()
    }

    inner class UploadResult(val index: Int) : UploadFileResult {
        override fun uploadSuccess() {
            this@JkmReportActivity.handler.sendEmptyMessage(index + 3)
        }

        override fun uploadFail() {
            this@JkmReportActivity.handler.sendEmptyMessage(4)
        }

        override fun uploadProgress(percent: Double) {

        }
    }
    override fun onDestroy() {
        super.onDestroy()
        presenter.unregister(this)
    }
}