package de.rwth_aachen.phyphox.features.experiment.old.parser;

import static de.rwth_aachen.phyphox.features.experiment.old.PhyphoxFile.translation;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import de.rwth_aachen.phyphox.PhyphoxExperiment;
import de.rwth_aachen.phyphox.features.experiment.Experiment;
import de.rwth_aachen.phyphox.features.experiment.old.parser.error.PhyphoxFileException;

public class TranslationBlockParser extends XmlBlockParser {

        TranslationBlockParser(XmlPullParser xpp, PhyphoxExperiment experiment, Experiment parent) {
            super(xpp, experiment, parent);
        }

        @Override
        protected void processStartTag(String tag) throws XmlPullParserException, PhyphoxFileException, IOException {
            switch (tag.toLowerCase()) {
                case "title": //A title in our language? Great, take it!
                    experiment.title = getText();
                    break;
                case "category": //Category in the correct language
                    experiment.category = getText();
                    break;
                case "description": //Description in the correct language
                    experiment.description = getText().trim().replaceAll("(?m) +$", "").replaceAll("(?m)^ +", "");
                    break;
                case "link": //Links to external sources like documentation
                    boolean highlighted = getBooleanAttribute("highlight", false);
                    String label = getStringAttribute("label");
                    String link = getText().trim().replaceAll("(?m) +$", "").replaceAll("(?m)^ +", "");
                    experiment.links.put(label, link);
                    if (highlighted)
                        experiment.highlightedLinks.put(label, link);
                    break;
                case "string": //Some other translation. In labels and names of view elements, the string defined here as the attribute "original" will be replaced by the text in this tag
                    translation.put(getStringAttribute("original"), getText()); //Store it in our translation mapping
                    break;
                default: //Unknown tag
                    throw new PhyphoxFileException("Unknown tag "+tag, xpp.getLineNumber());
            }
        }

    }
