package de.rwth_aachen.phyphox.features.experiment.old.parser;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.location.LocationManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.UUID;
import java.util.Vector;

import de.rwth_aachen.phyphox.Bluetooth.Bluetooth;
import de.rwth_aachen.phyphox.Bluetooth.BluetoothInput;
import de.rwth_aachen.phyphox.DataOutput;
import de.rwth_aachen.phyphox.camera.CameraInput;
import de.rwth_aachen.phyphox.camera.depth.DepthInput;
import de.rwth_aachen.phyphox.GpsInput;
import de.rwth_aachen.phyphox.PhyphoxExperiment;
import de.rwth_aachen.phyphox.R;
import de.rwth_aachen.phyphox.SensorInput;
import de.rwth_aachen.phyphox.camera.helper.CameraHelper;
import de.rwth_aachen.phyphox.features.experiment.Experiment;
import de.rwth_aachen.phyphox.features.experiment.old.parser.error.PhyphoxFileException;

public class InputBlockParser extends XmlBlockParser {

        InputBlockParser(XmlPullParser xpp, PhyphoxExperiment experiment, Experiment parent) {
            super(xpp, experiment, parent);
        }

        @Override
        protected void processStartTag(String tag) throws XmlPullParserException, PhyphoxFileException, IOException {
            switch (tag.toLowerCase()) {
                case "sensor": { //A sensor input (in the sense of android sensor)
                    double rate = getDoubleAttribute("rate", 0.); //Aquisition rate (we always request fastest rate, but average or just pick every n-th readout)
                    int stride = getIntAttribute("stride", 1);
                    boolean average = getBooleanAttribute("average", false); //Average if we have a lower rate than the sensor can deliver?
                    SensorInput.SensorRateStrategy rateStrategy = (experiment.versionMajor > 1 || (experiment.versionMajor == 1 && experiment.versionMinor >= 14)) ? SensorInput.SensorRateStrategy.auto : SensorInput.SensorRateStrategy.limit;
                    String rateStrategyStr = getStringAttribute("rateStrategy");
                    if (rateStrategyStr != null && !rateStrategyStr.isEmpty()) {
                        try {
                            rateStrategy = SensorInput.SensorRateStrategy.valueOf(rateStrategyStr);
                        } catch (IllegalArgumentException e) {
                            throw new PhyphoxFileException("Invalid rate strategy.", xpp.getLineNumber());
                        }
                    }

                    String type = getStringAttribute("type");
                    int typeFilter = getIntAttribute("typeFilter", -1);
                    String nameFilter = getStringAttribute("nameFilter");
                    boolean ignoreUnavailable = getBooleanAttribute("ignoreUnavailable", false);

                    //Allowed input/output configuration
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "x"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = false;}},
                            new IoBlockParser.ioMapping() {{name = "y"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = false;}},
                            new IoBlockParser.ioMapping() {{name = "z"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = false;}},
                            new IoBlockParser.ioMapping() {{name = "t"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = false;}},
                            new IoBlockParser.ioMapping() {{name = "abs"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = false;}},
                            new IoBlockParser.ioMapping() {{name = "accuracy"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = false;}}
                    };
                    Vector<DataOutput> outputs = new Vector<>();
                    (new IoBlockParser(xpp, experiment, parent, null, outputs, null, outputMapping, "component")).process(); //Load inputs and outputs

                    //Add a sensor. If the string is unknown, sensorInput throws a PhyphoxFileException
                    try {
                        experiment.inputSensors.add(new SensorInput(type, nameFilter, typeFilter, ignoreUnavailable, rate, rateStrategy, stride, average, outputs, experiment.dataLock, experiment.experimentTimeReference));
                        experiment.inputSensors.lastElement().attachSensorManager(parent.sensorManager);
                    } catch (SensorInput.SensorException e) {
                        throw new PhyphoxFileException(e.getMessage(), xpp.getLineNumber());
                    }

                    //Check if the sensor is available on this device
                    if (!(experiment.inputSensors.lastElement().isAvailable() || experiment.inputSensors.lastElement().ignoreUnavailable)) {
                        throw new PhyphoxFileException(parent.getResources().getString(R.string.sensorNotAvailableWarningText1) + " " + parent.getResources().getString(experiment.inputSensors.lastElement().getDescriptionRes()) + " " + parent.getResources().getString(R.string.sensorNotAvailableWarningText2));
                    }
                    break;
                }
                case "location": { //GPS input
                    //Check for recording permission
                    if (ContextCompat.checkSelfPermission(parent, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        //No permission? Request it (Android 6+, only)
                        ActivityCompat.requestPermissions(parent, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                        throw new PhyphoxFileException("Need permission to receive location through GPS."); //We will throw an error here, but when the user grants the permission, the activity will be restarted from the permission callback
                    }

                    //Allowed input/output configuration
                    //Allowed input/output configuration
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "lat"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = false;}},
                            new IoBlockParser.ioMapping() {{name = "lon"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = false;}},
                            new IoBlockParser.ioMapping() {{name = "z"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = false;}},
                            new IoBlockParser.ioMapping() {{name = "zwgs84"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = false;}},
                            new IoBlockParser.ioMapping() {{name = "v"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = false;}},
                            new IoBlockParser.ioMapping() {{name = "dir"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = false;}},
                            new IoBlockParser.ioMapping() {{name = "t"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = false;}},
                            new IoBlockParser.ioMapping() {{name = "accuracy"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = false;}},
                            new IoBlockParser.ioMapping() {{name = "zAccuracy"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = false;}},
                            new IoBlockParser.ioMapping() {{name = "status"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = false;}},
                            new IoBlockParser.ioMapping() {{name = "satellites"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = false;}}
                    };
                    Vector<DataOutput> outputs = new Vector<>();
                    (new IoBlockParser(xpp, experiment, parent, null, outputs, null, outputMapping, "component")).process(); //Load inputs and outputs

                    experiment.gpsIn = new GpsInput(outputs, experiment.dataLock, experiment.experimentTimeReference);
                    experiment.gpsIn.attachLocationManager((LocationManager)parent.getSystemService(Context.LOCATION_SERVICE));

                    if (!GpsInput.isAvailable(parent)) {
                        throw new PhyphoxFileException(parent.getResources().getString(R.string.sensorNotAvailableWarningText1) + " " + parent.getResources().getString(R.string.location) + " " + parent.getResources().getString(R.string.sensorNotAvailableWarningText2));
                    }

                    break;
                }
                case "audio": { //Audio input, aka microphone
                    //Check for recording permission
                    if (ContextCompat.checkSelfPermission(parent, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        //No permission? Request it (Android 6+, only)
                        ActivityCompat.requestPermissions(parent, new String[]{Manifest.permission.RECORD_AUDIO}, 0);
                        throw new PhyphoxFileException("Need permission to record audio."); //We will throw an error here, but when the user grants the permission, the activity will be restarted from the permission callback
                    }
                    experiment.micRate = getIntAttribute("rate", 48000); //Recording rate
                    experiment.appendAudioInput = getBooleanAttribute("append", false);

                    //Allowed input/output configuration
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "out"; asRequired = false; minCount = 1; maxCount = 1; valueAllowed = false;}},
                            new IoBlockParser.ioMapping() {{name = "rate"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = false;}}
                    };
                    Vector<DataOutput> outputs = new Vector<>();
                    (new IoBlockParser(xpp, experiment, parent, null, outputs, null, outputMapping, "component")).process(); //Load inputs and outputs

                    experiment.micOutput = outputs.get(0).buffer.name;
                    experiment.micBufferSize = outputs.get(0).size()*2; //Output-buffer size
                    if (outputs.size() > 1)
                        experiment.micRateOutput = outputs.get(1).buffer.name;
                    else
                        experiment.micRateOutput = "";

                    //Devices have a minimum buffer size. We might need to increase our buffer...
                    if (Build.MANUFACTURER.toLowerCase().contains("xiaomi"))
                        experiment.forceAudioRecordingCompatibilityFormat = true; //Several Xiaomi devices have issues supporting ENCODING_PCM_FLOAT, Falling back to 16bit ints is not that much of a disadvantage, so let's be safe and force them all to the legacy format
                    if (!experiment.forceAudioRecordingCompatibilityFormat)
                        experiment.minBufferSize = AudioRecord.getMinBufferSize(experiment.micRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_FLOAT)/2;
                    else
                        experiment.minBufferSize = AudioRecord.getMinBufferSize(experiment.micRate, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)/2;
                    if (experiment.minBufferSize < 0) {
                        throw new PhyphoxFileException("Could not initialize recording. (" + experiment.minBufferSize + ")", xpp.getLineNumber());
                    }
                    if (experiment.minBufferSize > experiment.micBufferSize) {
                        experiment.micBufferSize = experiment.minBufferSize;
                        Log.w("loadExperiment", "Audio buffer size had to be adjusted to " + experiment.minBufferSize);
                    }

                    break;
                }
                case "depth": {
                    if(!parent.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
                        throw new PhyphoxFileException("This device doesn't have the camera.");
                    }

                    //Check for camera permission
                    if (ContextCompat.checkSelfPermission(parent, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        //No permission? Request it (Android 6+, only)
                        ActivityCompat.requestPermissions(parent, new String[]{Manifest.permission.CAMERA}, 0);
                        throw new PhyphoxFileException("Need permission to access the camera."); //We will throw an error here, but when the user grants the permission, the activity will be restarted from the permission callback
                    }

                    String modeStr = getStringAttribute("mode");
                    if (modeStr == null)
                        modeStr = "closest";
                    else
                        modeStr = modeStr.toLowerCase();

                    DepthInput.DepthExtractionMode mode;
                    switch (modeStr) {
                        case "closest": {
                            mode = DepthInput.DepthExtractionMode.closest;
                            break;
                        }
                        case "weighted": {
                            mode = DepthInput.DepthExtractionMode.weighted;
                            break;
                        }
                        case "average": {
                            mode = DepthInput.DepthExtractionMode.average;
                            break;
                        }
                        default: {
                            throw new PhyphoxFileException("Unknown depth extraction mode: " + modeStr, xpp.getLineNumber());
                        }
                    }

                    double x1user = getDoubleAttribute("x1", 0.4);
                    double x2user = getDoubleAttribute("x2", 0.6);
                    double y1user = getDoubleAttribute("y1", 0.4);
                    double y2user = getDoubleAttribute("y2", 0.6);

                    //Careful: We will translate the user coordinate system to the camera coordinate system: x -> -y, y -> -x
                    double x1 = 1.0 - y1user;
                    double x2 = 1.0 - y2user;
                    double y1 = 1.0 - x1user;
                    double y2 = 1.0 - x2user;

                    //Allowed input/output configuration
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{
                                name = "z";
                                asRequired = false;
                                minCount = 1;
                                maxCount = 1;
                                valueAllowed = false;
                            }},
                            new IoBlockParser.ioMapping() {{
                                name = "t";
                                asRequired = true;
                                minCount = 0;
                                maxCount = 1;
                                valueAllowed = false;
                            }}
                    };
                    Vector<DataOutput> outputs = new Vector<>();
                    (new IoBlockParser(xpp, experiment, parent, null, outputs, null, outputMapping, "component")).process(); //Load inputs and outputs

                    CameraManager cameraManager = (CameraManager) parent.getSystemService(Context.CAMERA_SERVICE);
                    CameraHelper.updateCameraList(cameraManager);
                    experiment.depthInput = new DepthInput(mode, (float) x1, (float) x2, (float) y1, (float) y2, outputs, experiment.dataLock, experiment.experimentTimeReference, cameraManager);

                    if (!DepthInput.isAvailable()) {
                        throw new PhyphoxFileException(parent.getResources().getString(R.string.sensorNotAvailableWarningText1) + " " + parent.getResources().getString(R.string.sensorDepth) + " " + parent.getResources().getString(R.string.sensorNotAvailableWarningText2));
                    }

                    break;
                }
                case "camera": {
                    if(!parent.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
                        throw new PhyphoxFileException("This device doesn't have the camera.");
                    }

                    //Check for camera permission
                    if (ContextCompat.checkSelfPermission(parent, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        //No permission? Request it (Android 6+, only)
                        ActivityCompat.requestPermissions(parent, new String[]{Manifest.permission.CAMERA}, 0);
                        throw new PhyphoxFileException("Need permission to access the camera."); //We will throw an error here, but when the user grants the permission, the activity will be restarted from the permission callback
                    }

                    boolean autoExposure = getBooleanAttribute("auto_exposure", true);

                    String aeStrategyStr = getStringAttribute("aeStrategy");
                    if (aeStrategyStr == null) {
                        aeStrategyStr = "mean";
                    }

                    CameraInput.AEStrategy aeStrategy;
                    switch (aeStrategyStr) {
                        case "mean": {
                            aeStrategy = CameraInput.AEStrategy.mean;
                            break;
                        }
                        case "avoidOverexposure": {
                            aeStrategy = CameraInput.AEStrategy.avoidOverexposure;
                            break;
                        }
                        case "avoidUnderexposure": {
                            aeStrategy = CameraInput.AEStrategy.avoidUnderxposure;
                            break;
                        }
                        case "prioritizeFramerate": {
                            aeStrategy = CameraInput.AEStrategy.prioritizeFramerate;
                            break;
                        }
                        default: {
                            throw new PhyphoxFileException("Unknown aeStrategy: " + aeStrategyStr, xpp.getLineNumber());
                        }
                    }

                    String featureStr = getStringAttribute("feature");
                    if(featureStr == null)
                        featureStr = "photometric";
                    else featureStr = featureStr.toLowerCase();

                    CameraInput.PhyphoxCameraFeature feature;
                    switch (featureStr){
                        case "photometric": {
                            feature = CameraInput.PhyphoxCameraFeature.Photometric;
                            break;
                        }
                        case "spectroscopy": {
                            feature = CameraInput.PhyphoxCameraFeature.Spectroscopy;
                            break;
                        }
                        default: {
                            throw new PhyphoxFileException("Unknown feature name: " + featureStr, xpp.getLineNumber());
                        }
                    }

                    String lockedSetting = getStringAttribute("locked");
                    if (lockedSetting == null)
                        lockedSetting = "";

                    double x1user = getDoubleAttribute("x1", 0.4);
                    double x2user = getDoubleAttribute("x2", 0.6);
                    double y1user = getDoubleAttribute("y1", 0.4);
                    double y2user = getDoubleAttribute("y2", 0.6);

                    //Careful: We will translate the user coordinate system to the camera coordinate system: x -> -y, y -> -x
                    double x1 = 1.0 - y1user;
                    double x2 = 1.0 - y2user;
                    double y1 = 1.0 - x1user;
                    double y2 = 1.0 - x2user;

                    double thresholdAnalyzerThreshold = getDoubleAttribute("threshold", 0.5);

                    //Allowed input/output configuration
                    IoBlockParser.ioMapping[] outputMapping = {
                            new IoBlockParser.ioMapping() {{name = "t"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = false;}},
                            new IoBlockParser.ioMapping() {{name = "luma"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = false;}},
                            new IoBlockParser.ioMapping() {{name = "luminance"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = false;}},
                            new IoBlockParser.ioMapping() {{name = "hue"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = false;}},
                            new IoBlockParser.ioMapping() {{name = "saturation"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = false;}},
                            new IoBlockParser.ioMapping() {{name = "value"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = false;}},
                            new IoBlockParser.ioMapping() {{name = "threshold"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = false;}},
                            new IoBlockParser.ioMapping() {{name = "shutterSpeed"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = false;}},
                            new IoBlockParser.ioMapping() {{name = "iso"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = false;}},
                            new IoBlockParser.ioMapping() {{name = "aperture"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = false;}},
                            new IoBlockParser.ioMapping() {{name = "pixelPosition"; asRequired = true; minCount = 0; maxCount = 1; valueAllowed = false;}},
                    };

                    //String availableCameraSettings = getStringAttribute("setting");
                    //ArrayList<ExposureSettingMode> availableSettings = CameraHelper.convertInputSettingToSettingMode(availableCameraSettings);

                    Vector<DataOutput> outputs = new Vector<>();
                    (new IoBlockParser(xpp, experiment, parent, null, outputs, null, outputMapping, "component")).process(); //Load inputs and outputs

                    experiment.cameraInput= new CameraInput(
                            (float) x1,
                            (float) x2,
                            (float) y1,
                            (float) y2,
                            outputs,
                            experiment.dataLock,
                            experiment.experimentTimeReference,
                            feature,
                            autoExposure,
                            lockedSetting.isEmpty() ? null : lockedSetting,
                            aeStrategy,
                            thresholdAnalyzerThreshold);

                    break;

                }
                case "bluetooth": { //A bluetooth input
                        if (!Bluetooth.isSupported(parent)) {
                            throw new PhyphoxFileException(parent.getResources().getString(R.string.bt_android_version));
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && (ContextCompat.checkSelfPermission(parent, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(parent, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED)) {
                                //Android 12+: New Bluetooth scan permission required
                                ActivityCompat.requestPermissions(parent, new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT}, 0);
                                throw new PhyphoxFileException("Need permission to search for and access Bluetooth devices."); //We will throw an error here, but when the user grants the permission, the activity will be restarted from the permission callback
                            }
                            double rate = getDoubleAttribute("rate", 0.); //Aquisition rate

                            String idString = getTranslatedAttribute("id");

                            String nameFilter = getStringAttribute("name");
                            String addressFilter = getStringAttribute("address");
                            String uuidFilterStr = getStringAttribute("uuid");
                            UUID uuidFilter = null;
                            if (uuidFilterStr != null && !uuidFilterStr.isEmpty()) {
                                try {
                                    uuidFilter = UUID.fromString(uuidFilterStr);
                                } catch (Exception e) {
                                    throw new PhyphoxFileException("Invalid UUID: " + uuidFilterStr, xpp.getLineNumber());
                                }
                            }
                            Boolean autoConnect = getBooleanAttribute("autoConnect", false);

                            String modeStr = getStringAttribute("mode");
                            if (modeStr == null)
                                modeStr = "notification";
                            else
                                modeStr = modeStr.toLowerCase();

                            String modeFilter;
                            switch (modeStr) {
                                case "poll": {
                                    modeFilter = "poll";
                                    break;
                                }
                                case "notification": {
                                    modeFilter = "notification";
                                    break;
                                }
                                case "indication": {
                                    modeFilter = "indication";
                                    break;
                                }
                                default: {
                                    throw new PhyphoxFileException("Unknown bluetooth mode: " + modeStr, xpp.getLineNumber());
                                }
                            }

                            boolean subscribeOnStart = getBooleanAttribute("subscribeOnStart", false);
                            int mtu = getIntAttribute("mtu", 0);

                            Vector<DataOutput> outputs = new Vector<>();
                            Vector<Bluetooth.CharacteristicData> characteristics = new Vector<>();
                            (new BluetoothIoBlockParser(xpp, experiment, parent, outputs, null, characteristics)).process();
                            try {
                                BluetoothInput b = new BluetoothInput(idString, nameFilter, addressFilter, modeFilter, uuidFilter, autoConnect, rate, subscribeOnStart, outputs, experiment.dataLock, parent, parent, characteristics, experiment.experimentTimeReference);
                                if (mtu > 0)
                                    b.requestMTU = mtu;
                                experiment.bluetoothInputs.add(b);
                            } catch (PhyphoxFileException e) {
                                throw new PhyphoxFileException(e.getMessage(), xpp.getLineNumber()); // throw it again with LineNumber
                            }
                        }
                        break;
                }
                default: //Unknown tag
                    throw new PhyphoxFileException("Unknown tag "+tag, xpp.getLineNumber());
            }
        }

    }
