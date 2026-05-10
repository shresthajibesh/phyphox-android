package de.rwth_aachen.phyphox.features.experiment.old.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import de.rwth_aachen.phyphox.DataExport;
import de.rwth_aachen.phyphox.PhyphoxExperiment;
import de.rwth_aachen.phyphox.features.experiment.Experiment;
import de.rwth_aachen.phyphox.features.experiment.old.parser.error.PhyphoxFileException;

public class setBlockParser extends XmlBlockParser {
        private DataExport.ExportSet set;

        //This constructor takes an additional argument: The export set to be filled
        setBlockParser(XmlPullParser xpp, PhyphoxExperiment experiment, Experiment parent, DataExport.ExportSet set) {
            super(xpp, experiment, parent);
            this.set = set;
        }

        @Override
        protected void processStartTag(String tag) throws XmlPullParserException, PhyphoxFileException, IOException {
            switch (tag.toLowerCase()) {
                case "data": //Add this data buffer to the set
                    String name = xpp.getAttributeValue(XmlPullParser.NO_NAMESPACE, "name");
                    String src = getText();
                    if (experiment.getBuffer(src) != null)
                        set.addSource(name, src);
                    else
                        throw new PhyphoxFileException("Export buffer " + src + " has not been defined as a buffer.", xpp.getLineNumber());
                    break;
                default:
                    throw new PhyphoxFileException("Unknown tag "+tag, xpp.getLineNumber());
            }
        }

    }
