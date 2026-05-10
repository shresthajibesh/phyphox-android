package de.rwth_aachen.phyphox.features.experiment.old.parser;

import static de.rwth_aachen.phyphox.features.experiment.old.PhyphoxFile.isValidIdentifier;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import de.rwth_aachen.phyphox.DataBuffer;
import de.rwth_aachen.phyphox.PhyphoxExperiment;
import de.rwth_aachen.phyphox.features.experiment.Experiment;
import de.rwth_aachen.phyphox.features.experiment.old.parser.error.PhyphoxFileException;

public class DataContainersBlockParser extends XmlBlockParser {

        DataContainersBlockParser(XmlPullParser xpp, PhyphoxExperiment experiment, Experiment parent) {
            super(xpp, experiment, parent);
        }

        @Override
        protected void processStartTag(String tag)  throws IOException, XmlPullParserException, PhyphoxFileException {
            switch (tag.toLowerCase()) {
                case "container": //A view defines an arangement of elements displayed to the user
                    String type = getStringAttribute("type");
                    if (type != null && !type.equals("buffer")) //There currently is only one buffer type. This tag is for future additions.
                        throw new PhyphoxFileException("Unknown container type \"" + type + "\".", xpp.getLineNumber());

                    int size = getIntAttribute("size",1);
                    String strInit = getStringAttribute("init");
                    boolean isStatic = getBooleanAttribute("static", false);

                    String name = getText();
                    if (!isValidIdentifier(name))
                        throw new PhyphoxFileException("\"" + name + "\" is not a valid name for a data-container.", xpp.getLineNumber());

                    DataBuffer newBuffer = experiment.createBuffer(name, size, experiment.experimentTimeReference);
                    newBuffer.setStatic(isStatic);

                    if (strInit != null && !strInit.isEmpty()) {
                        String strInitArray[] = strInit.split(",");
                        Double init[] = new Double[strInitArray.length];
                        for (int i = 0; i < init.length; i++) {
                            try {
                                init[i] = Double.parseDouble(strInitArray[i].trim());
                            } catch (Exception e) {
                                init[i] = Double.NaN;
                            }
                        }
                        newBuffer.setInit(init);
                    }
                    break;
                default: //Unknown tag
                    throw new PhyphoxFileException("Unknown tag "+tag, xpp.getLineNumber());
            }
        }

    }
