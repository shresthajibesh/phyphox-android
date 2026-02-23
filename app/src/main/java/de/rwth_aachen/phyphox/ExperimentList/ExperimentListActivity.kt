package de.rwth_aachen.phyphox.ExperimentList

import android.Manifest
import android.annotation.SuppressLint
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
import android.content.res.Resources
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
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.WindowManager.BadTokenException
import android.view.animation.AnimationUtils
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.zxing.integration.android.IntentIntegrator
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
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.Arrays
import java.util.Locale
import java.util.UUID
import java.util.regex.Pattern
import java.util.zip.CRC32

class ExperimentListActivity : AppCompatActivity() {
    //A resource reference for easy access
    private var res: Resources? = null

    var progress: ProgressDialog? = null

    var bluetoothExperimentLoader: BluetoothExperimentLoader? = null
    var currentQRcrc32: Long = -1
    var currentQRsize: Int = -1
    var currentQRdataPackets: Array<ByteArray?>? = null

    var newExperimentDialogOpen: Boolean = false

    var popupWindow: PopupWindow? = null

    private var experimentRepository: ExperimentRepository? = null

    var creditsV: ImageView? = null
    var newExperimentButton: FloatingActionButton? = null
    var newExperimentBluetooth: FloatingActionButton? = null
    var newExperimentQR: FloatingActionButton? = null
    var newExperimentSimple: FloatingActionButton? = null
    var newExperimentBluetoothLabel: TextView? = null
    var newExperimentSimpleLabel: TextView? = null
    var newExperimentQRLabel: TextView? = null
    var sv: ReportingScrollView? = null
    var backgroundDimmer: View? = null

    //The onCreate block will setup some onClickListeners and display a do-not-damage-your-phone
    //  warning message.
    override fun onCreate(savedInstanceState: Bundle?) {
        //Switch from the theme used as splash screen to the theme for the activity
        //This method is for pre Android 12 devices: We set a theme that shows the splash screen and
        //on create is executed when all resources are loaded, which then replaces the theme with
        //the normal one.
        //On Android 12 this does not hurt, but Android 12 shows its own splash method (defined with
        //specific attributes in the theme), so the classic splash screen is not shown anyways
        //before setTheme is called and we see the normal theme right away.

        setTheme(R.style.Theme_Phyphox_DayNight)

        val themePreference: String = PreferenceManager
            .getDefaultSharedPreferences(this)
            .getString(
                getString(de.rwth_aachen.phyphox.R.string.setting_dark_mode_key),
                SettingsFragment.DARK_MODE_ON
            )!!
        SettingsFragment.setApplicationTheme(themePreference)

        //Basics. Call super-constructor and inflate the layout.
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_experiment_list)

        res = getResources() //Get Resource reference for easy access.

        creditsV = findViewById<ImageView>(R.id.credits)
        newExperimentButton = findViewById<FloatingActionButton>(R.id.newExperiment)

        newExperimentSimple = findViewById<FloatingActionButton>(R.id.newExperimentSimple)
        newExperimentSimpleLabel = findViewById<TextView>(R.id.newExperimentSimpleLabel)
        newExperimentBluetooth = findViewById<FloatingActionButton>(R.id.newExperimentBluetooth)
        newExperimentQR = findViewById<FloatingActionButton>(R.id.newExperimentQR)
        newExperimentBluetoothLabel = findViewById<TextView>(R.id.newExperimentBluetoothLabel)
        newExperimentQRLabel = findViewById<TextView>(R.id.newExperimentQRLabel)
        backgroundDimmer = findViewById<View>(R.id.experimentListDimmer)

        if (!displayDoNotDamageYourPhone()) { //Show the do-not-damage-your-phone-warning
            showSupportHintIfRequired()
        }

        WindowInsetHelper.setInsets(
            findViewById<View?>(R.id.experimentList),
            WindowInsetHelper.ApplyTo.PADDING,
            WindowInsetHelper.ApplyTo.IGNORE,
            WindowInsetHelper.ApplyTo.PADDING,
            WindowInsetHelper.ApplyTo.PADDING
        )
        WindowInsetHelper.setInsets(
            findViewById<View?>(R.id.expListHeader),
            WindowInsetHelper.ApplyTo.PADDING,
            WindowInsetHelper.ApplyTo.PADDING,
            WindowInsetHelper.ApplyTo.PADDING,
            WindowInsetHelper.ApplyTo.IGNORE
        )
        WindowInsetHelper.setInsets(
            findViewById<View?>(R.id.newExperiment),
            WindowInsetHelper.ApplyTo.IGNORE,
            WindowInsetHelper.ApplyTo.IGNORE,
            WindowInsetHelper.ApplyTo.MARGIN,
            WindowInsetHelper.ApplyTo.MARGIN
        )

        setUpOnClickListener()

        experimentRepository = ExperimentRepository()

        handleIntent(getIntent())
    }


    //If we return to this activity we want to reload the experiment list as other activities may
    //have changed it
    override fun onResume() {
        super.onResume()
        experimentRepository!!.loadAndShowMainExperimentList(this)
    }

    override fun onUserInteraction() {
        if (popupWindow != null) popupWindow!!.dismiss()
    }

    //Callback for premission requests done during the activity. (since Android 6 / Marshmallow)
    //If a new permission has been granted, we will just restart the activity to reload the experiment
    //   with the formerly missing permission
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            this.recreate()
        }
    }

    private fun setUpOnClickListener() {
        val ocl = View.OnClickListener { v: View? -> this.showPopupMenu(v!!) }

        creditsV!!.setOnClickListener(ocl)

        val neocl = View.OnClickListener { v: View? ->
            if (newExperimentDialogOpen) hideNewExperimentDialog()
            else showNewExperimentDialog()
        }

        val experimentListDimmer = findViewById<View>(R.id.experimentListDimmer)
        newExperimentButton!!.setOnClickListener(neocl)
        experimentListDimmer.setOnClickListener(neocl)

        val neoclSimple = View.OnClickListener { v: View? ->
            hideNewExperimentDialog()
            openSimpleExperimentConfigurationDialog(this)
        }

        newExperimentSimple!!.setOnClickListener(neoclSimple)
        newExperimentSimpleLabel!!.setOnClickListener(neoclSimple)

        val neoclBluetooth = View.OnClickListener { v: View? ->
            hideNewExperimentDialog()
            val bluetoothNameKeySet = experimentRepository!!.getBluetoothDeviceNameList().keys
            val bluetoothUUIDKeySet = experimentRepository!!.getBluetoothDeviceUUIDList().keys
            BluetoothScanner(
                this,
                bluetoothNameKeySet,
                bluetoothUUIDKeySet,
                object : BluetoothScanListener {
                    override fun onBluetoothDeviceFound(result: BluetoothDeviceInfo) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                            openBluetoothExperiments(
                                result.device,
                                result.uuids,
                                result.phyphoxService
                            )
                        }
                    }

                    override fun onBluetoothScanError(
                        msg: String?,
                        isError: Boolean?,
                        isFatal: Boolean?
                    ) {
                        showBluetoothScanError(
                            res!!.getString(R.string.bt_android_version),
                            true,
                            true
                        )
                    }
                }).execute()
        }

        newExperimentBluetooth!!.setOnClickListener(neoclBluetooth)
        newExperimentBluetoothLabel!!.setOnClickListener(neoclBluetooth)

        val neoclQR = View.OnClickListener { v: View? ->
            hideNewExperimentDialog()
            scanQRCode()
        }

        newExperimentQR!!.setOnClickListener(neoclQR)
        newExperimentQRLabel!!.setOnClickListener(neoclQR)

        sv = findViewById<ReportingScrollView>(R.id.experimentScroller)
        sv!!.setOnScrollChangedListener(ReportingScrollView.OnScrollChangedListener { scrollView: ReportingScrollView?, x: Int, y: Int, oldx: Int, oldy: Int ->
            val bottom = scrollView!!.getChildAt(scrollView.getChildCount() - 1).getBottom()
            if (y + 10 > bottom - scrollView.getHeight()) {
                scrollView.setOnScrollChangedListener(null)
                val settings = getSharedPreferences(Const.PREFS_NAME, 0)
                val editor = settings.edit()
                editor.putString("lastSupportHint", Const.phyphoxCatHintRelease)
                editor.apply()
            }
        })
    }

    private fun showPopupMenu(v: View) {
        val wrapper: Context =
            ContextThemeWrapper(this@ExperimentListActivity, R.style.Theme_Phyphox_DayNight)
        val popup = PopupMenu(wrapper, v)
        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->
            if (item!!.getItemId() == R.id.action_privacy) {
                openLink(res!!.getString(R.string.privacyPolicyURL))
                return@setOnMenuItemClickListener true
            } else if (item.getItemId() == R.id.action_credits) {
                openCreditDialog()
                return@setOnMenuItemClickListener true
            } else if (item.getItemId() == R.id.action_helpExperiments) {
                openLink(res!!.getString(R.string.experimentsPhyphoxOrgURL))
                return@setOnMenuItemClickListener true
            } else if (item.getItemId() == R.id.action_helpFAQ) {
                openLink(res!!.getString(R.string.faqPhyphoxOrgURL))
                return@setOnMenuItemClickListener true
            } else if (item.getItemId() == R.id.action_helpRemote) {
                openLink(res!!.getString(R.string.remotePhyphoxOrgURL))
                return@setOnMenuItemClickListener true
            } else if (item.getItemId() == R.id.action_settings) {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return@setOnMenuItemClickListener true
            } else if (item.getItemId() == R.id.action_deviceInfo) {
                openDeviceInfoDialog()
                return@setOnMenuItemClickListener true
            } else {
                return@setOnMenuItemClickListener false
            }
        })
        popup.inflate(R.menu.menu_help)
        popup.show()
    }

    private fun openLink(URLString: String?) {
        val uri = Uri.parse(URLString)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent)
        }
    }

    private fun openDeviceInfoDialog() {
        val sb = StringBuilder()

        var pInfo: PackageInfo?
        try {
            pInfo =
                getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS)
        } catch (e: Exception) {
            pInfo = null
        }

        if (Helper.isDarkTheme(res)) {
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
            for (i in pInfo.requestedPermissions.indices) {
                sb.append(
                    if (pInfo.requestedPermissions!![i].startsWith("android.permission.")) pInfo.requestedPermissions!![i].substring(
                        19
                    ) else pInfo.requestedPermissions!![i]
                )
                sb.append(": ")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) sb.append(if ((pInfo.requestedPermissionsFlags!![i] and PackageInfo.REQUESTED_PERMISSION_GRANTED) == 0) "no" else "yes")
                else sb.append("API < 16")
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            for (i in Build.SUPPORTED_ABIS.indices) {
                if (i > 0) sb.append(", ")
                sb.append(Build.SUPPORTED_ABIS[i])
            }
        } else {
            sb.append("API < 21")
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
                sb.append(res!!.getString(SensorInput.getDescriptionRes(sensor.getType())))
                sb.append("</b> (type ")
                sb.append(sensor.getType())
                sb.append(")")
                sb.append("<br />")
                sb.append("- Name: ")
                sb.append(sensor.getName())
                sb.append("<br />")
                sb.append("- Reporting Mode: ")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    sb.append(sensor.getReportingMode())
                } else {
                    sb.append("API < 21")
                }
                sb.append("<br />")
                sb.append("- Range: ")
                sb.append(sensor.getMaximumRange())
                sb.append(" ")
                sb.append(SensorInput.getUnit(sensor.getType()))
                sb.append("<br />")
                sb.append("- Resolution: ")
                sb.append(sensor.getResolution())
                sb.append(" ")
                sb.append(SensorInput.getUnit(sensor.getType()))
                sb.append("<br />")
                sb.append("- Min delay: ")
                sb.append(sensor.getMinDelay())
                sb.append(" µs")
                sb.append("<br />")
                sb.append("- Max delay: ")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    sb.append(sensor.getMaxDelay())
                } else {
                    sb.append("API < 21")
                }
                sb.append(" µs")
                sb.append("<br />")
                sb.append("- Power: ")
                sb.append(sensor.getPower())
                sb.append(" mA")
                sb.append("<br />")
                sb.append("- Vendor: ")
                sb.append(sensor.getVendor())
                sb.append("<br />")
                sb.append("- Version: ")
                sb.append(sensor.getVersion())
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sb.append(getCamera2FormattedCaps(false))
        } else {
            sb.append("API < 21")
        }
        sb.append("</font>")

        val text = Html.fromHtml(sb.toString())
        val ctw = ContextThemeWrapper(this@ExperimentListActivity, R.style.Theme_Phyphox_DayNight)
        val builder = AlertDialog.Builder(ctw)
        builder.setMessage(text)
            .setTitle(R.string.deviceInfo)
            .setPositiveButton(R.string.copyToClipboard, object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, id: Int) {
                    //Copy the device info to the clipboard and notify the user

                    val cm = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                    val data = ClipData.newPlainText(res!!.getString(R.string.deviceInfo), text)
                    cm.setPrimaryClip(data)

                    Toast.makeText(
                        this@ExperimentListActivity,
                        res!!.getString(R.string.deviceInfoCopied),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
            .setNegativeButton(R.string.close, object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, id: Int) {
                    //Closed by user. Nothing to do.
                }
            })
        val dialog = builder.create()
        dialog.show()
    }

    private fun openCreditDialog() {
        //Create the credits as an AlertDialog
        val ctw = ContextThemeWrapper(this@ExperimentListActivity, R.style.Theme_Phyphox)
        val credits = AlertDialog.Builder(ctw)
        val creditsInflater = ctw.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val creditLayout = creditsInflater.inflate(R.layout.credits, null)

        //Set the credit texts, which require HTML markup
        val tv = creditLayout.findViewById<View?>(R.id.creditNames) as TextView

        val creditsNamesSpannable = SpannableStringBuilder()
        var first = true
        for (line in res!!.getString(R.string.creditsNames).split("\\n".toRegex())
            .dropLastWhile { it.isEmpty() }.toTypedArray()) {
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
                Spanned.SPAN_INCLUSIVE_INCLUSIVE
            )
        }
        tv.setText(creditsNamesSpannable)

        //The following texts are not translateable. Get them from the basic English version.
        var conf = res!!.getConfiguration()
        conf = Configuration(conf)
        conf.setLocale(Locale.ENGLISH)
        val localizedRes = getBaseContext().createConfigurationContext(conf).getResources()

        val tvA = creditLayout.findViewById<View?>(R.id.creditsApache) as TextView
        tvA.setText(Html.fromHtml(localizedRes.getString(R.string.creditsApache)))
        val tvB = creditLayout.findViewById<View?>(R.id.creditsZxing) as TextView
        tvB.setText(Html.fromHtml(localizedRes.getString(R.string.creditsZxing)))
        val tvC = creditLayout.findViewById<View?>(R.id.creditsPahoMQTT) as TextView
        tvC.setText(Html.fromHtml(localizedRes.getString(R.string.creditsPahoMQTT)))

        //Finish alertDialog builder
        credits.setView(creditLayout)
        credits.setPositiveButton(
            res!!.getText(R.string.close),
            object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    //Nothing to do. Just close the thing.
                }
            })

        //Present the dialog
        credits.show()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showSupportHint() {
        if (popupWindow != null) return
        val inflater = this.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val hintView = inflater.inflate(R.layout.support_phyphox_hint, null)
        val text = hintView.findViewById<TextView>(R.id.support_phyphox_hint_text)
        text.setText(res!!.getString(R.string.categoryPhyphoxOrgHint))
        val iv = hintView.findViewById<ImageView>(R.id.support_phyphox_hint_arrow)
        val lp = iv.getLayoutParams() as LinearLayout.LayoutParams
        lp.gravity = Gravity.CENTER_HORIZONTAL
        iv.setLayoutParams(lp)

        popupWindow = PopupWindow(
            hintView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        if (Build.VERSION.SDK_INT >= 21) {
            popupWindow!!.setElevation(4.0f)
        }

        popupWindow!!.setOutsideTouchable(false)
        popupWindow!!.setTouchable(false)
        popupWindow!!.setFocusable(false)
        val ll = hintView.findViewById<LinearLayout>(R.id.support_phyphox_hint_root)

        ll.setOnTouchListener(OnTouchListener { view: View?, motionEvent: MotionEvent? ->
            if (popupWindow != null) popupWindow!!.dismiss()
            true
        })

        popupWindow!!.setOnDismissListener(PopupWindow.OnDismissListener { popupWindow = null })

        val root = findViewById<View>(R.id.rootExperimentList)
        root.post(Runnable {
            try {
                popupWindow!!.showAtLocation(
                    root,
                    Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL,
                    0,
                    0
                )
            } catch (e: BadTokenException) {
                Log.e(
                    "showHint",
                    "Bad token when showing hint. This is not unusual when app is rotating while showing the hint."
                )
            }
        })
    }

    private fun showSupportHintIfRequired() {
        try {
            if (getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_PERMISSIONS
                ).versionName!!.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()[0] != Const.phyphoxCatHintRelease
            ) return
        } catch (e: Exception) {
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
            val tempPath = File(getFilesDir(), "temp_zip")
            val extensions = arrayOf<String?>("phyphox")
            val files: MutableCollection<File> = FileUtils.listFiles(tempPath, extensions, true)
            if (files.size == 0) {
                Toast.makeText(
                    this,
                    "Error: There is no valid phyphox experiment in this zip file.",
                    Toast.LENGTH_LONG
                ).show()
            } else if (files.size == 1) {
                //Create an intent for this file
                val intent = Intent(this, Experiment::class.java)
                intent.setData(Uri.fromFile(files.iterator().next()))
                if (preselectedDevice != null) intent.putExtra(
                    Const.EXPERIMENT_PRESELECTED_BLUETOOTH_ADDRESS,
                    preselectedDevice.getAddress()
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
                            false
                        )
                        val shortInfo = AssetExperimentLoader.loadExperimentShortInfo(
                            data,
                            ExperimentListEnvironment(this)
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
                            "Error: Could not load experiment \"" + file + "\" from zip file.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                val builder = AlertDialog.Builder(this)
                val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater

                val view = inflater.inflate(R.layout.open_multipe_dialog, null)
                builder.setView(view)
                    .setPositiveButton(
                        R.string.open_save_all,
                        DialogInterface.OnClickListener { dialog: DialogInterface?, id: Int ->
                            zipRepository.saveExperimentsToMainList(ExperimentListEnvironment(this))
                            experimentRepository!!.loadAndShowMainExperimentList(this)
                            dialog!!.dismiss()
                        })
                    .setNegativeButton(
                        R.string.cancel,
                        DialogInterface.OnClickListener { dialog: DialogInterface?, id: Int -> dialog!!.dismiss() })
                val dialog = builder.create()

                (view.findViewById<View?>(R.id.open_multiple_dialog_instructions) as TextView).setText(
                    R.string.open_zip_dialog_instructions
                )

                val catList =
                    view.findViewById<View?>(R.id.open_multiple_dialog_list) as LinearLayout

                dialog.setTitle(getResources().getString(R.string.open_zip_title))

                zipRepository.addExperimentCategoriesToLinearLayout(catList, this.getResources())

                dialog.show()
            }
        } else {
            Toast.makeText(this@ExperimentListActivity, result, Toast.LENGTH_LONG).show()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun loadExperimentFromBluetoothDevice(device: BluetoothDevice) {
        val parent = this
        if (bluetoothExperimentLoader == null) {
            bluetoothExperimentLoader = BluetoothExperimentLoader(
                getBaseContext(),
                object : BluetoothExperimentLoaderCallback {
                    override fun updateProgress(transferred: Int, total: Int) {
                        parent.runOnUiThread(object : Runnable {
                            override fun run() {
                                if (total > 0) {
                                    if (progress!!.isIndeterminate()) {
                                        progress!!.dismiss()
                                        progress = ProgressDialog(parent)
                                        progress!!.setTitle(res!!.getString(R.string.loadingTitle))
                                        progress!!.setMessage(res!!.getString(R.string.loadingText))
                                        progress!!.setIndeterminate(false)
                                        progress!!.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                                        progress!!.setCancelable(true)
                                        progress!!.setOnCancelListener(object :
                                            DialogInterface.OnCancelListener {
                                            override fun onCancel(dialogInterface: DialogInterface?) {
                                                if (bluetoothExperimentLoader != null) bluetoothExperimentLoader!!.cancel()
                                            }
                                        })
                                        progress!!.setProgress(transferred)
                                        progress!!.setMax(total)
                                        progress!!.show()
                                    } else {
                                        progress!!.setProgress(transferred)
                                    }
                                }
                            }
                        })
                    }

                    override fun dismiss() {
                        parent.runOnUiThread(object : Runnable {
                            override fun run() {
                                progress!!.dismiss()
                            }
                        })
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
                                device.getAddress()
                            )
                            startActivity(intent)
                        }
                    }
                })
        }
        progress = ProgressDialog.show(
            this, res!!.getString(R.string.loadingTitle), res!!.getString(
                R.string.loadingText
            ), true, true, object : DialogInterface.OnCancelListener {
                override fun onCancel(dialogInterface: DialogInterface?) {
                    if (bluetoothExperimentLoader != null) bluetoothExperimentLoader!!.cancel()
                }
            })
        bluetoothExperimentLoader!!.loadExperimentFromBluetoothDevice(device)
    }

    fun handleIntent(intent: Intent) {
        if (progress != null) progress!!.dismiss()

        val scheme = intent.getScheme()
        if (scheme == null) return
        var isZip = false
        if (scheme == ContentResolver.SCHEME_FILE) {
            if (scheme == ContentResolver.SCHEME_FILE && !intent.getData()!!.getPath()!!
                    .startsWith(getFilesDir().getPath()) && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //Android 6.0: No permission? Request it!
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE),
                    0
                )
                //We will stop here. If the user grants the permission, the permission callback will restart the action with the same intent
                return
            }
            val uri = intent.getData()

            val data = ByteArray(4)
            val `is`: InputStream?
            try {
                `is` = this.getContentResolver().openInputStream(uri!!)
                if (`is`!!.read(data, 0, 4) < 4) {
                    Toast.makeText(this, "Error: File truncated.", Toast.LENGTH_LONG).show()
                    return
                }
            } catch (e: FileNotFoundException) {
                Toast.makeText(this, "Error: File not found.", Toast.LENGTH_LONG).show()
                return
            } catch (e: IOException) {
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
                    this, res!!.getString(R.string.loadingTitle), res!!.getString(
                        R.string.loadingText
                    ), true
                )
                ZipIntentHandler(intent, this).execute()
            }
        } else if (scheme == ContentResolver.SCHEME_CONTENT || scheme == "phyphox" || scheme == "http" || scheme == "https") {
            progress = ProgressDialog.show(
                this, res!!.getString(R.string.loadingTitle), res!!.getString(
                    R.string.loadingText
                ), true
            )
            CopyIntentHandler(intent, this).execute()
        }
    }

    protected fun showNewExperimentDialog() {
        newExperimentDialogOpen = true

        val rotate45In =
            AnimationUtils.loadAnimation(getBaseContext(), R.anim.experiment_list_fab_rotate45)
        val fabIn = AnimationUtils.loadAnimation(getBaseContext(), R.anim.experiment_list_fab_in)
        val labelIn =
            AnimationUtils.loadAnimation(getBaseContext(), R.anim.experiment_list_label_in)
        val fadeDark =
            AnimationUtils.loadAnimation(getBaseContext(), R.anim.experiment_list_fade_dark)

        newExperimentButton!!.startAnimation(rotate45In)
        newExperimentSimple!!.startAnimation(fabIn)
        newExperimentSimpleLabel!!.startAnimation(labelIn)
        newExperimentBluetooth!!.startAnimation(fabIn)
        newExperimentBluetoothLabel!!.startAnimation(labelIn)
        newExperimentQR!!.startAnimation(fabIn)
        newExperimentQRLabel!!.startAnimation(labelIn)
        backgroundDimmer!!.startAnimation(fadeDark)

        newExperimentSimple!!.setClickable(true)
        newExperimentSimpleLabel!!.setClickable(true)
        newExperimentBluetooth!!.setClickable(true)
        newExperimentBluetoothLabel!!.setClickable(true)
        newExperimentQR!!.setClickable(true)
        newExperimentQRLabel!!.setClickable(true)
        backgroundDimmer!!.setClickable(true)
    }

    protected fun hideNewExperimentDialog() {
        newExperimentDialogOpen = false

        val rotate0In =
            AnimationUtils.loadAnimation(getBaseContext(), R.anim.experiment_list_fab_rotate0)
        val fabOut = AnimationUtils.loadAnimation(getBaseContext(), R.anim.experiment_list_fab_out)
        val labelOut =
            AnimationUtils.loadAnimation(getBaseContext(), R.anim.experiment_list_label_out)
        val fadeTransparent =
            AnimationUtils.loadAnimation(getBaseContext(), R.anim.experiment_list_fade_transparent)

        newExperimentSimple!!.setClickable(false)
        newExperimentSimpleLabel!!.setClickable(false)
        newExperimentBluetooth!!.setClickable(false)
        newExperimentBluetoothLabel!!.setClickable(false)
        newExperimentQR!!.setClickable(false)
        newExperimentQRLabel!!.setClickable(false)
        backgroundDimmer!!.setClickable(false)

        newExperimentButton!!.startAnimation(rotate0In)
        newExperimentSimple!!.startAnimation(fabOut)
        newExperimentSimpleLabel!!.startAnimation(labelOut)
        newExperimentBluetooth!!.startAnimation(fabOut)
        newExperimentBluetoothLabel!!.startAnimation(labelOut)
        newExperimentQR!!.startAnimation(fabOut)
        newExperimentQRLabel!!.startAnimation(labelOut)
        backgroundDimmer!!.startAnimation(fadeTransparent)
    }

    protected fun scanQRCode() {
        val qrScan = IntentIntegrator(this)

        qrScan.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
        qrScan.setPrompt(getResources().getString(R.string.newExperimentQRscan))
        qrScan.setBeepEnabled(false)
        qrScan.setOrientationLocked(true)

        qrScan.initiateScan()
    }

    protected fun showQRScanError(msg: String?, isError: Boolean) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(msg)
            .setTitle(if (isError) R.string.newExperimentQRErrorTitle else R.string.newExperimentQR)
            .setPositiveButton(
                if (isError) R.string.tryagain else R.string.doContinue,
                object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, id: Int) {
                        scanQRCode()
                    }
                })
            .setNegativeButton(
                res!!.getString(R.string.cancel),
                object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, id: Int) {
                    }
                })
        this.runOnUiThread(object : Runnable {
            override fun run() {
                val dialog = builder.create()
                dialog.show()
            }
        })
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    protected fun showBluetoothExperimentReadError(msg: String?, device: BluetoothDevice) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(msg)
            .setTitle(R.string.newExperimentBTReadErrorTitle)
            .setPositiveButton(R.string.tryagain, object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, id: Int) {
                    loadExperimentFromBluetoothDevice(device)
                }
            })
            .setNegativeButton(
                res!!.getString(R.string.cancel),
                object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, id: Int) {
                    }
                })
        this.runOnUiThread(object : Runnable {
            override fun run() {
                val dialog = builder.create()
                dialog.show()
            }
        })
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent) {
        super.onActivityResult(requestCode, resultCode, intent)
        val scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent)
        val textResult: String?
        if (scanResult != null && (scanResult.getContents().also { textResult = it }) != null) {
            if (textResult!!.lowercase(Locale.getDefault())
                    .startsWith("http://") || textResult.lowercase(
                    Locale.getDefault()
                ).startsWith("https://") || textResult.lowercase(Locale.getDefault())
                    .startsWith("phyphox://")
            ) {
                //This is an URL, open it
                //Create an intent for this new file
                val URLintent = Intent(this, Experiment::class.java)
                URLintent.setData(
                    Uri.parse(
                        "phyphox://" + textResult.split(
                            "//".toRegex(),
                            limit = 2
                        ).toTypedArray()[1]
                    )
                )
                URLintent.setAction(Intent.ACTION_VIEW)
                handleIntent(URLintent)
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
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }
                val crc32 =
                    (((data[7].toInt() and 0xff).toLong() shl 24) or ((data[8].toInt() and 0xff).toLong() shl 16) or ((data[9].toInt() and 0xff).toLong() shl 8) or ((data[10].toInt() and 0xff).toLong()))
                val index = data[11].toInt()
                val count = data[12].toInt()

                if ((currentQRcrc32 >= 0 && currentQRcrc32 != crc32) || (currentQRsize >= 0 && count != currentQRsize) || (currentQRsize >= 0 && index >= currentQRsize)) {
                    showQRScanError(res!!.getString(R.string.newExperimentQRcrcMismatch), true)
                    currentQRsize = -1
                    currentQRcrc32 = -1
                }
                if (currentQRcrc32 < 0) {
                    currentQRcrc32 = crc32
                    currentQRsize = count
                    currentQRdataPackets = arrayOfNulls<ByteArray>(count)
                }
                currentQRdataPackets!![index] = Arrays.copyOfRange(data, 13, data.size)
                var missing = 0
                for (i in 0..<currentQRsize) {
                    if (currentQRdataPackets!![i] == null) missing++
                }
                if (missing == 0) {
                    //We have all the data. Write it to a temporary file and give it to our default intent handler...
                    val tempPath = File(getFilesDir(), "temp_qr")
                    if (!tempPath.exists()) {
                        if (!tempPath.mkdirs()) {
                            showQRScanError(
                                "Could not create temporary directory to write zip file.",
                                true
                            )
                            return
                        }
                    }
                    val files = tempPath.list()
                    for (file in files!!) {
                        if (!(File(tempPath, file).delete())) {
                            showQRScanError(
                                "Could not clear temporary directory to extract zip file.",
                                true
                            )
                            return
                        }
                    }

                    var totalSize = 0

                    for (i in 0..<currentQRsize) {
                        totalSize += currentQRdataPackets!![i]!!.size
                    }
                    val dataReceived = ByteArray(totalSize)
                    var offset = 0
                    for (i in 0..<currentQRsize) {
                        System.arraycopy(
                            currentQRdataPackets!![i],
                            0,
                            dataReceived,
                            offset,
                            currentQRdataPackets!![i]!!.size
                        )
                        offset += currentQRdataPackets!![i]!!.size
                    }

                    val crc32Received = CRC32()
                    crc32Received.update(dataReceived)
                    if (crc32Received.getValue() != crc32) {
                        Log.e(
                            "qrscan",
                            "Received CRC32 " + crc32Received.getValue() + " but expected " + crc32
                        )
                        showQRScanError(res!!.getString(R.string.newExperimentQRBadCRC), true)
                        return
                    }

                    val zipData = Helper.inflatePartialZip(dataReceived)

                    val zipFile: File?
                    try {
                        zipFile = File(tempPath, "qr.zip")
                        val out = FileOutputStream(zipFile)
                        out.write(zipData)
                        out.close()
                    } catch (e: Exception) {
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
                        res!!.getString(R.string.newExperimentQRCodesMissing1) + " " + currentQRsize + " " + res!!.getString(
                            R.string.newExperimentQRCodesMissing2
                        ) + " " + missing, false
                    )
                }
            } else {
                //QR code does not contain or reference a phyphox experiment
                showQRScanError(res!!.getString(R.string.newExperimentQRNoExperiment), true)
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @SuppressLint("MissingPermission") //TODO: The permission is actually checked when entering the entire BLE dialog and I do not see how we could reach this part of the code if it failed. However, I cannot rule out some other mechanism of revoking permissions during an app switch or from the notifications bar (?), so a cleaner implementation might be good idea
    fun openBluetoothExperiments(
        device: BluetoothDevice,
        uuids: MutableSet<UUID?>,
        phyphoxService: Boolean
    ) {
        val parent = this

        val hiddenBluetoothRepository = ExperimentRepository()
        hiddenBluetoothRepository.loadHiddenBluetoothExperiments(this)

        val experiments: MutableSet<String?> = HashSet<String?>()
        if (device.getName() != null) {
            for (name in hiddenBluetoothRepository.getBluetoothDeviceNameList().keys) {
                if (device.getName().contains(name)) {
                    val experimentsForName =
                        hiddenBluetoothRepository.getBluetoothDeviceNameList().get(name)
                    if (experimentsForName != null) experiments.addAll(experimentsForName)
                }
            }
        }

        for (uuid in uuids) {
            val experimentsForUUID =
                hiddenBluetoothRepository.getBluetoothDeviceUUIDList().get(uuid)
            if (experimentsForUUID != null) experiments.addAll(experimentsForUUID)
        }
        val supportedExperiments = experiments

        if (supportedExperiments.isEmpty() && phyphoxService) {
            //We do not have any experiments for this device, so there is no choice. Just load the experiment provided by the device.
            loadExperimentFromBluetoothDevice(device)
            return
        }

        val builder = AlertDialog.Builder(parent)
        val inflater = parent.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view = inflater.inflate(R.layout.open_multipe_dialog, null)
        builder.setView(view)

        val bleRepository = ExperimentRepository()

        if (!supportedExperiments.isEmpty()) {
            builder.setPositiveButton(
                R.string.open_save_all,
                object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, id: Int) {
                        bleRepository.saveExperimentsToMainList(ExperimentListEnvironment(parent))
                        experimentRepository!!.loadAndShowMainExperimentList(parent)
                        dialog.dismiss()
                    }
                })
        }
        builder.setNegativeButton(
            R.string.cancel,
            DialogInterface.OnClickListener { dialog: DialogInterface?, id: Int -> dialog!!.dismiss() })

        var instructions = ""
        if (!supportedExperiments.isEmpty()) {
            instructions += res!!.getString(R.string.open_bluetooth_assets)
        }
        if (!supportedExperiments.isEmpty() && phyphoxService) instructions += "\n\n"
        if (phyphoxService) {
            instructions += res!!.getString(R.string.newExperimentBluetoothLoadFromDeviceInfo)
            builder.setNeutralButton(
                R.string.newExperimentBluetoothLoadFromDevice,
                object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface, id: Int) {
                        loadExperimentFromBluetoothDevice(device)
                        dialog.dismiss()
                    }
                })
        }
        val dialog = builder.create()

        (view.findViewById<View?>(R.id.open_multiple_dialog_instructions) as TextView).setText(
            instructions
        )

        dialog.setTitle(parent.getResources().getString(R.string.open_bluetooth_assets_title))

        val assetManager = parent.getAssets()
        for (file in supportedExperiments) {
            //Load details for each experiment
            try {
                val input = assetManager.open("experiments/" + file)
                val data = ExperimentLoadInfoData(input, file, null, true)
                val shortInfo = AssetExperimentLoader.loadExperimentShortInfo(
                    data,
                    ExperimentListEnvironment(parent)
                )
                if (shortInfo != null) {
                    bleRepository.addExperiment(shortInfo, this)
                }
                input.close()
            } catch (e: IOException) {
                Log.e(
                    "ExperimentList",
                    "Error: Could not load experiment \"" + file + "\" from asset."
                )
                Toast.makeText(
                    parent,
                    "Error: Could not load experiment \"" + file + "\" from asset.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        val parentLayout = view.findViewById<LinearLayout>(R.id.open_multiple_dialog_list)
        bleRepository.setPreselectedBluetoothAddress(device.getAddress())
        bleRepository.addExperimentCategoriesToLinearLayout(parentLayout, this.getResources())

        dialog.show()
    }

    protected fun showBluetoothScanError(msg: String?, isError: Boolean, isFatal: Boolean) {
        val parent = this

        val builder = AlertDialog.Builder(this)
        builder.setMessage(msg)
            .setTitle(if (isError) R.string.newExperimentBluetoothErrorTitle else R.string.newExperimentBluetooth)
        if (!isFatal) {
            builder.setPositiveButton(
                if (isError) R.string.tryagain else R.string.doContinue,
                object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, id: Int) {
                        //(new RunBluetoothScan()).execute();
                        val bluetoothNameKeySet =
                            experimentRepository!!.getBluetoothDeviceNameList().keys
                        val bluetoothUUIDKeySet =
                            experimentRepository!!.getBluetoothDeviceUUIDList().keys

                        BluetoothScanner(
                            parent,
                            bluetoothNameKeySet,
                            bluetoothUUIDKeySet,
                            object : BluetoothScanListener {
                                override fun onBluetoothDeviceFound(result: BluetoothDeviceInfo) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                        openBluetoothExperiments(
                                            result.device,
                                            result.uuids,
                                            result.phyphoxService
                                        )
                                    }
                                }

                                override fun onBluetoothScanError(
                                    msg: String?,
                                    isError: Boolean?,
                                    isFatal: Boolean?
                                ) {
                                    showBluetoothScanError(
                                        res!!.getString(R.string.bt_android_version),
                                        true,
                                        true
                                    )
                                }
                            }).execute()
                    }
                })
        }
        builder.setNegativeButton(
            res!!.getString(R.string.cancel),
            object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, id: Int) {
                }
            })
        runOnUiThread(Runnable {
            val dialog = builder.create()
            dialog.show()
        })
    }

    //Displays a warning message that some experiments might damage the phone
    private fun displayDoNotDamageYourPhone(): Boolean {
        //Use the app theme and create an AlertDialog-builder
        val ctw = ContextThemeWrapper(this, R.style.Theme_Phyphox_DayNight)
        val adb = AlertDialog.Builder(ctw)
        val adbInflater = ctw.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val warningLayout = adbInflater.inflate(R.layout.donotshowagain, null)

        //This reference is used to address a do-not-show-again checkbox within the dialog
        val dontShowAgain = warningLayout.findViewById<View?>(R.id.donotshowagain) as CheckBox

        //Setup AlertDialog builder
        adb.setView(warningLayout)
        adb.setTitle(R.string.warning)
        adb.setPositiveButton(res!!.getText(R.string.ok), object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                //User clicked ok. Did the user decide to skip future warnings?
                var skipWarning = false
                if (dontShowAgain.isChecked()) skipWarning = true

                //Store user decision
                val settings = getSharedPreferences(Const.PREFS_NAME, 0)
                val editor = settings.edit()
                editor.putBoolean("skipWarning", skipWarning)
                editor.apply()
            }
        })

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
    private fun openSimpleExperimentConfigurationDialog(c: Context?) {
        val ctw = ContextThemeWrapper(this, R.style.Theme_Phyphox_DayNight)
        val neDialog = AlertDialog.Builder(ctw)
        val neInflater = ctw.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val neLayout = neInflater.inflate(R.layout.new_experiment, null)

        neDialog.setView(neLayout)
        neDialog.setTitle(R.string.newExperiment)
        neDialog.setPositiveButton(
            res!!.getText(R.string.ok),
            DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                //Here we have to create the experiment definition file
                val creator = SimpleExperimentCreator(c, neLayout)
                creator.generateAndOpenSimpleExperiment()
            })
        neDialog.setNegativeButton(
            res!!.getText(R.string.cancel),
            DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int -> })

        neDialog.show()
    }
}

