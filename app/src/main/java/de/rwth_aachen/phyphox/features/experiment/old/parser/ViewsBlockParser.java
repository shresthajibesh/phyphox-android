package de.rwth_aachen.phyphox.features.experiment.old.parser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import de.rwth_aachen.phyphox.ExpView;
import de.rwth_aachen.phyphox.PhyphoxExperiment;
import de.rwth_aachen.phyphox.features.experiment.Experiment;
import de.rwth_aachen.phyphox.features.experiment.old.parser.error.PhyphoxFileException;

public class ViewsBlockParser extends XmlBlockParser {

        ViewsBlockParser(XmlPullParser xpp, PhyphoxExperiment experiment, Experiment parent) {
            super(xpp, experiment, parent);
        }

        @Override
        protected void processStartTag(String tag)  throws IOException, XmlPullParserException, PhyphoxFileException {
            switch (tag.toLowerCase()) {
                case "view": //A view defines an arangement of elements displayed to the user
                    ExpView newView = new ExpView(); //Create a new view
                    newView.name = getTranslatedAttribute("label"); //Fill its name
                    (new ViewBlockParser(xpp, experiment, parent, newView)).process(); //And load its elements
                    if (newView.name != null && newView.elements.size() > 0) //We will only add it if it has a name and at least a single view
                        experiment.experimentViews.add(newView);
                    else {
                        //No name or no views. Complain!
                        throw new PhyphoxFileException("Invalid view.", xpp.getLineNumber());
                    }
                    break;
                default: //Unknown tag
                    throw new PhyphoxFileException("Unknown tag "+tag, xpp.getLineNumber());
            }
        }

    }
