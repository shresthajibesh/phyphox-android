package de.rwth_aachen.phyphox.features.experiment.old.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.UUID;
import java.util.Vector;

import de.rwth_aachen.phyphox.AudioOutput;
import de.rwth_aachen.phyphox.Bluetooth.Bluetooth;
import de.rwth_aachen.phyphox.Bluetooth.BluetoothOutput;
import de.rwth_aachen.phyphox.DataInput;
import de.rwth_aachen.phyphox.PhyphoxExperiment;
import de.rwth_aachen.phyphox.R;
import de.rwth_aachen.phyphox.features.experiment.ExperimentActivity;
import de.rwth_aachen.phyphox.features.experiment.old.parser.error.PhyphoxFileException;

public class OutputBlockParser extends XmlBlockParser {

        OutputBlockParser(XmlPullParser xpp, PhyphoxExperiment experiment, ExperimentActivity parent) {
            super(xpp, experiment, parent);
        }

        @Override
        protected void processStartTag(String tag) throws XmlPullParserException, PhyphoxFileException, IOException {
            switch (tag.toLowerCase()) {
                case "audio": { //Audio output, aka speaker
                    boolean loop = getBooleanAttribute("loop", false); //Loop the output?
                    int rate = getIntAttribute("rate", 48000); //Sample frequency
                    boolean normalize = getBooleanAttribute("normalize", false); //Normalize amplitude of all inputs
                    AudioOutput audioOutput = new AudioOutput(loop, rate, normalize);
                    (new AudioOutputPluginBlockParser(xpp, experiment, parent, audioOutput)).process();
                    experiment.audioOutput = audioOutput;
                    break;

                }
                case "bluetooth": { //A bluetooth output
                    if (!Bluetooth.isSupported(parent)) {
                        throw new PhyphoxFileException(parent.getResources().getString(R.string.bt_android_version));
                    } else {
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
                        int mtu = getIntAttribute("mtu", 0);

                        Vector<DataInput> inputs = new Vector<>();
                        Vector<Bluetooth.CharacteristicData> characteristics = new Vector<>();
                        (new BluetoothIoBlockParser(xpp, experiment, parent, null, inputs, characteristics)).process();
                        BluetoothOutput b = new BluetoothOutput(idString, nameFilter, addressFilter, uuidFilter, autoConnect, parent, parent, inputs, characteristics);
                        if (mtu > 0)
                            b.requestMTU = mtu;
                        experiment.bluetoothOutputs.add(b);
                    }
                    break;
                }
                default: //Unknown tag...
                    throw new PhyphoxFileException("Unknown tag "+tag, xpp.getLineNumber());
            }
        }

    }
