package de.rwth_aachen.phyphox.features.experiment.old.parser;

import static de.rwth_aachen.phyphox.features.experiment.old.PhyphoxFile.languageRating;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import de.rwth_aachen.phyphox.Helper.Helper;
import de.rwth_aachen.phyphox.PhyphoxExperiment;
import de.rwth_aachen.phyphox.features.experiment.Experiment;
import de.rwth_aachen.phyphox.features.experiment.old.parser.error.PhyphoxFileException;

public class TranslationsBlockParser extends XmlBlockParser {

        TranslationsBlockParser(XmlPullParser xpp, PhyphoxExperiment experiment, Experiment parent) {
            super(xpp, experiment, parent);
        }

        @Override
        protected void processStartTag(String tag)  throws IOException, XmlPullParserException, PhyphoxFileException {
            switch (tag.toLowerCase()) {
                case "translation": //A translation block holds all translation information for a single language
                    String thisLocale = getStringAttribute("locale");
                    int thisLaguageRating = Helper.getLanguageRating(parent.getResources(), thisLocale);
                    if (thisLaguageRating > languageRating) { //Check if the language matches better than previous ones...
                        languageRating = thisLaguageRating;
                        (new TranslationBlockParser(xpp, experiment, parent)).process(); //Jepp, use it!
                    } else
                        (new XmlBlockParser(xpp, experiment, parent)).process(); //Nope. Use the empty block parser to skip it
                    break;
                default: //Unknown tag...
                    throw new PhyphoxFileException("Unknown tag "+tag, xpp.getLineNumber());
            }
        }

    }
