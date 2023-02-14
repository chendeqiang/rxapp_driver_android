package com.mxingo.driver.module

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
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
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.*
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.mxingo.driver.R
import com.mxingo.driver.dialog.MessageDialog
import com.mxingo.driver.dialog.SelectImageTypeDialog
import com.mxingo.driver.model.CommEntity
import com.mxingo.driver.model.DriverInfoEntity
import com.mxingo.driver.model.GetCheckInfo
import com.mxingo.driver.model.QiNiuTokenEntity
import com.mxingo.driver.module.base.data.MyModulePreference
import com.mxingo.driver.module.base.data.UserInfoPreferences
import com.mxingo.driver.module.base.http.ComponentHolder
import com.mxingo.driver.module.base.http.MyPresenter
import com.mxingo.driver.module.base.log.LogUtils
import com.mxingo.driver.module.take.CarLevel
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

@TargetApi(Build.VERSION_CODES.CUPCAKE)
class DriverCarRegistrationActivity : BaseActivity() {

    private lateinit var tvDriverName: TextView
    private lateinit var tvMobile: TextView
    private lateinit var tvCarBrand: TextView
    private lateinit var tvCarNo: TextView
    private lateinit var tvCarType: TextView
    private lateinit var tvStatus: TextView
    private lateinit var btnSubmit:Button
    private lateinit var rlMain:RelativeLayout


    private lateinit var driverInfo: DriverInfoEntity

    private var bitFront: Bitmap? = null
    private var bitBack: Bitmap? = null
    private var bitDriver: Bitmap? = null
    private var bitDriving: Bitmap? = null
    private var bitInsurance: Bitmap? = null
    private var bitDriverWyc: Bitmap? = null
    private var bitCarWyc: Bitmap? = null

    private lateinit var imgTmp: ImageView

    private lateinit var imgUpload: Array<ImageView>

    private var imgFrontUri: Uri? = null
    private var imgBackUri: Uri? = null
    private var imgDriverUri: Uri? = null
    private var imgDrivingUri: Uri? = null
    private var imgInsuranceUri: Uri? = null
    private var imgDriverWycUri: Uri? = null
    private var imgCarWycUri: Uri? = null

    private var keyFront: String = ""
    private var keyBack: String = ""
    private var keyDriver: String = ""
    private var keyDriving: String = ""
    private var keyInsurance: String = ""
    private var keyDriverWyc: String = ""
    private var keyCarWyc: String = ""

    private var urlFront: String = ""
    private var urlBack: String = ""
    private var urlDriver: String = ""
    private var urlDriving: String = ""
    private var urlInsurance: String = ""
    private var urlDriverWyc: String = ""
    private var urlCarWyc: String = ""

    private var cameraFile: File? = null
    private var outputUri: Uri? = null
    private val upload = UploadFile()

    private var status = -1

    @Inject
    lateinit var presenter: MyPresenter

    private lateinit var progress: MyProgress

    private var token = ""

    companion object {
        const val START_CAMERA = 101
        const val PERMISSION_CAMERA = 101
        const val PERMISSION_ALBUM = 102
        const val START_ALBUM = 102
        const val CROP = 100
        const val REQUEST_IMAGE = 1000

        @JvmStatic
        fun startDriverCarRegistrationActivity(context: Context, info: DriverInfoEntity) {

            context.startActivity(Intent(context, DriverCarRegistrationActivity::class.java).putExtra(Constants.DRIVER_INFO, info))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_car_registration)
        ComponentHolder.appComponent!!.inject(this)
        presenter.register(this)
        progress = MyProgress(this)
        initView()
    }

    private fun initView() {
        setToolbar(toolbar = findViewById(R.id.toolbar) as Toolbar)
        (findViewById(R.id.tv_toolbar_title) as TextView).text = "网约车认证"
        driverInfo = intent.getSerializableExtra(Constants.DRIVER_INFO) as DriverInfoEntity
        tvDriverName = findViewById(R.id.tv_driver_name) as TextView
        tvCarBrand = findViewById(R.id.tv_car_brand) as TextView
        tvCarNo = findViewById(R.id.tv_car_no) as TextView
        tvCarType = findViewById(R.id.tv_car_type) as TextView
        tvMobile = findViewById(R.id.tv_mobile) as TextView
        tvStatus = findViewById(R.id.tv_status) as TextView
        btnSubmit =findViewById(R.id.btn_submit) as Button
        rlMain =findViewById(R.id.rl_main) as RelativeLayout

        imgUpload = arrayOf(findViewById(R.id.img_front) as ImageView,
                findViewById(R.id.img_back) as ImageView,
                findViewById(R.id.img_driver) as ImageView,
                findViewById(R.id.img_driving) as ImageView,
                findViewById(R.id.img_insurance) as ImageView,
                findViewById(R.id.img_driver_wyc) as ImageView,
                findViewById(R.id.img_car_wyc) as ImageView)


        btnSubmit.setOnClickListener {
            if (bitFront == null) {
                ShowToast.showCenter(this, "请选择身份证正面照")
                return@setOnClickListener
            }
            if (bitBack == null) {
                ShowToast.showCenter(this, "请选择身份证反面照")
                return@setOnClickListener
            }
            if (bitDriver == null) {
                ShowToast.showCenter(this, "请选择驾照")
                return@setOnClickListener
            }
            if (bitDriving == null) {
                ShowToast.showCenter(this, "请选择驾驶证")
                return@setOnClickListener
            }
            if (bitInsurance == null) {
                ShowToast.showCenter(this, "请选择保险单")
                return@setOnClickListener
            }
            if (bitDriverWyc == null) {
                ShowToast.showCenter(this, "请选择网约车人证")
                return@setOnClickListener
            }
            if (bitCarWyc == null) {
                ShowToast.showCenter(this, "请选择网约车车证")
                return@setOnClickListener
            }
            progress.show()
            presenter.getQiNiuToken()
        }

        imgUpload[0].setOnClickListener {
            if (bitFront == null && (status == DriverCheckInfoStatus.UNSUBMIT.status || status == DriverCheckInfoStatus.CANTPASS.status)) {
                imgTmp = imgUpload[0]
                showImageDialog()
            } else {
                LogUtils.d("url", urlFront)
                ImageActivity.startImageActivity(this, imgFrontUri, urlFront, 0, REQUEST_IMAGE)
            }

        }

        imgUpload[1].setOnClickListener {
            if (bitBack == null && (status == DriverCheckInfoStatus.UNSUBMIT.status || status == DriverCheckInfoStatus.CANTPASS.status)) {
                imgTmp = imgUpload[1]
                showImageDialog()
            } else {
                ImageActivity.startImageActivity(this, imgBackUri, urlBack, 1, REQUEST_IMAGE)
            }
        }

        imgUpload[2].setOnClickListener {
            if (bitDriver == null && (status == DriverCheckInfoStatus.UNSUBMIT.status || status == DriverCheckInfoStatus.CANTPASS.status)) {
                imgTmp = imgUpload[2]
                showImageDialog()
            } else {
                ImageActivity.startImageActivity(this, imgDriverUri, urlDriver, 2, REQUEST_IMAGE)
            }
        }

        imgUpload[3].setOnClickListener {
            if (bitDriving == null && (status == DriverCheckInfoStatus.UNSUBMIT.status || status == DriverCheckInfoStatus.CANTPASS.status)) {
                imgTmp = imgUpload[3]
                showImageDialog()
            } else {
                ImageActivity.startImageActivity(this, imgDrivingUri, urlDriving, 3, REQUEST_IMAGE)
            }
        }

        imgUpload[4].setOnClickListener {
            if (bitInsurance == null && (status == DriverCheckInfoStatus.UNSUBMIT.status || status == DriverCheckInfoStatus.CANTPASS.status)) {
                imgTmp = imgUpload[4]
                showImageDialog()
            } else {
                ImageActivity.startImageActivity(this, imgInsuranceUri, urlInsurance, 4, REQUEST_IMAGE)
            }
        }
        imgUpload[5].setOnClickListener {
            if (bitDriverWyc == null && (status == DriverCheckInfoStatus.UNSUBMIT.status || status == DriverCheckInfoStatus.CANTPASS.status)) {
                imgTmp = imgUpload[5]
                showImageDialog()
            } else {
                ImageActivity.startImageActivity(this, imgDriverWycUri, urlDriverWyc, 5, REQUEST_IMAGE)
            }
        }
        imgUpload[6].setOnClickListener {
            if (bitCarWyc == null && (status == DriverCheckInfoStatus.UNSUBMIT.status || status == DriverCheckInfoStatus.CANTPASS.status)) {
                imgTmp = imgUpload[6]
                showImageDialog()
            } else {
                ImageActivity.startImageActivity(this, imgCarWycUri, urlCarWyc, 6, REQUEST_IMAGE)
            }
        }
        if (intent.getSerializableExtra(Constants.DRIVER_INFO) != null) {
            tvDriverName.text = driverInfo.driver.cname
            tvCarType.text = CarLevel.getKey(driverInfo.carLevel)
            tvMobile.text = UserInfoPreferences.getInstance().mobile
            //tvMobile.text = MyModulePreference.getInstance().mobile

                    progress.show()
            presenter.getCheckInfo(driverInfo.driver.cuuid)
        }
    }

    @Subscribe
    fun loadData(any: Any) {
        when {
            any::class == QiNiuTokenEntity::class -> getQiNiuToken(any)
            any::class == GetCheckInfo::class -> getCheckInfo(any)
            any::class == CommEntity::class -> checkInfo(any)
        }
    }

    private fun checkInfo(any: Any) {
        val data = any as CommEntity
        if (data.rspCode == "00") {
            ShowToast.showCenter(this, "提交成功")
            finish()
        } else {
            ShowToast.showCenter(this, data.rspDesc)
        }
        progress.dismiss()
    }

    private fun getCheckInfo(any: Any) {
        val data = any as GetCheckInfo
        if (data.rspCode == "00") {
            tvCarBrand.text = data.carBrand
            tvCarNo.text = data.carNo
            if (data.driverCheckInfo != null) {
                tvStatus.text = DriverCheckInfoStatus.getDesc(data.driverCheckInfo.status)
                status = data.driverCheckInfo.status
                if (data.driverCheckInfo.status in (0..1)) {
                    btnSubmit.visibility = View.GONE
                }
                urlFront = Constants.pictureIp + data.driverCheckInfo.imgIdface
                urlBack = Constants.pictureIp + data.driverCheckInfo.imgIdback
                urlDriver = Constants.pictureIp + data.driverCheckInfo.imgDriverlicense
                urlDriving = Constants.pictureIp + data.driverCheckInfo.imgVehiclelicense
                urlInsurance = Constants.pictureIp + data.driverCheckInfo.imgInsurance
                urlDriverWyc =Constants.pictureIp + data.driverCheckInfo.img_wycdriver
                urlCarWyc =Constants.pictureIp + data.driverCheckInfo.img_wyccar

                Glide.with(this).load(urlFront + "?${Constants.pictureSmall}").into(imgUpload[0])
                Glide.with(this).load(urlBack + "?${Constants.pictureSmall}").into(imgUpload[1])
                Glide.with(this).load(urlDriver + "?${Constants.pictureSmall}").into(imgUpload[2])
                Glide.with(this).load(urlDriving + "?${Constants.pictureSmall}").into(imgUpload[3])
                Glide.with(this).load(urlInsurance + "?${Constants.pictureSmall}").into(imgUpload[4])
                Glide.with(this).load(urlDriverWyc + "?${Constants.pictureSmall}").into(imgUpload[5])
                Glide.with(this).load(urlCarWyc + "?${Constants.pictureSmall}").into(imgUpload[6])
            }
            rlMain.visibility = View.VISIBLE
        } else {
            ShowToast.showCenter(this, data.rspDesc)
        }
        progress.dismiss()

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
            if (checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    val message = MessageDialog(this)
                    message.setMessageText("您的权限申请失败，请前往应用信息打开相机权限")
                    message.setOnOkClickListener {
                        val packageURI = Uri.parse("package:" + this@DriverCarRegistrationActivity.packageName)
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

    private fun clearIndexImage(index: Int) {
        imgUpload[index].setImageResource(R.drawable.ic_add)
        when (index) {
            0 -> {
                bitFront = null
                imgFrontUri = null
            }
            1 -> {
                bitBack = null
                imgBackUri = null
            }
            2 -> {
                bitDriver = null
                imgDriverUri = null
            }
            3 -> {
                bitDriving = null
                imgDrivingUri = null
            }
            4 -> {
                bitInsurance = null
                imgInsuranceUri = null
            }
            5 -> {
                bitDriverWyc = null
                imgDriverWycUri = null
            }
            6 -> {
                bitCarWyc = null
                imgCarWycUri = null
            }
        }
    }
    private val handler = Handler(Handler.Callback {
        if (it.what == 1) {
            imgTmp.setImageBitmap(BitmapUtil.compressUri(this, outputUri, CROP, CROP))
            when (imgTmp) {
                imgUpload[0] -> {
                    bitFront = BitmapUtil.compressUri(this, outputUri, 720, 1080)
                    imgFrontUri = outputUri
                }
                imgUpload[1] -> {
                    bitBack = BitmapUtil.compressUri(this, outputUri, 720, 1080)
                    imgBackUri = outputUri
                }

                imgUpload[2] -> {
                    bitDriver = BitmapUtil.compressUri(this, outputUri, 720, 1080)
                    imgDriverUri = outputUri
                }

                imgUpload[3] -> {
                    bitDriving = BitmapUtil.compressUri(this, outputUri, 720, 1080)
                    imgDrivingUri = outputUri
                }

                imgUpload[4] -> {
                    bitInsurance = BitmapUtil.compressUri(this, outputUri, 720, 1080)
                    imgInsuranceUri = outputUri
                }

                imgUpload[5] -> {
                    bitDriverWyc = BitmapUtil.compressUri(this, outputUri, 720, 1080)
                    imgDriverWycUri = outputUri
                }

                imgUpload[6] -> {
                    bitCarWyc = BitmapUtil.compressUri(this, outputUri, 720, 1080)
                    imgCarWycUri = outputUri
                }
            }
        } else if (it.what == 2) {
            progress.setLabel("上传中")
            progress.setDetailsLabel("身份证正面照上传中...")
            keyFront = getKey()
            upload(bitFront!!, keyFront, token, 0)
        } else if (it.what == 3) {
            progress.setDetailsLabel("身份证正反面上传中...")
            keyBack = getKey()
            upload(bitBack!!, keyBack, token, 1)
        } else if (it.what == 4) {
            progress.setDetailsLabel("驾驶证上传中...")
            keyDriver = getKey()
            upload(bitDriver!!, keyDriver, token, 2)
        } else if (it.what == 5) {
            progress.setDetailsLabel("行驶证上传中...")
            keyDriving = getKey()
            upload(bitDriving!!, keyDriving, token, 3)
        } else if (it.what == 6) {
            progress.setDetailsLabel("保险单上传中...")
            keyInsurance = getKey()
            upload(bitInsurance!!, keyInsurance, token, 4)
        }else if (it.what == 7){
            progress.setDetailsLabel("网约车人证上传中...")
            keyDriverWyc = getKey()
            upload(bitDriverWyc!!, keyDriverWyc, token, 5)
        }else if (it.what == 8){
            progress.setDetailsLabel("网约车车证上传中...")
            keyCarWyc = getKey()
            upload(bitCarWyc!!, keyCarWyc, token, 6)
        }else if (it.what == 9) {
            progress.dismiss()
            ShowToast.showCenter(this, "上传完成")
            progress.setLabel("请稍等")
            progress.setDetailsLabel("请求中")
            presenter.checkInfo(driverInfo.driver.cuuid, tvDriverName.text.toString(), tvMobile.text.toString(), tvCarBrand.text.toString(),
                    tvCarNo.text.toString(), driverInfo.carLevel, keyFront, keyBack, keyDriver, keyDriving, keyInsurance,keyDriverWyc,keyCarWyc)
        } else if (it.what == 10) {
            progress.dismiss()
        }
        true
    })

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
            this@DriverCarRegistrationActivity.handler.sendEmptyMessage(index + 3)
        }

        override fun uploadFail() {
            this@DriverCarRegistrationActivity.handler.sendEmptyMessage(10)
        }

        override fun uploadProgress(percent: Double) {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.unregister(this)
    }
}
