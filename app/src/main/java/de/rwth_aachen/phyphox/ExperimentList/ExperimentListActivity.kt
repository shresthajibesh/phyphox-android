package de.rwth_aachen.phyphox.ExperimentList

import android.Manifest
import android.annotation.SuppressLint
import android.app.ComponentCaller
import android.app.ProgressDialog
import android.bluetooth.BluetoothDevice
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Typeface
import android.hardware.Sensor
import android.hardware.SensorManager
import android.hardware.camera2.CameraCharacteristics
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager.BadTokenException
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.preference.PreferenceManager
import com.google.zxing.integration.android.IntentIntegrator
import dagger.hilt.android.AndroidEntryPoint
import de.rwth_aachen.phyphox.Bluetooth.BluetoothExperimentLoader
import de.rwth_aachen.phyphox.Bluetooth.BluetoothExperimentLoader.BluetoothExperimentLoaderCallback
import de.rwth_aachen.phyphox.Bluetooth.BluetoothScanDialog.BluetoothDeviceInfo
import de.rwth_aachen.phyphox.Experiment
import de.rwth_aachen.phyphox.ExperimentList.datasource.AssetExperimentLoader
import de.rwth_aachen.phyphox.ExperimentList.datasource.ExperimentRepository
import de.rwth_aachen.phyphox.ExperimentList.handler.BluetoothScanner
import de.rwth_aachen.phyphox.ExperimentList.handler.BluetoothScanner.BluetoothScanListener
import de.rwth_aachen.phyphox.ExperimentList.handler.CopyIntentHandler
import de.rwth_aachen.phyphox.ExperimentList.handler.SimpleExperimentCreator
import de.rwth_aachen.phyphox.ExperimentList.handler.ZipIntentHandler
import de.rwth_aachen.phyphox.ExperimentList.model.Const
import de.rwth_aachen.phyphox.ExperimentList.model.ExperimentListEnvironment
import de.rwth_aachen.phyphox.ExperimentList.model.ExperimentLoadInfoData
import de.rwth_aachen.phyphox.ExperimentList.viewmodel.ExperimentListViewModel
import de.rwth_aachen.phyphox.Helper.Helper
import de.rwth_aachen.phyphox.Helper.ReportingScrollView
import de.rwth_aachen.phyphox.Helper.WindowInsetHelper
import de.rwth_aachen.phyphox.PhyphoxFile
import de.rwth_aachen.phyphox.R
import de.rwth_aachen.phyphox.SensorInput
import de.rwth_aachen.phyphox.SettingsActivity.SettingsActivity
import de.rwth_aachen.phyphox.SettingsActivity.SettingsFragment
import de.rwth_aachen.phyphox.camera.depth.DepthInput
import de.rwth_aachen.phyphox.camera.helper.CameraHelper.getCamera2FormattedCaps
import de.rwth_aachen.phyphox.databinding.ActivityExperimentListBinding
import de.rwth_aachen.phyphox.databinding.DonotshowagainBinding
import de.rwth_aachen.phyphox.databinding.LayoutCreditsBinding
import de.rwth_aachen.phyphox.databinding.NewExperimentBinding
import de.rwth_aachen.phyphox.databinding.OpenMultipeDialogBinding
import de.rwth_aachen.phyphox.databinding.SupportPhyphoxHintBinding
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.Locale
import java.util.UUID
import java.util.regex.Pattern
import java.util.zip.CRC32
import javax.inject.Inject
import kotlin.getValue

@AndroidEntryPoint
class ExperimentListActivity : AppCompatActivity() {

    var progress: ProgressDialog? = null

    var bluetoothExperimentLoader: BluetoothExperimentLoader? = null
    var currentQRcrc32: Long = -1
    var currentQRsize: Int = -1
    var currentQRdataPackets: Array<ByteArray?>? = null

    var newExperimentDialogOpen: Boolean = false

    var popupWindow: PopupWindow? = null


    @Inject
    lateinit var experimentRepository: ExperimentRepository

    private val viewModel: ExperimentListViewModel by viewModels()

    // region - old click listeners
    val neocl = View.OnClickListener { _: View? ->
        if (newExperimentDialogOpen) hideNewExperimentDialog()
        else showNewExperimentDialog()
    }
    val neoclSimple = View.OnClickListener { _: View? ->
        hideNewExperimentDialog()
        openSimpleExperimentConfigurationDialog(this)
    }
    val neoclBluetooth = View.OnClickListener { _: View? ->
        hideNewExperimentDialog()
        val bluetoothNameKeySet = experimentRepository.getBluetoothDeviceNameList().keys
        val bluetoothUUIDKeySet = experimentRepository.getBluetoothDeviceUUIDList().keys
        BluetoothScanner(
            this,
            bluetoothNameKeySet,
            bluetoothUUIDKeySet,
            object : BluetoothScanListener {
                override fun onBluetoothDeviceFound(result: BluetoothDeviceInfo) {
                    openBluetoothExperiments(
                        result.device,
                        result.uuids,
                        result.phyphoxService,
                    )
                }

                override fun onBluetoothScanError(
                    msg: String?,
                    isError: Boolean?,
                    isFatal: Boolean?,
                ) {
                    showBluetoothScanError(
                        msg = getString(R.string.bt_android_version),
                        isError = true,
                        isFatal = true,
                    )
                }
            },
        ).execute()
    }
    val neoclQR = View.OnClickListener { _: View? ->
        hideNewExperimentDialog()
        scanQRCode()
    }

    val onScrollChangedListener: ReportingScrollView.OnScrollChangedListener =
        ReportingScrollView.OnScrollChangedListener { scrollView, _, y, _, _ ->
            val bottom = scrollView!!.getChildAt(scrollView.childCount - 1).bottom
            if (y + 10 > bottom - scrollView.height) {
                scrollView.setOnScrollChangedListener(null)
                val settings = getSharedPreferences(Const.PREFS_NAME, 0)
                settings.edit {
                    putString("lastSupportHint", Const.phyphoxCatHintRelease)
                }
            }
        }
    //endregion

    // region - view bindings
    private lateinit var binding: ActivityExperimentListBinding
    private val creditsLayout by lazy { LayoutCreditsBinding.inflate(layoutInflater) }
    private val supportHintBinding by lazy { SupportPhyphoxHintBinding.inflate(layoutInflater) }

    private val openMultipleDialogBinding by lazy { OpenMultipeDialogBinding.inflate(layoutInflater) }
    private val doNotShowaAgainBinding by lazy { DonotshowagainBinding.inflate(layoutInflater) }

    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        val themePreference: String = PreferenceManager.getDefaultSharedPreferences(this).getString(
            getString(R.string.setting_dark_mode_key),
            SettingsFragment.DARK_MODE_ON,
        )!!
        SettingsFragment.setApplicationTheme(themePreference)

        super.onCreate(savedInstanceState)
        binding = ActivityExperimentListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!displayDoNotDamageYourPhone()) { //Show the do-not-damage-your-phone-warning
            showSupportHintIfRequired()
        }
        viewModel.ld()
        setWindowInsets()

        setUpOnClickListener()

        handleIntent(intent)
    }

    private fun setWindowInsets() {
        WindowInsetHelper.setInsets(
            binding.experimentList,
            WindowInsetHelper.ApplyTo.PADDING,
            WindowInsetHelper.ApplyTo.IGNORE,
            WindowInsetHelper.ApplyTo.PADDING,
            WindowInsetHelper.ApplyTo.PADDING,
        )
        WindowInsetHelper.setInsets(
            binding.expListHeader,
            WindowInsetHelper.ApplyTo.PADDING,
            WindowInsetHelper.ApplyTo.PADDING,
            WindowInsetHelper.ApplyTo.PADDING,
            WindowInsetHelper.ApplyTo.IGNORE,
        )
        WindowInsetHelper.setInsets(
            binding.newExperiment,
            WindowInsetHelper.ApplyTo.IGNORE,
            WindowInsetHelper.ApplyTo.IGNORE,
            WindowInsetHelper.ApplyTo.MARGIN,
            WindowInsetHelper.ApplyTo.MARGIN,
        )
    }

    override fun onResume() {
        super.onResume()
        experimentRepository.loadAndShowMainExperimentList(this)
    }

    override fun onUserInteraction() {
        if (popupWindow != null) popupWindow!!.dismiss()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray,
        deviceId: Int,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            this.recreate()
        }
    }

    private fun setUpOnClickListener() {
        binding.credits.setOnClickListener {
            showPopupMenu(it)
        }
        binding.newExperiment.setOnClickListener(neocl)
        binding.experimentListDimmer.setOnClickListener(neocl)
        binding.newExperimentSimple.setOnClickListener(neoclSimple)
        binding.newExperimentSimpleLabel.setOnClickListener(neoclSimple)
        binding.newExperimentBluetooth.setOnClickListener(neoclBluetooth)
        binding.newExperimentBluetoothLabel.setOnClickListener(neoclBluetooth)
        binding.newExperimentQR.setOnClickListener(neoclQR)
        binding.newExperimentQRLabel.setOnClickListener(neoclQR)
        binding.experimentScroller.setOnScrollChangedListener(onScrollChangedListener)
    }

    private fun showPopupMenu(v: View) {
        val wrapper: Context = ContextThemeWrapper(this@ExperimentListActivity, R.style.Theme_Phyphox_DayNight)
        val popup = PopupMenu(wrapper, v)
        popup.setOnMenuItemClickListener { item: MenuItem? ->
            when (item?.itemId) {
                R.id.action_privacy -> {
                    openLink(getString(R.string.privacyPolicyURL))
                    return@setOnMenuItemClickListener true
                }

                R.id.action_credits -> {
                    openCreditDialog()
                    return@setOnMenuItemClickListener true
                }

                R.id.action_helpExperiments -> {
                    openLink(getString(R.string.experimentsPhyphoxOrgURL))
                    return@setOnMenuItemClickListener true
                }

                R.id.action_helpFAQ -> {
                    openLink(getString(R.string.faqPhyphoxOrgURL))
                    return@setOnMenuItemClickListener true
                }

                R.id.action_helpRemote -> {
                    openLink(getString(R.string.remotePhyphoxOrgURL))
                    return@setOnMenuItemClickListener true
                }

                R.id.action_settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    return@setOnMenuItemClickListener true
                }

                R.id.action_deviceInfo -> {
                    openDeviceInfoDialog()
                    return@setOnMenuItemClickListener true
                }

                else -> {
                    return@setOnMenuItemClickListener false
                }
            }
        }
        popup.inflate(R.menu.menu_help)
        popup.show()
    }

    private fun openLink(urlString: String?) {
        urlString?.toUri()?.let { uri ->
            val intent = Intent(Intent.ACTION_VIEW, uri)
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }

    }

    private fun openDeviceInfoDialog() {
        val sb = StringBuilder()
        val pInfo = try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
        } catch (_: Exception) {
            null
        }

        if (Helper.isDarkTheme(resources)) {
            sb.append(" <font color='white'")
        } else {
            sb.append(" <font color='black'")
        }

        sb.append("<b>phyphox</b><br />")
        if (pInfo != null) {
            sb.append("Version: ")
            sb.append(pInfo.versionName)
            sb.append("<br />")
            sb.append("Build: ")
            sb.append(pInfo.versionCode)
            sb.append("<br />")
        } else {
            sb.append("Version: Unknown<br />")
            sb.append("Build: Unknown<br />")
        }
        sb.append("File format: ")
        sb.append(PhyphoxFile.phyphoxFileVersion)
        sb.append("<br /><br />")

        sb.append("<b>Permissions</b><br />")
        if (pInfo != null && pInfo.requestedPermissions != null) {
            for (i in (pInfo.requestedPermissions as Array<out Any?>).indices) {
                sb.append(
                    if (pInfo.requestedPermissions!![i].startsWith("android.permission.")) pInfo.requestedPermissions!![i].substring(
                        19,
                    ) else pInfo.requestedPermissions!![i],
                )
                sb.append(": ")
                sb.append(if ((pInfo.requestedPermissionsFlags!![i] and PackageInfo.REQUESTED_PERMISSION_GRANTED) == 0) "no" else "yes")
                sb.append("<br />")
            }
        } else {
            if (pInfo == null) sb.append("Unknown<br />")
            else sb.append("None<br />")
        }
        sb.append("<br />")

        sb.append("<b>Device</b><br />")
        sb.append("Model: ")
        sb.append(Build.MODEL)
        sb.append("<br />")
        sb.append("Brand: ")
        sb.append(Build.BRAND)
        sb.append("<br />")
        sb.append("Board: ")
        sb.append(Build.DEVICE)
        sb.append("<br />")
        sb.append("Manufacturer: ")
        sb.append(Build.MANUFACTURER)
        sb.append("<br />")
        sb.append("ABIS: ")
        for (i in Build.SUPPORTED_ABIS.indices) {
            if (i > 0) sb.append(", ")
            sb.append(Build.SUPPORTED_ABIS[i])
        }
        sb.append("<br />")
        sb.append("Base OS: ")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            sb.append(Build.VERSION.BASE_OS)
        } else {
            sb.append("API < 23")
        }
        sb.append("<br />")
        sb.append("Codename: ")
        sb.append(Build.VERSION.CODENAME)
        sb.append("<br />")
        sb.append("Release: ")
        sb.append(Build.VERSION.RELEASE)
        sb.append("<br />")
        sb.append("Patch: ")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            sb.append(Build.VERSION.SECURITY_PATCH)
        } else {
            sb.append("API < 23")
        }
        sb.append("<br /><br />")

        sb.append("<b>Sensors</b><br /><br />")
        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager?
        if (sensorManager == null) {
            sb.append("Unkown<br />")
        } else {
            for (sensor in sensorManager.getSensorList(Sensor.TYPE_ALL)) {
                sb.append("<b>")
                sb.append(getString(SensorInput.getDescriptionRes(sensor.type)))
                sb.append("</b> (type ")
                sb.append(sensor.type)
                sb.append(")")
                sb.append("<br />")
                sb.append("- Name: ")
                sb.append(sensor.name)
                sb.append("<br />")
                sb.append("- Reporting Mode: ")
                sb.append(sensor.reportingMode)
                sb.append("<br />")
                sb.append("- Range: ")
                sb.append(sensor.maximumRange)
                sb.append(" ")
                sb.append(SensorInput.getUnit(sensor.type))
                sb.append("<br />")
                sb.append("- Resolution: ")
                sb.append(sensor.resolution)
                sb.append(" ")
                sb.append(SensorInput.getUnit(sensor.type))
                sb.append("<br />")
                sb.append("- Min delay: ")
                sb.append(sensor.minDelay)
                sb.append(" µs")
                sb.append("<br />")
                sb.append("- Max delay: ")
                sb.append(sensor.maxDelay)
                sb.append(" µs")
                sb.append("<br />")
                sb.append("- Power: ")
                sb.append(sensor.power)
                sb.append(" mA")
                sb.append("<br />")
                sb.append("- Vendor: ")
                sb.append(sensor.vendor)
                sb.append("<br />")
                sb.append("- Version: ")
                sb.append(sensor.version)
                sb.append("<br /><br />")
            }
        }
        sb.append("<br /><br />")

        sb.append("<b>Cameras</b><br /><br />")
        sb.append("<b>Depth sensors</b><br />")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            sb.append("- Depth sensors front: ")
            val depthFront = DepthInput.countCameras(CameraCharacteristics.LENS_FACING_FRONT)
            sb.append(depthFront)
            sb.append("<br />")
            sb.append("- Max resolution front: ")
            sb.append(if (depthFront > 0) DepthInput.getMaxResolution(CameraCharacteristics.LENS_FACING_FRONT) else "-")
            sb.append("<br />")
            sb.append("- Max frame rate front: ")
            sb.append(if (depthFront > 0) DepthInput.getMaxRate(CameraCharacteristics.LENS_FACING_FRONT) else "-")
            sb.append("<br />")
            sb.append("- Depth sensors back: ")
            val depthBack = DepthInput.countCameras(CameraCharacteristics.LENS_FACING_FRONT)
            sb.append(depthBack)
            sb.append("<br />")
            sb.append("- Max resolution back: ")
            sb.append(if (depthBack > 0) DepthInput.getMaxResolution(CameraCharacteristics.LENS_FACING_BACK) else "-")
            sb.append("<br />")
            sb.append("- Max frame rate back: ")
            sb.append(if (depthBack > 0) DepthInput.getMaxRate(CameraCharacteristics.LENS_FACING_BACK) else "-")
            sb.append("<br />")
        } else {
            sb.append("API < 23")
        }
        sb.append("<br /><br />")

        sb.append("<b>Camera 2 API</b><br />")
        sb.append(getCamera2FormattedCaps(false))
        sb.append("</font>")

        val text = Html.fromHtml(sb.toString())
        val ctw = ContextThemeWrapper(this@ExperimentListActivity, R.style.Theme_Phyphox_DayNight)
        val builder = AlertDialog.Builder(ctw)
        builder.setMessage(text).setTitle(R.string.deviceInfo).setPositiveButton(
            R.string.copyToClipboard,
        ) { _, _ -> //Copy the device info to the clipboard and notify the user

            val cm = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val data = ClipData.newPlainText(getString(R.string.deviceInfo), text)
            cm.setPrimaryClip(data)

            Toast.makeText(
                this@ExperimentListActivity,
                getString(R.string.deviceInfoCopied),
                Toast.LENGTH_SHORT,
            ).show()
        }.setNegativeButton(
            R.string.close,
        ) { _, _ ->
            //Closed by user. Nothing to do.
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun openCreditDialog() {

        //Create the credits as an AlertDialog
        val ctw = ContextThemeWrapper(this@ExperimentListActivity, R.style.Theme_Phyphox)
        val credits = AlertDialog.Builder(ctw)


        val creditsNamesSpannable = SpannableStringBuilder()
        var first = true
        for (line in getString(R.string.creditsNames).split("\\n".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()) {
            if (first) first = false
            else creditsNamesSpannable.append("\n")
            creditsNamesSpannable.append(line.trim { it <= ' ' })
        }
        val matcher = Pattern.compile("^.*:$", Pattern.MULTILINE).matcher(creditsNamesSpannable)
        while (matcher.find()) {
            creditsNamesSpannable.setSpan(
                StyleSpan(Typeface.BOLD),
                matcher.start(),
                matcher.end(),
                Spanned.SPAN_INCLUSIVE_INCLUSIVE,
            )
        }
        creditsLayout.creditNames.text = creditsNamesSpannable

        //The following texts are not translateable. Get them from the basic English version.
        var conf = resources.configuration
        conf = Configuration(conf)
        conf.setLocale(Locale.ENGLISH)
        val localizedRes = baseContext.createConfigurationContext(conf).resources

        creditsLayout.creditsApache.text = Html.fromHtml(localizedRes.getString(R.string.creditsApache))
        creditsLayout.creditsZxing.text = Html.fromHtml(localizedRes.getString(R.string.creditsZxing))
        creditsLayout.creditsPahoMQTT.text = Html.fromHtml(localizedRes.getString(R.string.creditsPahoMQTT))

        credits.setView(creditsLayout.root)
        credits.setPositiveButton(
            getText(R.string.close),
        ) { _, _ -> }

        //Present the dialog
        credits.show()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showSupportHint() {
        if (popupWindow != null) return
        supportHintBinding.supportPhyphoxHintText.text = getString(R.string.categoryPhyphoxOrgHint)
        supportHintBinding.supportPhyphoxHintArrow.apply {
            val layoutParams = this.layoutParams as LinearLayout.LayoutParams
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL
        }
        popupWindow = PopupWindow(
            supportHintBinding.root,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
        ).apply {
            elevation = 4.0f
            isOutsideTouchable = false
            isTouchable = false
            isFocusable = false
        }
        supportHintBinding.supportPhyphoxHintRoot.setOnTouchListener { _: View?, _: MotionEvent? ->
            if (popupWindow != null) popupWindow!!.dismiss()
            true
        }

        popupWindow!!.setOnDismissListener { popupWindow = null }
        binding.rootExperimentList.post {
            try {
                popupWindow!!.showAtLocation(
                    binding.rootExperimentList,
                    Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,
                    0,
                    0,
                )
            } catch (_: BadTokenException) {
                Log.e(
                    "showHint",
                    "Bad token when showing hint. This is not unusual when app is rotating while showing the hint.",
                )
            }
        }
    }

    private fun showSupportHintIfRequired() {
        try {
            if (packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_PERMISSIONS,
                ).versionName!!.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()[0] != Const.phyphoxCatHintRelease
            ) return
        } catch (_: Exception) {
            return
        }

        val settings = getSharedPreferences(Const.PREFS_NAME, 0)
        val lastSupportHint: String = settings.getString("lastSupportHint", "")!!
        if (lastSupportHint == Const.phyphoxCatHintRelease) {
            return
        }

        showSupportHint()
    }


    fun showError(error: String?) {
        if (progress != null) progress!!.dismiss()
        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
    }

    fun zipReady(result: String, preselectedDevice: BluetoothDevice?) {
        if (progress != null) progress!!.dismiss()
        if (result.isEmpty()) {
            val tempPath = File(filesDir, "temp_zip")
            val extensions = arrayOf<String?>("phyphox")
            val files: List<File> = tempPath.walkTopDown().filter { it.isFile }.filter { file ->
                extensions.isEmpty() || extensions.any { ext -> file.extension.equals(ext, ignoreCase = true) }
            }.toList()
            if (files.isEmpty()) {
                Toast.makeText(
                    this,
                    "Error: There is no valid phyphox experiment in this zip file.",
                    Toast.LENGTH_LONG,
                ).show()
            } else if (files.size == 1) {
                //Create an intent for this file
                val intent = Intent(this, Experiment::class.java)
                intent.setData(Uri.fromFile(files.iterator().next()))
                if (preselectedDevice != null) intent.putExtra(
                    Const.EXPERIMENT_PRESELECTED_BLUETOOTH_ADDRESS,
                    preselectedDevice.address,
                )
                intent.putExtra(Const.EXPERIMENT_ISTEMP, "temp_zip")
                intent.setAction(Intent.ACTION_VIEW)

                //Open the file
                startActivity(intent)
            } else {
                //Load experiments from local files
                val zipRepository = ExperimentRepository()
                for (file in files) {
                    //Load details for each experiment
                    try {
                        val input: InputStream = FileInputStream(file)
                        val data = ExperimentLoadInfoData(
                            input,
                            tempPath.toURI().relativize(file.toURI()).getPath(),
                            "temp_zip",
                            false,
                        )
                        val shortInfo = AssetExperimentLoader.loadExperimentShortInfo(
                            data,
                            ExperimentListEnvironment(this),
                        )
                        if (shortInfo != null) {
                            zipRepository.addExperiment(shortInfo, this)
                        }
                        //loadExperimentInfo(input, tempPath.toURI().relativize(file.toURI()).getPath(), "temp_zip", false, zipExperiments, null, null);
                        input.close()
                    } catch (e: IOException) {
                        Log.e("zip", e.message!!)
                        Toast.makeText(
                            this,
                            "Error: Could not load experiment \"$file\" from zip file.",
                            Toast.LENGTH_LONG,
                        ).show()
                    }
                }


                val builder =
                    AlertDialog.Builder(this).setTitle(R.string.open_zip_title).setView(openMultipleDialogBinding.root)
                        .setPositiveButton(R.string.open_save_all) { dialog: DialogInterface, _: Int ->
                            zipRepository.saveExperimentsToMainList(ExperimentListEnvironment(this))
                            experimentRepository.loadAndShowMainExperimentList(this)
                            dialog.dismiss()
                        }.setNegativeButton(R.string.cancel) { dialog: DialogInterface, _: Int ->
                            dialog.dismiss()
                        }
                val dialog = builder.create()

                openMultipleDialogBinding.openMultipleDialogInstructions.setText(R.string.open_zip_dialog_instructions)
                zipRepository.addExperimentCategoriesToLinearLayout(
                    /* target = */ this.openMultipleDialogBinding.openMultipleDialogList,
                    /* res = */ this.getResources(),
                )
                dialog.show()
            }
        } else {
            Toast.makeText(this@ExperimentListActivity, result, Toast.LENGTH_LONG).show()
        }
    }

    fun loadExperimentFromBluetoothDevice(device: BluetoothDevice) {
        val parent = this
        if (bluetoothExperimentLoader == null) {
            bluetoothExperimentLoader = BluetoothExperimentLoader(
                baseContext,
                object : BluetoothExperimentLoaderCallback {
                    override fun updateProgress(transferred: Int, total: Int) {
                        parent.runOnUiThread {
                            if (total > 0) {
                                if (progress!!.isIndeterminate()) {
                                    progress!!.dismiss()
                                    progress = ProgressDialog(parent)
                                    progress!!.setTitle(getString(R.string.loadingTitle))
                                    progress!!.setMessage(getString(R.string.loadingText))
                                    progress!!.isIndeterminate = false
                                    progress!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                                    progress!!.setCancelable(true)
                                    progress!!.setOnCancelListener { if (bluetoothExperimentLoader != null) bluetoothExperimentLoader!!.cancel() }
                                    progress!!.progress = transferred
                                    progress!!.max = total
                                    progress!!.show()
                                } else {
                                    progress!!.progress = transferred
                                }
                            }
                        }
                    }

                    override fun dismiss() {
                        parent.runOnUiThread { progress!!.dismiss() }
                    }

                    override fun error(msg: String?) {
                        dismiss()
                        showBluetoothExperimentReadError(msg, device)
                    }

                    override fun success(experimentUri: Uri?, isZip: Boolean) {
                        dismiss()
                        val intent = Intent(parent, Experiment::class.java)
                        intent.setData(experimentUri)
                        intent.setAction(Intent.ACTION_VIEW)
                        if (isZip) {
                            ZipIntentHandler(intent, parent, device).execute()
                        } else {
                            intent.putExtra(
                                Const.EXPERIMENT_PRESELECTED_BLUETOOTH_ADDRESS,
                                device.address,
                            )
                            startActivity(intent)
                        }
                    }
                },
            )
        }
        progress = ProgressDialog.show(
            this, getString(R.string.loadingTitle),
            getString(
                R.string.loadingText,
            ),
            true, true,
        ) { if (bluetoothExperimentLoader != null) bluetoothExperimentLoader!!.cancel() }
        bluetoothExperimentLoader!!.loadExperimentFromBluetoothDevice(device)
    }

    fun handleIntent(intent: Intent) {
        if (progress != null) progress!!.dismiss()

        val scheme = intent.scheme ?: return
        var isZip: Boolean
        if (scheme == ContentResolver.SCHEME_FILE) {
            if (!intent.data!!.path!!.startsWith(filesDir.path) && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //Android 6.0: No permission? Request it!
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    0,
                )
                //We will stop here. If the user grants the permission, the permission callback will restart the action with the same intent
                return
            }
            val uri = intent.data

            val data = ByteArray(4)
            val inputStream: InputStream?
            try {
                inputStream = this.contentResolver.openInputStream(uri!!)
                if (inputStream!!.read(data, 0, 4) < 4) {
                    Toast.makeText(this, "Error: File truncated.", Toast.LENGTH_LONG).show()
                    inputStream.close()
                    return
                }
                inputStream.close()
            } catch (_: FileNotFoundException) {
                Toast.makeText(this, "Error: File not found.", Toast.LENGTH_LONG).show()
                return
            } catch (_: IOException) {
                Toast.makeText(this, "Error: IOException.", Toast.LENGTH_LONG).show()
                return
            }

            isZip =
                (data[0].toInt() == 0x50 && data[1].toInt() == 0x4b && data[2].toInt() == 0x03 && data[3].toInt() == 0x04)

            if (!isZip) {
                //This is just a single experiment - Start the Experiment activity and let it handle the intent
                val forwardedIntent = Intent(intent)
                forwardedIntent.setClass(this, Experiment::class.java)
                this.startActivity(forwardedIntent)
            } else {
                //We got a zip-file. Let's see what's inside...
                progress = ProgressDialog.show(
                    this, getString(R.string.loadingTitle),
                    getString(
                        R.string.loadingText,
                    ),
                    true,
                )
                ZipIntentHandler(intent, this).execute()
            }
        } else if (scheme == ContentResolver.SCHEME_CONTENT || scheme == "phyphox" || scheme == "http" || scheme == "https") {
            progress = ProgressDialog.show(
                this, getString(R.string.loadingTitle),
                getString(
                    R.string.loadingText,
                ),
                true,
            )
            CopyIntentHandler(intent, this).execute()
        }
    }

    fun showNewExperimentDialog() {
        newExperimentDialogOpen = true

        val rotate45In = AnimationUtils.loadAnimation(baseContext, R.anim.experiment_list_fab_rotate45)
        val fabIn = AnimationUtils.loadAnimation(baseContext, R.anim.experiment_list_fab_in)
        val labelIn = AnimationUtils.loadAnimation(baseContext, R.anim.experiment_list_label_in)
        val fadeDark = AnimationUtils.loadAnimation(baseContext, R.anim.experiment_list_fade_dark)

        binding.newExperiment.startAnimation(rotate45In)
        binding.newExperimentSimple.startAnimation(fabIn)
        binding.newExperimentSimpleLabel.startAnimation(labelIn)
        binding.newExperimentBluetooth.startAnimation(fabIn)
        binding.newExperimentBluetoothLabel.startAnimation(labelIn)
        binding.newExperimentQR.startAnimation(fabIn)
        binding.newExperimentQRLabel.startAnimation(labelIn)
        binding.experimentListDimmer.startAnimation(fadeDark)

        binding.newExperimentSimple.isClickable = true
        binding.newExperimentSimpleLabel.isClickable = true
        binding.newExperimentBluetooth.isClickable = true
        binding.newExperimentBluetoothLabel.isClickable = true
        binding.newExperimentQR.isClickable = true
        binding.newExperimentQRLabel.isClickable = true
        binding.experimentListDimmer.isClickable = true
    }

    fun hideNewExperimentDialog() {
        newExperimentDialogOpen = false

        val rotate0In = AnimationUtils.loadAnimation(baseContext, R.anim.experiment_list_fab_rotate0)
        val fabOut = AnimationUtils.loadAnimation(baseContext, R.anim.experiment_list_fab_out)
        val labelOut = AnimationUtils.loadAnimation(baseContext, R.anim.experiment_list_label_out)
        val fadeTransparent = AnimationUtils.loadAnimation(baseContext, R.anim.experiment_list_fade_transparent)

        binding.newExperimentSimple.isClickable = false
        binding.newExperimentSimpleLabel.isClickable = false
        binding.newExperimentBluetooth.isClickable = false
        binding.newExperimentBluetoothLabel.isClickable = false
        binding.newExperimentQR.isClickable = false
        binding.newExperimentQRLabel.isClickable = false
        binding.experimentListDimmer.isClickable = false

        binding.newExperiment.startAnimation(rotate0In)
        binding.newExperimentSimple.startAnimation(fabOut)
        binding.newExperimentSimpleLabel.startAnimation(labelOut)
        binding.newExperimentBluetooth.startAnimation(fabOut)
        binding.newExperimentBluetoothLabel.startAnimation(labelOut)
        binding.newExperimentQR.startAnimation(fabOut)
        binding.newExperimentQRLabel.startAnimation(labelOut)
        binding.experimentListDimmer.startAnimation(fadeTransparent)
    }

    fun scanQRCode() {
        val qrScan = IntentIntegrator(this)

        qrScan.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
        qrScan.setPrompt(getResources().getString(R.string.newExperimentQRscan))
        qrScan.setBeepEnabled(false)
        qrScan.setOrientationLocked(true)

        qrScan.initiateScan()
    }

    fun showQRScanError(msg: String?, isError: Boolean) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(msg).setTitle(if (isError) R.string.newExperimentQRErrorTitle else R.string.newExperimentQR)
            .setPositiveButton(
                if (isError) R.string.tryagain else R.string.doContinue,
            ) { _, _ -> scanQRCode() }.setNegativeButton(
                getString(R.string.cancel),
            ) { _, _ -> }
        this.runOnUiThread {
            val dialog = builder.create()
            dialog.show()
        }
    }

    fun showBluetoothExperimentReadError(msg: String?, device: BluetoothDevice) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(msg).setTitle(R.string.newExperimentBTReadErrorTitle).setPositiveButton(
            R.string.tryagain,
        ) { _, _ -> loadExperimentFromBluetoothDevice(device) }.setNegativeButton(
            getString(R.string.cancel),
        ) { _, _ -> }
        this.runOnUiThread {
            val dialog = builder.create()
            dialog.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?, caller: ComponentCaller) {
        super.onActivityResult(requestCode, resultCode, intent, caller)
        val scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent)
        val textResult: String = scanResult.contents
        if (scanResult != null) {
            if (textResult.lowercase(Locale.getDefault()).startsWith("http://") || textResult.lowercase(
                    Locale.getDefault(),
                ).startsWith("https://") || textResult.lowercase(Locale.getDefault()).startsWith("phyphox://")
            ) {
                //This is a URL, open it
                //Create an intent for this new file
                val urlIntent = Intent(this, Experiment::class.java)
                urlIntent.setData(
                    ("phyphox://" + textResult.split(
                        "//".toRegex(),
                        limit = 2,
                    ).toTypedArray()[1]).toUri(),
                )
                urlIntent.setAction(Intent.ACTION_VIEW)
                handleIntent(urlIntent)
            } else if (textResult.startsWith("phyphox")) {
                //The QR code contains the experiment itself. The first 13 bytes are:
                // p h y p h o x [crc32] [i] [n]
                //"phyphox" as string (7 bytes)
                //crc32 hash (big endian) of the submitted experiment (has to be the same for each qr code if the experiment is spread across multiple codes)
                //i is the index of this code in a sequence of n code (starting at zero, so i starts at 0 and end with n-1
                //n is the total number of codes for this experiment
                val data = intent.getByteArrayExtra("SCAN_RESULT_BYTE_SEGMENTS_0")
                if (data == null) {
                    Toast.makeText(
                        this@ExperimentListActivity,
                        "Unexpected error: Could not retrieve data from QR code.",
                        Toast.LENGTH_LONG,
                    ).show()
                    return
                }
                val crc32 =
                    (((data[7].toInt() and 0xff).toLong() shl 24) or ((data[8].toInt() and 0xff).toLong() shl 16) or ((data[9].toInt() and 0xff).toLong() shl 8) or ((data[10].toInt() and 0xff).toLong()))
                val index = data[11].toInt()
                val count = data[12].toInt()

                if ((currentQRcrc32 >= 0 && currentQRcrc32 != crc32) || (currentQRsize >= 0 && count != currentQRsize) || (currentQRsize in 0..index)) {
                    showQRScanError(getString(R.string.newExperimentQRcrcMismatch), true)
                    currentQRsize = -1
                    currentQRcrc32 = -1
                }
                if (currentQRcrc32 < 0) {
                    currentQRcrc32 = crc32
                    currentQRsize = count
                    currentQRdataPackets = arrayOfNulls(count)
                }
                currentQRdataPackets!![index] = data.copyOfRange(13, data.size)
                var missing = 0
                for (i in 0 downTo currentQRsize) {
                    if (currentQRdataPackets!![i] == null) missing++
                }
                if (missing == 0) {
                    //We have all the data. Write it to a temporary file and give it to our default intent handler...
                    val tempPath = File(filesDir, "temp_qr")
                    if (!tempPath.exists()) {
                        if (!tempPath.mkdirs()) {
                            showQRScanError(
                                "Could not create temporary directory to write zip file.",
                                true,
                            )
                            return
                        }
                    }
                    val files = tempPath.list()
                    for (file in files!!) {
                        if (!(File(tempPath, file).delete())) {
                            showQRScanError(
                                "Could not clear temporary directory to extract zip file.",
                                true,
                            )
                            return
                        }
                    }

                    var totalSize = 0

                    for (i in 0 downTo currentQRsize) {
                        totalSize += currentQRdataPackets!![i]!!.size
                    }
                    val dataReceived = ByteArray(totalSize)
                    var offset = 0
                    for (i in 0 downTo currentQRsize) {
                        System.arraycopy(
                            currentQRdataPackets!![i],
                            0,
                            dataReceived,
                            offset,
                            currentQRdataPackets!![i]!!.size,
                        )
                        offset += currentQRdataPackets!![i]!!.size
                    }

                    val crc32Received = CRC32()
                    crc32Received.update(dataReceived)
                    if (crc32Received.value != crc32) {
                        Log.e(
                            "qrscan",
                            "Received CRC32 " + crc32Received.value + " but expected " + crc32,
                        )
                        showQRScanError(getString(R.string.newExperimentQRBadCRC), true)
                        return
                    }

                    val zipData = Helper.inflatePartialZip(dataReceived)

                    val zipFile: File?
                    try {
                        zipFile = File(tempPath, "qr.zip")
                        val out = FileOutputStream(zipFile)
                        out.write(zipData)
                        out.close()
                    } catch (_: Exception) {
                        showQRScanError("Could not write QR content to zip file.", true)
                        return
                    }

                    currentQRsize = -1
                    currentQRcrc32 = -1

                    val zipIntent = Intent(this, Experiment::class.java)
                    zipIntent.setData(Uri.fromFile(zipFile))
                    zipIntent.setAction(Intent.ACTION_VIEW)
                    ZipIntentHandler(zipIntent, this).execute()
                } else {
                    showQRScanError(
                        getString(R.string.newExperimentQRCodesMissing1) + " " + currentQRsize + " " + getString(
                            R.string.newExperimentQRCodesMissing2,
                        ) + " " + missing,
                        false,
                    )
                }
            } else {
                //QR code does not contain or reference a phyphox experiment
                showQRScanError(getString(R.string.newExperimentQRNoExperiment), true)
            }
        }
    }

    @SuppressLint("MissingPermission") //TODO: The permission is actually checked when entering the entire BLE dialog and I do not see how we could reach this part of the code if it failed. However, I cannot rule out some other mechanism of revoking permissions during an app switch or from the notifications bar (?), so a cleaner implementation might be good idea
    fun openBluetoothExperiments(
        device: BluetoothDevice,
        uuids: MutableSet<UUID?>,
        phyphoxService: Boolean,
    ) {
        val parent = this

        val hiddenBluetoothRepository = ExperimentRepository()
        hiddenBluetoothRepository.loadHiddenBluetoothExperiments(this)

        val experiments: MutableSet<String?> = HashSet()
        if (device.getName() != null) {
            for (name in hiddenBluetoothRepository.getBluetoothDeviceNameList().keys) {
                if (device.getName().contains(name)) {
                    val experimentsForName = hiddenBluetoothRepository.getBluetoothDeviceNameList()[name]
                    if (experimentsForName != null) experiments.addAll(experimentsForName)
                }
            }
        }

        for (uuid in uuids) {
            val experimentsForUUID = hiddenBluetoothRepository.getBluetoothDeviceUUIDList()[uuid]
            if (experimentsForUUID != null) experiments.addAll(experimentsForUUID)
        }

        if (experiments.isEmpty() && phyphoxService) {
            //We do not have any experiments for this device, so there is no choice. Just load the experiment provided by the device.
            loadExperimentFromBluetoothDevice(device)
            return
        }

        val builder =
            AlertDialog.Builder(parent).setTitle(parent.getResources().getString(R.string.open_bluetooth_assets_title))
                .setView(openMultipleDialogBinding.root).setNegativeButton(
                    R.string.cancel,
                ) { dialog: DialogInterface?, _: Int -> dialog!!.dismiss() }

        if (!experiments.isEmpty()) {
            builder.setPositiveButton(
                R.string.open_save_all,
            ) { dialog, _ ->
                experimentRepository.saveExperimentsToMainList(ExperimentListEnvironment(parent))
                experimentRepository.loadAndShowMainExperimentList(parent)
                dialog.dismiss()
            }
        }

        var instructions = ""
        if (!experiments.isEmpty()) {
            instructions += getString(R.string.open_bluetooth_assets)
        }
        if (!experiments.isEmpty() && phyphoxService) instructions += "\n\n"
        if (phyphoxService) {
            instructions += getString(R.string.newExperimentBluetoothLoadFromDeviceInfo)
            builder.setNeutralButton(
                R.string.newExperimentBluetoothLoadFromDevice,
            ) { dialog, _ ->
                loadExperimentFromBluetoothDevice(device)
                dialog.dismiss()
            }
        }
        val dialog = builder.create()

        openMultipleDialogBinding.openMultipleDialogInstructions.text = instructions


        val assetManager = parent.assets
        for (file in experiments) {
            //Load details for each experiment
            try {
                val input = assetManager.open("experiments/$file")
                val data = ExperimentLoadInfoData(input, file, null, true)
                val shortInfo = AssetExperimentLoader.loadExperimentShortInfo(
                    data,
                    ExperimentListEnvironment(parent),
                )
                if (shortInfo != null) {
                    experimentRepository.addExperiment(shortInfo, this)
                }
                input.close()
            } catch (_: IOException) {
                Log.e(
                    "ExperimentList",
                    "Error: Could not load experiment \"$file\" from asset.",
                )
                Toast.makeText(
                    parent,
                    "Error: Could not load experiment \"$file\" from asset.",
                    Toast.LENGTH_LONG,
                ).show()
            }
        }

        experimentRepository.setPreselectedBluetoothAddress(device.address)
        experimentRepository.addExperimentCategoriesToLinearLayout(
            /* target = */ openMultipleDialogBinding.openMultipleDialogList,
            /* res = */ this.getResources(),
        )
        dialog.show()
    }

    fun showBluetoothScanError(msg: String?, isError: Boolean, isFatal: Boolean) {
        val parent = this

        val builder = AlertDialog.Builder(this)
        builder.setMessage(msg)
            .setTitle(if (isError) R.string.newExperimentBluetoothErrorTitle else R.string.newExperimentBluetooth)
        if (!isFatal) {
            builder.setPositiveButton(
                if (isError) R.string.tryagain else R.string.doContinue,
            ) { _, _ -> //(new RunBluetoothScan()).execute();
                val bluetoothNameKeySet = experimentRepository.getBluetoothDeviceNameList().keys
                val bluetoothUUIDKeySet = experimentRepository.getBluetoothDeviceUUIDList().keys

                BluetoothScanner(
                    parent,
                    bluetoothNameKeySet,
                    bluetoothUUIDKeySet,
                    object : BluetoothScanListener {
                        override fun onBluetoothDeviceFound(result: BluetoothDeviceInfo) {
                            openBluetoothExperiments(
                                result.device,
                                result.uuids,
                                result.phyphoxService,
                            )
                        }

                        override fun onBluetoothScanError(
                            msg: String?,
                            isError: Boolean?,
                            isFatal: Boolean?,
                        ) {
                            showBluetoothScanError(
                                msg = getString(R.string.bt_android_version),
                                isError = true,
                                isFatal = true,
                            )
                        }
                    },
                ).execute()
            }
        }
        builder.setNegativeButton(
            getString(R.string.cancel),
        ) { _, _ -> }
        runOnUiThread {
            val dialog = builder.create()
            dialog.show()
        }
    }

    //Displays a warning message that some experiments might damage the phone
    private fun displayDoNotDamageYourPhone(): Boolean {
        //Use the app theme and create an AlertDialog-builder
        val ctw = ContextThemeWrapper(this, R.style.Theme_Phyphox_DayNight)
        val adb =
            AlertDialog.Builder(ctw).setView(doNotShowaAgainBinding.root).setTitle(R.string.warning).setPositiveButton(
                    getText(R.string.ok),
                ) { _, _ -> //User clicked ok. Did the user decide to skip future warnings?
                    var skipWarning = false
                    if (doNotShowaAgainBinding.donotshowagain.isChecked) skipWarning = true

                    //Store user decision
                    getSharedPreferences(Const.PREFS_NAME, 0).edit {
                        putBoolean("skipWarning", skipWarning)
                    }
                }

        //Check preferences if the user does not want to see warnings
        val settings = getSharedPreferences(Const.PREFS_NAME, 0)
        val skipWarning = settings.getBoolean("skipWarning", false)
        if (!skipWarning) {
            adb.show() //User did not decide to skip, so show it.
            return true
        } else {
            return false
        }
    }

    //This displays a rather complex dialog to allow users to set up a simple experiment
    private fun openSimpleExperimentConfigurationDialog(context: Context?) {
        val ctw = ContextThemeWrapper(this, R.style.Theme_Phyphox_DayNight)
        val newExperimentBinding = NewExperimentBinding.inflate(layoutInflater)
        AlertDialog.Builder(ctw).setView(newExperimentBinding.root).setTitle(R.string.newExperiment).setPositiveButton(
                getText(R.string.ok),
            ) { _: DialogInterface?, _: Int ->
                val creator = SimpleExperimentCreator(context, newExperimentBinding.root)
                creator.generateAndOpenSimpleExperiment()
            }.setNegativeButton(
                getText(R.string.cancel),
            ) { _: DialogInterface?, _: Int -> }.show()
    }
}
