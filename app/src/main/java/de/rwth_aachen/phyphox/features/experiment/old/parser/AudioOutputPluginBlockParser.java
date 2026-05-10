package de.rwth_aachen.phyphox.features.experiment.old.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import de.rwth_aachen.phyphox.AudioOutput;
import de.rwth_aachen.phyphox.DataBuffer;
import de.rwth_aachen.phyphox.DataInput;
import de.rwth_aachen.phyphox.PhyphoxExperiment;
import de.rwth_aachen.phyphox.features.experiment.ExperimentActivity;
import de.rwth_aachen.phyphox.features.experiment.old.parser.error.PhyphoxFileException;

public class AudioOutputPluginBlockParser extends XmlBlockParser {
        AudioOutput audioOutput;
        AudioOutput.AudioOutputPlugin currentPlugin = null;
        int level = 0;

        AudioOutputPluginBlockParser (XmlPullParser xpp, PhyphoxExperiment experiment, ExperimentActivity parent, AudioOutput audioOutput) {
            super(xpp, experiment, parent);
            this.audioOutput = audioOutput;
        }

        @Override
        protected void processStartTag(String tag) throws IOException, XmlPullParserException, PhyphoxFileException {
            level++;
            switch (tag.toLowerCase()) {
                case "input": {
                    String parameter = getStringAttribute("parameter");

                    DataInput input;
                    String type = getStringAttribute("type");
                    if (type == null)
                        type = "buffer";

                    if (type.equals("buffer")) {
                        String bufferName = getText();
                        DataBuffer buffer = experiment.getBuffer(bufferName);
                        if (buffer == null) {
                            throw new PhyphoxFileException("Buffer \"" + bufferName + "\" not defined.", xpp.getLineNumber());
                        }
                        input = new DataInput(buffer, false);
                    } else if (type.equals("value")) {
                        double value;
                        try {
                            value = Double.valueOf(getText());
                        } catch (NumberFormatException e) {
                            throw new PhyphoxFileException("Invalid number format.", xpp.getLineNumber());
                        }
                        input = new DataInput(value);
                    } else {
                        throw new PhyphoxFileException("Unknown input type \""+type+"\".", xpp.getLineNumber());
                    }
                    if (level == 1) {
                        //Direct input
                        currentPlugin = audioOutput.new AudioOutputPluginDirect(input);
                    } else if (level == 2) {
                        //Parameter
                        if (currentPlugin != null) {
                            if (parameter == null)
                                throw new PhyphoxFileException("Parameter attribute required for this plugin.", xpp.getLineNumber());

                            if (!currentPlugin.setParameter(parameter, input))
                                throw new PhyphoxFileException("Parameter \""+parameter+"\" not supported by this plugin.", xpp.getLineNumber());
                        } else {
                            throw new PhyphoxFileException("Unexpected input tag. No related audio plugin.", xpp.getLineNumber());
                        }
                    } else {
                        throw new PhyphoxFileException("Unexpected input tag.", xpp.getLineNumber());
                    }
                    break;
                }
                case "tone": {
                    String parameter = getStringAttribute("waveform");
                    if(parameter == null)
                        parameter = "sine";
                    AudioOutput.Waveform waveform = AudioOutput.Waveform.SINE;
                    switch (parameter){
                        case "square":
                            waveform = AudioOutput.Waveform.SQUARE;
                            break;
                        case "sawtooth":
                            waveform = AudioOutput.Waveform.SAWTOOTH;
                            break;
                        default:
                            break;
                    }
                    if (level == 1) {
                        //Tone plugin
                        currentPlugin = audioOutput.new AudioOutputPluginTone(waveform);
                    } else {
                        throw new PhyphoxFileException("Unexpected tone tag.", xpp.getLineNumber());
                    }
                    break;
                }
                case "noise": {
                    if (level == 1) {
                        //Noise plugin
                        currentPlugin = audioOutput.new AudioOutputPluginNoise();
                    } else {
                        throw new PhyphoxFileException("Unexpected noise tag.", xpp.getLineNumber());
                    }
                    break;
                }
                default: {
                    throw new PhyphoxFileException("Unexpected tag \"" + tag + "\"", xpp.getLineNumber());
                }
            }
        }

        @Override
        protected void processEndTag(String tag) {
            level--;
            if (level == 0 && currentPlugin != null) {
                audioOutput.attachPlugin(currentPlugin);
                currentPlugin = null;
            }
        }
    }
