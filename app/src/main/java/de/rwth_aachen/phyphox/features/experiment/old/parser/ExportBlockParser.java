package de.rwth_aachen.phyphox.features.experiment.old.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import de.rwth_aachen.phyphox.DataExport;
import de.rwth_aachen.phyphox.PhyphoxExperiment;
import de.rwth_aachen.phyphox.features.experiment.Experiment;
import de.rwth_aachen.phyphox.features.experiment.old.parser.error.PhyphoxFileException;

public class ExportBlockParser extends XmlBlockParser {

        ExportBlockParser(XmlPullParser xpp, PhyphoxExperiment experiment, Experiment parent) {
            super(xpp, experiment, parent);
        }

        @Override
        protected void processStartTag(String tag) throws XmlPullParserException, IOException, PhyphoxFileException {
            switch (tag.toLowerCase()) {
                case "set": //An export set. These just group some dataBuffers to be exported as a set
                    DataExport.ExportSet set = experiment.exporter.new ExportSet(xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "name")); //Create the set with the given name
                    (new SetBlockParser(xpp, experiment, parent, set)).process(); //Parse the information within
                    experiment.exporter.addSet(set); //Add the set
                break;
                default:
                    throw new PhyphoxFileException("Unknown tag "+tag, xpp.getLineNumber());
            }
        }

        @Override
        protected void processEndTag(String tag) {

        }
    }
